package rozi.encryptor.twofish;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import rozi.encryptor.SaveEnryptor;

import static rozi.encryptor.twofish.Constants.*;
import static rozi.encryptor.twofish.Utils.*;

public class TwofishSaveEnryptor implements SaveEnryptor {

    public static void main(String[] args) {
        TwofishSaveEnryptor twofishSaveEnryptor = new TwofishSaveEnryptor();
        twofishSaveEnryptor.testRun();
    }

    private void testRun() {
        String message = "test";
        String key = "kluczyk!";
        SessionKey sessionKey = KeyProducer.makeKey(key.getBytes());
        byte[] messageBytes = message.getBytes();
        byte[] bytesWithTrailingChars = fillWithTrailingZeros(messageBytes);
        byte[] encrypted = TwofishAlgorithm.blockEncrypt(bytesWithTrailingChars, 0, sessionKey);
        List<String> collect = Stream.of(encrypted).map(String::new).collect(Collectors.toList());
        System.out.println(collect);
        byte[] deencrypted = TwofishAlgorithm.blockDecrypt(encrypted, 0, sessionKey);
        String deencryptedMessage = new String(deencrypted);
        System.out.println(deencryptedMessage);;
    }

    private byte[] fillWithTrailingZeros(final byte[] messageBytes) {
        if (messageBytes.length % 64 == 0){
            return messageBytes;
        } else {
            int newLength = 64 * (messageBytes.length / 64 + 1);
            return Arrays.copyOf(messageBytes, newLength);
        }
    }

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
