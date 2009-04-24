package org.mazur.hater.signals;

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
  
  public BinSignal defaultValue() {
    return BinSignal.FALSE;
  }
  
  public BinSignal next(BinSignal value) {
    return not(value);
  }
  public BinSignal prev(BinSignal value) {
    return not(value);
  }
  
}
