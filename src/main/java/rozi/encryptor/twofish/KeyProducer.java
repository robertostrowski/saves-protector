package rozi.encryptor.twofish;

import static rozi.encryptor.twofish.Utils.*;
import static rozi.encryptor.twofish.Constants.*;

public class KeyProducer {

    public static SessionKey makeKey(byte[] k) {
        if (k == null) {
            throw new RuntimeException("Empty key");
        }
        int length = k.length;
        if (!(length == 8 || length == 16 || length == 24 || length == 32)) {
            throw new RuntimeException("Incorrect key length");
        }

        int bit64PartCount = length / 8;
        int subkeyCount = 8 + 2 * ROUNDS;
        int[] even32bitElements = new int[4];
        int[] odd32bitElements = new int[4];
        int[] sBoxKey = new int[4];

        // oblicz klucz na podstawie S-bx używając (12, 8) Reed-Solomon code over GF(256)
        int i, j, offset = 0;
        for (i = 0, j = bit64PartCount - 1; i < 4 && offset < length; i++, j--, offset += 8) {
            even32bitElements[i] = toInt(k, offset);
            odd32bitElements[i] = toInt(k, offset + 4);
            sBoxKey[j] = RsMdsEncode(even32bitElements[i], odd32bitElements[i]); // reverse order
        }
        // oblicz subkeys dla PHT
        int q, A, B;
        int[] subKeys = new int[subkeyCount];
        for (i = q = 0; i < subkeyCount / 2; i++, q += SK_STEP) {
            A = F(bit64PartCount, q, even32bitElements); // A używa parzystych elementów
            B = F(bit64PartCount, q + SK_BUMP, odd32bitElements); // B nieparzystych
            B = B << 8 | B >>> 24;
            A += B;
            subKeys[2 * i] = A;               // połącz z PHT
            A += B;
            subKeys[2 * i + 1] = A << SK_ROTL | A >>> (32 - SK_ROTL);
        }

        int k0 = sBoxKey[0];
        int k1 = sBoxKey[1];
        int k2 = sBoxKey[2];
        int k3 = sBoxKey[3];
        int b0, b1, b2, b3;
        int[] sBox = new int[4 * 256];
        for (i = 0; i < 256; i++) {
            b0 = b1 = b2 = b3 = i;
            switch (bit64PartCount & 3) {
                case 1:
                    sBox[2 * i] = MDS[0][(P[P_01][b0] & 0xFF) ^ bytePart0(k0)];
                    sBox[2 * i + 1] = MDS[1][(P[P_11][b1] & 0xFF) ^ bytePart1(k0)];
                    sBox[0x200 + 2 * i] = MDS[2][(P[P_21][b2] & 0xFF) ^ bytePart2(k0)];
                    sBox[0x200 + 2 * i + 1] = MDS[3][(P[P_31][b3] & 0xFF) ^ bytePart3(k0)];
                    break;
                case 0: // same as 4
                    b0 = (P[P_04][b0] & 0xFF) ^ bytePart0(k3);
                    b1 = (P[P_14][b1] & 0xFF) ^ bytePart1(k3);
                    b2 = (P[P_24][b2] & 0xFF) ^ bytePart2(k3);
                    b3 = (P[P_34][b3] & 0xFF) ^ bytePart3(k3);
                case 3:
                    b0 = (P[P_03][b0] & 0xFF) ^ bytePart0(k2);
                    b1 = (P[P_13][b1] & 0xFF) ^ bytePart1(k2);
                    b2 = (P[P_23][b2] & 0xFF) ^ bytePart2(k2);
                    b3 = (P[P_33][b3] & 0xFF) ^ bytePart3(k2);
                case 2: // 128-bit keys
                    sBox[2 * i] = MDS[0][(P[P_01][(P[P_02][b0] & 0xFF) ^ bytePart0(k1)] & 0xFF) ^ bytePart0(k0)];
                    sBox[2 * i + 1] = MDS[1][(P[P_11][(P[P_12][b1] & 0xFF) ^ bytePart1(k1)] & 0xFF) ^ bytePart1(k0)];
                    sBox[0x200 + 2 * i] = MDS[2][(P[P_21][(P[P_22][b2] & 0xFF) ^ bytePart2(k1)] & 0xFF) ^ bytePart2(
                            k0)];
                    sBox[0x200 + 2 * i + 1] = MDS[3][(P[P_31][(P[P_32][b3] & 0xFF) ^ bytePart3(k1)] & 0xFF) ^ bytePart3(
                            k0)];
            }
        }

        return new SessionKey(sBox, subKeys);
    }

    private static int F(int k64Cnt, int x, int[] k32) {
        int b0 = bytePart0(x);
        int b1 = bytePart1(x);
        int b2 = bytePart2(x);
        int b3 = bytePart3(x);
        int k0 = k32[0];
        int k1 = k32[1];
        int k2 = k32[2];
        int k3 = k32[3];

        int result = 0;
        switch (k64Cnt & 3) {
            case 1:
                result = MDS[0][(P[P_01][b0] & 0xFF) ^ bytePart0(k0)] ^ MDS[1][(P[P_11][b1] & 0xFF) ^ bytePart1(k0)]
                        ^ MDS[2][(P[P_21][b2] & 0xFF) ^ bytePart2(k0)] ^ MDS[3][(P[P_31][b3] & 0xFF) ^ bytePart3(k0)];
                break;
            case 0:  // same as 4
                b0 = (P[P_04][b0] & 0xFF) ^ bytePart0(k3);
                b1 = (P[P_14][b1] & 0xFF) ^ bytePart1(k3);
                b2 = (P[P_24][b2] & 0xFF) ^ bytePart2(k3);
                b3 = (P[P_34][b3] & 0xFF) ^ bytePart3(k3);
            case 3:
                b0 = (P[P_03][b0] & 0xFF) ^ bytePart0(k2);
                b1 = (P[P_13][b1] & 0xFF) ^ bytePart1(k2);
                b2 = (P[P_23][b2] & 0xFF) ^ bytePart2(k2);
                b3 = (P[P_33][b3] & 0xFF) ^ bytePart3(k2);
            case 2:
                result = MDS[0][(P[P_01][(P[P_02][b0] & 0xFF) ^ bytePart0(k1)] & 0xFF) ^ bytePart0(k0)] ^ MDS[1][
                        (P[P_11][(P[P_12][b1] & 0xFF) ^ bytePart1(k1)] & 0xFF) ^ bytePart1(k0)] ^ MDS[2][
                        (P[P_21][(P[P_22][b2] & 0xFF) ^ bytePart2(k1)] & 0xFF) ^ bytePart2(k0)] ^ MDS[3][
                        (P[P_31][(P[P_32][b3] & 0xFF) ^ bytePart3(k1)] & 0xFF) ^ bytePart3(k0)];
                break;
        }
        return result;
    }

    private static int RsRem(int x) {
        int b = (x >>> 24) & 0xFF;
        int g2 = ((b << 1) ^ ((b & 0x80) != 0 ? RS_GF_FDBK : 0)) & 0xFF;
        int g3 = (b >>> 1) ^ ((b & 0x01) != 0 ? (RS_GF_FDBK >>> 1) : 0) ^ g2;
        return (x << 8) ^ (g3 << 24) ^ (g2 << 16) ^ (g3 << 8) ^ b;
    }

    private static int RsMdsEncode(int k0, int k1) {
        int r = k1;
        for (int i = 0; i < 4; i++) {
            r = RsRem(r);
        }
        r ^= k0;
        for (int i = 0; i < 4; i++) {
            r = RsRem(r);
        }
        return r;
    }
}
