package rozi;

import rozi.encryptor.BlowfishSaveEnryptor;
import rozi.encryptor.SaveEnryptor;
import rozi.encryptor.twofish.TwofishSaveEnryptor;
import rozi.gui.GUI;
import rozi.gui.GuiActionHandler;
import rozi.gui.GuiState;

public class SavesProtector implements GuiActionHandler {

    private final GUI gui;

    public SavesProtector() {
        gui = new GUI(this);
    }

    @Override
    public void okClicked(final GuiState guiState) {
        final SaveEnryptor saveEnryptor;
        switch (guiState.getSelectedAlgorithm()) {
            default:
            case TWOFISH:
                saveEnryptor = new TwofishSaveEnryptor();
                break;
            case BLOWFISH:
                saveEnryptor = new BlowfishSaveEnryptor();
                break;
        }
        final String output;
        switch (guiState.getSelectedOperation()) {
            default:
            case DECRYPTION:
                output = saveEnryptor.decrypt(guiState.getInputFile());

                break;
            case ENCRUPTION:
                output = saveEnryptor.encrypt(guiState.getInputFile());
                break;
        }
        gui.setResultPreview(output);
    }
}
