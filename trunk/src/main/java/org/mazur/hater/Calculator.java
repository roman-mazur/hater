/**
 * 
 */
package org.mazur.hater;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.mazur.hater.gui.IterationsFrame;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.model.ModelContainer;
import org.mazur.hater.model.OutElement;
import org.mazur.hater.signals.SignalValue;
import org.mazur.hater.signals.TripleSignal;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class Calculator {

  private static final Logger LOG = Logger.getLogger(Calculator.class);
  
  private ModelContainer model;
  
  private String lastMesssage;
  
  private LinkedList<Map<AbstractElement, SignalValue>> iterations = new LinkedList<Map<AbstractElement, SignalValue>>();
  
  /** Critical path. */
  private int criticalPath = 0;

  /** Flag to reset init values. */
  private boolean resetInitValues = false;
  
  /**
   * @param resetInitValues the resetInitValues to set
   */
  public void setResetInitValues(final boolean resetInitValues) {
    this.resetInitValues = resetInitValues;
  }

  /**
   * @return the iterations
   */
  public LinkedList<Map<AbstractElement, SignalValue>> getIterations() {
    return iterations;
  }

  public Calculator(final ModelContainer model) {
    this.model = model;
  }
  
  protected boolean repeat(final Map<AbstractElement, SignalValue> m1, final Map<AbstractElement, SignalValue> m2) {
    boolean r = m1.entrySet().equals(m2.entrySet()) && m1.keySet().equals(m2.keySet());
    LOG.debug("Check repeat: " + r);
    return r;
  }
  
  /**
   * @return the criticalPath
   */
  public int getCriticalPath() {
    return criticalPath;
  }

  protected void setCriticalPath(final int value) {
    this.criticalPath = value;
  }
  
  protected ModelContainer getModel() {
    return model;
  }
  
  private int maxIterations() {
    int res = 0;
    for (AbstractElement in : model.getInputs()) {
      int a = in.getChildrenDepth(new HashMap<AbstractElement, Integer>());
      LOG.debug("From " + in.getLabel() + ": " + a);
      res = res < a ? a : res;
    }
    setCriticalPath(res - 1);
    LOG.debug("Critical way: " + res);
    return res;
  }
  
  protected void setLastMesssage(String lastMesssage) {
    this.lastMesssage = lastMesssage;
  }
  
  protected LinkedHashMap<AbstractElement, SignalValue> init(final List<SignalValue> inputValues, final int allSize) {
    int i = 0;
    for (SignalValue v : inputValues) {
      model.getInputs().get(i++).setValue(v);
    }
    LinkedHashMap<AbstractElement, SignalValue> currentResult = new LinkedHashMap<AbstractElement, SignalValue>(allSize);
    LOG.debug("start values:");
    for (AbstractElement el : model.getMainElemets()) {
      if (resetInitValues || el.getValue() == null) { el.setValue(el.getInitValue()); }
      LOG.debug(el.getValue());
      currentResult.put(el, el.getValue());
    }
    for (AbstractElement el : model.getOutputs()) {
      el.setValue(null);
      currentResult.put(el, el.getValue());
    }
    return currentResult;
  }
  
  public void process(final List<SignalValue> inputValues) {
    lastMesssage = null;
    LOG.info("Start calculations for " + inputValues);
    if (inputValues.contains(null)) { return; }
    iterations.clear();
    
    List<AbstractElement> all = new LinkedList<AbstractElement>(model.getMainElemets());
    all.addAll(model.getOutputs());
    
    // init
    LinkedHashMap<AbstractElement, SignalValue> currentResult = init(inputValues, all.size());
    
    int maxCount = maxIterations();
    
    int count = 0;
    boolean repeatedResult = false;
    do {
      iterations.add(new LinkedHashMap<AbstractElement, SignalValue>(currentResult));
      if (LOG.isDebugEnabled()) {
        LOG.debug("Iterations:");
        for (Map<AbstractElement, SignalValue> map : iterations) {
          LOG.debug("----");
          for (Entry<AbstractElement, SignalValue> entry : map.entrySet()) {
            LOG.debug(entry.getKey() + " -> " + entry.getValue());
          }
        }
      }
      currentResult = new LinkedHashMap<AbstractElement, SignalValue>(all.size());
      LOG.debug("Iteration " + count);
      for (AbstractElement el : all) {
        SignalValue nv = el.calculate();
        LOG.debug(el.getLabel() + " -> " + nv);
        currentResult.put(el, nv);
      }
      for (AbstractElement el : all) {
        SignalValue b = currentResult.get(el);
        if (el instanceof OutElement) {
          b = currentResult.get(el.getInputs().get(0));
          currentResult.put(el, b);
        } else {
          b = currentResult.get(el);
        }
        el.setValue(b);
        LOG.debug("Set value " + b + " for " + el.getLabel());
      }
      count++;
      repeatedResult = repeat(iterations.getLast(), currentResult);
    } while (!repeatedResult && count < maxCount);
    
    LOG.info("Total count of iterations: " + iterations.size());
    
    if (!repeatedResult) {
      LOG.debug("Defining the generator.");
      StringBuilder sb = new StringBuilder("Generator is detected on elements: ");
      Map<AbstractElement, SignalValue> comparator = iterations.getLast();
      for (AbstractElement el : comparator.keySet()) {
        SignalValue prevValue = comparator.get(el);
        SignalValue currentValue = currentResult.get(el);
        if (!prevValue.equals(currentValue)) {
          sb.append(el.getLabel()).append(", ");
        }
      }
      lastMesssage = sb.toString();
    } else {
      StringBuilder sb = new StringBuilder();
      for (Entry<AbstractElement, SignalValue> e : iterations.getLast().entrySet()) {
        if (e.getValue() == TripleSignal.UNDEFINED && !(e.getKey() instanceof OutElement)) {
          sb.append(e.getKey().getLabel()).append(", ");
        }
      }
      if (sb.length() > 0) {
        sb.insert(0, "Generator is detected on elements: ");
        lastMesssage = sb.toString();
      }
      iterations.add(currentResult);
    }
  }
  
  public Map<AbstractElement, SignalValue> getLastValues() {
    return iterations.getLast();
  }
  
  public int getIterationsCount() {
    return iterations.size();
  }
  
  public String getMessage() {
    return lastMesssage;
  }
  
  public JFrame getIterationsFrame() {
    return new IterationsFrame(this);
  }
}
