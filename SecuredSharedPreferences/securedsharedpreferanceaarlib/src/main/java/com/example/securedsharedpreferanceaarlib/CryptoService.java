package com.example.securedsharedpreferanceaarlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyInfo;
import android.util.Log;

import com.example.securedsharedpreferanceaarlib.CipherWrapper.CipherCore;
import com.example.securedsharedpreferanceaarlib.CipherWrapper.SymmetricKeyWrapper;

import java.security.Key;
import java.security.KeyPair;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

public class CryptoService {
    private static final String TAG = "CryptoService";
    private static final String MASTER_KEY = "MASTER_SYMMETRIC_KEY";
    private static final String ALGORITHM_AES = "AES";  // symmetric algorithm
    SharedPreferences pref = null;
    Context mContext = null;
    KeyStoreWrapper mKeyStore = null;

    CryptoService(Context context) {
        Log.e(TAG, "CryptoService Cons");
        mContext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mKeyStore = new KeyStoreWrapper(context);
        createMasterKey(null);
    }

    private void createMasterKey(String passwd) {
        Log.e(TAG, "createMasterKey ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createAndroidSymmetricKey(); // Android provided symmetric key
        } else {
            createDefaultSymmetricKey();  // Java provided symmetric key
        }
    }
    public void removeMasterKey() {
        mKeyStore.removeAndroidKeyStoreKey(MASTER_KEY);
    }

    public EncryptedData encrypt(String data) {
        Log.e(TAG, "Encrypt ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return encryptWithAndroidSymmetricKey(data);  // Symmetric algo
        } else {
            return encryptWithDefaultSymmetricKey(data); // Hybrid algo ( Java symmetric + Android Asymmetric key pair  )
        }

    }

