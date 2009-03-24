/**
 * 
 */
package org.mazur.hater.model;


/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class InElement extends MarginElement {

  private static final long serialVersionUID = -474856657086547036L;

  public InElement() {
    super(0);
  }
  
  @Override
  public ElementType getElementType() {
    return ElementType.IN;
  }

  @Override
  protected String getPrefix() {
    return "x";
  }
  
  @Override
  public Boolean calculate() {
    return getValue();
  }
}
