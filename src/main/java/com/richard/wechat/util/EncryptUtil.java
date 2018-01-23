package com.richard.wechat.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title EncryptUtil
 * @todo TODO
 */

public class EncryptUtil {

	static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String encrypt(String str, String type) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(type);
			messageDigest.update(str.getBytes("UTF-8"));
			byte[] digest = messageDigest.digest();
			return getFormattedText(digest).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getFormattedText(byte[] bytes) {
		final StringBuilder buf = new StringBuilder(bytes.length * 2);
		for (int j = 0; j < bytes.length; j++) {
			buf.append(hexDigits[(bytes[j] >> 4) & 0x0f]);
			buf.append(hexDigits[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}

	private static byte[] getByteFromFormattedText(String formattedText) {
		char[] chars = formattedText.toUpperCase().toCharArray();
		byte[] bs = new byte[chars.length / 2];
		for (int i = 0; i * 2 < chars.length; i++) {
			bs[i] = (byte) (getByte(chars[2 * i]) << 4 | getByte(chars[2 * i + 1]));
		}
		return bs;
	}

	private static byte getByte(char c) {
		return (byte) (c > 57 ? (c - 55) : (c - 48));
	}

	public final static String ENCRYPT_TYPE_MD5 = "MD5";
	public final static String ENCRYPT_TYPE_SHA512 = "SHA-512";
	public final static String ENCRYPT_TYPE_SHA256 = "SHA-256";
	public final static String ENCRYPT_TYPE_SHA1 = "SHA-1";

	public static String MD5(String string) {
		return encrypt(string, ENCRYPT_TYPE_MD5);
	}

	public static String SHA512(String string) {
		return encrypt(string, ENCRYPT_TYPE_SHA512);
	}

	public static String SHA256(String string) {
		return encrypt(string, ENCRYPT_TYPE_SHA256);
	}

	public static String SHA1(String string) {
		return encrypt(string, ENCRYPT_TYPE_SHA1);
	}

	@SuppressWarnings("restriction")
	public static String encodeBase64(String str) {
		try {
			return new String(new sun.misc.BASE64Encoder().encode(str.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("restriction")
	public static byte[] decodeBase64(String str) {
		try {
			return new sun.misc.BASE64Decoder().decodeBuffer(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encodeDES(String content, String key) {
		return symmetricEncode("DES", null, "DES", content, key);
	}

	public static String decodeDES(String content, String key) throws Exception {
		return symmetricDecode("DES", null, "DES", content, key);
	}

	public static String encodeDES(String encryptString, String key, String iv) {
		return symmetricEncode("DES/CBC/PKCS5Padding", null, "DES", encryptString, key, iv);
	}

	public static String decodeDES(String encryptString, String key, String iv) {
		return symmetricDecode("DES/CBC/PKCS5Padding", null, "DES", encryptString, key, iv);
	}

	public static String encodeAES(String content, String key) {
		return symmetricEncode("AES", "AES", "AES", content, key);
	}

	public static String decodeAES(String content, String key) {
		return symmetricDecode("AES", "AES", "AES", content, key);
	}

	public static String encodeAES(String content, String key, String iv) {
		return symmetricEncode("AES/CBC/PKCS5Padding", "AES", "AES", content, key, iv);
	}

	public static String decodeAES(String content, String key, String iv) {
		return symmetricDecode("AES/CBC/PKCS5Padding", "AES", "AES", content, key, iv);
	}

	public static String encodeAES_ISO(String content, String key, String iv) {
		return symmetricEncode("AES/CBC/ISO10126Padding", null, "AES", content, key, iv);
	}

	public static String decodeAES_ISO(String content, String key, String iv) {
		return symmetricDecode("AES/CBC/ISO10126Padding", null, "AES", content, key, iv);
	}

	public static String encrypt_aes_base64(String content, String key, String iv) {
		try {
			byte[] raw = key.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
			IvParameterSpec ivKey = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivKey);
			byte[] encrypted = cipher.doFinal(content.getBytes());
			return Base64.encodeBase64String(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt_aes_base64(String sSrc, String sKey, String ivKey) {
		try {
			byte[] raw = sKey.getBytes("ASCII");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/ISO10126Padding");
			IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] encrypted1 = Base64.decodeBase64(sSrc);
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original);
				return originalString;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String symmetricEncode(String type, String keyGenerator, String keyFormat, String content,
			String key) {
		try {
			byte[] enCodeFormat = getKeyBytes(key, keyGenerator);
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, keyFormat);
			Cipher cipher = Cipher.getInstance(type);
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] byteRresult = cipher.doFinal(byteContent);
			return getFormattedText(byteRresult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String symmetricDecode(String type, String keyGenerator, String keyFormat, String content,
			String key) {
		if (content.length() < 1)
			return null;
		byte[] byteRresult = getByteFromFormattedText(content);
		try {
			byte[] enCodeFormat = getKeyBytes(key, keyGenerator);
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, keyFormat);
			Cipher cipher = Cipher.getInstance(type);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] result = cipher.doFinal(byteRresult);
			return new String(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String symmetricEncode(String type, String keyGenerator, String keyFormat, String content, String key,
			String iv) {
		try {

			byte[] enCodeFormat = getKeyBytes(key, keyGenerator);
			IvParameterSpec ivpara = new IvParameterSpec(iv.getBytes());
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, keyFormat);
			Cipher cipher = Cipher.getInstance(type);
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivpara);
			byte[] byteRresult = cipher.doFinal(byteContent);
			return getFormattedText(byteRresult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String symmetricDecode(String type, String keyGenerator, String keyFormat, String content, String key,
			String iv) {
		if (content.length() < 1)
			return null;
		byte[] byteRresult = getByteFromFormattedText(content);
		try {
			IvParameterSpec ivpara = new IvParameterSpec(iv.getBytes());
			byte[] enCodeFormat = getKeyBytes(key, keyGenerator);
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, keyFormat);
			Cipher cipher = Cipher.getInstance(type);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivpara);
			byte[] result = cipher.doFinal(byteRresult);
			return new String(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] getKeyBytes(String key, String keyGenerator)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		if (keyGenerator == null) {
			return key.getBytes("ASCII");
		} else {
			KeyGenerator kgen = KeyGenerator.getInstance(keyGenerator);
			kgen.init(128, new SecureRandom(key.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			return secretKey.getEncoded();
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(encodeBase64("sadasgh556+~#|}sd"));
		System.out.println(SHA1("11lfvW3ilGcPWgDMr4KIiIxmRsVa12dUUN").toLowerCase());
	}

	public static String encryptDES(String encryptString, String key, String iv) {
		IvParameterSpec ivpara = new IvParameterSpec(iv.getBytes());
		SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "DES");
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey, ivpara);
			byte[] encrtypeByte = cipher.doFinal(encryptString.getBytes());
			return byte2str(encrtypeByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String byte2str(byte[] encrtypeByte) {
		StringBuffer sb = new StringBuffer();
		for (byte b : encrtypeByte) {
			int ii = b & 0xff;
			String hex = Integer.toHexString(ii);
			sb.append(hex.length() == 1 ? "0" + hex : hex);
		}
		return sb.toString();
	}
}