    public String decrypt(EncryptedData encryptedData) {
        Log.e(TAG, "decrypt ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return decryptWithAndroidSymmetricKey(encryptedData);  // Symmetric algo
        } else {
            return decryptWithDefaultSymmetricKey(encryptedData);  // Hybrid algo ( Java symmetric + Android Asymmetric key pair  )
        }
    }
    private void createAndroidSymmetricKey() {
        Log.e(TAG, "createAndroidSymmetricKey");
        SecretKey masterKey = mKeyStore.getAndroidKeyStoreSymmetricKey(MASTER_KEY, mContext);
        if (masterKey != null) {
            return;
        }
        Key symmetricKey = mKeyStore.createAndroidKeyStoreSymmetricKey(MASTER_KEY);
    }
    @TargetApi(23)
    private KeyInfo getKeyInfo() {
        SecretKey masterKey = null;
        KeyInfo keyInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            masterKey = mKeyStore.getAndroidKeyStoreSymmetricKey(MASTER_KEY, mContext); // Android provided symmetric key
        } else {
            // get private key
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                SecretKeyFactory factory = SecretKeyFactory.getInstance(masterKey.getAlgorithm(), "AndroidKeyStore");
                keyInfo = (KeyInfo) factory.getKeySpec(masterKey, KeyInfo.class);
            } catch (Exception e) {  //
                Log.e(TAG, e.getMessage());
            }
        }
        return keyInfo;
    }

    @TargetApi(23)
    private void ShowKeyInfo() {
        Log.e(TAG, "********************************************KeyIno Start ********************************************");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            KeyInfo keyInfo = getKeyInfo();
            Log.e(TAG, keyInfo.toString());

            Log.e(TAG, "KeySize = " + keyInfo.getKeySize());
            Log.e(TAG, "KeystoreAlias = " + keyInfo.getKeystoreAlias());
            Log.e(TAG, "getOrigion = " + keyInfo.getOrigin());
            Log.e(TAG, "getPurposes = " + keyInfo.getPurposes());
            Log.e(TAG, "getUserAuthenticationValidityDurationSeconds = " + keyInfo.getUserAuthenticationValidityDurationSeconds());
            Log.e(TAG, "isInsideSecureHardware =  " + keyInfo.isInsideSecureHardware());
            Log.e(TAG, "isInvalidatedByBiometricEnrollment = " + keyInfo.isInvalidatedByBiometricEnrollment());
            //Log.e(TAG, "isTrustedUserPresenceRequired" + keyInfo.isTrustedUserPresenceRequired()); // required API level 28.
            Log.e(TAG, "isUserAuthenticationRequired = " + keyInfo.isUserAuthenticationRequired());
            Log.e(TAG, "isUserAuthenticationValidWhileOnBody = " + keyInfo.isUserAuthenticationValidWhileOnBody());
            //Log.e(TAG, "isUserConfirmationRequired" + keyInfo.isUserConfirmationRequired()); // required API level 28.
            Log.e(TAG, "********************************************KeyIno Ends ********************************************");
        }
    }

    private void createDefaultSymmetricKey() {
        Log.e(TAG, "createDefaultSymmetricKey");
        String encryptedSymmetricKey = null;
        encryptedSymmetricKey = pref.getString("encryptedSymmetricKey", null);
        if (encryptedSymmetricKey != null) {
            Log.e(TAG, " Encrypted symmetric already created " + encryptedSymmetricKey);
            return;
        }
        SecretKey symmetricKey = mKeyStore.generateDefaultSymmetricKey();  // symmetric key from Java default provider
        KeyPair masterKey = mKeyStore.createAndroidKeyStoreAsymmetricKeyPair(MASTER_KEY); // Key pair from Android keyStore

        try {
            encryptedSymmetricKey = new SymmetricKeyWrapper().wrapKey(symmetricKey, masterKey.getPublic());
        } catch (Exception e) {
            e.printStackTrace();
        }
        pref.edit().putString("encryptedSymmetricKey", encryptedSymmetricKey).apply(); // store encrypted symmetric key in shared preference
    }

    private EncryptedData encryptWithAndroidSymmetricKey(String data) {
        Log.e(TAG, "encryptWithAndroidSymmetricKey");
        SecretKey masterKey = mKeyStore.getAndroidKeyStoreSymmetricKey(MASTER_KEY, mContext);
        CipherCore cipherObj = null;
        try {
            cipherObj = new CipherCore(mContext, CipherCore.TRANSFORMATION_SYMMETRIC);
            EncryptedData encryptedResult = cipherObj.encrypt(data, masterKey);
            return encryptedResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decryptWithAndroidSymmetricKey(EncryptedData encryptedData) {
        Log.e(TAG, "decryptWithAndroidSymmetricKey");
        SecretKey masterKey = mKeyStore.getAndroidKeyStoreSymmetricKey(MASTER_KEY, mContext);
        CipherCore cipherObj = null;
        try {
            cipherObj = new CipherCore(mContext, CipherCore.TRANSFORMATION_SYMMETRIC);
            return cipherObj.decrypt(encryptedData.getEncryptedString(), masterKey, encryptedData.getIv());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private EncryptedData encryptWithDefaultSymmetricKey(String data) {
        Log.e(TAG, "encryptWithDefaultSymmetricKey ");
        KeyPair asymmetricKeyPair = mKeyStore.getAndroidKeyStoreAsymmetricKeyPair(MASTER_KEY, mContext); // get the asymmetric key.
        String encryptedSymmetricMasterKey = pref.getString("encryptedSymmetricKey", null); // getting the encrypted Symmetric key
        CipherCore cipherObj = null;
        try {
            SecretKey symmetricKey =
                    (SecretKey) new SymmetricKeyWrapper().
                            unWrapKey(encryptedSymmetricMasterKey, // unWrapKey is similar to decrypting the encrypted symmetric key by  Private Key
                                    ALGORITHM_AES,                 // wrappedKeyAlgorithm is the algorithm associated with the wrapped key
                                    Cipher.SECRET_KEY,             // Wrapped Key
                                    asymmetricKeyPair.getPrivate());  // unWrapp the key with
            cipherObj = new CipherCore(mContext, CipherCore.TRANSFORMATION_SYMMETRIC);
            return cipherObj.encrypt(data, symmetricKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decryptWithDefaultSymmetricKey(EncryptedData encryptedData) {
        Log.e(TAG, "decryptWithDefaultSymmetricKey ");
        KeyPair asymmetricKeyPair = mKeyStore.getAndroidKeyStoreAsymmetricKeyPair(MASTER_KEY, mContext);
        String encryptedSymmetricMasterKey = pref.getString("encryptedSymmetricKey", null); // getting the eccryptted Symmetric key
        CipherCore cipherObj = null;
        try {
            SecretKey symmetricKey =
                    (SecretKey) new SymmetricKeyWrapper().
                            unWrapKey(encryptedSymmetricMasterKey,   // unWrapKey is similar to decrypting the encrypted symmetric key by  Private Key
                                    ALGORITHM_AES,                   // wrappedKeyAlgorithm is the algorithm associated with the wrapped key
                                    Cipher.SECRET_KEY,               // Wrapped Key
                                    asymmetricKeyPair.getPrivate()); // unWrapp the key with
            cipherObj = new CipherCore(mContext, CipherCore.TRANSFORMATION_SYMMETRIC);
            return cipherObj.decrypt(encryptedData.getEncryptedString(), symmetricKey, encryptedData.getIv());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
