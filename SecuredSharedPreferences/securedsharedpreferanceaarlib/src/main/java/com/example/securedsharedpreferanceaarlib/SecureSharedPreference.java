package com.example.securedsharedpreferanceaarlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class SecureSharedPreference {
    private static final String TAG = "SecureSharedPreference";
    private static SecureSharedPreference mInstance;
    private Context mContext = null;
    private SharedPreferences mPref = null;
    private CryptoService mCrServiec = null;

    private  SecureSharedPreference(Context context) {
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
        mCrServiec = new CryptoService(context);
    }

    public static synchronized SecureSharedPreference getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new SecureSharedPreference(context);
        }
        return mInstance;
    }

    public void putInt(final String key, final int value) {
        EncryptedData encryptedValue = mCrServiec.encrypt(String.valueOf(value)); // This is for Value
        if(encryptedValue == null) {
            Log.e(TAG, "Encrypted data is null ");
            return;
        }
        mPref.edit().putString(key, encryptedValue.getEncryptedString()).apply();
        mPref.edit().putString(key + "-Iv", encryptedValue.getIv()).apply();
    }

    public void putLong(final String key, final long value) {
        EncryptedData encryptedValue = mCrServiec.encrypt(String.valueOf(value)); // This is for Value
        if(encryptedValue == null) {
            Log.e(TAG, "Encrypted data is null ");
            return;
        }
        mPref.edit().putString(key, encryptedValue.getEncryptedString()).apply();
        mPref.edit().putString(key + "-Iv", encryptedValue.getIv()).apply();
    }

    public void putString(final String key, final String value) {
        EncryptedData encryptedValue = mCrServiec.encrypt(value); // This is for Value
        Log.e(TAG, "Encrypted Value of  " + value + " = " + encryptedValue);
        if(encryptedValue == null) {
            Log.e(TAG, "Encrypted data is null ");
            return;
        }
        mPref.edit().putString(key, encryptedValue.getEncryptedString()).apply();
        mPref.edit().putString(key + "-Iv", encryptedValue.getIv()).apply();
    }

    public void putFloat(final String key, final float value) {
        EncryptedData encryptedValue = mCrServiec.encrypt(String.valueOf(value)); // This is for Value
        if(encryptedValue == null) {
            Log.e(TAG, "Encrypted data is null ");
            return;
        }
        mPref.edit().putString(key, encryptedValue.getEncryptedString()).apply();
        mPref.edit().putString(key + "-Iv", encryptedValue.getIv()).apply();
    }

    public void putBool(final String key, final boolean value) {
        EncryptedData encryptedValue = mCrServiec.encrypt(String.valueOf(value)); // This is for Value
        if(encryptedValue == null) {
            Log.e(TAG, "Encrypted data is null ");
            return;
        }
        mPref.edit().putString(key, encryptedValue.getEncryptedString()).apply();
        mPref.edit().putString(key + "-Iv", encryptedValue.getIv()).apply();
    }

    public void putStringSet(final String key, final Set<String> value) {
        EncryptedData encryptedValue = mCrServiec.encrypt(String.valueOf(value));
        if(encryptedValue == null) {
            Log.e(TAG, "Encrypted data is null ");
            return;
        }
        mPref.edit().putString(key, encryptedValue.getEncryptedString()).apply();
        mPref.edit().putString(key + "-Iv", encryptedValue.getIv()).apply();
    }

    public void putDouble(final String key, final double value) {
        EncryptedData encryptedValue = mCrServiec.encrypt(String.valueOf(value)); // This is for Value
        if(encryptedValue == null) {
            Log.e(TAG, "Encrypted data is null ");
            return;
        }
        mPref.edit().putString(key, encryptedValue.getEncryptedString()).apply();
        mPref.edit().putString(key + "-Iv", encryptedValue.getIv()).apply();
    }

    public String getString(final String key, final String defaultValue) {
        String EncryptedString = mPref.getString(key, defaultValue);
        if (defaultValue == EncryptedString) return defaultValue;
        String Iv = mPref.getString(key + "-Iv", null);
        if (Iv == null) return null;
        EncryptedData encryptedData = new EncryptedData(EncryptedString, Iv);
        String decryptedString = mCrServiec.decrypt(encryptedData);
        //    Log.e(TAG,"Decrypted Value of  "+EncryptedString + " = "+ decryptedString);
        return decryptedString;
    }

    public int getInt(final String key, final int defaultValue) {
        String EncryptedString = mPref.getString(key, null);
        if (EncryptedString == null) {
            return defaultValue;
        }
        String Iv = mPref.getString(key + "-Iv", null);
        if (Iv == null) return defaultValue;
        EncryptedData encryptedData = new EncryptedData(EncryptedString, Iv);
        String decryptedString = mCrServiec.decrypt(encryptedData);
        //    Log.e(TAG,"Decrypted Value of  "+EncryptedString + " = "+ decryptedString);
        return Integer.parseInt(decryptedString);
    }

    public long getLong(final String key, final long defaultValue) {
        String EncryptedString = mPref.getString(key, null);
        if (EncryptedString == null) {
            return defaultValue;
        }
        String Iv = mPref.getString(key + "-Iv", null);
        if (Iv == null) return defaultValue;
        EncryptedData encryptedData = new EncryptedData(EncryptedString, Iv);
        String decryptedString = mCrServiec.decrypt(encryptedData);
        //    Log.e(TAG,"Decrypted Value of  "+EncryptedString + " = "+ decryptedString);
        return Long.parseLong(decryptedString);
    }

    public float getFloat(final String key, final float defaultValue) {
        String EncryptedString = mPref.getString(key, null);
        if (EncryptedString == null) {
            return defaultValue;
        }
        String Iv = mPref.getString(key + "-Iv", null);
        if (Iv == null) return defaultValue;
        EncryptedData encryptedData = new EncryptedData(EncryptedString, Iv);
        String decryptedString = mCrServiec.decrypt(encryptedData);
        //    Log.e(TAG,"Decrypted Value of  "+EncryptedString + " = "+ decryptedString);
        return Float.parseFloat(decryptedString);
    }

    public boolean getBool(final String key, final boolean defaultValue) {
        String EncryptedString = mPref.getString(key, null);
        if (EncryptedString == null) {
            return defaultValue; //Difficult to understand the difference between default value and actual storage
        }
        String Iv = mPref.getString(key + "-Iv", null);
        if (Iv == null)
            return defaultValue; //Difficult to understand the difference between default value and actual storage
        EncryptedData encryptedData = new EncryptedData(EncryptedString, Iv);
        String decryptedString = mCrServiec.decrypt(encryptedData);
        //     Log.e(TAG,"Decrypted Value of  "+EncryptedString + " = "+ decryptedString);
        return Boolean.parseBoolean(decryptedString);
    }

    public Set<String> getStringSet(final String key, final Set<String> defaultValue) {
        String EncryptedString = mPref.getString(key, null);
        if (EncryptedString == null) {
            return defaultValue;
        }
        String Iv = mPref.getString(key + "-Iv", null);
        if (Iv == null) return defaultValue;
        EncryptedData encryptedData = new EncryptedData(EncryptedString, Iv);
        String decryptedString = mCrServiec.decrypt(encryptedData);
        //     Log.e(TAG,"Decrypted Value of  "+EncryptedString + " = "+ decryptedString);
        Set<String> set = new HashSet<String>();
        set.add(decryptedString);
        return set;
    }

    public double getDouble(final String key, final double defaultValue) {
        String EncryptedString = mPref.getString(key, null);
        if (EncryptedString == null) {
            return defaultValue;
        }
        String Iv = mPref.getString(key + "-Iv", null);
        if (Iv == null) return defaultValue;
        EncryptedData encryptedData = new EncryptedData(EncryptedString, Iv);
        String decryptedString = mCrServiec.decrypt(encryptedData);
        //   Log.e(TAG,"Decrypted Value of  "+EncryptedString + " = "+ decryptedString);
        return Double.parseDouble(decryptedString);
    }

    public boolean contains(final String key) {
        return mPref.contains(key);
    }

    void removeKey(final String key) {
        mPref.edit().remove(key);
        mPref.edit().apply();
    }

    void clearStorage() {
        mPref.edit().clear();
        mPref.edit().apply();
    }

}
