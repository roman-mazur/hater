/**
 * 
 */
package org.mazur.hater.model;

import java.awt.Color;

/**
 * "And" element.
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class AndElement extends AbstractElement {

  private static final long serialVersionUID = 9111308044640127297L;

  public AndElement(final int inCount) {
    super(inCount);
  }

  @Override
  public ElementType getElementType() {
    return ElementType.AND;
  }

  @Override
  protected Color getViewColor() {
    return Color.RED;
  }

  @Override
  public Boolean calculate() {
    if (getInputs().isEmpty()) { return null; }
    boolean result = true;
    for (AbstractElement input : getInputs()) {
      if (input.getValue() == null) { return null; }
      result &= input.getValue();
    }
    return result;
  }

}
