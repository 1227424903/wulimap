package com.map.wulimap.Activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.support.v7.app.AppCompatActivity;

import com.map.wulimap.R;
import com.map.wulimap.util.DownloadUtil;
import com.map.wulimap.util.FileUtil;
import com.map.wulimap.util.HtmlService;


public class MainActivity extends AppCompatActivity {
    //初始化变量
    ImageView imageView;
    String a = "";
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
                    a = HtmlService.getHtml("http://wode123123.sinaapp.com/10.php");
                    a = a.trim();
                } catch (Exception e) {
                }
                handler.sendEmptyMessageDelayed(2, 50);
            }
        }.start();
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
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
                case 1:
                    //第一次
                    MainActivity.this.startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                    MainActivity.this.finish();
                    break;
                case 2: {
                    //图片处理  第一次处理
                    preferences = getSharedPreferences("qd", MODE_PRIVATE);
                    editor = preferences.edit();
                    int qd = preferences.getInt("qd", 0);
                    String fm = preferences.getString("fm", null);
                    if (a.equals(fm)) {
                    } else {
                        if (!FileUtil.wenjianshifoucunzai("/sdcard/map")) {
                            FileUtil.chuanjianmulu("/sdcard/map");
                        }
                        DownloadUtil down = new DownloadUtil();
                        down.downloadApk("qidong.jpg", "http://1.wode123123.sinaapp.com/photo/" + a + ".jpg", "/sdcard/map/");
                        editor.putString("fm", a);
                        editor.commit();
                    }

                    if (qd == 1) {
                        handler.sendEmptyMessageDelayed(0, 3000);
                    } else {
                        editor.putInt("qd", 1);
                        editor.commit();
                        handler.sendEmptyMessageDelayed(1, 3000);
                    }
                    break;
                }
            }
        }

        ;
    };

}



