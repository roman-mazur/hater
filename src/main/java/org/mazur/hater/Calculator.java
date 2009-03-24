/**
 * 
 */
package org.mazur.hater;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.model.ModelContainer;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class Calculator {

  private static final Logger LOG = Logger.getLogger(Calculator.class);
  
  private ModelContainer model;
  
  private LinkedList<Map<AbstractElement, Boolean>> iterations = new LinkedList<Map<AbstractElement,Boolean>>();
  
  /**
   * @return the iterations
   */
  public LinkedList<Map<AbstractElement, Boolean>> getIterations() {
    return iterations;
  }

  public Calculator(final ModelContainer model) {
    this.model = model;
  }
  
  private boolean repeat(final Map<AbstractElement, Boolean> m1, final Map<AbstractElement, Boolean> m2) {
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
  
  public void process(final List<Boolean> inputValues) {
    LOG.info("Start calculations for " + inputValues);
    iterations.clear();
    
    List<AbstractElement> all = new LinkedList<AbstractElement>(model.getMainElemets());
    all.addAll(model.getOutputs());
    
    // init
    int i = 0;
    for (Boolean v : inputValues) {
      model.getInputs().get(i++).setValue(v);
    }
    HashMap<AbstractElement, Boolean> currentResult = new HashMap<AbstractElement, Boolean>(all.size());
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
    do {
      iterations.add(currentResult);
      currentResult = new HashMap<AbstractElement, Boolean>(all.size());
      LOG.debug("Iteration " + count);
      for (AbstractElement el : all) {
        Boolean nv = el.calculate();
        LOG.debug(el.getLabel() + " -> " + nv);
        currentResult.put(el, nv);
      }
      for (AbstractElement el : all) {
        Boolean b = currentResult.get(el);
        el.setValue(b);
        LOG.debug("Set value " + b + " for " + el.getLabel());
      }
      count++;
    } while (!repeat(iterations.getLast(), currentResult) && count <= maxCount);
    
  }
  
  public Map<AbstractElement, Boolean> getLastValues() {
    return iterations.getLast();
  }
  
  public int getIterationsCount() {
    return iterations.size();
  }
  
}
