package rozi.encryptor;

import java.io.BufferedReader;
import java.io.File;

public interface SaveEncryptor {

    String encrypt(String text, String key);

    String decrypt(String text, String key);
}
