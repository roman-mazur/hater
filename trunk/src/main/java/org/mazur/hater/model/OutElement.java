/**
 * 
 */
package org.mazur.hater.model;

import org.mazur.hater.signals.SignalValue;


/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class OutElement extends MarginElement {

  private static final long serialVersionUID = 4171095168353775168L;

  public OutElement(final SignalModelHolder sHolder) {
    super(sHolder, 1);
  }
  
  @Override
  public ElementType getElementType() {
    return ElementType.OUT;
  }

  @Override
  protected String getPrefix() {
    return "y";
  }

  @Override
  public SignalValue calculate() {
    return getInputs().get(0).getValue();  
  }

  public SignalValue getValue() {
    return calculate();
  }
}
