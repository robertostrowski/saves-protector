package rozi.encryptor.twofish;

public class Utils {

    public static int bytePart0(int x) {
        return bytePart(x, 0);
    }

    public static int bytePart1(int x) {
        return bytePart(x, 1);
    }

    public static int bytePart2(int x) {
        return bytePart(x, 2);
    }

    public static int bytePart3(int x) {
        return bytePart(x, 3);
    }

    public static int bytePart(int x, int N) {
        return (x >>> (8 * (N % 4))) & 0xFF;
    }

    public static byte[] toByteArray(int... val) {
        byte[] result = new byte[val.length * 4];
        int index = 0;
        for (int v : val) {
            result[index++] = (byte) v;
            result[index++] = (byte) (v >> 8);
            result[index++] = (byte) (v >> 16);
            result[index++] = (byte) (v >> 24);
        }
        return result;
    }

    public static int[] fromByteArray(byte[] in, int inOffset) {
        int a = toInt(in, inOffset);
        int b = toInt(in, inOffset + 4);
        int c = toInt(in, inOffset + 8);
        int d = toInt(in, inOffset + 12);
        return new int[] { a, b, c, d };
    }

    public static int toInt(byte[] in, int off) {
        return (in[off++] & 0xFF) | (in[off++] & 0xFF) << 8 | (in[off++] & 0xFF) << 16 | (in[off] & 0xFF) << 24;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
