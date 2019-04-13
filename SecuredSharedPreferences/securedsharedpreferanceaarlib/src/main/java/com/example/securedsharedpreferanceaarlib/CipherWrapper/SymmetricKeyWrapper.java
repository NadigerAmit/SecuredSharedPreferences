package com.example.securedsharedpreferanceaarlib.CipherWrapper;

import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class SymmetricKeyWrapper {
    private static final String TAG = "KeyWrapper";
    private static final String TRANSFORMATION_ASYMMETRIC = "RSA/ECB/PKCS1Padding";
    String mTransformation = null;
    Cipher mCipher;

    public SymmetricKeyWrapper() throws NoSuchAlgorithmException, NoSuchPaddingException {
        Log.d(TAG, "KeyWrapper Con ");
        mCipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC);
    }
    public String wrapKey(Key keyToBeWrapped, Key keyToWrapWith) {
        try {
            mCipher.init(Cipher.WRAP_MODE, keyToWrapWith); // keyToWrapWith = public key
            byte[] decodedData = mCipher.wrap(keyToBeWrapped); // keyToBeWrapped = plan secret(Master) key is encrypted using public key
            return Base64.encodeToString(decodedData, Base64.DEFAULT); //
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Key unWrapKey(String wrappedKeyData, String algorithm, int wrappedKeyType, Key keyToUnWrapWith) {
        try {
            byte[] encryptedKeyData = Base64.decode(wrappedKeyData, Base64.DEFAULT); // encrypted secret Key converted to byte
            mCipher.init(Cipher.UNWRAP_MODE, keyToUnWrapWith);  //keyToUnWrapWith = private key
            return mCipher.unwrap(encryptedKeyData, algorithm, wrappedKeyType);  // Decrypting the encrypted  secret key with private key.
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
