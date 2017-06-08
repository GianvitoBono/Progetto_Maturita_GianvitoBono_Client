package com.giansoft.cryptedchat;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypter{
    private final static byte[] keyValue = "c67XreYLAVFCzjrR".getBytes();
    private final static String ALGO = "AES";


    public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeToString(c.doFinal(Data.getBytes()), Base64.DEFAULT);
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decValue = c.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
        return new String(decValue);
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }


    public static String encryptWKey(String Data, Key key) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeToString(c.doFinal(Data.getBytes()), Base64.DEFAULT);
    }

    public static String decryptWKey(String encryptedData, Key key) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decValue = c.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
        return new String(decValue);
    }
}