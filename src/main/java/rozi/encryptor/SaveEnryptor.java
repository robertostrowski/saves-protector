package rozi.encryptor;

import java.io.BufferedReader;
import java.io.File;

public interface SaveEnryptor {

    String encrypt(File input);

    String decrypt(File input);
}
