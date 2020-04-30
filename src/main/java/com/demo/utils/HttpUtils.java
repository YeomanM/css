package com.demo.utils;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.zip.GZIPInputStream;

public class HttpUtils {
	public static final String UTF_CHARSET = "UTF-8";
	public static final String GBK_CHARSET = "GBK";
	private static List<String> userAgents = null;
	private static CookieManager manager = null;

	/**
	 * URLConnection没有自动管理cookies的功能，因此，需要借由CookieManager来管理，这点对那些需要cookie完成登录的网站而言，如新浪微博，
	 * 是非常重要的，如果没有设置的话，将登录失败
	 */
	static {
		manager = new CookieManager();
		CookieHandler.setDefault(manager);
	}

	static {
		userAgents = new ArrayList<String>();
		userAgents.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
		userAgents.add("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
		userAgents.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
		userAgents.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)");
		userAgents.add("Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11");
		userAgents.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon 2.0)");
		userAgents.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)");
		userAgents.add("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		userAgents.add(
				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
		userAgents.add(
				"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
		userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
		userAgents.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; TencentTraveler 4.0)");
		userAgents.add(
				"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)");
		userAgents.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
		userAgents.add(
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1 QQBrowser/6.9.11079.201");
		userAgents.add(
				"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.33 Safari/534.3 SE 2.X MetaSr 1.0");
		userAgents.add(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
	}

	public static HttpURLConnection openConnection(URL url, HttpProxy httpProxy) throws Exception {
		HttpURLConnection urlConn = null;

		//不需要代理
		if (httpProxy == null) {
			urlConn = (HttpURLConnection) url.openConnection();
		} else {
			// JDK 8u111版本后，目标页面为HTTPS协议，启用proxy用户密码鉴权
			System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

			//需要代理认证
			if (httpProxy.isAuthenticate()){
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(httpProxy.getUserName(), httpProxy.getPassWord().toCharArray());
					}
				});
			}

			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxy.getProxyIp(), httpProxy.getProxyPort()));
			urlConn = (HttpURLConnection) url.openConnection(proxy);
		}

