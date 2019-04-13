package com.example.securedsharedpreferanceaarlib.CipherWrapper;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.example.securedsharedpreferanceaarlib.EncryptedData;

import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

public class CipherCore {
    public static final String TRANSFORMATION_SYMMETRIC = "AES/CBC/PKCS7Padding";
    /**
     * This class wraps [Java Cipher] class apis with some additional possibilities.
     * Main logic of encryption and decryption happens here
     */
    private static final String TAG = "CipherCore";
    static byte[] mIv;
    String mTransformation = null;
    Cipher mCipher;

    // This is called for encryption
    public CipherCore(Context context, String transformation) throws
            java.security.NoSuchAlgorithmException,
            javax.crypto.NoSuchPaddingException {
        mTransformation = transformation;
        init(transformation);
        Log.e(TAG, "CipherCore Con ");
    }

    private void init(String transformation) throws
            java.security.NoSuchAlgorithmException,
            javax.crypto.NoSuchPaddingException{
        Log.d(TAG, "CipherWrapper Con ");
        mCipher = Cipher.getInstance(transformation);
    }

    public EncryptedData encrypt(String data, Key key) {
        try {
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            Log.e(TAG, "Key in encryptor = " + key.toString());
            IvParameterSpec ivParams = mCipher.getParameters().getParameterSpec(IvParameterSpec.class);
            mIv = ivParams.getIV();
            byte[] bytes = mCipher.doFinal(data.getBytes());
            Log.e(TAG, "In Encryptor IV = " + Arrays.toString(ivParams.getIV()));
            return new EncryptedData(Base64.encodeToString(bytes, Base64.DEFAULT), Arrays.toString(ivParams.getIV()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String data, Key key, String Iv) {
        byte[] array;
        Log.e(TAG, "Key in decryptor = " + key.toString());
        IvParameterSpec ivParams = null;  // This class specifies an initialization vector (IV).
        if (Iv != null) {
            String[] split = Iv.substring(1, Iv.length() - 1).split(", ");
            array = new byte[split.length];
            for (int i = 0; i < split.length; i++) {
                array[i] = Byte.parseByte(split[i]);
                ivParams = new IvParameterSpec(array);  // Creates an IvParameterSpec object using the bytes in Iv,got during encryption.
            }
        }
        try {
            Log.e(TAG, " EncryptedString : " + data);
            Log.e(TAG, "In Decrypt IV = " + Iv);
            mCipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] encryptedData = Base64.decode(data, Base64.DEFAULT);
            byte[] decodedData = mCipher.doFinal(encryptedData);  // Actual Decryption
            String str = new String(decodedData, "UTF-8");
            Log.e(TAG, " Plan Text : " + str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " Plan Text : e=  " + e.getMessage());
        }
        return null;
    }
}
