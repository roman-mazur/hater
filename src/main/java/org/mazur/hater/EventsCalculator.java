package org.mazur.hater;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.mazur.hater.gui.EventsIterationsFrame;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.model.ModelContainer;
import org.mazur.hater.model.OutElement;
import org.mazur.hater.signals.SignalValue;

public class EventsCalculator extends Calculator {
  
  /** Logger. */
  private static final Logger LOG = Logger.getLogger(EventsCalculator.class);
  
  private LinkedList<IterationContainer> allIterations = new LinkedList<IterationContainer>();
  
  /** Time line bits. */
  private BitSet timeLineBits = new BitSet();
  
  private TreeMap<Integer, HashSet<AbstractElement>> plan = new TreeMap<Integer, HashSet<AbstractElement>>(); 
  
  private int time = 0;
  
  private ArrayList<SignalValue> lastInputs = null;
  
  private Calculator simpleCalculator;
  
  public EventsCalculator(ModelContainer model) {
    super(model);
    simpleCalculator = new Calculator(model);
    simpleCalculator.setResetInitValues(true);
  }

  private void schedule(final int delay, final AbstractElement e) {
    int t = time + delay - 1;
    HashSet<AbstractElement> pl = plan.get(t);
    if (pl == null) {
      pl = new HashSet<AbstractElement>();
      plan.put(t, pl);
    }
    pl.add(e);
    timeLineBits.set(t);
  }
  
  private Collection<AbstractElement> next() {
    time = timeLineBits.nextSetBit(time);
    if (time < 0) { return Collections.emptyList(); }
    ArrayList<AbstractElement> closest = new ArrayList<AbstractElement>(plan.get(time));
    ++time;
    return closest;
  }
  
  private List<AbstractElement> generator(final IterationContainer current) {
    LinkedList<AbstractElement> result = new LinkedList<AbstractElement>();
    IterationContainer last = allIterations.getLast();
    for (IterationContainer ic : allIterations) {
      if (ic != last) {
        Iterator<Entry<AbstractElement, SignalValue>> i1 = ic.values.entrySet().iterator(), 
                                                      i2 = current.values.entrySet().iterator();
        boolean g = true;
        while (i1.hasNext() && i2.hasNext() && g) {
          Entry<AbstractElement, SignalValue> e1 = i1.next(), e2 = i2.next();
          if (!e1.getKey().equals(e2.getKey())) { LOG.warn("Error!!!!!!!!!"); }
          LOG.debug("Compare " + e1.getKey() + " and " + e1.getKey() + ": " + e1.getValue() + " vs " + e2.getValue() + " at " + ic.time
              + " last: " + last.time);
          g &= e1.getValue().equals(e2.getValue());
        }
        if (g) {
          LOG.debug("Compared iteration: " + ic.time);
          Iterator<Entry<AbstractElement, SignalValue>> ii1 = current.values.entrySet().iterator(), 
                                                        ii2 = last.values.entrySet().iterator();
          while (ii1.hasNext() && ii2.hasNext()) {
            Entry<AbstractElement, SignalValue> e1 = ii1.next(), e2 = ii2.next();
            LOG.debug("Compare " + e1.getKey() + " and " + e1.getKey() + ": " + e1.getValue() + " vs " + e2.getValue() + " at " + ic.time
                + " last: " + last.time);
            if (!e1.getValue().equals(e2.getValue()) && !(e1.getKey() instanceof OutElement)) { 
              result.add(e1.getKey());
            }
          }
          break; 
        }
      }
    }
    return result;
  }
  
  @Override
  public void process(final List<SignalValue> inputValues) {
    setLastMesssage(null);
    time = 0;
    allIterations.clear();
    timeLineBits.clear();
    plan.clear();
    if (isResetInitValues()) { simpleCalculator.process(inputValues); return; }
    
    LOG.info("Start calculations for " + inputValues);
    if (inputValues.contains(null)) { return; }
    
    // init
    ArrayList<AbstractElement> all = new ArrayList<AbstractElement>(getModel().getMainElemets());
    all.addAll(getModel().getOutputs());
    IterationContainer currentResult = new IterationContainer();
    currentResult.values = init(inputValues, all.size());
    currentResult.time = 0;
    int i = 0;
    for (AbstractElement in : getModel().getInputs()) {
      if (lastInputs == null || lastInputs.size() != inputValues.size() || !lastInputs.get(i).equals(inputValues.get(i))
          || isResetInitValues()) {
        for (AbstractElement e : in.getOutputs()) {
          schedule(e.getDelay(), e); 
        }
      }
      i++;
    }
    
    List<AbstractElement> gen = Collections.emptyList();
    do {
      allIterations.add(currentResult);
      LinkedHashMap<AbstractElement, SignalValue> savedValues = currentResult.values;
      currentResult = new IterationContainer();
      currentResult.changes = next();
      currentResult.time = time;
      currentResult.values = new LinkedHashMap<AbstractElement, SignalValue>(savedValues);
      LOG.info("---------------------------------");
      LOG.info("T" + currentResult.time + " " + currentResult.changes);
      for (AbstractElement e : currentResult.changes) {
        SignalValue saved = e.getValue();
        e.setValue(e.calculate());
        currentResult.values.put(e, e.getValue());
        LOG.info(e + " -> " + e.getValue());
        if (!saved.equals(e.getValue())) {
          for (AbstractElement o : e.getOutputs()) {
            if (o instanceof OutElement) {
              o.setValue(e.getValue());
              currentResult.values.put(o, o.getValue());
            } else {
              schedule(o.getDelay(), o); 
            }
          }
        }
        gen = generator(currentResult);
      }
    } while (!currentResult.changes.isEmpty() && gen.isEmpty());
    
    lastInputs = new ArrayList<SignalValue>(inputValues);
    
    if (!gen.isEmpty()) {
      allIterations.add(currentResult);
      setLastMesssage(gen.toString());
    }
    
  }
  
  @Override
  public Map<AbstractElement, SignalValue> getLastValues() {
    return isResetInitValues() ? simpleCalculator.getLastValues() : allIterations.getLast().values;
  }
  
  public List<IterationContainer> getAllIterations() {
    return allIterations;
  }
  
  @Override
  public JFrame getIterationsFrame() {
    return isResetInitValues() ? simpleCalculator.getIterationsFrame() : new EventsIterationsFrame(this);
  }
  
  public static class IterationContainer {
    private int time;
    private LinkedHashMap<AbstractElement, SignalValue> values;
    private Collection<AbstractElement> changes;
    public int getTime() {
      return time;
    }
    public LinkedHashMap<AbstractElement, SignalValue> getValues() {
      return values;
    }
    public Collection<AbstractElement> getChanges() {
      return changes;
    }
  }
}
