package ck.panda.payment.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The following code is sample code to test for the convenience of business and
 * provided businesses as needed for your site, according to technical writing ,
 * not have to use the code. The code for study and research Alipay interface ,
 * only provide a reference .
 */
@Component
public class AlipayCore {

	/** The Alipay log path. */
	@Value(value = "${alipay.logpath}")
	private static String alipayLog;

	/**
	 * Remove the array parameter null and signature
	 *
	 * @param SArray
	 *            signature parameter set
	 * @return Remove null parameter signature of a new signature parameter set
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * Sort the array of all elements , and in accordance with
	 * "parameter = parameter value " model with "&" character to a string
	 * splicing
	 *
	 * @param Params
	 *            need to sort and group participation character stitching
	 *            parameters
	 * @return String after splicing
	 */
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);

			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

	/**
	 * Write the log, convenient test ( see the website needs to be changed to
	 * the record stored in the database )
	 *
	 * @param SWord
	 *            be written log of text
	 */
	public static void logResult(String sWord) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(alipayLog + "alipay_log_" + System.currentTimeMillis() + ".txt");
			writer.write(sWord);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Makefile Summary
	 *
	 * @param StrFilePath
	 *            file path
	 * @param File_digest_type
	 *            digest algorithm
	 * @return File Summary Results
	 */
	public static String getAbstract(String strFilePath, String file_digest_type) throws IOException {
		PartSource file = new FilePartSource(new File(strFilePath));
		if (file_digest_type.equals("MD5")) {
			return DigestUtils.md5Hex(file.createInputStream());
		} else if (file_digest_type.equals("SHA")) {
			return DigestUtils.sha256Hex(file.createInputStream());
		} else {
			return "";
		}
	}
}
