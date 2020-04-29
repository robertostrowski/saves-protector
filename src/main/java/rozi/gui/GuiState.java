package rozi.gui;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class GuiState {

    EnryptionAlgorithm selectedAlgorithm;
    Operation selectedOperation;
    String key;
    String input;

    public enum EnryptionAlgorithm {
        BLOWFISH, TWOFISH
    }

    public enum Operation {
        ENCRYPTION, DECRYPTION
    }
}
