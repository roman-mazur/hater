/**
 * 
 */
package org.mazur.hater.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.model.ModelContainer;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class SelectElemetnsFrame extends JFrame {

  private static final long serialVersionUID = -8781583709208973985L;

  private ModelContainer model;
  
  private JPanel mainPanel;
  
  private SelectListener handler;
  
  private List<AbstractElement> result;
  
  public SelectElemetnsFrame(final ModelContainer model) {
    super("Select elements");
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
        if (handler != null) { 
          handler.process(result);
        }
        SelectElemetnsFrame.this.setVisible(false);
      }
    });
    bottomBtns.add(BorderLayout.CENTER, btn);
  }
  
  public void display(final List<AbstractElement> exclude, final SelectListener onSelect) {
    if (isVisible()) { return; }
    mainPanel.removeAll();
    result = new LinkedList<AbstractElement>();
    LinkedList<AbstractElement> res = new LinkedList<AbstractElement>();
    for (AbstractElement el : model.getAllElemets()) {
      if (!exclude.contains(el)) { res.add(el); }
    }
    if (res.isEmpty()) {
      handler = null;
      mainPanel.setLayout(new BorderLayout());
      mainPanel.add(BorderLayout.CENTER, new JLabel("No elemets."));
    } else {
      handler = onSelect;
      mainPanel.setLayout(new GridLayout(res.size(), 2));
      for (final AbstractElement el : res) {
        mainPanel.add(new JLabel(el.getLabel()));
        final JCheckBox box = new JCheckBox();
        box.setSelected(false);
        box.addActionListener(new ActionListener() {
          public void actionPerformed(final ActionEvent e) {
            if (box.isSelected()) {
              result.add(el);
            } else {
              result.remove(el);
            }
          }
        });
        mainPanel.add(box);
      }
    }
    pack();
    setVisible(true);
  }

  public static interface SelectListener {
    void process(final List<AbstractElement> elements);
  }
}
