package com.aichebaba.rooftop.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Http请求获取
 */
public class HttpTextUtil {
    public static String getHttpText(String url) {
        HttpClient http = new HttpClient();
        GetMethod get = new GetMethod(url);

        // 添加头信息告诉服务端可以对Response进行GZip压缩
        // get.setRequestHeader("Accept-Encoding", "gzip, deflate");
        try {
            int statusCode = http.executeMethod(get);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + get.getStatusLine());
            }

            // 打印解压后的返回信息
            return getRsString(get.getResponseBodyAsStream());
        } catch (Exception e) {
            System.err.println("页面无法访问");
            e.printStackTrace();
        } finally {
            get.releaseConnection();
        }
        return null;
    }

    /**
     * 获取httpclient请求返回的数据(用于替换使用BODY字符串方式获取带来的问题)
     *
     * @param inputStream
     * @return
     * @author zhangy
     */
    public static String getRsString(InputStream inputStream) {
        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuffer = new StringBuilder();
        String str = "";
        try {
            while ((str = br.readLine()) != null) {
                stringBuffer.append(str).append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

}
