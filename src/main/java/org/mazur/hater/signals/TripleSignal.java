package org.mazur.hater.signals;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public enum TripleSignal implements SignalValue {
  TRUE(true), FALSE(false), UNDEFINED(null);
  
  private Boolean v;

  private TripleSignal(final Boolean v) { this.v = v; }
  
  Boolean getValue() { return v; }
  static TripleSignal valueOf(final Boolean v) {
    return v == null ? UNDEFINED : v ? TRUE : FALSE;
  }
  
  public Object getPojo() { return v; }
  public String getPrintable() { 
    return v == null ? "x" : v ? "1" : "0"; 
  }
  
  public double getDouble() {
    return v == null ? 0.5 : v ? 1 : 0;
  }
}
