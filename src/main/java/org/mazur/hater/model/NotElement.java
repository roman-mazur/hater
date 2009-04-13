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
public class NotElement extends AbstractElement {

  private static final long serialVersionUID = -6864546670231024303L;

  public NotElement(final SignalModelHolder sHolder) {
    super(sHolder, 1);
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
  public SignalValue calculate() {
    return getSignalModelHolder().getOperations().not(getInputs().get(0).getValue());
  }

}
