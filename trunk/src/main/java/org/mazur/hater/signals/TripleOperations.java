package org.mazur.hater.signals;

import java.util.List;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class TripleOperations extends AbstractOperations<TripleSignal> {

  public TripleSignal or(final List<TripleSignal> values) {
    Boolean res = false;
    for (TripleSignal value : values) {
      if (value.getValue() == null) {
        res = null;
        break;
      }
      res |= value.getValue();
    }
    return TripleSignal.valueOf(res);
  }
  public TripleSignal and(final List<TripleSignal> values) {
    Boolean res = true;
    for (TripleSignal value : values) {
      if (value.getValue() == null) {
        res = null;
        break;
      }
      res &= value.getValue();
    }
    return TripleSignal.valueOf(res);
  }

  public TripleSignal not(final TripleSignal value) {
    switch (value) {
    case UNDEFINED: return TripleSignal.UNDEFINED;
    case TRUE: return TripleSignal.FALSE;
    case FALSE: return TripleSignal.TRUE;
    default:
      return null;
    }
  }

  public TripleSignal one() { return TripleSignal.TRUE; }
  public TripleSignal zero() { return TripleSignal.FALSE; }
  
  public TripleSignal parseValue(final String str) {
    return "1".equals(str) ? one() : "0".equals(str) ? zero() : "x".equals(str) ? TripleSignal.UNDEFINED : null;
  }
  
  public TripleSignal defaultValue() {
    return TripleSignal.UNDEFINED;
  }
  
  public TripleSignal next(TripleSignal value) {
    switch (value) {
    case FALSE: return TripleSignal.UNDEFINED;
    case UNDEFINED: return TripleSignal.TRUE;
    case TRUE: return TripleSignal.FALSE;
    default:
      return null;
    }
  }
  public TripleSignal prev(TripleSignal value) {
    switch (value) {
    case FALSE: return TripleSignal.TRUE;
    case TRUE: return TripleSignal.UNDEFINED;
    case UNDEFINED: return TripleSignal.FALSE;
    default:
      return null;
    }
  }

}
