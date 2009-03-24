/**
 * 
 */
package org.mazur.hater.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.model.ModelContainer;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class SetInitValuesFrame extends JFrame {

  private static final Logger LOG = Logger.getLogger(SetInitValuesFrame.class);
  
  private static final long serialVersionUID = -7640036288745879787L;

  private ModelContainer model;
  
  private JPanel mainPanel;
  
  public SetInitValuesFrame(final ModelContainer model) {
    super("Initial values");
    this.model = model;
    mainPanel = new JPanel();
    Container pane = getContentPane();
    pane.setLayout(new BorderLayout());
    pane.add(BorderLayout.CENTER, mainPanel);
    
    JPanel bottomBtns = new JPanel();
    bottomBtns.setLayout(new BorderLayout());
    pane.add(BorderLayout.SOUTH, bottomBtns);
    
    JButton btn = new JButton("OK");
    btn.addActionListener(new AbstractAction() {
      private static final long serialVersionUID = 4935453095029776738L;
      public void actionPerformed(final ActionEvent e) {
        SetInitValuesFrame.this.setVisible(false);
      }
    });
    bottomBtns.add(BorderLayout.CENTER, btn);
  }

  public void display() {
    if (isVisible()) { return; }
    LOG.debug("Display " + getClass() + " - '" + getTitle() + "' frame");
    mainPanel.removeAll();
    List<AbstractElement> elements = model.getMainElemets();
    if (elements.isEmpty()) {
      mainPanel.setLayout(new BorderLayout());
      mainPanel.add(BorderLayout.CENTER, new JLabel("No elemets."));
    } else {
      mainPanel.setLayout(new GridLayout(elements.size(), 2));
      for (final AbstractElement e : model.getMainElemets()) {
        mainPanel.add(new JLabel(e.getLabel()));
        final JTextField field = new JTextField(e.getInitValueStr());
        field.addActionListener(new ActionListener() {
          public void actionPerformed(final ActionEvent event) {
            e.parseInitValue(field.getText());
            field.setText(e.getInitValueStr());
          }
        });
        mainPanel.add(field);
      }
    }
    pack();
    setVisible(true);
  }
  
}
