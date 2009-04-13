package org.mazur.hater.signals;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public interface SignalValue {

  /**
   * @return symbols
   */
  String getPrintable();
  /**
   * @return internal object-value
   */
  Object getPojo();
  /**
   * @return number representation
   */
  double getDouble();
  
}
