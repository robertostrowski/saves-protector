package rozi.encryptor.twofish;

import static rozi.encryptor.twofish.Constants.ROUNDS;
import static rozi.encryptor.twofish.Utils.bytePart;
import static rozi.encryptor.twofish.Utils.fromByteArray;
import static rozi.encryptor.twofish.Utils.toByteArray;

/**
 * https://www.youtube.com/watch?v=SpaXSMkJLs0
 * <p>
 * Twofish kroki:
 * <p>
 * 1. input whitening = plaintext * key
 * <p>
 * 2. 16 rund mieszania tekstu
 * 2.a S-boxes
 * 2.b Macierze MDS
 * 2.c PHT
 * 2.d dodawanie MOD 2^32
 * <p>
 * 3. output whitening
 */
public class TwofishAlgorithm {

    private static int F(int[] sBox, int x, int R) {
        return sBox[2 * bytePart(x, R)] ^ //
                sBox[2 * bytePart(x, R + 1) + 1] ^ //
                sBox[0x200 + 2 * bytePart(x, R + 2)] ^ //
                sBox[0x200 + 2 * bytePart(x, R + 3) + 1];
    }


    public static byte[] blockEncrypt(byte[] in, int inOffset, SessionKey sessionKey) {

        int[] sBox = sessionKey.sBox;
        int[] subKey = sessionKey.subKey;

        //input i output są XORowane z 8 podkluczami k0... k7, te operacje to input/output whitening

        int[] rs = fromByteArray(in, inOffset);
        int r0 = rs[0];
        int r1 = rs[1];
        int r2 = rs[2];
        int r3 = rs[3];

        r0 ^= subKey[0];
        r1 ^= subKey[1];
        r2 ^= subKey[2];
        r3 ^= subKey[3];

        // wynik input whitening to r0...r3

        int f0, f1;
        int k = 8;
        for (int R = 0; R < ROUNDS; R += 2) {
            // r0 i r1 przechodzą przez funkcję F, otrzymujemy f0 i f1
            f0 = F(sBox, r0, 0);
            f1 = F(sBox, r1, 3);

            // r2 jest XORowane z pierwszym wynikiem F i rotowane o jeden bit w prawo
            r2 ^= f0 + f1 + subKey[k++];
            r2 = r2 >>> 1 | r2 << 31;

            // r3 jest rotowane w lewo o bit i XORowane z drugim wyjściem F
            r3 = r3 << 1 | r3 >>> 31;
            r3 ^= f0 + 2 * f1 + subKey[k++];

            f0 = F(sBox, r2, 0);
            f1 = F(sBox, r3, 3);
            r0 ^= f0 + f1 + subKey[k++];
            r0 = r0 >>> 1 | r0 << 31;
            r1 = r1 << 1 | r1 >>> 31;
            r1 ^= f0 + 2 * f1 + subKey[k++];
        }

        // output whitening
        r2 ^= subKey[4];
        r3 ^= subKey[4 + 1];
        r0 ^= subKey[4 + 2];
        r1 ^= subKey[4 + 3];

        return toByteArray(r2, r3, r0, r1);
    }

    /*
    to samo co encrypt, tylko w odwrotnej kolejności, tj na wyjściu podawaliśmy r2, r3, r0, r1 więc w tej kolejności są teraz wejścia
    w końcu jest symetryczne, pewnie dałoby się to do jednej funkcji sprowadzić
     */
    public static byte[] blockDecrypt(byte[] in, int inOffset, SessionKey sessionKey) {
        int[] sBox = sessionKey.sBox;
        int[] sKey = sessionKey.subKey;

        int[] rs = fromByteArray(in, inOffset);
        int r2 = rs[0];
        int r3 = rs[1];
        int r0 = rs[2];
        int r1 = rs[3];

        r2 ^= sKey[4];
        r3 ^= sKey[5];
        r0 ^= sKey[6];
        r1 ^= sKey[7];

        int k = 8 + 2 * ROUNDS - 1;
        int t0, t1;
        for (int R = 0; R < ROUNDS; R += 2) {
            t0 = F(sBox, r2, 0);
            t1 = F(sBox, r3, 3);
            r1 ^= t0 + 2 * t1 + sKey[k--];
            r1 = r1 >>> 1 | r1 << 31;
            r0 = r0 << 1 | r0 >>> 31;
            r0 ^= t0 + t1 + sKey[k--];

            t0 = F(sBox, r0, 0);
            t1 = F(sBox, r1, 3);
            r3 ^= t0 + 2 * t1 + sKey[k--];
            r3 = r3 >>> 1 | r3 << 31;
            r2 = r2 << 1 | r2 >>> 31;
            r2 ^= t0 + t1 + sKey[k--];

        }
        r0 ^= sKey[0];
        r1 ^= sKey[1];
        r2 ^= sKey[2];
        r3 ^= sKey[3];

        return toByteArray(r0, r1, r2, r3);
    }
}
