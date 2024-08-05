package com.aichebaba.rooftop.utils;

import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author user
 * 
 */
public class HttpKit {

	private static final String DEFAULT_CHARSET = "UTF-8";

	 /**
     * 发送Get请求
     * @param url
     * @return
     */
	public static String get(String url) {
		StringBuffer bufferRes = null;
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象  
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL urlGet = new URL(url);
			HttpsURLConnection http = (HttpsURLConnection) urlGet
					.openConnection();
			// 连接超时
			http.setConnectTimeout(25000);
			// 读取超时 --服务器响应比较慢，增大时间
			http.setReadTimeout(25000);
			http.setRequestMethod("GET");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setSSLSocketFactory(ssf);
			http.setDoOutput(true);
			http.setDoInput(true);
			http.connect();

			InputStream in = http.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in,
					DEFAULT_CHARSET));
			String valueString = null;
			bufferRes = new StringBuffer();
			while ((valueString = read.readLine()) != null) {
				bufferRes.append(valueString);
			}
			in.close();
			if (http != null) {
				// 关闭连接
				http.disconnect();
			}
			return bufferRes.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 发送Get请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String get(String url, Map<String, String> params) {
		return get(initParams(url, params));
	}

	/**
	 * 发送Post请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, String params) {
		StringBuffer bufferRes = null;
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象  
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL urlGet = new URL(url);
			HttpsURLConnection http = (HttpsURLConnection) urlGet
					.openConnection();
			// 连接超时
			http.setConnectTimeout(25000);
			// 读取超时 --服务器响应比较慢，增大时间
			http.setReadTimeout(25000);
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			http.setSSLSocketFactory(ssf);
			http.setDoOutput(true);
			http.setDoInput(true);
			http.connect();

			OutputStream out = http.getOutputStream();
			out.write(params.getBytes("UTF-8"));
			out.flush();
			out.close();

			InputStream in = http.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in,
					DEFAULT_CHARSET));
			String valueString = null;
			bufferRes = new StringBuffer();
			while ((valueString = read.readLine()) != null) {
				bufferRes.append(valueString);
			}
			in.close();
			if (http != null) {
				 // 关闭连接
				http.disconnect();
			}
			return bufferRes.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String post2(String url,Object param) throws Exception{
		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
	}
	
	  public static String sendGet(String url, String param) {
	        String result = "";
	        BufferedReader in = null;
	        try {
	            String urlNameString = url + "?" + param;
	            URL realUrl = new URL(urlNameString);
	            // 打开和URL之间的连接
	            URLConnection connection = realUrl.openConnection();
	            // 设置通用的请求属性
	            connection.setRequestProperty("accept", "*/*");
	            connection.setRequestProperty("connection", "Keep-Alive");
	            connection.setRequestProperty("user-agent",
	                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	            // 建立实际的连接
	            connection.connect();
	            // 获取所有响应头字段
	            Map<String, List<String>> map = connection.getHeaderFields();
	            // 遍历所有的响应头字段
//	            for (String key : map.keySet()) {
//	                System.out.println(key + "--->" + map.get(key));
//	            }
	            // 定义 BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(new InputStreamReader(
	                    connection.getInputStream()));
	            String line;
	            while ((line = in.readLine()) != null) {
	                result += line;
	            }
	        } catch (Exception e) {
//	            System.out.println("发送GET请求出现异常！" + e);
	            e.printStackTrace();
	        }
	        // 使用finally块来关闭输入流
	        finally {
	            try {
	                if (in != null) {
	                    in.close();
	                }
	            } catch (Exception e2) {
	                e2.printStackTrace();
	            }
	        }
	        return result;
	    }

	
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            /*conn.setRequestProperty("accept", "**");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");*/
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    

	/**
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private static String initParams(String url, Map<String, String> params) {
		if (null == params || params.isEmpty()) {
			return url;
		}
		StringBuilder sb = new StringBuilder(url);
		if (url.indexOf("?") == -1) {
			sb.append("?");
		} else {
			sb.append("&");
		}
		boolean first = true;
		for (Entry<String, String> entry : params.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append("&");
			}
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append("=");
			if (StringUtils.isNotEmpty(value)) {
				try {
					sb.append(URLEncoder.encode(value, DEFAULT_CHARSET));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}


/**
 * 证书管理
 */
class MyX509TrustManager implements X509TrustManager {

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}
}
