package com.jszsoft.securecloudnote.common.util;

import com.google.gson.Gson;
import com.jszsoft.securecloudnote.R;
import com.jszsoft.securecloudnote.hclient.dto.msgs.RequestHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

import static android.provider.Settings.Global.getString;


public class CommonUtils {

    private native static String tokenFromJNI();

    private native static String systemIdFromJNI();

    static {
        System.loadLibrary("load-env");
    }

    public static RequestHeader createHeader(String userId) {


//        String accessToken = "[access token to connect hamster application on PCF]";
//        String systemId = "[system id for accessing hamster application on PCF]";

        String accessToken = tokenFromJNI();
        String systemId = systemIdFromJNI();

        RequestHeader header = new RequestHeader();
        header.setRqUID(UUID.randomUUID().toString());
        header.setSystemID(systemId);
        header.setUserID(userId);
        header.setToken(accessToken);

        return header;
    }

    public static String toJsonString(Object object) {
        if (object == null)
            return "";

        Gson gson = new Gson();

        return gson.toJson(object);
    }

    public static <T> T toObject(String jsonString, Class<T> clazz) {
        Gson gson = new Gson();

        return gson.fromJson(jsonString, clazz);
    }

    public static String encryptText(String salt, String inText) throws Exception {
        SecretKeySpec secretKey = getSecretKey(salt);
        Cipher cipher = Cipher.getInstance(CommonConstants.CIPHER_SUITE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.encodeToString(cipher.doFinal(inText.getBytes("UTF-8")), Base64.DEFAULT | Base64.NO_WRAP);
    }

    public static String decryptText(String salt, String inText) throws Exception {
        SecretKeySpec secretKey = getSecretKey(salt);
        Cipher cipher = Cipher.getInstance(CommonConstants.CIPHER_SUITE);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.decode(inText, Base64.DEFAULT)));
    }

    private static SecretKeySpec getSecretKey(String myKey) throws Exception {
        MessageDigest sha = null;
        byte[] key = myKey.getBytes("UTF-8");
        sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);

        return new SecretKeySpec(key, "AES");
    }

    public static String hashText(String inText) throws Exception {
        if (inText == null) return null;

        MessageDigest digest;
        digest= MessageDigest.getInstance("SHA-256");

        digest.reset();
        digest.update(inText.getBytes());
        byte[] contentStream = digest.digest();

        StringBuffer encryptedText = new StringBuffer();
        for (int i = 0; i < contentStream.length; i++) {
            encryptedText.append(Integer.toString((contentStream[i] & 0xff) + 0x100, 16).substring(1));
        }
        return encryptedText.toString();
    }

    public static String formatDate(Date inDate) {
        if (inDate == null) return "";

        SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DATE_FORMAT_STRING);

        return df.format(inDate);
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
