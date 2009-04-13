package org.mazur.hater.signals;

import java.util.Arrays;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public abstract class AbstractOperations<T extends SignalValue> implements SignalOperations<T> {

  public T and(final T... values) {
    return and(Arrays.asList(values));
  }

  public T or(T... values) {
    return or(Arrays.asList(values));
  }

}
