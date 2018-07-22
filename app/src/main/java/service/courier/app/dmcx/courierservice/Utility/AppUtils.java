package service.courier.app.dmcx.courierservice.Utility;

import android.os.Message;
import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import service.courier.app.dmcx.courierservice.Variables.Vars;

public class AppUtils {

    public static String FirstLetterCapital(String word) {
        String firstLetter = word.substring(0, 1).toUpperCase();
        String restLetters = word.substring(1).toLowerCase();
        return firstLetter + restLetters;
    }

    public static class Hash {
        private static final String AES = "AES";
        private static final String SHA256 = "SHA-256";

        private static SecretKeySpec generateSecretKey(String passwd) throws Exception {
            final MessageDigest digest = MessageDigest.getInstance(SHA256);
            byte[] bytes = passwd.getBytes();
            digest.update(bytes, 0, bytes.length);
            byte[] key = digest.digest();

            SecretKeySpec spec = new SecretKeySpec(key, AES);
            return spec;
        }

        public static String encrypt(String password) throws Exception {
            String salt = Vars.PASSWORD_SALT;

            SecretKeySpec secret = generateSecretKey(salt);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] bytes = cipher.doFinal(password.getBytes());

            return Base64.encodeToString(bytes, Base64.DEFAULT);
        }

        public static String decrypt(String password) throws Exception {
            String salt = Vars.PASSWORD_SALT;

            SecretKeySpec secret = generateSecretKey(salt);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, secret);
            byte[] decode = Base64.decode(password, Base64.DEFAULT);
            byte[] bytes = cipher.doFinal(decode);

            return new String(bytes);
        }
    }

}
