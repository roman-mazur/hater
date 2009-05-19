package org.mazur.hater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.model.InElement;
import org.mazur.hater.model.ModelContainer;
import org.mazur.hater.model.OutElement;
import org.mazur.hater.signals.SignalValue;
import org.mazur.hater.signals.TripleSignal;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class RangeCalculator extends Calculator {
  
  /** Logger. */
  private static final Logger LOG = Logger.getLogger(RangeCalculator.class);
  
  public RangeCalculator(final ModelContainer model) {
    super(model);
  }

  private int maxIterations() {
    int res = 0;
    for (AbstractElement in : getModel().getInputs()) {
      int a = in.getCycleDepth(new HashMap<AbstractElement, Integer>());
      LOG.debug("------------ From " + in.getLabel() + ": " + a);
      res = res < a ? a : res;
    }
    setCriticalPath(res);
    res += 2;
    LOG.debug("Iterations count: " + res);
    return res;
  }
  
  private ArrayList<AbstractElement> sortElements() {
    LinkedList<AbstractElement> queue = new LinkedList<AbstractElement>(getModel().getInputs());
    LinkedList<AbstractElement> result = new LinkedList<AbstractElement>();
    HashSet<AbstractElement> visited = new HashSet<AbstractElement>();
    do {
      AbstractElement e = queue.poll();
      if (visited.contains(e)) { continue; }
      visited.add(e);
      LOG.debug("current: " + e);
      for (AbstractElement o : e.getOutputs()) { queue.add(o); }
      if (!(e instanceof InElement)) { result.add(e); }
    } while (!queue.isEmpty());
    return new ArrayList<AbstractElement>(result);
  }
  
  @Override
  public void process(final List<SignalValue> inputValues) {
    setLastMesssage(null);
    LOG.info("Start calculations for " + inputValues);
    if (inputValues.contains(null)) { return; }
    
    ArrayList<AbstractElement> all = sortElements();
    LOG.debug("Sorted result: " + all);
    
    int maxIterations = maxIterations();

    // init
    LinkedHashMap<AbstractElement, SignalValue> currentResult = init(inputValues, all.size());
    
    int iterationsCounter = 0;
    boolean repeatedResult = false;
    do {
      getIterations().add(new LinkedHashMap<AbstractElement, SignalValue>(currentResult));
      if (LOG.isDebugEnabled()) {
        LOG.debug("Iterations:");
        for (Map<AbstractElement, SignalValue> map : getIterations()) {
          LOG.debug("----");
          for (Entry<AbstractElement, SignalValue> entry : map.entrySet()) {
            LOG.debug(entry.getKey() + " -> " + entry.getValue());
          }
        }
      }
      currentResult = new LinkedHashMap<AbstractElement, SignalValue>(all.size());
      LOG.debug("Iteration " + iterationsCounter);
      for (AbstractElement el : all) {
        SignalValue nv = el.calculate();
        LOG.debug(el.getLabel() + " -> " + nv);
        currentResult.put(el, nv);
        if (el instanceof OutElement) {
          nv = currentResult.get(el.getInputs().get(0));
          currentResult.put(el, nv);
        }
        el.setValue(nv);
      }
      repeatedResult = repeat(getIterations().getLast(), currentResult);
      iterationsCounter++;
    } while (!repeatedResult && iterationsCounter < maxIterations);
    
    if (!repeatedResult) {
      LOG.debug("Defining the generator.");
      StringBuilder sb = new StringBuilder("Generator is detected on elements: ");
      Map<AbstractElement, SignalValue> comparator = getIterations().getLast();
      for (AbstractElement el : comparator.keySet()) {
        SignalValue prevValue = comparator.get(el);
        SignalValue currentValue = currentResult.get(el);
        if (!prevValue.equals(currentValue)) {
          sb.append(el.getLabel()).append(", ");
        }
      }
      setLastMesssage(sb.toString());
    } else {
      StringBuilder sb = new StringBuilder();
      for (Entry<AbstractElement, SignalValue> e : getIterations().getLast().entrySet()) {
        if (e.getValue() == TripleSignal.UNDEFINED && !(e.getKey() instanceof OutElement)) {
          sb.append(e.getKey().getLabel()).append(", ");
        }
      }
      if (sb.length() > 0) {
        sb.insert(0, "Generator is detected on elements: ");
        setLastMesssage(sb.toString());
      }
      getIterations().add(currentResult);
    }
    setLastMesssage((getMessage() != null ? getMessage() : "") + " Sort: " + all);
    LOG.debug("------------------------------------");
    LOG.debug(getMessage());
  }
  
}
