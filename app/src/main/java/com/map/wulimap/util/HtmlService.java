package com.map.wulimap.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


//get操作
public class HtmlService {
    public static String getHtml(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream inStream = conn.getInputStream();
        byte[] data = StreamTool.readInputStream(inStream);
        String html = new String(data, "utf-8");
        return html;
    }

}
