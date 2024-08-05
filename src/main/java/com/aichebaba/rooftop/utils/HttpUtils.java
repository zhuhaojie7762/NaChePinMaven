package com.aichebaba.rooftop.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    private static Logger log = LoggerFactory.getLogger(HttpUtils.class);
    private static final String line_break = "\r\n";

    public static String post(String urlStr, Map<String, String> textMap) {
        return postForm(urlStr, textMap, null);
    }

    public static String postJson(String urlStr, Map<String, String> textMap, String json) {
        HttpURLConnection conn;
        OutputStream out = null;
        BufferedReader reader = null;
        try {
            StringBuilder sbUrl = new StringBuilder(urlStr);
            if (textMap != null) {
                for (Map.Entry<String, String> entry : textMap.entrySet()) {
                    if (sbUrl.indexOf("?") != -1) {
                        sbUrl.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                    } else {
                        sbUrl.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
            }
            System.out.println("request url: " + sbUrl);
            URL url = new URL(sbUrl.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            out = new DataOutputStream(conn.getOutputStream());

            out.write(json.getBytes());
            log.info("post json : " + json);
            out.flush();
            out.close();

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            reader.close();
            log.info("response : " + sb);
            System.out.println("response: " + sb);
            return sb.toString();

        } catch (IOException e) {
            log.error("", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }

        return null;
    }

    public static String postForm(String urlStr, Map<String, String> textMap, Map<String, String> fileMap) {

        HttpURLConnection conn;
        String BOUNDARY = "---------------------------123821742118716";
        OutputStream out = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
            out = new DataOutputStream(conn.getOutputStream());

            if (textMap != null) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : textMap.entrySet()) {
                    sb.append(line_break).append("--").append(BOUNDARY).append(line_break);
                    sb.append("Content-Disposition: from-data; name=\"").append(entry.getKey()).append("\"")
                            .append(line_break).append(line_break);
                    sb.append(entry.getValue());
                }
                out.write(sb.toString().getBytes());
                log.debug("text params : " + sb);
            }

            if (fileMap != null) {
                for (Map.Entry<String, String> entry : fileMap.entrySet()) {
                    if (entry.getValue() != null) {
                        File file = new File(entry.getValue());
                        String filename = file.getName();
                        String contentType = new MimetypesFileTypeMap().getContentType(file);
                        if (filename.endsWith(".png")) {
                            contentType = "image/png";
                        }
                        if (contentType == null || "".equals(contentType)) {
                            contentType = "application/octet-stream";
                        }
                        StringBuilder sb = new StringBuilder();
                        sb.append(line_break).append("--").append(BOUNDARY).append(line_break);
                        sb.append("Content-Disposition: form-data; name=\"").append(entry.getKey())
                                .append("\"; filename=\"").append(filename).append("\"").append(line_break);
                        sb.append("Content-Type:").append(contentType).append(line_break).append(line_break);
                        out.write(sb.toString().getBytes());

                        DataInputStream in = new DataInputStream(new FileInputStream(file));
                        int bytes;
                        byte[] bufferOut = new byte[1025];
                        while ((bytes = in.read(bufferOut)) != -1) {
                            out.write(bufferOut, 0, bytes);
                        }
                        in.close();
                    }
                }
            }

            byte[] endData = (line_break + "--" + BOUNDARY + "--" + line_break).getBytes();
            out.write(endData);
            out.flush();
            out.close();

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            reader.close();
            log.debug("response: " + sb);
            return sb.toString();

        } catch (IOException e) {
            log.error("", e);
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }

        return null;

    }

    public static String get(String urlStr, Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            StringBuilder sb = new StringBuilder(urlStr);
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (sb.indexOf("?") != -1) {
                        sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                    } else {
                        sb.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    }
                }
            }
            URL realUrl = new URL(sb.toString());
            System.out.println("request url: " + sb);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response: " + result);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
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
        return result.toString();
    }
}