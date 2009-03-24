/**
 * 
 */
package org.mazur.hater.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class ModelContainer implements Serializable {

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
}
