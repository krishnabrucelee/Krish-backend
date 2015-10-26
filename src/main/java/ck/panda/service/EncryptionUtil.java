package ck.panda.service;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class EncryptionUtil {

	static Cipher cipher;

	/** To encrypt text using 'AES' algorithm. */
	public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
		cipher = Cipher.getInstance("AES");
		byte[] plainTextByte = plainText.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		Base64.Encoder encoder = Base64.getEncoder();
		String encryptedText = encoder.encodeToString(encryptedByte);
		return encryptedText;
	}

}
