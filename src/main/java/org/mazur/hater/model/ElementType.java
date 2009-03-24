/**
 * 
 */
package org.mazur.hater.model;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public enum ElementType {
  AND("&"), OR("1"), NAND("^&"), NOR("^1"), NOT("^"), IN("in"), OUT("out");
  private String lbl;
  private ElementType(final String labl) {
    this.lbl = labl;
  }
  public String getLabel() {
    return lbl;
  }
}
