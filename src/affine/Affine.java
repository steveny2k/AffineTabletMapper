package affine;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class Affine {

  private static class Geometry {

    double fullWidth, fullHeight;
    double desiredWidth, desiredHeight;
    double offsetX, offsetY;

    public Geometry(double fullWidth, double fullHeight,
        double desiredWidth, double desiredHeight,
        double offsetX, double offsetY) {
      this.fullWidth = fullWidth;
      this.fullHeight = fullHeight;
      this.desiredWidth = desiredWidth;
      this.desiredHeight = desiredHeight;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
    }
  }

  public static double[] calculateAffine(Geometry geom) {
    AffineTransform at = AffineTransform.getScaleInstance(
        geom.desiredWidth / geom.fullWidth, geom.desiredHeight / geom.fullHeight);
    System.err.println("Scale affine is " + at);
    at.translate(geom.offsetX / geom.desiredWidth, geom.offsetY / geom.desiredHeight);
    System.err.println("With translation affine is " + at);
    double[] outputMatrix = new double[6];
    at.getMatrix(outputMatrix);
    return outputMatrix;
  }

  public static String asText(double[] data) {
    StringBuilder output = new StringBuilder();
    int off = 0;
    int limit = data.length * 2;
    for (int i = 0; i < limit; i += 2) {
      if (i == 6) {
        off = 1;
      }
      int idx = (i % data.length) + off;
      output.append(String.format("%f ", data[idx]));
    }
    output.append("0 0 1");
    return output.toString().trim();
  }

  public static void main(String[] args) {
    final Geometry geom = new Geometry(
        1920.0 + 1360.0, 1080.0,
        1280.0, 720.0,
        100.0, 145.0);
    if (args.length == 1 && "w".equalsIgnoreCase(args[0])) {
      System.err.println("GUI mode!");

      JFrame frame = new JFrame("Affine Transform");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 30);

      GridBagConstraints cons = new GridBagConstraints();
      JPanel lowPanel = new JPanel();
      lowPanel.setLayout(new GridBagLayout());
      JTextField output = new JTextField();
      output.setFont(font);
      output.setEditable(false);
      cons.weightx = 1;
      cons.fill = GridBagConstraints.HORIZONTAL;
      cons.gridx = 0;
      cons.gridy = 0;
      lowPanel.add(output, cons);
      final ActionListener execute = e -> {
        // return the calculated string, allow OS / shell script to use the data.
        System.out.println(asText(calculateAffine(geom)));
        System.exit(0);
      };
      JButton trigger = new JButton("Done");
      lowPanel.add(trigger);
      cons.weightx = 0;
      cons.gridx++;
      frame.add(lowPanel, BorderLayout.SOUTH);

      JPanel panel = new JPanel();
      frame.add(panel, BorderLayout.CENTER);
      panel.setLayout(new GridBagLayout());
      cons.insets = new Insets(5, 5, 5, 5);

      JLabel fullLabel = new JLabel("full:");
      fullLabel.setFont(font);
      cons.gridy = 0;
      cons.gridx = 0;
      panel.add(fullLabel, cons);

      final JTextField fullWidthText = new JTextField("" + geom.fullWidth);
      fullWidthText.setFont(font);
      cons.gridx++;
      panel.add(fullWidthText, cons);

      final JTextField fullHeightText = new JTextField("" + geom.fullHeight);
      fullHeightText.setFont(font);
      cons.gridx++;
      panel.add(fullHeightText, cons);

      cons.gridy++;
      cons.gridx = 0;

      JLabel desiredLabel = new JLabel("desired:");
      desiredLabel.setFont(font);
      panel.add(desiredLabel, cons);

      final JTextField desiredWidthText = new JTextField("" + geom.desiredWidth);
      desiredWidthText.setFont(font);
      cons.gridx++;
      panel.add(desiredWidthText, cons);

      final JTextField desiredHeightText = new JTextField("" + geom.desiredHeight);
      desiredHeightText.setFont(font);
      cons.gridx++;
      panel.add(desiredHeightText, cons);

      cons.gridy++;
      cons.gridx = 0;

      JLabel offsetLabel = new JLabel("offset:");
      offsetLabel.setFont(font);
      panel.add(offsetLabel, cons);

      final JTextField offsetWidthText = new JTextField("" + geom.offsetX);
      offsetWidthText.setFont(font);
      cons.gridx++;
      panel.add(offsetWidthText, cons);

      final JTextField offsetHeightText = new JTextField("" + geom.offsetY);
      offsetHeightText.setFont(font);
      cons.gridx++;
      panel.add(offsetHeightText, cons);

      // calculator
      KeyListener handler = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
          try {
            JTextField source = (JTextField) (e.getSource());
            double value = Double.parseDouble(
                source.getText());
            if (source == fullWidthText) {
              geom.fullWidth = value;
            } else if (source == fullHeightText) {
              geom.fullHeight = value;
            } else if (source == desiredWidthText) {
              geom.desiredWidth = value;
            } else if (source == desiredHeightText) {
              geom.desiredHeight = value;
            } else if (source == offsetWidthText) {
              geom.offsetX = value;
            } else if (source == offsetHeightText) {
              geom.offsetY = value;
            }
            output.setText(asText(calculateAffine(geom)));
            trigger.addActionListener(execute);
          } catch (NumberFormatException nfe) {
            // ignore this event, numbers are trash right now
//                        System.err.println("NFE");
            trigger.removeActionListener(execute);
          }
        }
      };

      fullHeightText.addKeyListener(handler);
      fullWidthText.addKeyListener(handler);
      desiredHeightText.addKeyListener(handler);
      desiredWidthText.addKeyListener(handler);
      offsetHeightText.addKeyListener(handler);
      offsetWidthText.addKeyListener(handler);

      frame.pack();
      Rectangle bounds = frame.getBounds();
      bounds.width = 1_000;
      frame.setBounds(bounds);
      frame.setVisible(true);
    } else if (args.length == 6) {
      try {
        geom.fullWidth = Double.parseDouble(args[0]);
        geom.fullHeight = Double.parseDouble(args[1]);
        geom.desiredWidth = Double.parseDouble(args[2]);
        geom.desiredHeight = Double.parseDouble(args[3]);
        geom.offsetX = Double.parseDouble(args[4]);
        geom.offsetY = Double.parseDouble(args[5]);
        System.err.println("Transform: " + asText(calculateAffine(geom)));
        return; // successful completion
      } catch (NumberFormatException nfe) {
      }
    } else {
      System.out.println("Usages: java affine.Affine w | <geometry>");
      System.out.println("  w - prompt for geometry with GUI");
      System.out.println("  <geometry> is fullwidth fullheight desiredwidth desiredheight offsetx offsety");
    }
  }
//    [0.39, 0.00, 0.03] 
//    [0.00, 0.66, 0.13]
}
