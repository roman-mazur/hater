package org.mazur.hater.signals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

  public List<T> nextTerm(final List<T> values, int l) {
    List<T> result = new ArrayList<T>(l);
    for (int i = 0; i < l; i++) { result.add(zero()); }
    if (values == null) { return result; }
    for (int i = 0; i < l; i++) { result.set(i, values.get(i)); }
    for (int i = l - 1; i >= 0; i--) {
      result.set(i, next(values.get(i)));
      if (prev(result.get(i)) != one()) { break; }
    }
    return result;
  }
}
