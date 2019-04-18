package cn.com.infosec.netseal.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.com.infosec.netseal.common.log.LoggerUtil;

public class HttpClientUtil {

	public static String get(String url, String message) throws Exception {
		String result = null;
		try {

			if (url.startsWith("https")) {
				result = httpsPost(url, message);

			} else if (url.startsWith("http")) {
				result = httpPost(url, message);
			} else {
				throw new Exception("url error, " + url);
			}
		} catch (Exception e) {
			throw new Exception("http request error, " + e.getMessage());

		}
		return result;

	}

	public static boolean testUrl(String url) throws Exception {
		int code = 0;
		boolean res = false;
		try {
			if (url.startsWith("https")) {
				code = httpsGet(url);
			} else if (url.startsWith("http")) {
				code = httpGet(url);
			} else {
				throw new Exception("url error, " + url);
			}
			if (code == 404) {
				throw new Exception("error status:" + code);
			}
			res = true;
		} catch (Exception e) {
			LoggerUtil.errorlog("ca test error, ", e);
			throw new Exception("http request error " + e.getMessage());
		}
		return res;
	}

	private static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * POST方式请求服务器(https协议)
	 * 
	 * @param url
	 *            请求地址
	 * @param content
	 *            数据
	 * @param charset
	 *            编码
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws IOException
	 */
	public static String httpsPost(String url, String message) throws Exception {
		HttpsURLConnection conn = null;
		String result = null;
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

			URL console = new URL(url);
			conn = (HttpsURLConnection) console.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			// 设置属性
			conn.setRequestMethod("POST");
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(10000);
			conn.setDoOutput(true);
			conn.setDoInput(true);

			// 设置http请求头
			conn.setRequestProperty("Connection", "keep-alive"); // http1.1
			conn.setRequestProperty("Content-type", "application/json;charset=utf-8");

			// 把提交的数据以输出流的形式提交到服务器
			OutputStream os = conn.getOutputStream();
			os.write(message.getBytes("utf-8"));
			os.flush();
			os.close();
			// 通过响应码来判断是否连接成功
			if (conn.getResponseCode() == 200) {
				// 获得服务器返回的字节流
				InputStream is = conn.getInputStream();

				// 内存输出流,适合数据量比较小的字符串 和 图片
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int len = 0;
				while ((len = is.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				is.close();
				// 可使用 toByteArray() 和 toString() 获取数据。
				result = baos.toString();
			} else {
				throw new Exception("response code error, " + conn.getResponseCode());
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		return result;

	}

	public static String httpPost(String url, String message) throws Exception {
		HttpURLConnection conn = null;
		String result = null;
		try {
			URL console = new URL(url);
			conn = (HttpURLConnection) console.openConnection();
			// 设置属性
			conn.setRequestMethod("POST");
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(10000);
			conn.setDoOutput(true);
			conn.setDoInput(true);

			// 设置http请求头
			conn.setRequestProperty("Connection", "keep-alive"); // http1.1
			conn.setRequestProperty("Content-type", "application/json;charset=utf-8");

			// 把提交的数据以输出流的形式提交到服务器
			OutputStream os = conn.getOutputStream();
			os.write(message.getBytes("utf-8"));

			os.flush();
			os.close();
			// 通过响应码来判断是否连接成功
			if (conn.getResponseCode() == 200) {
				// 获得服务器返回的字节流
				InputStream is = conn.getInputStream();

				// 内存输出流,适合数据量比较小的字符串 和 图片
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int len = 0;
				while ((len = is.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				is.close();
				// 可使用 toByteArray() 和 toString() 获取数据。
				result = baos.toString();
			} else {
				throw new Exception("response code error, " + conn.getResponseCode());
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (conn != null)
				conn.disconnect();
		}

		return result;
	}

	public static int httpsGet(String url) throws Exception {
		int code = 0;
		HttpsURLConnection conn = null;
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

			URL console = new URL(url);
			conn = (HttpsURLConnection) console.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			// 设置属性
			conn.setRequestMethod("POST");
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(10000);

			// 设置http请求头
			conn.setRequestProperty("Connection", "keep-alive"); // http1.1
			conn.setRequestProperty("Content-type", "application/json;charset=utf-8");
			conn.connect();
			code = conn.getResponseCode();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (conn != null)
				conn.disconnect();
		}

		return code;
	}

	public static int httpGet(String url) throws Exception {
		int code = 0;
		HttpURLConnection conn = null;
		try {
			URL console = new URL(url);

			conn = (HttpURLConnection) console.openConnection();
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(10000);

			conn.setRequestProperty("Connection", "keep-alive"); // http1.1
			conn.setRequestProperty("Content-type", "application/json;charset=utf-8");
			conn.connect();

			code = conn.getResponseCode();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			if (conn != null)
				conn.disconnect();
		}

		return code;
	}

	public static void main(String[] args) throws Exception {
		// HttpClientUtil.setUrl("https://www.baidu.com");
		// HttpClientUtil.setUrl("http://192.168.2.205:8180/");
		String message = "{'authCode':'123456789123456789','certRequest':'MIHVMHwCAQAwHDEaMBgGA1UEAwwRcDEwXzE1MzgyOTIxMjEwNzIwWTATBgcqhkjOPQIBBggqgRzP\nVQGCLQNCAATJpll4ehM2T+uxEc+yUgTB9c/GdOyZyBs9yx4+78+CjuESdMyms/MYYxs6DNYpkWsd\nm6MNJXeL8sTe2wfiSCT2MAwGCCqBHM9VAYN1BQADRwAwRAIgpUzque9YP509iziXKJMWb9aeXQng\nlrGhmoho8LwCrM8CIAS+nSlcgeK2qOav53HMb0+S9RWyqflP1B9K3GGqQDl0','customer':{'address':'321','city':'123','contactphone':'321','county':'321','credentialsNumber':'321','credentialsType':'15','keyType':'SM2_S','mobilephone':'321','name':'321','org':'321','orgUnit':'321','province':'12','transactor':'222','transactorCredentialsNumber':'321','transactorCredentialsType':'10'},'projectId':'3','username':'test'}";
		String str = HttpClientUtil.get("https://www.yunnanca.net/ws/cert/signCertByP10", message);
		System.out.println(str);
	}

}
