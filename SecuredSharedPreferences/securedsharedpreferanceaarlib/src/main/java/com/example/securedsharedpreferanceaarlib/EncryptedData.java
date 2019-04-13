package com.example.securedsharedpreferanceaarlib;

public class EncryptedData {

    private String mEncryptedString;
    private String mIv;
    public EncryptedData(String encrptdStr, String iVStr) {
        mEncryptedString = encrptdStr;
        mIv = iVStr;
    }
    String getEncryptedString(){return mEncryptedString;}
    String getIv(){ return mIv; }

}
