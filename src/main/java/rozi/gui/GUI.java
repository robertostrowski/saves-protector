package rozi.gui;

import javax.swing.*;

public class GUI extends JFrame {

    private JRadioButton blowfishRadioButton;
    private JRadioButton twofishRadioButton;
    private JButton okButton;
    private JTextField keyField;
    public JPanel rootPanel;
    private JRadioButton encryptButton;
    private JRadioButton decryptButton;
    private JTextArea outputTextArea;
    private JTextArea inputTextArea;

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

        ButtonGroup algorithmButtonGroup = new ButtonGroup();
        algorithmButtonGroup.add(blowfishRadioButton);
        algorithmButtonGroup.add(twofishRadioButton);

        ButtonGroup encryptMethodButtonGroup = new ButtonGroup();
        encryptMethodButtonGroup.add(encryptButton);
        encryptMethodButtonGroup.add(decryptButton);

        okButton.addActionListener(e -> actionHandler.okClicked(
                new GuiState(selectedAlgorithm(), selectedOperation(), getKey(), getInput())));
    }

    private String getInput(){
        return inputTextArea.getText();
    }

    private String getKey(){
        return keyField.getText();
    }

    private GuiState.Operation selectedOperation() {
        if (encryptButton.isSelected()) {
            return GuiState.Operation.ENCRYPTION;
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

    public void setOutput(final String output) {
        outputTextArea.setText(output);
    }
}
