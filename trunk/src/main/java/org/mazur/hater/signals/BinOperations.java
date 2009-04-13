package org.mazur.hater.signals;

import java.util.ArrayList;
import java.util.List;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class BinOperations extends AbstractOperations<BinSignal> {

  public BinSignal and(final List<BinSignal> values) {
    boolean res = true;
    for (BinSignal value : values) {
      res &= value.getValue();
      if (!res) { break; }
    }
    return BinSignal.valueOf(res);
  }
  public BinSignal or(final List<BinSignal> values) {
    boolean res = false;
    for (BinSignal value : values) {
      res |= value.getValue();
      if (res) { break; }
    }
    return BinSignal.valueOf(res);
  }
  public BinSignal not(BinSignal value) { return BinSignal.valueOf(!value.getValue()); }
  public BinSignal one() { return BinSignal.TRUE; }
  public BinSignal zero() { return BinSignal.FALSE; }

  public BinSignal parseValue(final String str) {
    return "1".equals(str) ? one() : "0".equals(str) ? zero() : null;
  }
  
  public List<BinSignal> nextTerm(final List<BinSignal> values, final int l) {
    List<BinSignal> result = new ArrayList<BinSignal>(l);
    for (int i = 0; i < l; i++) { result.add(zero()); }
    if (values == null) { return result; }
    for (int i = 0; i < l; i++) { result.set(i, values.get(i)); }
    for (int i = l - 1; i >= 0; i--) {
      result.set(i, not(values.get(i)));
      if (result.get(i).getValue()) { break; }
    }
    return result;
  }
  
  public BinSignal defaultValue() {
    return BinSignal.FALSE;
  }
}
