package rozi.gui;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.File;

@Value
@AllArgsConstructor
public class GuiState {

    private EnryptionAlgorithm selectedAlgorithm;
    private Operation selectedOperation;
    private File inputFile;
    private File keyFile;

    public enum EnryptionAlgorithm {
        BLOWFISH, TWOFISH
    }

    public enum Operation {
        ENCRUPTION, DECRYPTION
    }
}
