/**
 * 
 */
package org.mazur.hater.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.model.ModelContainer;
import org.mazur.hater.model.ModelContainer.SignalModel;
import org.mazur.hater.signals.SignalValue;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class SetInitValuesFrame extends JFrame {

  private static final Logger LOG = Logger.getLogger(SetInitValuesFrame.class);
  
  private static final long serialVersionUID = -7640036288745879787L;

  private HaterFrame frame;
  
  private ModelContainer model;
  
  private JPanel mainPanel, choisePanel;
  
  private HashMap<AbstractElement, JTextField> fields = new HashMap<AbstractElement, JTextField>();
  
  private void updateAllInitValues() {
    for (Entry<AbstractElement, JTextField> pair : fields.entrySet()) {
      SignalValue v = model.getOperations().defaultValue();
      pair.getKey().setInitValue(v);
      pair.getValue().setText(v.getPrintable());
    }
  }
  
  public SetInitValuesFrame(final HaterFrame frame) {
    super("Initial values");
    this.frame = frame;
    mainPanel = new JPanel();
    Container pane = getContentPane();
    pane.setLayout(new BorderLayout());
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BorderLayout());
    centerPanel.add(BorderLayout.NORTH, new JLabel("Set initial values:"));
    centerPanel.add(BorderLayout.CENTER, mainPanel);
    pane.add(BorderLayout.CENTER, centerPanel);
    
    choisePanel = new JPanel();
    pane.add(BorderLayout.NORTH, choisePanel);
    
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

  private void composeFields() {
    choisePanel.removeAll();
    choisePanel.setLayout(new GridLayout(SignalModel.values().length + 1, 1));
    choisePanel.add(new JLabel("Choose the signal model:          "));
    ButtonGroup signalModelChoise = new ButtonGroup();
    for (final SignalModel sm : SignalModel.values()) {
      JRadioButton btn = new JRadioButton(sm.getText());
      btn.setSelected(sm == model.getSignalModel());
      signalModelChoise.add(btn);
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) { 
          model.setSignalModel(sm);
          updateAllInitValues();
        }
      });
      choisePanel.add(btn);
    }
  }
  
  public void display() {
    if (isVisible()) { return; }
    model = frame.getCurrentModel();
    composeFields();
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
        final JTextField field = new JTextField(e.getInitValue().getPrintable());
        field.addActionListener(new ActionListener() {
          public void actionPerformed(final ActionEvent event) {
            SignalValue nv = e.getSignalModelHolder().getOperations().parseValue(field.getText());
            if (nv != null) { e.setInitValue(nv); }
            field.setText(e.getInitValue().getPrintable());
          }
        });
        mainPanel.add(field);
        fields.put(e, field);
      }
    }
    pack();
    setVisible(true);
  }
  
}
