/**
 * 
 */
package org.mazur.hater.model;

import java.awt.Color;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class NandElement extends AndElement {

  private static final long serialVersionUID = 1117934689450806287L;

  public NandElement(final int inCount) {
    super(inCount);
  }

  @Override
  public ElementType getElementType() {
    return ElementType.NAND;
  }

  @Override
  protected Color getViewColor() {
    return Color.CYAN;
  }
  
  @Override
  public Boolean calculate() {
    Boolean v = super.calculate();
    return v == null ? null : !v;
  }
}
