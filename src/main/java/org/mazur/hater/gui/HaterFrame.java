/**
 * 
 */
package org.mazur.hater.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;
import org.mazur.hater.model.ModelContainer;

/**
 * Main frame.
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 */
public class HaterFrame extends JFrame {

  /** Logger. */
  private static final Logger LOG = Logger.getLogger(HaterFrame.class);
  
  private static final long serialVersionUID = -1889221954717041209L;

  private SetInitValuesFrame initValuesFrame;
  
  private JTabbedPane editorsPane = new JTabbedPane();
  
  private JFileChooser filesChooser = new JFileChooser();
  
  private Action[] commonActions = {
    new OpenAction(), new SaveAction()
  };
  
  public HaterFrame(final Dimension size) {
    super("Hater");
    filesChooser.setFileFilter(new FileNameExtensionFilter("Hater files", "ht"));
    Container pane = getContentPane();
    pane.setLayout(new BorderLayout());
    addEditor("noname", new ModelContainer());
    pane.add(BorderLayout.CENTER, editorsPane);
  
    initValuesFrame = new SetInitValuesFrame(this);
    
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

    JMenu commonMenu = new JMenu("Common");
    for (Action a : commonActions) {
      JMenuItem item = new JMenuItem();
      item.setAction(a);
      commonMenu.add(item);
    }
    
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(editMenu);
    menuBar.add(commonMenu);
    pane.add(BorderLayout.NORTH, menuBar);
    
    pack();
  }

  private void addEditor(final String name, final ModelContainer model) {
    EditorPanel editor = new EditorPanel(model);
    editorsPane.addTab(name, editor);
  }
  
  public ModelContainer getCurrentModel() {
    return ((EditorPanel)editorsPane.getSelectedComponent()).getModel();
  }
  
  private String getCaption() {
    return "";
  }
  
  private class OpenAction extends AbstractAction {
    private static final long serialVersionUID = 1283170242419242236L;
    public OpenAction() { super("Open"); }
    public void actionPerformed(final ActionEvent e) {
      int retValue = filesChooser.showOpenDialog(HaterFrame.this);
      if (retValue == JFileChooser.APPROVE_OPTION) {
        try {
          File f = filesChooser.getSelectedFile();
          FileInputStream in = new FileInputStream(f);
          ModelContainer model = ModelContainer.load(in);
          addEditor(f.getName(), model);
        } catch (IOException ex) {
          LOG.error("Error while reading the model.", ex);
        }
      }
    }
  }
  
  private class SaveAction extends AbstractAction {
    private static final long serialVersionUID = 2874676209422350329L;
    public SaveAction() { super("Save"); }
    public void actionPerformed(final ActionEvent e) {
      int res = filesChooser.showSaveDialog(HaterFrame.this);
      if (res == JFileChooser.APPROVE_OPTION) {
        try {
          FileOutputStream out = new FileOutputStream(filesChooser.getSelectedFile());
          getCurrentModel().save(out);
        } catch (IOException ex) {
          LOG.error("Error while saving the model.", ex);
        }
      }
    }
  }
  
}
