package rozi.encryptor.twofish;

public class SessionKey {

    final int[] sBox;
    final int[] subKey;

    public SessionKey(final int[] sBox, final int[] subKey) {
        this.sBox = sBox;
        this.subKey = subKey;
    }
}
