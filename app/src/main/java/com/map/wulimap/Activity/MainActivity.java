package com.map.wulimap.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.support.v7.app.AppCompatActivity;

import com.map.wulimap.R;
import com.map.wulimap.util.Constant;
import com.map.wulimap.util.DownloadUtil;
import com.map.wulimap.util.FileUtil;
import com.map.wulimap.util.HtmlService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;


public class MainActivity extends AppCompatActivity {
    //初始化变量
    private ImageView imageView;
    private String getjieguo1;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initsetting();
        initview();

    }


    public void initsetting() {
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_main);
    }


    public void initview() {
//显示图片
        if (FileUtil.wenjianshifoucunzai("/sdcard/map/qidong.jpg")) {
            imageView = (ImageView) findViewById(R.id.qidongye);
            Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/map/qidong.jpg");
            imageView.setImageBitmap(bitmap);
        }

//获取图片名
        new Thread() {
            public void run() {
                try {
                    getjieguo1 = HtmlService.getHtml(Constant.PHP_URL + "fengmian.php");
                    getjieguo1 = getjieguo1.trim();
                } catch (Exception e) {
                }
                if (getjieguo1 == null || getjieguo1 == "") {
                    getjieguo1 = "qq";
                }
                //图片处理  第一次处理
                preferences = getSharedPreferences("qd", MODE_PRIVATE);
                editor = preferences.edit();
                String qd = preferences.getString("qd", null);
                String fm = preferences.getString("fm", null);
                if (getjieguo1.equals(fm)) {
                } else {
                    if (!FileUtil.wenjianshifoucunzai("/sdcard/map")) {
                        FileUtil.chuanjianmulu("/sdcard/map");
                    }
                    DownloadUtil down = new DownloadUtil();
                    down.downloadApk("qidong.jpg", Constant.PHP_URL + "photo/" + getjieguo1 + ".jpg", "/sdcard/map/");
                    editor.putString("fm", getjieguo1);
                    editor.commit();
                }

                if ("1".equals(qd)) {
                    handler.sendEmptyMessageDelayed(1, 3000);
                } else {
                    editor.putString("qd", "1");
                    editor.commit();
                    handler.sendEmptyMessageDelayed(2, 3000);
                }
            }
        }.start();
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //是否登陆过
                    preferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                    if ("1".equals(preferences.getString("biaoji", null))) {
                        MainActivity.this.startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        MainActivity.this.finish();
                    } else {
                        MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        MainActivity.this.finish();
                    }
                    break;
                case 2:
                    //第一次
                    MainActivity.this.startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    MainActivity.this.finish();
                    break;


            }
        }

        ;
    };


}



