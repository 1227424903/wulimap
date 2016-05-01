package com.map.wulimap.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//下载  辅助
public class StreamTool {


    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
