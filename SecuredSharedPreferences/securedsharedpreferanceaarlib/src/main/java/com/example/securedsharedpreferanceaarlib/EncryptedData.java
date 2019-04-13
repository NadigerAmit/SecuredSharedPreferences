package com.example.securedsharedpreferanceaarlib;

public class EncryptedData {

    private String mEncryptedString;
    private String mIv;
    public EncryptedData(String encrptdStr, String iVStr) {
        mEncryptedString = encrptdStr;
        mIv = iVStr;
    }
    public String getEncryptedString(){return mEncryptedString;}
    public String getIv(){ return mIv; }

}
