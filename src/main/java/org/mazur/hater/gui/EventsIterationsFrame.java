package org.mazur.hater.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.mazur.hater.EventsCalculator;
import org.mazur.hater.EventsCalculator.IterationContainer;
import org.mazur.hater.model.AbstractElement;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class EventsIterationsFrame extends JFrame {
  private static final long serialVersionUID = -4236848806382858625L;

  public EventsIterationsFrame(final EventsCalculator calculator) {
    super("Iterations frame");
    JTable table = new JTable();
    final ArrayList<AbstractElement> elemetnsArray = 
      new ArrayList<AbstractElement>(calculator.getAllIterations().get(0).getValues().keySet());
    final List<IterationContainer> iterations = new ArrayList<IterationContainer>(calculator.getAllIterations());
    table.setModel(new AbstractTableModel() {
      private static final long serialVersionUID = -126345026888159672L;
      public int getRowCount() { return iterations.size(); }
      public int getColumnCount() { return elemetnsArray.size() + 1; }
      public String getColumnName(final int column) {
        if (column == 0) { return "Time"; }
        return elemetnsArray.get(column - 1).getLabel(); 
      }
      @Override
      public Class<?> getColumnClass(int columnIndex) { return String.class; }
      public Object getValueAt(final int row, final int column) {
        IterationContainer c = iterations.get(row);
        if (column == 0) { return String.valueOf(c.getTime()); }
        return c.getValues().get(elemetnsArray.get(column - 1)).getPrintable();
      }
    });
    JScrollPane scroll = new JScrollPane();
    scroll.getViewport().add(table);
    getContentPane().add(scroll);
  }
  
}
