/**
 * 
 */
package org.mazur.hater;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
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
  
  /**
   * @return the iterations
   */
  public LinkedList<Map<AbstractElement, SignalValue>> getIterations() {
    return iterations;
  }

  public Calculator(final ModelContainer model) {
    this.model = model;
  }
  
  private boolean repeat(final Map<AbstractElement, SignalValue> m1, final Map<AbstractElement, SignalValue> m2) {
    boolean r = m1.entrySet().equals(m2.entrySet()) && m1.keySet().equals(m2.keySet());
    LOG.debug("Check repeat: " + r);
    return r;
  }
  
  private int maxIterations() {
    int res = 0;
    for (AbstractElement in : model.getInputs()) {
      int a = in.getChildrenDepth(new HashSet<AbstractElement>());
      LOG.debug("From " + in.getLabel() + ": " + a);
      res = res < a ? a : res;
    }
    res++;
    LOG.debug("Critical way: " + res);
    return res;
  }
  
  public void process(final List<SignalValue> inputValues) {
    lastMesssage = null;
    LOG.info("Start calculations for " + inputValues);
    iterations.clear();
    
    List<AbstractElement> all = new LinkedList<AbstractElement>(model.getMainElemets());
    all.addAll(model.getOutputs());
    
    // init
    int i = 0;
    for (SignalValue v : inputValues) {
      model.getInputs().get(i++).setValue(v);
    }
    HashMap<AbstractElement, SignalValue> currentResult = new HashMap<AbstractElement, SignalValue>(all.size());
    for (AbstractElement el : model.getMainElemets()) {
      el.setValue(el.getInitValue());
      currentResult.put(el, el.getValue());
    }
    for (AbstractElement el : model.getOutputs()) {
      el.setValue(null);
      currentResult.put(el, el.getValue());
    }
   
    int maxCount = maxIterations();
    
    int count = 0;
    boolean repeatedResult = false;
    do {
      iterations.add(currentResult);
      currentResult = new HashMap<AbstractElement, SignalValue>(all.size());
      LOG.debug("Iteration " + count);
      for (AbstractElement el : all) {
        SignalValue nv = el.calculate();
        LOG.debug(el.getLabel() + " -> " + nv);
        currentResult.put(el, nv);
      }
      for (AbstractElement el : all) {
        SignalValue b = currentResult.get(el);
        el.setValue(b);
        LOG.debug("Set value " + b + " for " + el.getLabel());
      }
      count++;
      repeatedResult = repeat(iterations.getLast(), currentResult);
    } while (!repeatedResult && count <= maxCount);
    
    LOG.info("Total count of iterations: " + iterations.size());
    
    if (!repeatedResult) {
      StringBuilder sb = new StringBuilder("Generator is detected on elements: ");
      for (AbstractElement el : iterations.getLast().keySet()) {
        SignalValue prevValue = iterations.getLast().get(el);
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
}
