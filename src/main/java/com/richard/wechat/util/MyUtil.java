package com.richard.wechat.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title MyUtil
 * @todo TODO
 */

public class MyUtil {

	/**
	 * 获得当前子类及其父类中的方法声明
	 * 
	 * @param subClass
	 * @param methodName
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getUnitClass(Class<?> subClass, String methodName) throws NoSuchMethodException {
		for (Class<?> superClass = subClass; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(methodName, String.class);
			} catch (NoSuchMethodException e) {
				// Method不在当前类定义,继续向上转型
			}
		}
		throw new NoSuchMethodException();
	}

	public static String changeObjToXml(Object obj) throws Exception {
		String xml = "";
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JAXBContext context;
		context = JAXBContext.newInstance(obj.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.marshal(obj, os);
		xml = new String(os.toByteArray(), "UTF-8");
		return xml;
	}

	public static String convertXmlCdata(String text) {
		Pattern pattern = Pattern.compile("\\<\\!\\[CDATA\\[(?<text>[^\\]]*)\\]\\]\\>");
		Matcher matcher = pattern.matcher(text);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return text;
	}

	public static String changeCharset(String str, String newCharset) throws UnsupportedEncodingException {
		if (str != null) {
			byte[] bs = str.getBytes();
			return new String(bs, newCharset);
		}
		return str;
	}
}