//		if (urlConn instanceof HttpsURLConnection) {
//			//信任所有https请求
//			SSLContext context = SSLContext.getInstance("SSL");
//			TrustManager[] tms = {new NothingX509TrustManager()};
//			context.init(null, tms, new SecureRandom());
//			SSLSocketFactory factory = context.getSocketFactory();
//			((HttpsURLConnection) urlConn).setSSLSocketFactory(factory);
//		}

		return urlConn;
	}

	public static HttpURLConnection openConnection(String urlStr, HttpProxy httpProxy) throws Exception {
		URL url = new URL(urlStr);
		return (HttpURLConnection) openConnection(url, httpProxy);
	}

	public static String httpGetRequest(String urlStr, HttpProxy httpProxy) throws Exception {
		return httpGetRequest(urlStr, httpProxy, null);
	}

	public static String httpGetRequest(String urlStr, HttpProxy httpProxy, Properties requestHeaders)
			throws Exception {
		return httpGetRequest(urlStr, httpProxy, requestHeaders, UTF_CHARSET);
	}

	public static String httpGetRequest(String urlStr, HttpProxy httpProxy, Properties requestHeaders,
                                        String defaultResponseCharset) throws Exception {
		return httpGetRequest(openConnection(urlStr, httpProxy), requestHeaders, defaultResponseCharset);
	}

	public static String httpGetRequest(String urlStr, HttpProxy httpProxy, int timeout) throws Exception {
		return httpGetRequest(urlStr, httpProxy, null, timeout);
	}

	public static String httpGetRequest(String urlStr, HttpProxy httpProxy,
			Properties requestHeaders, int timeout)
			throws Exception {
		return httpGetRequest(urlStr, httpProxy, requestHeaders, UTF_CHARSET, timeout);
	}

	
	public static String httpGetRequest(String urlStr, HttpProxy httpProxy, Properties requestHeaders,
                                        String defaultResponseCharset, int timeout) throws Exception {
		return httpGetRequest(openConnection(urlStr, httpProxy), requestHeaders, defaultResponseCharset, timeout);
	}
	

	public static String httpGetRequest(HttpURLConnection conn, Properties requestHeaders,
			String defaultCharset) throws Exception {
		return httpGetRequest(conn, requestHeaders, defaultCharset, 60);
	}
	
	public static String httpGetRequest(HttpURLConnection conn, Properties requestHeaders,
			String defaultCharset, int timeout) throws Exception {
		BufferedReader reader = null;

		try {
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(timeout * 1000);
			conn.setReadTimeout(timeout * 1000);
			
			if (null != requestHeaders) {
				for (Object key : requestHeaders.keySet()) {
					conn.setRequestProperty((String) key, (String) requestHeaders.get(key));
				}
			}

			return readContentFromConn(conn, defaultCharset);
		} finally {
			IOUtils.closeQuietly(reader);
//			reader.close();

			if (null != conn) {
				conn.disconnect();
			}
		}
	}

	public static String httpPostRequest(String urlStr, HttpProxy httpProxy) throws Exception {
		return httpPostRequest(urlStr, httpProxy, null);
	}
	
	public static String httpPostRequest(String urlStr, HttpProxy httpProxy, int timeout) throws Exception {
		return httpPostRequest(urlStr, httpProxy, null, timeout);
	}
	
	public static String httpPostRequest(String urlStr, HttpProxy httpProxy,
			Properties requestBodies)throws Exception {
		return httpPostRequest(urlStr, httpProxy, requestBodies, 60);
	}

	public static String httpPostRequest(String urlStr, HttpProxy httpProxy,
			Properties requestBodies, int timeout)
			throws Exception {
		return httpPostRequest(urlStr, httpProxy, null, requestBodies, timeout);
	}

	public static String httpPostRequest(String urlStr, HttpProxy httpProxy, Properties requestHeaders,
                                         Properties contentProps, int timeout) throws Exception {
		StringBuilder sendContentBuf = new StringBuilder();

		if (null != contentProps) {
			for (Object key : contentProps.keySet()) {
				sendContentBuf.append(String.valueOf(key)).append("=");
				sendContentBuf.append(contentProps.get(key)).append("&");
			}

			sendContentBuf.deleteCharAt(sendContentBuf.lastIndexOf("&"));
		}

		return httpPostRequest(urlStr, httpProxy, requestHeaders, sendContentBuf.toString(), timeout);
	}

	public static String httpPostRequest(String urlStr, HttpProxy httpProxy, Properties requestHeaders,
                                         String sendContent) throws Exception {
		return httpPostRequest(urlStr, httpProxy, requestHeaders, sendContent, 60);
	}
	
	public static String httpPostRequest(String urlStr, HttpProxy httpProxy, Properties requestHeaders,
                                         String sendContent, int timeout) throws Exception {
		HttpURLConnection conn = openConnection(urlStr, httpProxy);
		return httpPostRequest(conn, requestHeaders, sendContent, timeout);
	}
	
	public static String httpPostRequest(HttpURLConnection conn, Properties requestHeaders, 
			String sendContent) throws Exception{
		return httpPostRequest(conn, requestHeaders, sendContent, 60);
	}
	
	public static String httpPostRequest(HttpURLConnection conn, Properties requestHeaders, 
			String sendContent, int timeout)
			throws Exception {
		BufferedReader reader = null;
		OutputStream outputStream = null;

		try {
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(timeout * 1000);
			conn.setReadTimeout(timeout * 1000);
			conn.setDoOutput(true);// 是否输入参数

			if (null != requestHeaders) {
				for (Object key : requestHeaders.keySet()) {
					conn.setRequestProperty((String) key, (String) requestHeaders.get(key));
				}
			}

			outputStream = conn.getOutputStream();
			outputStream.write(sendContent.getBytes());
			outputStream.flush();

			return readContentFromConn(conn, UTF_CHARSET);
		} finally {
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(reader);
			
			if (null != conn) {
				conn.disconnect();
			}
		}
	}
	
	private static String readContentFromConn(HttpURLConnection conn, String defaultCharset) throws Exception {
		String contentEncoding = conn.getContentEncoding();
		String content = "";
		String charSet = defaultCharset;

		String contentType = conn.getContentType();
		boolean isNeedChangeCharset = false; // 是否需要重新编码

		if (StringUtils.isEmpty(contentType)) {
			isNeedChangeCharset = true;
		} else {
			charSet = fetchCharset(contentType);

			// 如果是默认字符集,则重新编码
			if (StringUtils.isBlank(charSet)) {
				isNeedChangeCharset = true;
				charSet = defaultCharset;
			}
		}

		byte[] byteArray = null;

		if (null != contentEncoding && contentEncoding.toLowerCase().contains("gzip")) {
			InputStream inputStream = null;
			GZIPInputStream gzipStream = null;
			
			try {
				inputStream = conn.getInputStream();
				gzipStream = new GZIPInputStream(inputStream);
				byteArray = IOUtils.toByteArray(gzipStream);
			} finally {
				IOUtils.closeQuietly(gzipStream);
				IOUtils.closeQuietly(inputStream);
			}
		} else {
			InputStream inputStream = null;
			
			try {
				inputStream = conn.getInputStream();
				byteArray = IOUtils.toByteArray(inputStream);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}

		content = new String(byteArray, charSet);
		
		// 根据内容,重新编码
		if (isNeedChangeCharset) {
			String newCharset = RegexUtils.extractData(content, "<meta[^>]*?charset=([\"\\w\\-]*)")
					.replaceAll("\"", "").trim();
			if (StringUtils.isNotBlank(newCharset)) {
				content = new String(byteArray, newCharset);
			}
		}

		int resCode = conn.getResponseCode();

		if (resCode != HttpURLConnection.HTTP_OK) {
			throw new Exception("http code: " + resCode + "\n" + content);
		}

		return content;
	}
	
	public static void downloadFromUrl(String urlStr, String filePath, String fileName, HttpProxy httpProxy) throws Exception {
		HttpURLConnection conn = openConnection(urlStr, httpProxy);
		downloadFromUrl(conn, filePath, fileName);
	}
	
	public static void downloadFromUrl(HttpURLConnection conn, String filePath, String fileName) throws Exception {
		InputStream inputStream = null;
		
		try {
			conn.setConnectTimeout(30 * 1000);
			conn.setRequestProperty("User-Agent", getRandomUserAgent());
			inputStream = conn.getInputStream();
			byte[] byteDatas = IOUtils.toByteArray(inputStream);
			FileUtils.writeByteArrayToFile(new File(filePath, fileName), byteDatas);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	public static List<HttpCookie> httpCookiesGet(String urlStr, HttpProxy httpProxy) throws Exception {
		return httpCookiesGet(urlStr, httpProxy, null);
	}

	public static List<HttpCookie> httpCookiesGet(String urlStr, HttpProxy httpProxy,
			Properties requestHeaders) throws Exception {
		HttpURLConnection conn = openConnection(urlStr, httpProxy);
		return httpCookiesGet(conn, requestHeaders);
	}

	public static List<HttpCookie> httpCookiesGet(HttpURLConnection conn, Properties requestHeaders) throws Exception {
		BufferedReader reader = null;

		try {
			HttpURLConnection.setFollowRedirects(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(60 * 1000);
			conn.setReadTimeout(60 * 1000);

			if (null != requestHeaders) {
				for (Object key : requestHeaders.keySet()) {
					conn.setRequestProperty((String) key, (String) requestHeaders.get(key));
				}
			}

			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder strBuf = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}

			CookieStore cookieJar = manager.getCookieStore();
			return cookieJar.getCookies();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}

			if (null != conn) {
				conn.disconnect();
			}
		}
	}

	public static String fetchRealUrl(String urlStr, HttpProxy httpProxy) throws Exception {
		return fetchRealUrl(urlStr, httpProxy, null);
	}

	public static String fetchRealUrl(String urlStr, HttpProxy httpProxy, Properties requestHeaders)
			throws Exception {
		return fetchRealUrl(openConnection(urlStr, httpProxy), requestHeaders);
	}

	public static String fetchRealUrl(HttpURLConnection conn, Properties requestHeaders) throws Exception {
		try {
			conn.setConnectTimeout(60 * 1000);
			conn.setReadTimeout(60 * 1000);

			if (null != requestHeaders) {
				for (Object key : requestHeaders.keySet()) {
					conn.setRequestProperty((String) key, (String) requestHeaders.get(key));
				}
			}

			conn.getResponseCode();
			return conn.getURL().toString();
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
		}

	}

	public static String getRandomUserAgent() {
		int randomValue = new Random().nextInt(999999) + 1;
		return userAgents.get(randomValue % userAgents.size());
	}

	public static String fetchCharset(String contentType) {
		String charSet = RegexUtils.extractData(contentType, "charset=(.+)$");
		return charSet;
	}

	public static String appendUrlParameter(String url, String parameter) {
		String result = url;

		if (url.contains("?")) {
			result = result + "&" + parameter;
		} else {
			result = result + "?" + parameter;
		}

		return result;
	}

	public static boolean isHttpsUrl(URL url) {
		if (url.getProtocol().equalsIgnoreCase("https"))
			return true;
		return false;
	}

	public static boolean isHttpUrl(URL url) {
		if (url.getProtocol().equalsIgnoreCase("http"))
			return true;
		return false;
	}

	public static boolean isHttpsUrl(String url) {
		try {
			return isHttpsUrl(new URL(url));
		} catch (MalformedURLException e) {
			return false;
		}
	}

	public static boolean isHttpUrl(String url) {
		try {
			return isHttpUrl(new URL(url));
		} catch (MalformedURLException e) {
			return false;
		}
	}

}
