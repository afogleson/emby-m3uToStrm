/*
 * Created by JFormDesigner on Tue Jan 05 12:07:43 EST 2021
 */

package com.afogleson.ui;

import com.afogleson.m3u.parser.M3UFileParser;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Allen Fogleson
 */
public class HandlerPanel extends JPanel {

    private JFrame jFrame = null;

    public HandlerPanel(JFrame frame) {
        jFrame = frame;
        initComponents();
        String startingPath = System.getProperty("user.home");
        outputPathText.setText(startingPath);
    }

    public HandlerPanel() {
        initComponents();
        String startingPath = System.getProperty("user.home");
        outputPathText.setText(startingPath);
    }

    private void FileRadioActionPerformed(ActionEvent e) {
        if(fileRadio.isSelected()) {
            urlNameLabel.setVisible(false);
            urlTextField.setVisible(false);
            fileSelectionLabel.setVisible(true);
            fileNameText.setVisible(true);
            browseButton.setVisible(true);
        }
    }

    private void urlRadioActionPerformed(ActionEvent e) {
        if(urlRadio.isSelected()) {
            urlNameLabel.setVisible(true);
            urlTextField.setVisible(true);
            fileSelectionLabel.setVisible(false);
            fileNameText.setVisible(false);
            browseButton.setVisible(false);
        }
    }

    private void browseButtonActionPerformed(ActionEvent e) {
        String startingPath = System.getProperty("user.home");
        JFileChooser chooser = createFileChoser("Output Directory",
                JFileChooser.FILES_ONLY,
                startingPath,
                "m3u");
        int returnVal = chooser.showOpenDialog(jFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File chosenFile = chooser.getSelectedFile();
            fileNameText.setText(chosenFile.getAbsolutePath());
        }
    }

    private void pathBrowseActionPerformed(ActionEvent e) {
        String startingPath = System.getProperty("user.home");
        JFileChooser chooser = createFileChoser("Output Directory",
                JFileChooser.DIRECTORIES_ONLY,
                startingPath,
                null);
        int returnVal = chooser.showOpenDialog(jFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File chosenFile = chooser.getSelectedFile();
            outputPathText.setText(chosenFile.getAbsolutePath());
        }
    }

