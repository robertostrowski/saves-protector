package rozi.encryptor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class BlowfishSaveEnryptor implements SaveEnryptor {

    private long modulo = (long) Math.pow(2, 32);

    // Subkeys initialisation with digits of pi.
    String[] Subkeys = { "243f6a88", "85a308d3", "13198a2e", "03707344", "a4093822",
            "299f31d0", "082efa98", "ec4e6c89", "452821e6", "38d01377",
            "be5466cf", "34e90c6c", "c0ac29b7", "c97c50dd", "3f84d5b5",
            "b5470917", "9216d5d9", "8979fb1b" };

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

    // Converts plaintext to binary representation
    private String hexToBin(String plainText) {
        StringBuilder binary = new StringBuilder();
        long num;
        String fourBits;

        // For each character of plaintext string
        for (int i = 0; i < plainText.length(); i++) {
            // Parse hexadecimal value to decimal value
            num = Long.parseUnsignedLong(plainText.charAt(i) + "", 16);
            // Convert it to binary string representation
            fourBits = Long.toBinaryString(num);

            // Make sure that 4 bits are present ( 10 -> 000010 -> 0010 )
            fourBits = "0000" + fourBits;
            fourBits = fourBits.substring(fourBits.length() - 4);

            binary.append(fourBits);
        }
        // 32-bit output representation of hexadecimal input
        return binary.toString();
    }

    // Converts binary strings into their hexadecimal representation
    private String binToHex(String binaryString) {
        // Binary => Decimal
        long num = Long.parseUnsignedLong(binaryString, 2);
        // Decimal => Hexadecimal
        StringBuilder hexOutput = new StringBuilder(Long.toHexString(num));
        // If there is length mismatch between binary representation and hexadecimal output, we must even length with leading zeros
        while (hexOutput.length() < (binaryString.length() / 4))
            hexOutput.insert(0, "0");
        return hexOutput.toString();
    }

    // XOR of 2 hexadecimal strings, must be of the same length
    private String xor(String hex1, String hex2) {
        // Hexes => Binaries
        String bin1 = hexToBin(hex1);
        String bin2 = hexToBin(hex2);
        StringBuilder ans = new StringBuilder("");

        // For each 2 Bits cast them to integers, xor them and append to result string
        for (int i = 0; i < bin1.length(); i++)
            ans.append(((bin1.charAt(i) - 48) ^ (bin2.charAt(i) - 48)));

        return binToHex(ans.toString());
    }

    // Modulo Sum of 2 hexadecimal strings resulting in new hexadecimal string
    private String moduloAddHexes(String hex1, String hex2) {
        String hexResult = "";

        // Hexes => Decimals
        long val1 = Long.parseUnsignedLong(hex1, 16);
        long val2 = Long.parseUnsignedLong(hex2, 16);
        // Decimal sum and modulo
        val1 = (val1 + val2) % modulo;
        // Result decimal => new hex
        hexResult = Long.toHexString(val1);

        // If hex is long enough, return it
        if(hexResult.length() == 8)
            return hexResult;

        // Otherwise add leading zeros
        hexResult = "00000000" + hexResult;
        return hexResult.substring(hexResult.length() - 8);
    }

    // Transforms string basing on feistel function
    private String feistelFunction(String plainText) {
        String[] a = new String[4];

        // divide 32bits into four 8-bit string
        for (int i = 0; i < 4; i++) {
            int col = Integer.parseUnsignedInt(hexToBin(plainText.substring(i*2, i*2 + 2)), 2);
            a[i] = SubstitutionBoxes.boxes[i][col];
        }
        String result = moduloAddHexes(a[0], a[1]);
        result = xor(result, a[2]);
        result = moduloAddHexes(result, a[3]);
        return result;
    }

    // Generate subkeys
    private void generateSubkeys(String key) {
        if(key.length() % 8 != 0)
            throw new IllegalArgumentException("Cannot generate subkeys for key not being multiplication of 32-bits");
        for (int i = 0, j = 0; i < Subkeys.length; i++, j = (j + 8) % key.length())
            // xor-ing 32-bit parts of the key with initial subkeys.
            Subkeys[i] = xor(Subkeys[i], key.substring(j, j + 8));
    }

    // round function
    private String round(int roundNumber, String plaintext) {
        String s1, s2;
        // Divide 64-bit string into 2 32-bit strings
        s1 = plaintext.substring(0, 8);
        s2 = plaintext.substring(8, 16);
        // S1 = S1 ^ P[round]
        s1 = xor(s1, Subkeys[roundNumber]);

        // output from F function
        // S1' = F(S1)
        String s1Transformed = feistelFunction(s1);

        // S2' = S1' ^ S2
        s2 = xor(s1Transformed, s2);

        // Order is changed!
        return s2 + s1;
    }

    // Encrypts plaintext block of 64 bits (16 hexes)
    private String encrypt(String hexString) {
        if(hexString.length() != 16)
            throw new IllegalArgumentException("String to be encrypted is not 64-bit sized!");
        for (int i = 0; i < 16; i++)
            hexString = round(i, hexString);

        // Last step
        String s1 = hexString.substring(0, 8);
        String s2 = hexString.substring(8, 16);
        s1 = xor(s1, Subkeys[16]);
        s2 = xor(s2, Subkeys[17]);

        // Order changed again!
        return s2 + s1;
    }

    // Decrypt cipherText into hex string
    private String decrypt(String plainText) {
        for (int i = 17; i > 1; i--)
            plainText = round(i, plainText);

        // Last step
        String s1 = plainText.substring(0, 8);
        String s2 = plainText.substring(8, 16);
        s1 = xor(s1, Subkeys[1]);
        s2 = xor(s2, Subkeys[0]);

        // Order changed!
        return s2 + s1;
    }

    public BlowfishSaveEnryptor() {

        // 64-bit
        String plainText = "abc12419b2c6e9f7";
        String key = "153be4a3c5c670e3";

        generateSubkeys(key);

        String cipherText = encrypt(plainText);
        System.out.println("Plain Text: " + plainText);
        System.out.println("Cipher Text: " + cipherText);
        plainText = decrypt(cipherText);
        System.out.println("Plain Text: " + plainText);
    }

    public static void main(String args[]) {
        new BlowfishSaveEnryptor();
    }
}
