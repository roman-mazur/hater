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
public class OrElement extends AbstractElement {

  private static final long serialVersionUID = 7816270991838633865L;

  public OrElement(final SignalModelHolder sHolder, final int inCount) {
    super(sHolder, inCount);
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
  public SignalValue calculate() {
    return getSignalModelHolder().getOperations().or(getInputSignals());
  }

}
