/**
 * 
 */
package org.mazur.hater.model;

import java.awt.Color;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class NotElement extends AbstractElement {

  private static final long serialVersionUID = -6864546670231024303L;

  public NotElement() {
    super(1);
  }

  @Override
  public String getLabel() {
    return getElementType().getLabel() + "-" + getVisibleNumber();
  }
  
  @Override
  public ElementType getElementType() {
    return ElementType.NOT;
  }

  @Override
  protected Color getViewColor() {
    return Color.ORANGE;
  }

  @Override
  public Boolean calculate() {
    if (getInputs().isEmpty()) { return null; }
    Boolean in = getInputs().get(0).getValue();
    return in == null ? null : !in;
  }

}
