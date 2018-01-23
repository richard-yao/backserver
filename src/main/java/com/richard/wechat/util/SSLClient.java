package com.richard.wechat.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
/**
 * @author YaoXiansheng
 * @date 2018年1月21日
 * @title SSLClient
 * @todo TODO
 */

@SuppressWarnings("deprecation")
public class SSLClient {

	public static HttpClient getSSLClient(HttpClient base) throws Exception {
		SSLContext ctx = SSLContext.getInstance("TLSv1.1");
		X509TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		ClientConnectionManager ccm = base.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", ssf, 443));
		return new DefaultHttpClient(ccm, base.getParams());
	}
}
