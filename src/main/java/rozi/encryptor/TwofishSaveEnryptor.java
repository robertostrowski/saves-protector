package rozi.encryptor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

public class TwofishSaveEnryptor implements SaveEnryptor {

    @Override
    public String encrypt(final File input) {
        try {
            return String.join("\n", Files.readAllLines(input.toPath()));
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decrypt(final File input) {
        try {
            return String.join("\n", Files.readAllLines(input.toPath()));
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
