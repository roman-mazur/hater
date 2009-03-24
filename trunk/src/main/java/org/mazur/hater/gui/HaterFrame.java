/**
 * 
 */
package org.mazur.hater.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Main frame.
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public class HaterFrame extends JFrame {

  private static final long serialVersionUID = -1889221954717041209L;

  private SetInitValuesFrame initValuesFrame;
  
  public HaterFrame(final Dimension size) {
    super("Hater");
    Container pane = getContentPane();
    pane.setLayout(new BorderLayout());
    EditorPanel editor = new EditorPanel();
    pane.add(BorderLayout.CENTER, editor);
  
    initValuesFrame = new SetInitValuesFrame(editor.getModel());
    JMenuBar menuBar = new JMenuBar();
    JMenu editMenu = new JMenu("Edit");
    JMenuItem initMenuItem = new JMenuItem();
    initMenuItem.setAction(new AbstractAction() {
      private static final long serialVersionUID = -4676733074377508282L;
      public void actionPerformed(final ActionEvent e) {
        initValuesFrame.display();
      }
    });
    initMenuItem.setText("Set initial values...");
    editMenu.add(initMenuItem);
    menuBar.add(editMenu);
    pane.add(BorderLayout.NORTH, menuBar);
    
    pack();
  }

  private String getCaption() {
    return "";
  }
  
}
