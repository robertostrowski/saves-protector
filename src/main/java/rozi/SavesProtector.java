package rozi;

import java.util.function.BiFunction;

import rozi.encryptor.BlowfishSaveEncryptor;
import rozi.encryptor.SaveEncryptor;
import rozi.encryptor.twofish.TwofishSaveEncryptor;
import rozi.gui.GUI;
import rozi.gui.GuiActionHandler;
import rozi.gui.GuiState;

import static rozi.gui.GuiState.EnryptionAlgorithm.BLOWFISH;
import static rozi.gui.GuiState.Operation.ENCRYPTION;

public class SavesProtector implements GuiActionHandler {

    private final GUI gui;

    public SavesProtector() {
        gui = new GUI(this);
    }

    @Override
    public void okClicked(final GuiState guiState) {
        GuiState.Operation operation = guiState.getSelectedOperation();
        GuiState.EnryptionAlgorithm algorithm = guiState.getSelectedAlgorithm();
        String input = guiState.getInput();
        String key = guiState.getKey();

        if (input == null || input.isEmpty() || key == null || key.isEmpty()){
            gui.setOutput("System: Klucz i wiadomość muszą być niepuste");
            return;
        }

        final SaveEncryptor encryptor = algorithm == BLOWFISH ? new BlowfishSaveEncryptor() : new TwofishSaveEncryptor();
        BiFunction<String, String, String> encryptionOperation =
                operation == ENCRYPTION ? encryptor::encrypt : encryptor::decrypt;
        try {
            String output = encryptionOperation.apply(input, key);
            gui.setOutput(output);
        } catch (Exception e){
            gui.setOutput("System: Wystąpił błąd :(");
            e.printStackTrace();
        }
    }
}
