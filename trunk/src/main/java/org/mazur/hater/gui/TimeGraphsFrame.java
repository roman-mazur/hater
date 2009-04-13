/**
 * 
 */
package org.mazur.hater.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mazur.hater.Calculator;
import org.mazur.hater.model.AbstractElement;
import org.mazur.hater.signals.SignalValue;

/**
 * @author Roman Mazur (Stanfy - http://www.stanfy.com)
 *
 */
public class TimeGraphsFrame extends JFrame {

  private static final Logger LOG = Logger.getLogger(TimeGraphsFrame.class);
  
  private static final long serialVersionUID = -8091758694981254124L;
  private Calculator calculator;
  
  public TimeGraphsFrame(final Calculator calculator, final List<AbstractElement> elements) {
    this.calculator = calculator;
    Container pane = getContentPane();
    pane.setLayout(new GridLayout(elements.size(), 1));
    for (AbstractElement el : elements) {
      pane.add(new DiagramPanel(el));
    }
    pack();
    setSize(new Dimension(600, 500));
  }
  
  private JFreeChart prepareJFreeChart(final AbstractElement el) {
    XYSeriesCollection xy = new XYSeriesCollection();
    XYSeries series = new XYSeries("");
    int counter = 0;
    for (Map<AbstractElement, SignalValue> values : calculator.getIterations()) {
      LOG.info(values);
      series.add(counter, values.get(el).getDouble());
      if (counter > 0) {
        series.add(counter - 1, values.get(el).getDouble());
      }
      counter++;
    }
    xy.addSeries(series);
    JFreeChart result = ChartFactory.createXYLineChart("", "", "", xy, PlotOrientation.VERTICAL, 
        false, false, false);
//    ((XYPlot)result.getPlot()).getRenderer().setSeriesStroke(0, new BasicStroke(1, BasicStroke.CAP_BUTT, 
//        BasicStroke.JOIN_MITER, 3, new float[] {3}, 0));
    return result;
  }
  
  private class DiagramPanel extends JPanel {
    private static final long serialVersionUID = -3115131475872266135L;

    public DiagramPanel(final AbstractElement element) {
      setLayout(new BorderLayout());
      JPanel temp = new JPanel();
      temp.add(new JLabel(element.getLabel()));
      temp.setSize(new Dimension(100, 100));
      add(BorderLayout.WEST, temp);
      JFreeChart chart = prepareJFreeChart(element);
      ChartPanel p = new ChartPanel(chart);
//      p.setSize(new Dimension(700, 100));
//      JScrollPane pane = new JScrollPane();
//      temp.add(p);
//      pane.getViewport().add(temp);
//      pane.setPreferredSize(new Dimension(600, 120));
      add(BorderLayout.CENTER, p);
      
    }
  }
  
}
