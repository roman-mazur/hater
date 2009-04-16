package org.mazur.hater.gui;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.mazur.hater.Calculator;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.signals.SignalValue;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class IterationsFrame extends JFrame {

  private static final long serialVersionUID = 8398015492244778216L;

  public IterationsFrame(final Calculator calculator) {
    super("Iterations frame");
    JTable table = new JTable();
    final ArrayList<AbstractElement> elemetnsArray = new ArrayList<AbstractElement>(calculator.getIterations().getFirst().keySet());
    final ArrayList<Map<AbstractElement, SignalValue>> iterations = 
      new ArrayList<Map<AbstractElement,SignalValue>>(calculator.getIterations());
    table.setModel(new AbstractTableModel() {
      private static final long serialVersionUID = -126345026888159672L;
      public int getRowCount() { return iterations.size(); }
      public int getColumnCount() { return elemetnsArray.size(); }
      public String getColumnName(final int column) { return elemetnsArray.get(column).getLabel(); }
      @Override
      public Class<?> getColumnClass(int columnIndex) { return String.class; }
      public Object getValueAt(final int row, final int column) {
        return iterations.get(row).get(elemetnsArray.get(column)).getPrintable();
      }
    });
    JScrollPane scroll = new JScrollPane();
    scroll.getViewport().add(table);
    getContentPane().add(scroll);
  }
  
}
