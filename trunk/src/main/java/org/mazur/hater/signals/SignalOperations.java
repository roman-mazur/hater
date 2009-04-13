package org.mazur.hater.signals;

import java.util.List;


/**
 * Version: $Id$
 * @param T signal value type
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public interface SignalOperations<T extends SignalValue> {

  T and(final T... values);
  T or(final T... values);
  T and(final List<T> values);
  T or(final List<T> values);
  T not(final T value);
  
  T one();
  T zero();
  
  T parseValue(final String str);
  
  List<T> nextTerm(final List<T> values, final int l);
  
  T defaultValue();
}
