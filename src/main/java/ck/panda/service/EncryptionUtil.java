package ck.panda.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

	static byte[] salt = new byte[8];

	/** To encrypt text using 'AES' algorithm. */
	public static byte[] encrypt(String encriptText) throws GeneralSecurityException, NoSuchPaddingException, IOException{
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec keySpec = new PBEKeySpec(encriptText.toCharArray(), salt, 65536,256);
		SecretKey secretKey = factory.generateSecret(keySpec);
		SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
	    return cipher.doFinal(encriptText.getBytes());
	}

	/** To decrypt text using 'AES' algorithm. */
	public static String decrypt(byte[] decryptText){
		return null;
	}

}
