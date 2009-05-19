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
public class NorElement extends OrElement {

  private static final long serialVersionUID = 4591692837813007807L;

  public NorElement(final SignalModelHolder sHolder, final int inCount) {
    super(sHolder, inCount);
    setDelay(2);
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
  public SignalValue calculate() {
    SignalValue v = super.calculate();
    return getSignalModelHolder().getOperations().not(v);
  }
}
