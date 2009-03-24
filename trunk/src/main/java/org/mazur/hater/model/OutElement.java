/**
 * 
 */
package org.mazur.hater.model;


/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class OutElement extends MarginElement {

  private static final long serialVersionUID = 4171095168353775168L;

  public OutElement() {
    super(1);
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
  public Boolean calculate() {
    return getInputs().isEmpty() ? null : getInputs().get(0).getValue();  
  }

}
