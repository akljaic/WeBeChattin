package com.air.encryption2;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AESCryptography implements IEncryption {

    private final byte encryptionKey[] ={9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};
    private Cipher cipher;
    private Cipher deCipher;
    private SecretKeySpec secretKeySpec;

    public AESCryptography() {
        initCryptMessage();
    }

    private void initCryptMessage() {
        try {
            cipher = Cipher.getInstance("AES");
            deCipher = Cipher.getInstance("AES");


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");
    }

    @Override
    public String EncryptMessage(String message) {
        byte [] stringByte = message.getBytes();
        byte [] encryptedByte = new byte[stringByte.length];
        String returnString = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        try {
            return returnString = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return returnString;
    }

    @Override
    public String DecryptMessage(String message) {
        byte[] EncryptedByte = new byte[0];
        try {
            EncryptedByte = message.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String decryptedString = message;
        byte[] decryption;

        try {
            deCipher.init(cipher.DECRYPT_MODE, secretKeySpec);
            decryption = deCipher.doFinal(EncryptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }
}
