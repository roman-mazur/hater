/**
 * 
 */
package org.mazur.hater.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.mazur.hater.Calculator;
import org.mazur.hater.EventsCalculator;
import org.mazur.hater.RangeCalculator;
import org.mazur.hater.gui.SelectElemetnsFrame.SelectListener;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.model.AndElement;
import org.mazur.hater.model.EditModelException;
import org.mazur.hater.model.ElementType;
import org.mazur.hater.model.InElement;
import org.mazur.hater.model.ModelContainer;
import org.mazur.hater.model.NandElement;
import org.mazur.hater.model.NorElement;
import org.mazur.hater.model.NotElement;
import org.mazur.hater.model.OrElement;
import org.mazur.hater.model.OutElement;
import org.mazur.hater.model.AbstractElement.DefaultElementView;
import org.mazur.hater.signals.SignalValue;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class EditorPanel extends JPanel {

  /** serialVersionUID. */
  private static final long serialVersionUID = -8893477435722606547L;

  /** Logger. */
  private static final Logger LOG = Logger.getLogger(EditorPanel.class);
  
  /** JGraph panel. */
  private JGraph jgraph = new JGraph();
  
  private ModelContainer modelContainer;
  
  private JToolBar toolsBar = new JToolBar();

  private SelectElemetnsFrame selectElemetnsFrame;
  
  private Calculator calculator;

  private static class GraphContainer implements Serializable {
    private static final long serialVersionUID = 1989245354682309485L;
    private HashSet<Object> objects = new HashSet<Object>();
  }

  private GraphContainer graphContainer = new GraphContainer();
  
  private enum ElementsTypesEnum {
    AND2(ElementType.AND, 2),
    NAND2(ElementType.NAND, 2),
    NOR2(ElementType.NOR, 2),
    OR2(ElementType.OR, 2),
    OR4(ElementType.OR, 4),
    NOT(ElementType.NOT) {
      @Override
      public String getLabel() { return ElementType.NOT.getLabel(); }
    },
    OUT(ElementType.OUT) {
      @Override
      public String getLabel() { return ElementType.OUT.getLabel(); }
    },
    IN(ElementType.IN) {
      @Override
      public String getLabel() { return ElementType.IN.getLabel(); }
    };
    
    private ElementType type;
    private int inCount;
    
    private ElementsTypesEnum(final ElementType t) {
      type = t;
    }
    private ElementsTypesEnum(final ElementType t, final int cnt) {
      type = t; inCount = cnt;
    }
    
    public String getLabel() { return type.getLabel() + "(" + inCount + ")"; }
    
    public AbstractElement createElement(final ModelContainer modelContainer) {
      switch (type) {
      case AND: return new AndElement(modelContainer, inCount);
      case OR: return new OrElement(modelContainer, inCount);
      case NAND: return new NandElement(modelContainer, inCount);
      case NOR: return new NorElement(modelContainer, inCount);
      case NOT: return new NotElement(modelContainer);
      case IN: return new InElement(modelContainer);
      case OUT: return new OutElement(modelContainer);
      default:
        return null;
      }
    }
  }
  
  private AbstractElement lastCreatedElement, lastSelectedElement;
  
  private MouseAdapter jgraphMouseListener = new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
      try {
        LOG.debug("Mouse click on jgraph lastElement=" + lastCreatedElement);
        if (lastCreatedElement != null) {
          modelContainer.add(lastCreatedElement);
          lastCreatedElement.prepareView(e.getPoint());
          jgraph.getGraphLayoutCache().insert(lastCreatedElement.getView());
          graphContainer.objects.add(lastCreatedElement.getView());
          if (lastCreatedElement.getElementType() == ElementType.IN) {
            valuesPanel.model.fireTableStructureChanged();
          }
          lastCreatedElement = null;
          return;
        }
        
        if (e.getButton() == MouseEvent.BUTTON3) {
          LOG.debug("Right button -> draw connection");
          if (lastSelectedElement == null) {
            DefaultElementView v = (DefaultElementView)jgraph.getFirstCellForLocation(e.getX(), e.getY());
            lastSelectedElement = v.getElement();
            LOG.debug("Last selected: " + lastSelectedElement);
          } else {
            DefaultElementView v = (DefaultElementView)jgraph.getFirstCellForLocation(e.getX(), e.getY());
            boolean confirmed = true;
            if (v.getElement() == lastSelectedElement) {
              confirmed = 
                (JOptionPane.showConfirmDialog(EditorPanel.this, 
                    "Are you sure you want to make the feedback with only one element?") 
                 == JOptionPane.YES_OPTION);
            }
            if (confirmed) {
              Object o = v.getElement().connect(lastSelectedElement);
              jgraph.getGraphLayoutCache().insert(o);
              graphContainer.objects.add(o);
            }
            lastSelectedElement = null;
          }
        }
      } catch (EditModelException ex) {
        LOG.error("User made an error.", ex);
        JOptionPane.showMessageDialog(EditorPanel.this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        lastSelectedElement = null;
      }
    }
  };
  
  private class ValuesTablePanel extends JPanel {
    private static final long serialVersionUID = 7251645661809004254L;

    private SelectListener addListener = new SelectListener() {
      public void process(List<AbstractElement> elements) {
        LOG.debug("Add " + elements + " to the values table");
        ValuesTablePanel.this.elements.addAll(elements);
        model.fireTableStructureChanged();
      }
    };
    
    private List<AbstractElement> elements = new LinkedList<AbstractElement>();
    
    private LinkedList<List<SignalValue>> values = new LinkedList<List<SignalValue>>(),
                                          results = new LinkedList<List<SignalValue>>();
    
    private JTable table;
    private DefaultTableModel model = new DefaultTableModel() {
      private static final long serialVersionUID = 8415405130230815321L;
      @Override
      public int getColumnCount() { 
        return elements.size() + modelContainer.getInputs().size(); 
      }
      @Override
      public String getColumnName(final int column) {
        if (column < modelContainer.getInputs().size()) {
          return modelContainer.getInputs().get(column).getLabel();
        }
        return elements.get(column - modelContainer.getInputs().size()).getLabel(); 
      }
      @Override
      public int getRowCount() {
        return values.size();
      }
      @Override
      public Object getValueAt(final int row, final int column) {
        List<SignalValue> vv;
        if (column < modelContainer.getInputs().size()) {
          vv = values.get(row); 
          return vv.size() <= column ? "" : vv.get(column) == null ? "" : vv.get(column).getPrintable();
        }
        int i = column - modelContainer.getInputs().size();
        vv = results.get(row);
        return vv.size() <= i ? "" : vv.get(i) == null ? "" : vv.get(i).getPrintable();
      }
      @Override
      public void setValueAt(final Object value, final int row, final int column) {
        if (column < modelContainer.getInputs().size()) {
          List<SignalValue> vv = values.get(row); 
          if (vv.size() > column) {
            vv.set(column, modelContainer.getOperations().parseValue((String)value));
          }
        }
      }
    };
    
    private int lastSelectedIndex = -1;
    
    private List<SignalValue> inc(final List<SignalValue> prev) {
      int l = modelContainer.getInputs().size();
      return modelContainer.getOperations().nextTerm(prev, l);
    }
    
    private void addAutoValue() {
      values.add(inc(values.isEmpty() ? null : values.getLast()));
      results.add(new LinkedList<SignalValue>());
      model.fireTableStructureChanged();
    }
    
    private void addValue() {
      LinkedList<SignalValue> vv = new LinkedList<SignalValue>();
      for (int i = 0; i < modelContainer.getInputs().size(); i++) {
        vv.add(null);
      }
      values.add(vv);
      results.add(new LinkedList<SignalValue>());
      model.fireTableStructureChanged();
    }

    private void clear() {
      values.clear();
      results.clear();
      model.fireTableStructureChanged();
    }
    
    public ValuesTablePanel() {
      super(true);
      setLayout(new BorderLayout());
      JPanel topPanel = new JPanel();
      topPanel.setLayout(new GridLayout(2, 1));
      final JCheckBox checker = new JCheckBox("Reset initial values");
      checker.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          calculator.setResetInitValues(checker.isSelected());
        }
      });
      topPanel.add(checker);
      topPanel.add(new JLabel("Values table"));
      add(BorderLayout.NORTH, topPanel);
      
      JPanel bottomBtns = new JPanel();
      bottomBtns.setLayout(new GridLayout(5, 1));
      JButton btn = new JButton("Add auto value");
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) { addAutoValue(); }
      });
      bottomBtns.add(btn);
      btn = new JButton("Add");
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) { addValue(); }
        
      });
      bottomBtns.add(btn);
      btn = new JButton("Add column...");
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          LinkedList<AbstractElement> temp = new LinkedList<AbstractElement>(elements);
          temp.addAll(modelContainer.getInputs());
          selectElemetnsFrame.display(temp, addListener);
        }
      });
      bottomBtns.add(btn);
      btn = new JButton("Clear");
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) { clear(); }
      });
      bottomBtns.add(btn);
      btn = new JButton("Iterations");
      btn.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          int index = table.getSelectedRow();
          if (index < 0) { return; }
          if (values.isEmpty()) { return; }
          //calculator.process(values.get(index));
          JFrame frame = calculator.getIterationsFrame();
          frame.pack();
          frame.setVisible(true);
        }
      });
      bottomBtns.add(btn);
      add(BorderLayout.SOUTH, bottomBtns);
      
      table = new JTable();
      table.setModel(model);
      JScrollPane pane = new JScrollPane();
      pane.setPreferredSize(new Dimension(200, 100));
      pane.getViewport().add(table);
      add(BorderLayout.CENTER, pane);
      
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(final ListSelectionEvent e) {
          if (values.isEmpty()) { return; }
          int index = table.getSelectedRow();
          if (index < 0) { return; }
          LOG.debug("!!!!! " + lastSelectedIndex + " vs " + index);
          if (lastSelectedIndex == index) { return; }
          lastSelectedIndex = index;
          List<SignalValue> vv = values.get(index);
          LOG.debug("------ Values: " + vv + ", " + index);
          calculator.process(vv);
          infoLabel.setText("Length of the crirical path: " + calculator.getCriticalPath());
          int i = 0;
          Map<AbstractElement, SignalValue> cResults = calculator.getLastValues();
          List<SignalValue> prevValues = results.get(index);
          for (AbstractElement el : elements) {
            LOG.debug("Copy value for " + el + ": " + cResults.get(el));
            if (i < prevValues.size()) {
              prevValues.set(i, cResults.get(el));
            } else {
              prevValues.add(cResults.get(el));
            }
            i++;
          }
          LOG.debug("Values size: " + prevValues);
          if (calculator.getMessage() != null) {
            //JOptionPane.showMessageDialog(EditorPanel.this, calculator.getMessage());
            generatorInfoLabel.setText(calculator.getMessage());
          } else {
            generatorInfoLabel.setText("");
          }
        }
      });
      table.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(final MouseEvent e) {
          if (e.getButton() == MouseEvent.BUTTON3) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                int index = table.getSelectedRow();
                if (index < 0) { return; }
                List<SignalValue> vv = values.get(index);
                LOG.debug("------ Values: " + vv + ", " + index);
                calculator.process(vv);
                new TimeGraphsFrame(calculator, elements).setVisible(true);
              }
            });
          }
        }
      });
    }
    
  }
  
  private ValuesTablePanel valuesPanel;
  
  private JLabel infoLabel = new JLabel("[info]"), generatorInfoLabel = new JLabel();
  
  /**
   * Constructor.
   */
  public EditorPanel(final ModelContainer modelContainer) {
    super(true);
    this.modelContainer = modelContainer;
    setLayout(new BorderLayout());
    GraphModel model = new DefaultGraphModel();
    GraphLayoutCache view = new GraphLayoutCache(model,
        new DefaultCellViewFactory());
    jgraph = new JGraph(model, view);
    jgraph.setGridEnabled(true);
    jgraph.setGridVisible(true);
    jgraph.setPortsVisible(true);
    jgraph.addMouseListener(jgraphMouseListener);
    JScrollPane scroll = new JScrollPane();
    scroll.getViewport().add(jgraph);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    JPanel infoPanel = new JPanel();
    Box b = Box.createVerticalBox();
    b.add(infoLabel); b.add(generatorInfoLabel);
    infoPanel.add(b);
    generatorInfoLabel.setForeground(Color.RED);
    mainPanel.add(BorderLayout.NORTH, infoPanel);
    mainPanel.add(BorderLayout.CENTER, scroll);
    
    JSplitPane split = new JSplitPane();
    split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    split.setRightComponent(mainPanel);
    
    buildElementsBar();
    add(BorderLayout.EAST, toolsBar);
    
    valuesPanel = new ValuesTablePanel();
    valuesPanel.setVisible(false);
    split.setLeftComponent(valuesPanel);
    
    add(BorderLayout.CENTER, split);
    
    selectElemetnsFrame = new SelectElemetnsFrame(modelContainer);
    calculator = new EventsCalculator(modelContainer); //new RangeCalculator(modelContainer);//new Calculator(modelContainer);

    for (AbstractElement element : this.modelContainer.getAllElemets()) {
      jgraph.getGraphLayoutCache().insert(element.getView());
    }
    for (AbstractElement element : this.modelContainer.getAllElemets()) {
      for (DefaultEdge edge : element.getView().getInputEdges()) {
        jgraph.getGraphLayoutCache().insert(edge);
      }
    }
  }
  
  private void buildElementsBar() {
    toolsBar.setOrientation(JToolBar.VERTICAL);
    
    for (final ElementsTypesEnum type : ElementsTypesEnum.values()) {
      JButton btn = new JButton();
      btn.setAction(new AbstractAction(){
        private static final long serialVersionUID = 2662813835155743722L;
        public void actionPerformed(final ActionEvent e) {
          lastCreatedElement = type.createElement(modelContainer);
        }
      });
      btn.setText(type.getLabel());
      btn.setToolTipText(type.getLabel());
      toolsBar.add(btn);
    }
    
    JButton btn = new JButton();
    btn.setAction(new AbstractAction() {
      private static final long serialVersionUID = 8562245773610009788L;
      public void actionPerformed(final ActionEvent e) {
        valuesPanel.setVisible(!valuesPanel.isVisible());
      }
    });
    btn.setText("tbl");
    toolsBar.add(btn);
  }
  
  public ModelContainer getModel() {
    return modelContainer;
  }

  public void save(final OutputStream out) throws IOException {
    ObjectOutputStream output = new ObjectOutputStream(out);
    output.writeObject(graphContainer);
    output.flush();
    output.writeObject(modelContainer);
    output.close();
  }
  
}
