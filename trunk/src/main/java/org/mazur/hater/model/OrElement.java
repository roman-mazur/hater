/**
 * 
 */
package org.mazur.hater.model;

import java.awt.Color;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class OrElement extends AbstractElement {

  private static final long serialVersionUID = 7816270991838633865L;

  public OrElement(final int inCount) {
    super(inCount);
  }

  @Override
  public ElementType getElementType() {
    return ElementType.OR;
  }

  @Override
  protected Color getViewColor() {
    return Color.GREEN;
  }

  @Override
  public Boolean calculate() {
    if (getInputs().isEmpty()) { return null; }
    boolean result = false;
    for (AbstractElement input : getInputs()) {
      if (input.getValue() == null) { return null; }
      result |= input.getValue();
    }
    return result;
  }

}
