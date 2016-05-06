package com.map.wulimap.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.map.wulimap.listener.QQShareListener;
import com.map.wulimap.R;
import com.map.wulimap.util.GetRoundedBitmapUtil;
import com.map.wulimap.util.LoadAndSaveImage;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.util.DownloadUtil;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.FileUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class MyYoujiDetailActivity extends AppCompatActivity {
    //初始化变量
    String youjiid;
    String shoujihao;
    String nicheng;
    String shijian;
    String didian;
    String index;
    String pinglunshu;
    String zanshu;
    String tupian;
    String neirong;
    String jinwei;
    String tupianbianmaming;
    String getjieguo;
    String zanderen = "";
//初始化控件

    LinearLayout linearLayout1;
    TextView zan;
    MaterialDialog progDialog;
    BottomSheet sheet;
    Tencent mTencent;
    QQShareListener myListener;
    //初始化分享控件
    private String appid = "wxc7cc4ae85bebdd30"; // 官网获得的appId
    private IWXAPI wxApi;// 第三方app和微信通讯的openapi接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_youji_detail);

        initsetting();
        initdate();
        initview();
//获取数据
        huoqushuju();
    }


    public void initview() {
//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = getIntent();
                intent.putExtra("keyword", "");
                MyYoujiDetailActivity.this.setResult(0, intent);
                MyYoujiDetailActivity.this.finish();
            }

        });

//图片处理
        final ImageView imageView = (ImageView) findViewById(R.id.tupian);
        if (FileUtil.wenjianshifoucunzai("/sdcard/map/" + tupian + "-yasuo.jpg")) {
            Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/map/" + tupian + "-yasuo.jpg");
            imageView.setImageBitmap(bitmap);
            //url编码
            try {
                tupianbianmaming = URLEncoder.encode(tupian, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //url编码
            try {
                tupianbianmaming = URLEncoder.encode(tupian, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoadAndSaveImage(MyYoujiDetailActivity.this, imageView, tupian + "-yasuo.jpg", "http://wode123123-test.stor.sinaapp.com/" + tupianbianmaming + "-yasuo.jpg", "/sdcard/map/");
                                /*
                                DownloadUtil down = new DownloadUtil();
                                down.downloadApk(tupianming + "-yasuo.jpg", "http://wode123123-test.stor.sinaapp.com/" + bianmatupianming + "-yasuo.jpg", "/sdcard/map/");
                             */
        }

//图片详情按钮
        imageView.setOnClickListener(new View.OnClickListener() { // 点击放大
            public void onClick(View paramView) {
                Uri uri;
                uri = Uri.parse("http://wode123123-test.stor.sinaapp.com/" + tupianbianmaming + "-yasuo.jpg");
                Log.e("uri", uri.toString());
                Intent intent = new Intent(MyYoujiDetailActivity.this, PhotoActivity.class);
                intent.putExtra("photo", uri);
                intent.putExtra("thumbnail", uri);
                MyYoujiDetailActivity.this.startActivity(intent);
            }
        });
//评论内容
        linearLayout1 = (LinearLayout) findViewById(R.id.list);
        if (Integer.parseInt(pinglunshu) == 0)
            linearLayout1.setVisibility(View.GONE);
//赞内容
        zan = (TextView) findViewById(R.id.juedehenzan);
        if (Integer.parseInt(zanshu) == 0) {
            //没有人赞
            zanderen = "暂时没有人赞";
            zan.setText(zanderen);
        }

//删除按钮
        final RippleView rippleView = (RippleView) findViewById(R.id.shanchu);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                String[] items = new String[]{"确定删除", "返回"};
                //对话框选择
                new AlertDialog.Builder(MyYoujiDetailActivity.this)
                        .setTitle("是否确定删除")
                        .setIcon(R.drawable.app_icon)
                        .setItems(items,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:

                                                new Thread() {
                                                    @Override
                                                    public void run() {

                                                        try {
                                                            getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/shanchuyouji.php?youjiid=" + youjiid + "&shoujihao=" + shoujihao + "&shanchu=1");
                                                            //删首尾空
                                                            getjieguo = getjieguo.trim();
                                                        } catch (Exception e) {
                                                            //网络异常
                                                            handler.sendEmptyMessageDelayed(2, 1000);
                                                        }
                                                        if (Integer.parseInt(getjieguo) == 0) {
                                                            //删除成功
                                                            handler.sendEmptyMessageDelayed(3, 1000);
                                                        } else {
                                                            //网络异常
                                                            handler.sendEmptyMessageDelayed(2, 1000);
                                                        }

                                                    }
                                                }.start();
                                                break;
                                            case 1:
                                                break;
                                        }
                                    }
                                }).create().show();
            }
        });

