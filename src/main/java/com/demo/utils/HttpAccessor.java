package com.demo.utils;

import java.io.File;
import java.net.HttpCookie;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

public class HttpAccessor {
	public static HttpProxy proxy;

    public static String doAttemptHttpGet(String url) throws Exception {
    	return doAttemptHttpGet(url, 60);
    }
    
    public static String doAttemptHttpGet(String url, int timeout) throws Exception {
    	return doAttemptHttpGet(url, null, timeout);
    }
    
    public static String doAttemptHttpGet(String url, Properties requestProps) throws Exception {
    	return doAttemptHttpGet(url, requestProps, 60);
    }
    
    public static synchronized String doAttemptHttpGet(String url, Properties requestProps, int timeout) throws Exception {
    	int count = 1;
		Exception curExp = null;
		String htmlPage = null;

		/**
		 * 在失败的情况下，允许3次获取html页面数据的机会
		 */
		while (count <= 3) {
			try {
				curExp = null;
				htmlPage = httpGetRequest(url, requestProps, timeout);
				break; // 获取成功的话，直接跳出循环
			} catch (Exception e) {
				curExp = e;
				count++;

				try {
					Thread.sleep(1000); // 睡眠1秒，之后再重新获取
				} catch (InterruptedException ee) {

				}
			}
		}

		if (null != curExp) {
			throw new Exception(url, curExp);
		}

		return htmlPage;
    }
    
    public static String httpGetRequest(String urlStr) throws Exception {
		return httpGetRequest(urlStr, null);
	}

	public static String httpGetRequest(String urlStr, Properties requestHeaders) throws Exception {
		return httpGetRequest(urlStr, requestHeaders, HttpUtils.UTF_CHARSET);
	}

	public static String httpGetRequest(String urlStr,
										Properties requestHeaders, String defaultResponseCharset) throws Exception {
		return httpGetRequest(urlStr, requestHeaders, defaultResponseCharset, 60);
	}

	public static String httpGetRequest(String urlStr, Properties requestHeaders, int timeout) throws Exception {
		return httpGetRequest(urlStr, requestHeaders, HttpUtils.UTF_CHARSET, timeout);
	}

	public static String httpGetRequest(String urlStr,
										Properties requestHeaders, String defaultResponseCharset, int timeout) throws Exception {
		return HttpUtils.httpGetRequest(urlStr, proxy, requestHeaders, defaultResponseCharset, timeout);
	}

    public static String httpGetRequest(String urlStr, int timeout) throws Exception {
		return httpGetRequest(urlStr, null, timeout);
	}
	
	public static String httpPostRequest(String urlStr) throws Exception {
		return httpPostRequest(urlStr, 60);
	}
	
	public static String httpPostRequest(String urlStr, int timeout) throws Exception {
		return httpPostRequest(urlStr, null, timeout);
	}
	
	public static String httpPostRequest(String urlStr,
			Properties requestBodies, int timeout) throws Exception {
		return httpPostRequest(urlStr, null, requestBodies, timeout);
	}
	
	public static String httpPostRequest(String urlStr, 
			Properties requestHeaders, Properties contentProps) throws Exception {
		return httpPostRequest(urlStr, requestHeaders, contentProps, 60);
	}
	
	public static String httpPostRequest(String urlStr, 
			Properties requestHeaders, Properties contentProps, int timeout) throws Exception {
		return HttpUtils.httpPostRequest(urlStr, proxy, requestHeaders, contentProps, timeout);
	}
	
	public static String httpPostRequest(String urlStr, 
			Properties requestHeaders, String sendContent) throws Exception {
		return httpPostRequest(urlStr, requestHeaders, sendContent, 60);
	}
	
	public static String httpPostRequest(String urlStr, 
			Properties requestHeaders, String sendContent, int timeout) throws Exception {
		return HttpUtils.httpPostRequest(urlStr, proxy, requestHeaders, sendContent, timeout);
	}
	
	public static String doAttemptHttpPost(String url, Properties requestHeaders, String sendContent) throws Exception {
	    return doAttemptHttpPost(url, requestHeaders, sendContent, 60);
	}
	
	public static synchronized String doAttemptHttpPost(String url, Properties requestHeaders, String sendContent, int timeout) throws Exception {
    	int count = 1;
		Exception curExp = null;
		String htmlPage = null;

		/**
		 * 在失败的情况下，允许3次获取html页面数据的机会
		 */
		while (count <= 3) {
			try {
				curExp = null;
				htmlPage = httpPostRequest(url, requestHeaders, sendContent, timeout);
				break; // 获取成功的话，直接跳出循环
			} catch (Exception e) {
				curExp = e;
				count++;

				try {
					Thread.sleep(1000); // 睡眠1秒，之后再重新获取
				} catch (InterruptedException ee) {

				}
			}
		}

		if (null != curExp) {
			throw new Exception(url, curExp);
		}

		return htmlPage;
    }
	
	public static void downloadFromUrl(String urlStr, String filePath, String fileName) throws Exception {
		HttpUtils.downloadFromUrl(urlStr, filePath, fileName, proxy);
	}
	
	public static List<HttpCookie> httpCookiesGet(String urlStr) throws Exception {
		return httpCookiesGet(urlStr, null);
	}
	
	public static List<HttpCookie> httpCookiesGet(String urlStr,
			Properties requestHeaders) throws Exception {
		return HttpUtils.httpCookiesGet(urlStr, proxy, requestHeaders);
	}
	
	public static String httpCookies(String urlStr) throws Exception {
		return httpCookies(urlStr, null);
    }
	
	public static String httpCookies(String urlStr, Properties requestHeaders) throws Exception {
		List<HttpCookie> cookies = httpCookiesGet(urlStr, requestHeaders);
		StringBuilder strBuf = new StringBuilder();
		
		for (HttpCookie cookie : cookies) {
			strBuf.append(cookie.getName() + "=" + cookie.getValue());
			strBuf.append("; ");
		}
		
		strBuf.deleteCharAt(strBuf.lastIndexOf(";"));
		return strBuf.toString();
	}
	
	public static String fetchRealUrl(String urlStr) throws Exception {
		return fetchRealUrl(urlStr, null);
	}
	
	public static String fetchRealUrl(String urlStr, Properties requestHeaders) throws Exception {
		return HttpUtils.fetchRealUrl(urlStr, proxy, requestHeaders);
	}
	
	public static URLConnection openConnection(String urlStr) throws Exception {
		return HttpUtils.openConnection(urlStr, proxy);
	}
    
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String url = "https://item.jd.com/3141248.html";
        System.out.println(HttpAccessor.httpGetRequest(url));
	}

	public static void setProxy(HttpProxy proxy) {
		HttpAccessor.proxy = proxy;
	}

	public static String getProxyIp() {
		return HttpAccessor.proxy.getProxyIp();
	}

	public static void setProxyIp(String proxyIp) {
		HttpAccessor.proxy.setProxyIp(proxyIp);
	}

	public static int getProxyPort() {
		return HttpAccessor.proxy.getProxyPort();
	}

	public static void setProxyPort(int proxyPort) {
		HttpAccessor.proxy.setProxyPort(proxyPort);
	}
}
