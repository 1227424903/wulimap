package com.map.wulimap.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class LoadAndSaveImage {
    ImageView view;
    Context mContext;
    private String apkNames;
    private int progress;
    private String apkUrl;
    private String savePath = "";
    private String saveFileName = "";
    private int apkCurrentDownload;
    private boolean interceptFlag = false;
    private int apkLength;

    public LoadAndSaveImage(Context context, ImageView view, String apkNames, String apkUrl, String path) {
        // TODO Auto-generated constructor stub
        this.view = view;
        mContext = context;
        this.apkNames = apkNames;
        this.apkUrl = apkUrl;
        this.savePath = path;
        this.saveFileName = savePath + apkNames;
        new Load_and_save_image().execute(apkUrl);
    }


    class Load_and_save_image extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... sUrl) {
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
            return null;
        }

        protected void onPostExecute(Void result) {
            Drawable d = Drawable.createFromPath(saveFileName);
            view.setImageDrawable(d);
        }

        ;
    }

}