/**
 * 
 */
package org.mazur.hater.model;

import java.awt.Color;

import org.mazur.hater.signals.SignalValue;

/**
 * "And" element.
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class AndElement extends AbstractElement {

  private static final long serialVersionUID = 9111308044640127297L;

  public AndElement(final SignalModelHolder sHolder, final int inCount) {
    super(sHolder, inCount);
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
  public SignalValue calculate() {
    return getSignalModelHolder().getOperations().and(getInputSignals());
  }

}
