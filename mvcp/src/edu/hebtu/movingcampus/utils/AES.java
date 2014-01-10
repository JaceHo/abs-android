package edu.hebtu.movingcampus.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.kobjects.base64.Base64;

public class AES {
	private static String cKey = "1fdaLKFJAO890abcDEF";

	public static String Decrypt(String paramString) throws Exception {
		return Decrypt(paramString, cKey);
	}

	public static String Decrypt(String paramString1, String paramString2)
			throws Exception {
		try {
			SecretKeySpec localSecretKeySpec = new SecretKeySpec(generateKey(
					paramString2).getBytes("utf-8"), "AES");
			Cipher localCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			localCipher.init(2, localSecretKeySpec);
			String str = new String(localCipher.doFinal(Base64
					.decode(paramString1)), "utf-8");
			return str;
		} catch (Exception localException) {
			System.out.println(localException.toString());
		}
		return null;
	}

	public static String Encrypt(String paramString) throws Exception {
		return Encrypt(paramString, cKey);
	}

	public static String Encrypt(String paramString1, String paramString2)
			throws Exception {
		SecretKeySpec localSecretKeySpec = new SecretKeySpec(generateKey(
				paramString2).getBytes("utf-8"), "AES");
		Cipher localCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		localCipher.init(1, localSecretKeySpec);
		return Base64
				.encode(localCipher.doFinal(paramString1.getBytes("utf-8")));
	}

	static String generateKey(String paramString) {
		String str = paramString;
		if ((paramString == null) || (paramString == ""))
			str = cKey;
		int i = paramString.length();
		if (i > 16)
			str = paramString.substring(0, 16);
		while (i >= 16)
			return str;
		do
			str = str + paramString;
		while (str.length() < 16);
		return str.substring(0, 16);
	}
}