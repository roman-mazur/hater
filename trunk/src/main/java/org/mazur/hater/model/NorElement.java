/**
 * 
 */
package org.mazur.hater.model;

import java.awt.Color;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class NorElement extends OrElement {

  private static final long serialVersionUID = 4591692837813007807L;

  public NorElement(final int inCount) {
    super(inCount);
  }

  @Override
  public ElementType getElementType() {
    return ElementType.NOR;
  }

  @Override
  protected Color getViewColor() {
    return Color.BLUE;
  }

  @Override
  public Boolean calculate() {
    Boolean v = super.calculate();
    return v == null ? null : !v;
  }
}
