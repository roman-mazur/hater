/**
 * 
 */
package org.mazur.hater.model;

import java.awt.Color;

import org.mazur.hater.signals.SignalValue;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class NandElement extends AndElement {

  private static final long serialVersionUID = 1117934689450806287L;

  public NandElement(final SignalModelHolder sHolder, final int inCount) {
    super(sHolder, inCount);
    setDelay(2);
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
  public SignalValue calculate() {
    SignalValue v = super.calculate();
    return getSignalModelHolder().getOperations().not(v);
  }
}
