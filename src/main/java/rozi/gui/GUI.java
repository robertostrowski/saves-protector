package rozi.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GUI extends JFrame {

    private JRadioButton blowfishRadioButton;
    private JRadioButton twofishRadioButton;
    private JButton okButton;
    private JTextField inputFileField;
    private JTextField keyFileField;
    private JButton inputFileButton;
    private JButton keyFileButton;
    public JPanel rootPanel;
    private JRadioButton encryptButton;
    private JRadioButton decryptButton;
    private JTextPane outputTextPane;

    private GuiActionHandler actionHandler;

    public GUI(GuiActionHandler actionHandler) {
        super("Saves protector");
        this.actionHandler = actionHandler;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        setLocation(150, 150);
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        inputFileButton.addActionListener(filePickerActionListener(inputFileField));
        keyFileButton.addActionListener(filePickerActionListener(keyFileField));

        ButtonGroup algorithmButtonGroup = new ButtonGroup();
        algorithmButtonGroup.add(blowfishRadioButton);
        algorithmButtonGroup.add(twofishRadioButton);

        ButtonGroup encryptMethodButtonGroup = new ButtonGroup();
        encryptMethodButtonGroup.add(encryptButton);
        encryptMethodButtonGroup.add(decryptButton);

        okButton.addActionListener(e -> actionHandler.okClicked(
                new GuiState(selectedAlgorithm(), selectedOperation(), selectedInputFile(), selectedKeyFile())));
    }

    private File selectedInputFile() {
        return Paths.get(inputFileField.getText()).toFile();
    }

    private File selectedKeyFile() {
        return Paths.get(keyFileField.getText()).toFile();
    }

    private GuiState.Operation selectedOperation() {
        if (encryptButton.isSelected()) {
            return GuiState.Operation.ENCRUPTION;
        }
        else {
            return GuiState.Operation.DECRYPTION;
        }
    }

    private GuiState.EnryptionAlgorithm selectedAlgorithm() {
        if (blowfishRadioButton.isSelected()) {
            return GuiState.EnryptionAlgorithm.BLOWFISH;
        }
        else {
            return GuiState.EnryptionAlgorithm.TWOFISH;
        }
    }

    private ActionListener filePickerActionListener(JTextComponent component) {

        return e -> {
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Save files", "yaml", "xml", "json");
            fc.setFileFilter(filter);
            int returnVal = fc.showOpenDialog(GUI.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                component.setText(file.getAbsolutePath());
            }
        };
    }

    public void setResultPreview(String output) {
        outputTextPane.setText(output);
    }
}
