package org.mazur.hater.model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.GraphConstants;

public abstract class MarginElement extends AbstractElement {

  private static final long serialVersionUID = 2060902050538984628L;

  private int marginNumber;
  
  public MarginElement(final int inCount) {
    super(inCount);
  }
 
  /**
   * @return the marginNumber
   */
  public int getMarginNumber() {
    return marginNumber;
  }

  /**
   * @param marginNumber the marginNumber to set
   */
  public void setMarginNumber(int marginNumber) {
    this.marginNumber = marginNumber;
  }

  protected abstract String getPrefix();
  
  @Override
  protected Color getViewColor() {
    return Color.BLACK;
  }

  @Override
  protected DefaultElementView createView(Point p) {
    DefaultElementView result = super.createView(p);
    GraphConstants.setBounds(result.getAttributes(), 
        new Rectangle2D.Double(p.x, p.y, 25, 25));
    GraphConstants.setForeground(result.getAttributes(), Color.WHITE);
    return result;
  }
  
  @Override
  public String getLabel() {
    return getPrefix() + getVisibleNumber();
  }
  
}
