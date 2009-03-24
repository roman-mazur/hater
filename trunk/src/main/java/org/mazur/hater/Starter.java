/**
 * 
 */
package org.mazur.hater;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.mazur.hater.gui.HaterFrame;

/**
 * Starter.
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public class Starter {

  /**
   * @param args
   */
  public static void main(final String[] args) {
    final int size = 500;
    Dimension d = new Dimension(size, size);
    HaterFrame frame = new HaterFrame(d);
    frame.setSize(d);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

}
