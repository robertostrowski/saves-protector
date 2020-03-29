package rozi.converter;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class HexConverter {

    // Hex -> Bytes -> String
    public static String hexToString(String hex) {
        int l = hex.length();
        byte[] bytes = new byte[l / 2];
        for (int i = 0; i < l; i += 2)
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    // String -> Char -> Decimal -> Hex
    public static String stringToHex(String str) {
        return String.format("%064x", new BigInteger(1, str.getBytes(StandardCharsets.UTF_8)));
    }
}
