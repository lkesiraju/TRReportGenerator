package com.sony.test.report;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * Sets up the HTTP request
 */
public class JenkinsClient {

	private static final Log LOGGER = LogFactory.getLog(JenkinsClient.class);
	
	private static final String ERROR_RESPONSE = "CLIENT_ERROR";
	
	private HttpClient client = null;
	
	public JenkinsClient(){
		client = new DefaultHttpClient();
	}

	public String getDataFromJenkins(URI uri)
	 {
		try{
			ignoreSslCertificationVerification();
			HttpResponse response = client.execute(new HttpGet(uri));
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LOGGER.error(ERROR_RESPONSE, e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(ERROR_RESPONSE, e);
			e.printStackTrace();
		}
		return null;
	 }

	public void closeConnection(){
		client.getConnectionManager().shutdown();
	}
	/**
	 * Ignore verification of SSL Server Certification. THIS IS FOR DEVELOPMENT
	 * ONLY. DO NOT USE THIS METHOD FOR PRODUCTION PURPOSE.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void ignoreSslCertificationVerification() {
		final KeyManager[] km = null;

		final TrustManager[] tm = { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };

		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(km, tm, null);
			LayeredSocketFactory sf = null;
			sf = new CustomSSLSocketFactory(context);
			Scheme sch = new Scheme("https", sf, 443);
			client.getConnectionManager().getSchemeRegistry().register(sch);
		}

		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Custom SSLSoscket factory class to ignore SSL Verification
	 * 
	 * 
	 */
	private class CustomSSLSocketFactory extends SSLSocketFactory {
		private SSLContext context = null;

		public CustomSSLSocketFactory(SSLContext context) {
			super(context);
			this.context = context;
		}

		public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
				throws IOException, UnknownHostException {
			return this.context.getSocketFactory().createSocket(host, port);
		}

		public Socket createSocket() throws IOException {
			return this.context.getSocketFactory().createSocket();
		}
	}
}
