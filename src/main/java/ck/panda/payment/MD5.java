package ck.panda.payment;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * The following code is sample code to test for the convenience of business and
 * provided businesses as needed for your site, according to technical writing,
 * not have to use the code. The code for study and research Alipay interface.
 */
public class MD5 {

	/**
	 * Signature string
	 *
	 * @param Text
	 *            string to be signed
	 * @param Key
	 *            Key
	 * @param Input_charset
	 *            encoding format
	 * @return Signature result
	 */
	public static String sign(String text, String key, String input_charset) {
		text = text + key;
		return DigestUtils.md5Hex(getContentBytes(text, input_charset));
	}

	/**
	 * Signature string
	 *
	 * @param Text
	 *            string to be signed
	 * @param Sign
	 *            signature result
	 * @param Key
	 *            Key
	 * @param Input_charset
	 *            encoding format
	 * @return Signature result
	 */
	public static boolean verify(String text, String sign, String key, String input_charset) {
		text = text + key;
		String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
		if (mysign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param content
	 * @param charset
	 * @return
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"MD5 signature error occurred during the specified encoding does not set your current specified encoding is set :"
							+ charset);
		}
	}

}