//地图按钮
        ImageButton go = (ImageButton) findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences4 = getSharedPreferences("ditu", MODE_PRIVATE);
                SharedPreferences.Editor editor4 = sharedPreferences4.edit();
                editor4.putString("didian", didian);
                editor4.putString("jinwei", jinwei);
                editor4.putString("tupian", tupian);
                editor4.putString("neirong", neirong);
                editor4.commit();

                Bundle bundle = new Bundle();
                bundle.putString("index", index);
                Intent intent = new Intent(MyYoujiDetailActivity.this, GoMapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


//分享按钮
        //微信
        wxApi = WXAPIFactory.createWXAPI(this, appid, true);
        wxApi.registerApp(appid);
        //腾讯
        mTencent = Tencent.createInstance("1105170987", this.getApplicationContext());
        myListener = new QQShareListener(MyYoujiDetailActivity.this);
        ImageButton imageButton11 = (ImageButton) findViewById(R.id.fenxiang);
        imageButton11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheet = new BottomSheet.Builder(MyYoujiDetailActivity.this).icon(GetRoundedBitmapUtil.getRoundedBitmap(R.drawable.app_icon_white_big, MyYoujiDetailActivity.this)).title("分享到:").sheet(R.menu.list_five_share).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.QQ:
                                final Bundle params = new Bundle();
                                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                                params.putString(QQShare.SHARE_TO_QQ_TITLE, "游记分享");
                                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, nicheng + "\n" + "\n" + shijian);
                                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=youji&id=" + youjiid);
                                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://1.wode123123.sinaapp.com/photo/egl.png");
                                mTencent.shareToQQ(MyYoujiDetailActivity.this, params, myListener);
                                break;
                            case R.id.QQZONE:
                                final Bundle params1 = new Bundle();
                                params1.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                                params1.putString(QQShare.SHARE_TO_QQ_TITLE, "游记分享");
                                params1.putString(QQShare.SHARE_TO_QQ_SUMMARY, nicheng + "\n" + "\n" + shijian);
                                params1.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=youji&id=" + youjiid);
                                params1.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://1.wode123123.sinaapp.com/photo/egl11.png");
                                params1.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                                mTencent.shareToQQ(MyYoujiDetailActivity.this, params1, myListener);
                                break;
                            case R.id.PENGYOUQUAN:
                                WXWebpageObject webpage = new WXWebpageObject();
                                webpage.webpageUrl = "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=youji&id=" + youjiid;
                                WXMediaMessage msg = new WXMediaMessage(webpage);
                                msg.title = "游记分享";
                                msg.description = nicheng + "\n\n" + shijian;
                                //这里替换一张自己工程里的图片资源
                                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon_white_small);
                                msg.setThumbImage(thumb);
                                SendMessageToWX.Req req = new SendMessageToWX.Req();
                                req.transaction = String.valueOf(System.currentTimeMillis());
                                req.message = msg;
                                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                                wxApi.sendReq(req);
                                break;
                            case R.id.WEIXIN:
                                WXWebpageObject webpage1 = new WXWebpageObject();
                                webpage1.webpageUrl = "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=youji&id=" + youjiid;
                                WXMediaMessage msg1 = new WXMediaMessage(webpage1);
                                msg1.title = "游记分享";
                                msg1.description = nicheng + "\n\n" + shijian;
                                //这里替换一张自己工程里的图片资源
                                Bitmap thumb1 = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon_white_small);
                                msg1.setThumbImage(thumb1);
                                SendMessageToWX.Req req1 = new SendMessageToWX.Req();
                                req1.transaction = String.valueOf(System.currentTimeMillis());
                                req1.message = msg1;
                                req1.scene = SendMessageToWX.Req.WXSceneSession;
                                wxApi.sendReq(req1);
                                break;
                            case R.id.yulan:
                                Bundle bundle = new Bundle();
                                bundle.putString("wangzhi", "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=youji&id=" + youjiid);
                                bundle.putString("biaoti", "故事地图-游记分享");
                                Intent intent = new Intent(MyYoujiDetailActivity.this, WebActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                        }
                    }
                }).build();
                sheet.show();
                Log.e("uri", "www");
            }
        });

    }


    public void initsetting() {
//沉浸模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


    }


    public void initdate() {
//获取传入数据
        Intent intent = getIntent();
        index = intent.getStringExtra("index");
        SharedPreferences sharedPreferences = getSharedPreferences("wodeyouji", MODE_PRIVATE);
        youjiid = sharedPreferences.getString("youjiid" + index, null);
        shoujihao = sharedPreferences.getString("shoujihao" + index, null);
        nicheng = sharedPreferences.getString("nicheng" + index, null);
        shijian = sharedPreferences.getString("shijian" + index, null);
        didian = sharedPreferences.getString("didian" + index, null);
        jinwei = sharedPreferences.getString("jinwei" + index, null);
        tupian = sharedPreferences.getString("tupian" + index, null);
        zanshu = sharedPreferences.getString("zanshu" + index, null);
        pinglunshu = sharedPreferences.getString("pinglunshu" + index, null);
        neirong = sharedPreferences.getString("neirong" + index, null);
//显示数据
        TextView textView = (TextView) findViewById(R.id.woderijishijian);
        textView.setText("时间:" + shijian);
        TextView textView1 = (TextView) findViewById(R.id.woderijineirong);
        textView1.setText(neirong);
        TextView textView3 = (TextView) findViewById(R.id.woderijizanshu);
        textView3.setText(zanshu);
        TextView textView2 = (TextView) findViewById(R.id.woderijididian);
        textView2.setText(didian);

    }

    //返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
            Intent intent = getIntent();
            intent.putExtra("keyword", "");
            MyYoujiDetailActivity.this.setResult(0, intent);
            MyYoujiDetailActivity.this.finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    //获取数据
    public void huoqushuju() {
        new Thread() {
            public void run() {

                //赞
                if (Integer.parseInt(zanshu) == 0) {
                } else {
                    try {
                        getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/huoquyoujizan.php?youjiid=" + youjiid);


                    } catch (Exception e) {
                        //网络异常
                        handler.sendEmptyMessageDelayed(2, 1000);
                    }
                    if (!(getjieguo == null || getjieguo == "")) {
                        //删首尾空
                        getjieguo = getjieguo.trim();
                        //josn解析 写入shar
                        try {
                            JSONArray arr = new JSONArray(getjieguo);
                            JSONObject jsonObject;
                            int j = 0;
                            for (int i = Integer.parseInt(zanshu) - 1; i >= 0; i--) {
                                jsonObject = (JSONObject) arr.get(j);
                                if (i == 0) {
                                    zanderen = zanderen + jsonObject.getString("nicheng") + "等觉得很赞";
                                } else {
                                    zanderen = zanderen + jsonObject.getString("nicheng") + ".";
                                }
                                j++;
                            }
                        } catch (JSONException ex) {
                        }
                    } else {
                        //联网问题
                        handler.sendEmptyMessageDelayed(2, 1000);
                    }
                }


//评论
                if (Integer.parseInt(pinglunshu) == 0) {
                } else {
                    try {
                        getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/huoquyoujipinglun.php?youjiid=" + youjiid);
                    } catch (Exception e) {
                        //网络异常
                        handler.sendEmptyMessageDelayed(2, 1000);
                    }
                    if (!(getjieguo == null || getjieguo == "")) {
                        //删首尾空
                        getjieguo = getjieguo.trim();
                        Log.e("uri", pinglunshu);
                        Log.e("uri", getjieguo);
                        //josn解析 写入shar
                        try {
                            JSONArray arr = new JSONArray(getjieguo);
                            SharedPreferences sharedPreferences1 = getSharedPreferences("wodeyoujipinglun", MODE_PRIVATE);
                            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                            JSONObject jsonObject;
                            int j = 0;
                            for (int i = Integer.parseInt(pinglunshu) - 1; i >= 0; i--) {
                                jsonObject = (JSONObject) arr.get(j);
                                editor1.putString("shijian" + i, jsonObject.getString("shijian"));
                                editor1.putString("nicheng" + i, jsonObject.getString("nicheng"));
                                editor1.putString("neirong" + i, jsonObject.getString("neirong"));
                                editor1.commit();
                                j++;
                                Log.e("uri", jsonObject.getString("neirong"));
                            }
                        } catch (JSONException ex) {
                        }
                    } else {
                        //联网问题
                        handler.sendEmptyMessageDelayed(2, 1000);
                    }
                }
                //数据获取完成
                handler.sendEmptyMessageDelayed(1, 1000);

            }
        }.start();
    }


    //显示
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //显示赞数据
                    zan.setText(zanderen);
                    SharedPreferences sharedPreferences2 = getSharedPreferences("wodeyoujipinglun", MODE_PRIVATE);
                    for (int i = 0; i < Integer.parseInt(pinglunshu); i++) {
                        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(MyYoujiDetailActivity.this)
                                .inflate(R.layout.comment_white, null);
                        TextView textView = (TextView) linearLayout.findViewById(R.id.pinglunnicheng);
                        textView.setText(sharedPreferences2.getString("nicheng" + i, null));
                        TextView textView1 = (TextView) linearLayout.findViewById(R.id.pinglunshujian);
                        textView1.setText(sharedPreferences2.getString("shijian" + i, null));
                        TextView textView2 = (TextView) linearLayout.findViewById(R.id.pinglunneirong);
                        textView2.setText(sharedPreferences2.getString("neirong" + i, null));
                        linearLayout1.addView(linearLayout);
                    }


                    break;
                case 2:
                    //网络异常
                    ToastUtil.show(MyYoujiDetailActivity.this, "获取数据失败！检查网络");

                    break;
                case 3:
                    //删除成功
                    SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("youjishu", Integer.toString(Integer.parseInt(sharedPreferences.getString("youjishu", null)) - 1));
                    editor.commit();
                    ToastUtil.show(MyYoujiDetailActivity.this, "删除成功");
                    Intent intent = getIntent();
                    intent.putExtra("keyword", index);
                    MyYoujiDetailActivity.this.setResult(0, intent);
                    MyYoujiDetailActivity.this.finish();

                    break;
            }
        }
    };


    //分享回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        QQShareListener myListener = new QQShareListener(MyYoujiDetailActivity.this);
        Tencent.onActivityResultData(requestCode, resultCode, data, myListener);
    }
}
