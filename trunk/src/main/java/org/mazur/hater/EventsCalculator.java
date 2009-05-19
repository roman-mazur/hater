package org.mazur.hater;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.mazur.hater.gui.EventsIterationsFrame;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.model.ModelContainer;
import org.mazur.hater.signals.SignalValue;

public class EventsCalculator extends Calculator {
  
  /** Logger. */
  private static final Logger LOG = Logger.getLogger(EventsCalculator.class);
  
  private LinkedList<IterationContainer> allIterations = new LinkedList<IterationContainer>();
   
  public EventsCalculator(ModelContainer model) {
    super(model);
  }

  @Override
  public void process(final List<SignalValue> inputValues) {
    setLastMesssage(null);
    LOG.info("Start calculations for " + inputValues);
    if (inputValues.contains(null)) { return; }
    
    // init
    ArrayList<AbstractElement> all = new ArrayList<AbstractElement>(getModel().getMainElemets());
    all.addAll(getModel().getOutputs());
    
    IterationContainer currentResult = new IterationContainer();
    currentResult.values = init(inputValues, all.size());
    currentResult.time = 0;
    allIterations.add(currentResult);
    
  }
  
  @Override
  public Map<AbstractElement, SignalValue> getLastValues() {
    return allIterations.getLast().values;
  }
  
  public List<IterationContainer> getAllIterations() {
    return allIterations;
  }
  
  @Override
  public JFrame getIterationsFrame() {
    return new EventsIterationsFrame(this);
  }
  
  public static class IterationContainer {
    private int time;
    private LinkedHashMap<AbstractElement, SignalValue> values;
    public int getTime() {
      return time;
    }
    public LinkedHashMap<AbstractElement, SignalValue> getValues() {
      return values;
    }
  }
}
