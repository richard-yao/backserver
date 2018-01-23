package com.richard.wechat.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title HttpsUtil
 * @todo TODO
 */

@SuppressWarnings("deprecation")
public class HttpsUtil {

	private static Logger logger = Logger.getLogger(HttpsUtil.class);

	public static String sendSSLPostRequest(String reqURL, String param, String type) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		BufferedReader reader = null;
		try {
			httpClient = SSLClient.getSSLClient(new DefaultHttpClient());
			HttpParams httpParams = httpClient.getParams();
			httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);// 请求连接超时时间
			httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);// 请求响应超时时间
			httpPost = new HttpPost(reqURL);
			httpPost.setHeader("Content-type", type);
			StringEntity stringEntity = new StringEntity(param, "UTF-8");
			httpPost.setEntity(stringEntity);

			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
				String inputLine = null;
				String resultData = "";
				while (((inputLine = reader.readLine()) != null)) {
					resultData += inputLine;
				}
				reader.close();
				return resultData;
			}
		} catch (Exception e) {
			logger.error(e, e);
		} finally {
			if (httpPost != null) {
				httpPost.abort();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
			if (httpClient != null && httpClient.getConnectionManager() != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		return null;
	}
	
	/**
	 * 发送xml格式数据到微信并获取微信返回值
	 * @param urlStr
	 * @param xml
	 * @return
	 */
	public static String sendXmlPost(String urlStr, String xml) {
		HttpURLConnection con = null;
		OutputStreamWriter out = null;
		BufferedReader reader = null;
		try {
			URL url = new URL(urlStr);
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "text/xml");
			con.setConnectTimeout(3000);
			con.setReadTimeout(3000);
			out = new OutputStreamWriter(con.getOutputStream());
			out.write(new String(xml.getBytes("UTF-8")));
			out.flush();
			out.close();
			int code = con.getResponseCode();
			if (code == 200) {
				String resultData = "";
				reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
				String inputLine = null;
				while (((inputLine = reader.readLine()) != null)) {
					resultData += inputLine;
				}
				reader.close();
				return resultData;
			}
		} catch (Exception e) {
			logger.error(e, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
			if (con != null) {
				con.disconnect();
			}
		}
		return null;
	}

	public static String sendSSLGetRequest(String uri) throws Exception {
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		String result = null;
		try {
			httpClient = SSLClient.getSSLClient(new DefaultHttpClient());
			HttpParams httpParams = httpClient.getParams();
			httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);// 请求连接超时时间
			httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);// 请求响应超时时间
			httpGet = new HttpGet(uri);
			httpGet.setHeader("Content-type", "application/json");
			HttpResponse response = httpClient.execute(httpGet);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "UTF-8");
				}
			}
		} finally {
			if (httpGet != null) {
				httpGet.abort();
			}
			if(httpClient != null && httpClient.getConnectionManager() != null ) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		return result;
	}
	
	public static String sendSSLXmlPost(String reqURL, String params) {
		return sendSSLPostRequest(reqURL, params, "text/xml");
	}
	
	public static String sendSSLJsonPost(String reqURL, String params) {
		return sendSSLPostRequest(reqURL, params, "application/json");
	}
}

