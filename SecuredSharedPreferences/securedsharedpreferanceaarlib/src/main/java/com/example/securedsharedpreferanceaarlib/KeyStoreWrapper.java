package com.example.securedsharedpreferanceaarlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

public class KeyStoreWrapper {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String TAG = "KeyStoreWrapper";
    private Context mContext;
    private KeyStore mkeyStore;//= createAndroidKeyStore();

    public KeyStoreWrapper(Context context) {
        Log.e(TAG, "KeyStoreWrapper Con <-");
        mContext = context;
        mkeyStore = createAndroidKeyStore();
    }

    private KeyStore createAndroidKeyStore() {
        try {
            mkeyStore = KeyStore.getInstance(ANDROID_KEY_STORE); // Get the Android key store
            Log.e(TAG, "getDefaultType Key store <-"+KeyStore.getDefaultType().toString());
            mkeyStore.load(null);    // <- Create an empty keystore based on our application Id.
            Log.e(TAG, "createAndroidKeyStore <-");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mkeyStore;
    }

    /**
     * @return symmetric key from Android Key Store or null if any key with given alias exists
     */
    public SecretKey getAndroidKeyStoreSymmetricKey(final String alias, Context context) {
        Key symmetricKey = null;
        try {
            Log.e(TAG, "getAndroidKeyStoreSymmetricKey  Alias = " + alias);
            symmetricKey = mkeyStore.getKey(alias, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (SecretKey) symmetricKey;
    }

    /**
     * @return asymmetric keypair from Android Key Store or null if any key with given alias exists
     */
    public KeyPair getAndroidKeyStoreAsymmetricKeyPair(final String alias, Context context) {
        Key privateKey = null;
        Key publicKey = null;
        try {
            privateKey = mkeyStore.getKey(alias, null);
            publicKey = mkeyStore.getCertificate(alias).getPublicKey();
         //   Log.e(TAG, "getAndroidKeyStoreAsymmetricKeyPair Pri  " + privateKey.toString());
         //   Log.e(TAG, "getAndroidKeyStoreAsymmetricKeyPair Pub  " + publicKey.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (privateKey != null && publicKey != null) {
            return new KeyPair((PublicKey) publicKey, (PrivateKey) privateKey);
        }
        return null;
    }

    @TargetApi(23)
    public SecretKey createAndroidKeyStoreSymmetricKey(final String alias) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE); // here ANDROID_KEY_STORE is provider.
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)  // Cipher block chaining , its block cipher
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)  // if block is not of correct size , need to pad.
                    .build();
            keyGenerator.init(spec);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    SecretKey generateDefaultSymmetricKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES"); // <= Create symmetric key(MASTER) with one of default Java Providers.
            Log.e(TAG, "generateDefaultSymmetricKey ");
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeAndroidKeyStoreKey(final String alias) {
        try {
            mkeyStore.deleteEntry(alias);
            Log.e(TAG, "removeAndroidKeyStoreKey ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KeyPair createAndroidKeyStoreAsymmetricKeyPair(final String alias) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE); // <--Get the KeyPairGenerator instance from keyStore of type "RSA."
            // <= This is how keystore and generated keypair are related.
            Log.e(TAG, "createAndroidKeyStoreAsymmetricKeyPair ");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                initGeneratorWithKeyGenParameterSpec(generator, alias);
            } else {
                initGeneratorWithKeyPairGeneratorSpec(generator, alias, mContext);
            }
            return generator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initGeneratorWithKeyGenParameterSpec(KeyPairGenerator generator, final String alias) {
        try {
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                    //  .setKeySize(1024)  default size is 2048
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1);
            generator.initialize(builder.build());
            Log.e(TAG, "initGeneratorWithKeyGenParameterSpec >M");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGeneratorWithKeyPairGeneratorSpec(KeyPairGenerator generator, final String alias, Context context) {
        Calendar startDate = new GregorianCalendar();
        Calendar endDate = new GregorianCalendar();
        endDate.add(Calendar.YEAR, 50);  // End date is 50 years later //need to check
        try {
            KeyPairGeneratorSpec.Builder builder = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(alias)   // We 'll use the alias later to retrieve the key.  It's a key for the key!
                    .setSerialNumber(BigInteger.ONE) // The serial number used for the self-signed certificate of the  generated pair.
                    .setSubject(new X500Principal("CN=${alias} CA Certificate")) // The subject used for the self-signed certificate of the generated pair
                    .setStartDate(startDate.getTime())  // Date range of validity for the generated pair.
                    .setEndDate(endDate.getTime());
            generator.initialize(builder.build());
            Log.e(TAG, "initGeneratorWithKeyPairGeneratorSpec ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
