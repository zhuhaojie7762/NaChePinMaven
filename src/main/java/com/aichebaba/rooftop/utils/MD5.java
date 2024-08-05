package com.aichebaba.rooftop.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SignatureException;

public class MD5 {
    /**
     * MD5方法
     *
     * @param text 明文
     * @param charset 密钥
     * @return 密文
     * @throws Exception
     */
	public static String md5(String text, String charset) throws Exception {
        if(charset == null || charset.length()==0) {
			charset = "UTF-8";
		}

		byte[] bytes = text.getBytes(charset);

		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(bytes);
		bytes = messageDigest.digest();

		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < bytes.length; i ++)
		{
			if((bytes[i] & 0xff) < 0x10)
			{
				sb.append("0");
			}

			sb.append(Long.toString(bytes[i] & 0xff, 16));
		}

		return sb.toString().toLowerCase();
	}

	/**
	 * 签名字符串
	 * @param text 需要签名的字符串
	 * @param key 密钥
	 * @param input_charset 编码格式
	 * @return 签名结果
	 */
	public static String sign(String text, String key, String input_charset) {
		text = text + key;
		return DigestUtils.md5Hex(getContentBytes(text, input_charset));
	}

	/**
	 * MD5验证方法
	 *
	 * @param text 明文
	 * @param charset 字符编码
	 * @param md5 密文
	 * @return true/false
	 * @throws Exception
	 */
	public static boolean verify(String text, String charset, String md5) throws Exception {
		String md5Text = md5(text, charset);
		if(md5Text.equalsIgnoreCase(md5))
		{
			return true;
		}

			return false;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(MD5.md5("1234567890", "UTF-8"));
	}

	/**
	 * 签名字符串
	 * @param text 需要签名的字符串
	 * @param sign 签名结果
	 * @param key 密钥
	 * @param input_charset 编码格式
	 * @return 签名结果
	 */
	public static boolean verify(String text, String sign, String key, String input_charset) {
		text = text + key;
		String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
		if(mysign.equals(sign)) {
			return true;
		}
		else {
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
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}
}