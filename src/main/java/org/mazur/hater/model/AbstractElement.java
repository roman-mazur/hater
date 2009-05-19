/**
 * 
 */
package org.mazur.hater.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.mazur.hater.signals.SignalValue;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public abstract class AbstractElement implements Serializable {

  private static final Logger LOG = Logger.getLogger(AbstractElement.class);
  
  private static final long serialVersionUID = 2224152428283941615L;

  private int inCount;
  
  private List<AbstractElement> inputs;
  
  private List<AbstractElement> outputs;

  private int internalNumber, visibleNumber;
  
  private DefaultElementView view;
  
  /** Value. */
  private SignalValue value = null, initValue = null;
  
  /** Element delay. */
  private int delay = 1;
  
  private SignalModelHolder signalModelHolder;
  
  public AbstractElement(final SignalModelHolder sHolder, final int inCount) {
    this.inCount = inCount;
    inputs = new ArrayList<AbstractElement>(inCount);
    outputs = new LinkedList<AbstractElement>();
    signalModelHolder = sHolder;
    value = signalModelHolder.getOperations().defaultValue();
    initValue = signalModelHolder.getOperations().defaultValue();
    setDelay(1);
  }
  
  public SignalModelHolder getSignalModelHolder() { return signalModelHolder; }
  
  public void setDelay(final int delay) {
    LOG.debug("SET DELAY: " + delay);
    this.delay = delay;
  }
  
  public int getDelay() { return delay; }
  
  /**
   * @return the initValue
   */
  public SignalValue getInitValue() {
    return initValue;
  }

  /**
   * @param initValue the initValue to set
   */
  public void setInitValue(final SignalValue initValue) {
    this.initValue = initValue;
  }

  /**
   * @return the value
   */
  public SignalValue getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(SignalValue value) {
    this.value = value;
  }

  /**
   * @return the visibleNumber
   */
  public int getVisibleNumber() {
    return visibleNumber;
  }

  /**
   * @param visibleNumber the visibleNumber to set
   */
  public void setVisibleNumber(int visibleNumber) {
    this.visibleNumber = visibleNumber;
  }

  
  /**
   * @return the number
   */
  public int getInternalNumber() {
    return internalNumber;
  }

  /**
   * @param number the number to set
   */
  public void setInternalNumber(final int number) {
    this.internalNumber = number;
  }

  /**
   * @return the outputs
   */
  public List<AbstractElement> getOutputs() {
    return outputs;
  }

  /**
   * @param outputs the outputs to set
   */
  public void setOutputs(List<AbstractElement> outputs) {
    this.outputs = outputs;
  }

  public abstract ElementType getElementType();
  public String getLabel() {
    return getElementType().getLabel() + "(" + inCount + ")-" + visibleNumber;
  }

  /**
   * @return the inCount
   */
  public int getInCount() {
    return inCount;
  }

  /**
   * @return the inputs
   */
  public List<AbstractElement> getInputs() {
    return inputs;
  }

  /**
   * @param inCount the inCount to set
   */
  public void setInCount(int inCount) {
    this.inCount = inCount;
  }

  /**
   * @param inputs the inputs to set
   */
  public void setInputs(List<AbstractElement> inputs) {
    this.inputs = inputs;
  }

  public DefaultElementView getView() {
    return view; 
  }
  
  protected abstract Color getViewColor();
  
  protected DefaultElementView createView(final Point p) {
    return new DefaultElementView(this, p);
  }
  
  public final void prepareView(final Point p) {
    view = createView(p);
  }
  
  public Object connect(final AbstractElement el) {
    if (inputs.size() >= inCount) {
      throw new EditModelException("Element " + getLabel() + " does not have any free inputs.");
    }
    inputs.add(el);
    el.outputs.add(this);
    return getView().connect(el.getView());
  }

  public abstract SignalValue calculate();
  
  protected List<SignalValue> getInputSignals() {
    List<SignalValue> values = new ArrayList<SignalValue>(inputs.size());
    for (AbstractElement in : inputs) { values.add(in.getValue()); }
    LOG.debug(getElementType() + " in signals: " + values);
    return values;
  }
  
  public int getChildrenDepth(final HashMap<AbstractElement, Integer> visited) {
    if (outputs.isEmpty()) { return 0; }
    int result = 0;
    Integer counter = visited.get(this);
    counter = counter == null ? 1 : counter + 1;
    visited.put(this, counter);
    for (AbstractElement el : outputs) {
      counter = visited.get(el);
      if (counter != null && counter > 1) { continue; }
      int a = el.getChildrenDepth(visited);
      result = result < a ? a : result;
    }
    return result + 1;
  }
  
  public int getCycleDepth(final HashMap<AbstractElement, Integer> visited) {
    if (outputs.isEmpty()) { return 0; }
    int result = 0;
    Integer counter = visited.get(this);
    counter = counter == null ? 1 : counter + 1;
    visited.put(this, counter);
    for (AbstractElement el : outputs) {
      Integer c = visited.get(el);
      if (c != null && c > 1) { continue; }
      int a = el.getCycleDepth(visited);
      result = result < a ? a : result;
    }
    if (counter > 1) { result++; }
    return result;
  }
  
  public static class DefaultElementView extends DefaultGraphCell {
    /** serialVersionUID. */
    private static final long serialVersionUID = -6992599972368530559L;
    
    private AbstractElement element;
    
    private List<DefaultEdge> inputEdges = new LinkedList<DefaultEdge>();
    
    public DefaultElementView(final AbstractElement element, final Point point) {
      this.element = element;
      GraphConstants.setBounds(getAttributes(), 
          new Rectangle2D.Double(point.x, point.y, 50, 65));
      GraphConstants.setOpaque(getAttributes(), true);
      GraphConstants.setValue(getAttributes(), element.getLabel());
      GraphConstants.setBorderColor(getAttributes(), Color.BLACK);
      GraphConstants.setGradientColor(getAttributes(), getColor());
      Rectangle2D bounds = GraphConstants.getBounds(getAttributes());
      DefaultPort inPort = new DefaultPort(new Point(0, (int)bounds.getWidth() / 2));
      DefaultPort outPort = new DefaultPort(new Point((int)bounds.getWidth() - 1, 
          (int)bounds.getWidth() / 2));
      add(inPort);
      add(outPort);
    }
    
    public AbstractElement getElement() { return element; }
    
    public List<DefaultEdge> getInputEdges() { return inputEdges; }
    
    private Object connect(final DefaultElementView v) {
      DefaultEdge edge = new DefaultEdge();
      DefaultPort sPort = (DefaultPort)v.getChildAt(1); 
      DefaultPort tPort = (DefaultPort)getChildAt(0); 
      edge.setSource(sPort);
      edge.setTarget(tPort);
      sPort.addEdge(edge);
      tPort.addEdge(edge);
      
      GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_CLASSIC);
      GraphConstants.setEndFill(edge.getAttributes(), true);
      inputEdges.add(edge);
      return edge;
    }
    
    public Color getColor() {
      return element.getViewColor();
    }
  }

  @Override
  public String toString() {
    return "[" + getLabel() + " " + getElementType() + "]";
  }
}
