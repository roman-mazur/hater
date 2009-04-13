/**
 * 
 */
package org.mazur.hater.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.mazur.hater.signals.BinOperations;
import org.mazur.hater.signals.SignalOperations;
import org.mazur.hater.signals.SignalValue;
import org.mazur.hater.signals.TripleOperations;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class ModelContainer implements Serializable, SignalModelHolder {

  private static final long serialVersionUID = -7481732169065168420L;

  /** Elements map. */
  private TreeMap<Integer, AbstractElement> elements = new TreeMap<Integer, AbstractElement>(); 
  
  private LinkedList<AbstractElement> inputs = new LinkedList<AbstractElement>(), 
                                      outputs = new LinkedList<AbstractElement>(),
                                      others = new LinkedList<AbstractElement>();

  private LinkedList<Integer> freeNumbers = new LinkedList<Integer>(),
                              freeVisibleNumbers = new LinkedList<Integer>(),
                              freeInNumbers = new LinkedList<Integer>(),
                              freeOutNumbers = new LinkedList<Integer>();
  
  private static void addToList(final AbstractElement el, final LinkedList<AbstractElement> list,
      final LinkedList<Integer> numbersList) {
    if (numbersList.isEmpty()) {
      el.setVisibleNumber(list.size() + 1);
    } else {
      el.setVisibleNumber(numbersList.pop());
    }
    list.add(el);
  }
  
  private static void removeFromList(final AbstractElement el, final LinkedList<AbstractElement> list,
      final LinkedList<Integer> numbersList) {
    list.remove(el);
    numbersList.push(el.getVisibleNumber());
  }

  public void add(final AbstractElement el) {
    if (freeNumbers.isEmpty()) {
      el.setInternalNumber(elements.size() + 1);
    } else {
      el.setInternalNumber(freeNumbers.pop());
    }
    elements.put(el.getInternalNumber(), el);
    
    switch (el.getElementType()) {
    case IN:
      addToList(el, inputs, freeInNumbers);
      break;
    case OUT: 
      addToList(el, outputs, freeOutNumbers);
      break;
    default:
      addToList(el, others, freeVisibleNumbers);
    }
  }
  
  public void remove(final AbstractElement el) {
    elements.remove(el.getInternalNumber());
    freeNumbers.push(el.getInternalNumber());
    
    switch (el.getElementType()) {
    case IN: 
      removeFromList(el, inputs, freeInNumbers); 
      break;
    case OUT: 
      removeFromList(el, outputs, freeOutNumbers); 
      break;
    default:
      removeFromList(el, others, freeVisibleNumbers); 
    }
  }
  
  public List<AbstractElement> getMainElemets() {
    return others;
  }
  
  public List<AbstractElement> getAllElemets() {
    return new ArrayList<AbstractElement>(elements.values());
  }
  
  public List<AbstractElement> getInputs() {
    return inputs;
  }
  
  public List<AbstractElement> getOutputs() {
    return outputs;
  }

  @SuppressWarnings("unchecked")
  public enum SignalModel {
    BIN(new BinOperations()), TRIPLE(new TripleOperations());
    private SignalOperations operations;
    private SignalModel(final SignalOperations operaions) {
      this.operations = operaions;
    }
    public String getText() { return toString(); }
  }
  
  private SignalModel currentModel = SignalModel.TRIPLE;
  
  @SuppressWarnings("unchecked")
  public SignalOperations<SignalValue> getOperations() {
    return currentModel.operations;
  }
  
  public void setSignalModel(final SignalModel model) { this.currentModel = model; }
  
  public SignalModel getSignalModel() { return this.currentModel; }

  public void save(final OutputStream stream) throws IOException {
    ObjectOutputStream out = new ObjectOutputStream(stream);
    out.writeObject(this);
    out.close();
  }
  
  public static ModelContainer load(final InputStream stream) throws IOException {
    ObjectInputStream in = new ObjectInputStream(stream);
    try {
      return (ModelContainer)in.readObject();
    } catch (ClassNotFoundException e) {
      return null;
    }
  }
  
}
