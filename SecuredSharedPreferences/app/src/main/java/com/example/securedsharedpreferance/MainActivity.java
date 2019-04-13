package com.example.securedsharedpreferance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.securedsharedpreferanceaarlib.SecureSharedPreference;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    EditText tvDecryptedText;
    Button mGetFromSharedPreferanceButton;
    Button encrypDecrypttButton;
    SecureSharedPreference mStorageMgr = null;
    SharedPreferences pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mStorageMgr = SecureSharedPreference.getInstance(this);
        if(mStorageMgr == null) {
            Log.e(TAG,"mStorageMgr is null");
        }
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        tvDecryptedText = findViewById(R.id.textDisplay);
        String encryptedStringInShrdPref = GetTextFromSharedPreferance();
        encrypDecrypttButton = findViewById(R.id.button3);
        if (encryptedStringInShrdPref != null &&
                tvDecryptedText.getText().toString().contains(encryptedStringInShrdPref)) {
            encrypDecrypttButton.setText("Decrypt");
        }

        encrypDecrypttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Encrypt the string
                if (encrypDecrypttButton.getText().equals("Decrypt")) {
                    tvDecryptedText.setText(decryptText());
                } else {
                    encryptText(getApplicationContext());
                }
                if (GetTextFromSharedPreferance()!= null && GetTextFromSharedPreferance().equals(tvDecryptedText.getText().toString())) {
                    encrypDecrypttButton.setText("Decrypt");
                } else {
                    encrypDecrypttButton.setText("Encr And Store");
                }
            }
        });
        mGetFromSharedPreferanceButton = findViewById(R.id.button4);
        if (encryptedStringInShrdPref == null) {
            mGetFromSharedPreferanceButton.setEnabled(false);
        } else {
            mGetFromSharedPreferanceButton.setEnabled(true);
        }
        mGetFromSharedPreferanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Decrypt the string
                String encryptedStringInShrdPref = GetTextFromSharedPreferance();
                if (encryptedStringInShrdPref != null &&
                        encryptedStringInShrdPref.length() > 0)
                    tvDecryptedText.setText(encryptedStringInShrdPref);
                if (GetTextFromSharedPreferance()!=null && GetTextFromSharedPreferance().equals(tvDecryptedText.getText().toString())) {
                    encrypDecrypttButton.setText("Decrypt");
                } else {
                    encrypDecrypttButton.setText("Encr And Store");
                }
            }
        });
    }

    private void SaveInStorageManagerPlane(String inputText) {  // This is demo for storing different values
        pref.edit().putString("EncryptedString", inputText).apply();
        pref.edit().putBoolean("EncryptedBoolean", true).apply();
        //pref.edit().put("EncryptedDouble", 99999.65d);
        pref.edit().putFloat("EncryptedFloat", 99999.45f).apply();
        pref.edit().putInt("EncryptedInt", 21).apply();
        pref.edit().putLong("EncryptedLong", 600851475143L).apply();

        Set<String> stringSet = new HashSet();
        stringSet.add("One");
        stringSet.add("Two");
        stringSet.add("Three");
        pref.edit().putStringSet("EncryptedStringSet", stringSet).apply();
        pref.edit().apply();
    }

    private void RetrieveFromStorageManagerPlane() {  // This is demo for retrieving  different values.
        String inputText = pref.getString("EncryptedString", "DefaultBVal");
        boolean retBool = pref.getBoolean("EncryptedBoolean", false);
        //double retDouble = pref.get("EncryptedDouble", 111111.65d);
        float retFloat = pref.getFloat("EncryptedFloat", 22222.65f);
        int retInt = pref.getInt("EncryptedInt", 21);
        long retLong = pref.getLong("EncryptedLong", 600851L);
        Set<String> stringSet1 = new HashSet();
        stringSet1.add("Ten");
        stringSet1.add("Eleven");
        stringSet1.add("Twelve");
        Set<String> stringSet = pref.getStringSet("EncryptedStringSet", stringSet1);
        Log.e(TAG, "*******************************  RetrieveFromStorageManager Start ********************************** ");
        Log.e(TAG, "Decrypted From SharedPref  string = " + inputText);
        Log.e(TAG, "Decrypted From SharedPref  bool = " + retBool);
        //   Log.e(TAG, "Decrypted From SharedPref  retDouble = " + retDouble);
        Log.e(TAG, "Decrypted From SharedPref  retFloat = " + retFloat);
        Log.e(TAG, "Decrypted From SharedPref  retInt = " + retInt);
        Log.e(TAG, "Decrypted From SharedPref  retLong = " + retLong);
        Iterator iterator = stringSet.iterator();
        Log.e(TAG, "Decrypted String set is below  = ");
        while (iterator.hasNext()) {
            String element = (String) iterator.next();
            Log.e(TAG, element);
        }
        Log.e(TAG, "*******************************  RetrieveFromStorageManager End ********************************** ");
    }

    private void SaveInStorageManager(String inputText) {  // This is demo for storing different values
        mStorageMgr.putString("EncryptedString", inputText);
        mStorageMgr.putBool("EncryptedBoolean", true);
        mStorageMgr.putDouble("EncryptedDouble", 99999.65d);
        mStorageMgr.putFloat("EncryptedFloat", 99999.45f);
        mStorageMgr.putInt("EncryptedInt", 21);
        mStorageMgr.putLong("EncryptedLong", 600851475143L);

        Set<String> stringSet = new HashSet();
        stringSet.add("One");
        stringSet.add("Two");
        stringSet.add("Three");
        mStorageMgr.putStringSet("EncryptedStringSet", stringSet);
    }

    private void RetrieveFromStorageManager() {  // This is demo for retrieving  different values.
        String inputText = mStorageMgr.getString("EncryptedString", "DefaultBVal");
        boolean retBool = mStorageMgr.getBool("EncryptedBoolean", false);
        double retDouble = mStorageMgr.getDouble("EncryptedDouble", 111111.65d);
        float retFloat = mStorageMgr.getFloat("EncryptedFloat", 22222.65f);
        int retInt = mStorageMgr.getInt("EncryptedInt", 21);
        long retLong = mStorageMgr.getLong("EncryptedLong", 600851L);
        Set<String> stringSet1 = new HashSet();
        stringSet1.add("Ten");
        stringSet1.add("Eleven");
        stringSet1.add("Twelve");
        Set<String> stringSet = mStorageMgr.getStringSet("EncryptedStringSet", stringSet1);
        Log.e(TAG, "*******************************  RetrieveFromStorageManager Start ********************************** ");
        Log.e(TAG, "Decrypted From SharedPref  string = " + inputText);
        Log.e(TAG, "Decrypted From SharedPref  bool = " + retBool);
        Log.e(TAG, "Decrypted From SharedPref  retDouble = " + retDouble);
        Log.e(TAG, "Decrypted From SharedPref  retFloat = " + retFloat);
        Log.e(TAG, "Decrypted From SharedPref  retInt = " + retInt);
        Log.e(TAG, "Decrypted From SharedPref  retLong = " + retLong);
        Iterator iterator = stringSet.iterator();
        Log.e(TAG, "Decrypted String set is below  = ");
        while (iterator.hasNext()) {
            String element = (String) iterator.next();
            Log.e(TAG, element);
        }
        Log.e(TAG, "*******************************  RetrieveFromStorageManager End ********************************** ");
    }

    private void encryptText(Context context) {
        String inputText = tvDecryptedText.getText().toString();
        SaveInStorageManager(inputText);  // storing in sharedPreference
        String encryptedString = pref.getString("EncryptedString", null); // getting the Encrypted string.
        if (encryptedString != null) {
            Log.e("MainActivity", "Encrypted Text from shard pref= " + encryptedString);
            tvDecryptedText.setText(encryptedString);
            mGetFromSharedPreferanceButton.setEnabled(true);
        }
        encrypDecrypttButton.setText("Decrypt");
    }

    private String GetTextFromSharedPreferance() {
        String encryptedString = pref.getString("EncryptedString", null); // getting the Encrypted string.
        return encryptedString;
    }

    private String decryptText() {
        String decryptedText = mStorageMgr.getString("EncryptedString", null);
        RetrieveFromStorageManager();
        if (decryptedText != null) {
            return decryptedText;
        }
        return null;
    }
}
