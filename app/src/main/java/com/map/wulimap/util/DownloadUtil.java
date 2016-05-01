package com.map.wulimap.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//下载文件工具
public class DownloadUtil {
    private String apkNames;
    private String apkUrl;
    private String savePath = "";
    private String saveFileName = "";
    private int apkCurrentDownload;
    private int progress;
    private Thread downLoadThread;
    private boolean interceptFlag = false;
    public boolean isdown = false;
    private int apkLength;

    public void downloadApk(String apkNames, String apkUrl, String path) {
        this.apkNames = apkNames;
        this.apkUrl = apkUrl;
        this.savePath = path;
        this.saveFileName = savePath + apkNames;
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }


    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                apkLength = conn.getContentLength();
                System.out.println();
                InputStream is = conn.getInputStream();
                apkCurrentDownload = 0;
                byte buf[] = new byte[1024];
                int length = -1;
                while ((length = is.read(buf)) != -1) {
                    apkCurrentDownload += length;
                    progress = (int) (((float) apkCurrentDownload / apkLength) * 100);


                    fos.write(buf, 0, length);
                    if (apkCurrentDownload == apkLength) {


                        break;
                    }
                    if (interceptFlag) {
                        ApkFile.delete();
                        break;
                    }
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


}