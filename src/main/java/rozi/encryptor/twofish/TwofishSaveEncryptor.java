package rozi.encryptor.twofish;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import rozi.encryptor.SaveEncryptor;

public class TwofishSaveEncryptor implements SaveEncryptor {

    @Override
    public String encrypt(final String text, final String key) {
        byte[] textBytes = prepareInput(text.getBytes());
        byte[] keyBytes = prepareKey(key.getBytes());
        SessionKey sessionKey = KeyProducer.makeKey(keyBytes);
        int blockCount = textBytes.length / 16;

        byte[] output = new byte[textBytes.length];
        for (int i = 0; i < blockCount; i++) {
            int offset = i * 16;
            byte[] encryptedBlock = TwofishAlgorithm.blockEncrypt(textBytes, offset, sessionKey);
            System.arraycopy(encryptedBlock, 0, output, offset, 16);
        }
        return Utils.bytesToHex(output);
    }

    @Override
    public String decrypt(final String text, final String key) {
        byte[] encodedBytes = Utils.hexStringToByteArray(text);
        byte[] keyBytes = prepareKey(key.getBytes());
        SessionKey sessionKey = KeyProducer.makeKey(keyBytes);
        int blockCount = encodedBytes.length / 16;

        byte[] output = new byte[encodedBytes.length];
        for (int i = 0; i < blockCount; i++) {
            int offset = i * 16;
            byte[] decryptedBlock = TwofishAlgorithm.blockDecrypt(encodedBytes, offset, sessionKey);

            System.arraycopy(decryptedBlock, 0, output, offset, 16);
        }

        return new String(output);
    }

    private byte[] prepareInput(final byte[] messageBytes) {
        if (messageBytes.length % 16 == 0) {
            return messageBytes;
        }
        else {
            int newLength = 16 * ((messageBytes.length / 16) + 1);
            return Arrays.copyOf(messageBytes, newLength);
        }
    }

    private byte[] prepareKey(final byte[] bytes) {
        int length = bytes.length;
        int full8ths = length / 8;

        final int newLength;

        switch (full8ths) {
            case 0:
            case 1:
            case 2:
            case 3:
                if (length % 8 == 0) {
                    return bytes;
                }
                else {
                    newLength = (full8ths + 1) * 8;
                    break;
                }
            case 4:
            default:
                newLength = 32;
                break;
        }
        return Arrays.copyOf(bytes, newLength);
    }
    public static void main(String[] args) {
        TwofishSaveEncryptor twofishSaveEncryptor = new TwofishSaveEncryptor();
        twofishSaveEncryptor.testRun();
    }

    private void testRun() {
        String message = "Niezła wiadomość :)";
        System.out.println(message);
        String key = "SuperSekret123";
        String encrypted = encrypt(message, key);
        System.out.println(encrypted);
        String decrypted = decrypt(encrypted, key);
        System.out.println(decrypted);
    }
}
