package org.mazur.hater.signals;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public enum BinSignal implements SignalValue {
  /** Values. */
  TRUE(true), FALSE(false);
  /** Internal value. */
  private boolean v;
  
  private BinSignal(final boolean v) { this.v = v; }
  public Object getPojo() { return Boolean.valueOf(v); }
  public String getPrintable() { return v ? "1" : "0"; }
  
  static BinSignal valueOf(final boolean v) {
    return v ? TRUE : FALSE;
  }
  boolean getValue() { return v; }
  
  public double getDouble() {
    return v ? 1 : 0;
  }
  
}