    /**
     *
     * A jfilechooser to select a file
     *
     * @param title the title of the file chooser
     * @param selectionMode the selection mode for the file chooser
     * @param startingPath where to start the chooser
     * @param fileExtension the file extension to use
     * @return a JFileChooser
     */
    public JFileChooser createFileChoser(String title, Integer selectionMode, String startingPath, String fileExtension) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        if(fileExtension != null && !fileExtension.isEmpty()) {
            if(fileExtension.startsWith(".")) {
                if(fileExtension.length() >= 2) {
                    fileExtension = fileExtension.substring(1);
                }
                else {
                    fileExtension = "";
                }
            }
            //we have to check again since we may have made the string empty
            if(fileExtension.length() >= 1) {

                FileFilter filter = new FileNameExtensionFilter(fileExtension, new String[] {fileExtension});
                chooser.setFileFilter(filter);
            }
        }
        if(selectionMode == null) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        else {
            chooser.setFileSelectionMode(selectionMode.intValue());
        }
        if(startingPath != null) {
            chooser.setCurrentDirectory(new File(startingPath));
        }
        return chooser;
    }

    private void createButtonActionPerformed(ActionEvent e) {
        boolean isFile = true;
        String path = fileNameText.getText();
        if (urlRadio.isSelected()) {
            isFile = false;
            path = urlTextField.getText();
        }
        try {
            int processedFiles = M3UFileParser.getInstance().parse(isFile, path, outputPathText.getText(), startYearText.getText(), endYearText.getText());
            //show a dialog box to let the user know we finished
            JOptionPane.showMessageDialog(jFrame,
                    "Processed " + processedFiles + " Entries in M3U");
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(jFrame, "Exception processing file");
        }
    }

    public void setYearValues(int startYear) {
        startYearText.setText("" + startYear);
        endYearText.setText("" + (startYear+1));
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("M3uHandler");
        hSpacer1 = new JPanel(null);
        titleLabel = new JLabel();
        hSpacer2 = new JPanel(null);
        selectionLabel = new JLabel();
        fileRadio = new JRadioButton();
        urlRadio = new JRadioButton();
        label3 = new JLabel();
        fileSelectionLabel = new JLabel();
        fileNameText = new JTextField();
        browseButton = new JButton();
        urlNameLabel = new JLabel();
        urlTextField = new JTextField();
        startYearLabel = new JLabel();
        startYearText = new JTextField();
        endYearLabel = new JLabel();
        endYearText = new JTextField();
        outputLabel = new JLabel();
        outputPathText = new JTextField();
        pathBrowse = new JButton();
        createButton = new JButton();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0, 0, 0, 55, 0, 0, 0, 0};
        ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
        ((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
        add(hSpacer1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- titleLabel ----
        titleLabel.setText(bundle.getString("gui.window.title"));
        titleLabel.setFont(new Font("Noto Sans", Font.PLAIN, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, new GridBagConstraints(1, 0, 6, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));
        add(hSpacer2, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //---- selectionLabel ----
        selectionLabel.setText(bundle.getString("url.or.file"));
        add(selectionLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- fileRadio ----
        fileRadio.setText(bundle.getString("file.radio.title"));
        fileRadio.setSelected(true);
        fileRadio.addActionListener(e -> FileRadioActionPerformed(e));
        add(fileRadio, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- urlRadio ----
        urlRadio.setText(bundle.getString("url.radio.title"));
        urlRadio.addActionListener(e -> urlRadioActionPerformed(e));
        add(urlRadio, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));
        add(label3, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- fileSelectionLabel ----
        fileSelectionLabel.setText(bundle.getString("file.selector.name"));
        add(fileSelectionLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));
        add(fileNameText, new GridBagConstraints(2, 2, 4, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- browseButton ----
        browseButton.setText(bundle.getString("browse.button.name"));
        browseButton.addActionListener(e -> browseButtonActionPerformed(e));
        add(browseButton, new GridBagConstraints(6, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- urlNameLabel ----
        urlNameLabel.setText(bundle.getString("url.name.label"));
        urlNameLabel.setVisible(false);
        add(urlNameLabel, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- urlTextField ----
        urlTextField.setVisible(false);
        add(urlTextField, new GridBagConstraints(2, 3, 5, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- startYearLabel ----
        startYearLabel.setText(bundle.getString("start.year.text"));
        add(startYearLabel, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));
        add(startYearText, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- endYearLabel ----
        endYearLabel.setText(bundle.getString("end.year.text"));
        add(endYearLabel, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));
        add(endYearText, new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- outputLabel ----
        outputLabel.setText(bundle.getString("output.path"));
        add(outputLabel, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));
        add(outputPathText, new GridBagConstraints(2, 6, 4, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- pathBrowse ----
        pathBrowse.setText(bundle.getString("browse.button.name"));
        pathBrowse.addActionListener(e -> pathBrowseActionPerformed(e));
        add(pathBrowse, new GridBagConstraints(6, 6, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- createButton ----
        createButton.setText(bundle.getString("create.button.text"));
        createButton.addActionListener(e -> createButtonActionPerformed(e));
        add(createButton, new GridBagConstraints(6, 7, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(fileRadio);
        buttonGroup1.add(urlRadio);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel hSpacer1;
    private JLabel titleLabel;
    private JPanel hSpacer2;
    private JLabel selectionLabel;
    private JRadioButton fileRadio;
    private JRadioButton urlRadio;
    private JLabel label3;
    private JLabel fileSelectionLabel;
    private JTextField fileNameText;
    private JButton browseButton;
    private JLabel urlNameLabel;
    private JTextField urlTextField;
    private JLabel startYearLabel;
    private JTextField startYearText;
    private JLabel endYearLabel;
    private JTextField endYearText;
    private JLabel outputLabel;
    private JTextField outputPathText;
    private JButton pathBrowse;
    private JButton createButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
