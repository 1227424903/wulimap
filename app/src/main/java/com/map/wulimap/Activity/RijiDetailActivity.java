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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.map.wulimap.listener.QQShareListener;
import com.map.wulimap.R;
import com.map.wulimap.util.FileUtil;
import com.map.wulimap.util.GetRoundedBitmapUtil;
import com.map.wulimap.util.LoadAndSaveImage;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.util.DownloadUtil;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.IshaveemojiUtil;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class RijiDetailActivity extends AppCompatActivity {
    //初始化变量
    String rijiid;
    String shoujihao;
    String nicheng;
    String shijian;
    String didian;
    String index;
    String pinglunshu;
    String zanshu;
    String tupian;
    String neirong;
    String gongkai;
    String jinwei;
    String shoujihao1;
    String nicheng1;
    String tupianbianmaming;
    String getjieguo;
    String zanderen = "";
    String zanderen1 = "";
    String nicheng2;
    String nicheng3;
    String bianmanicheng;
    String pinglunneirong1;
    String pinglunneirong;
    Tencent mTencent;
    QQShareListener myListener;
    Boolean shifouzan = false;
    //初始化控件
    LinearLayout linearLayout1;
    TextView zan;
    TextView zan1;
    ImageButton shou;
    MaterialDialog progDialog;
    BottomSheet sheet;

    //分享常量
    private String appid = "wxc7cc4ae85bebdd30"; // 官网获得的appId
    private IWXAPI wxApi;// 第三方app和微信通讯的openapi接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riji_detail);


        initsetting();
        initdate();
        initview1();
        //获取数据

        huoqushuju();
    }


    public void initview1() {
//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = getIntent();
                intent.putExtra("keyword", "");
                RijiDetailActivity.this.setResult(0, intent);
                RijiDetailActivity.this.finish();
            }

        });


//赞一下按钮
        final RippleView rippleView3 = (RippleView) findViewById(R.id.fab);
        rippleView3.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/zanriji.php?rijiid=" + rijiid + "&shoujihao=" + shoujihao1 + "&nicheng=" + nicheng2 + "&shoujihao1=" + shoujihao + "&nicheng1=" + nicheng3 + "&shanchu=0");
                            //删首尾空
                            getjieguo = getjieguo.trim();

                        } catch (Exception e) {
                            //网络异常
                            handler.sendEmptyMessageDelayed(2, 1000);
                        }
                        if (getjieguo.equals("1")) {
                            //赞成功
                            handler.sendEmptyMessageDelayed(4, 1000);
                        } else {
                            //取消赞
                            handler.sendEmptyMessageDelayed(5, 1000);
                        }
                    }
                }.start();


            }

        });


//踩踩按钮
        final RippleView rippleView = (RippleView) findViewById(R.id.weiguanzhu);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                try {
                    bianmanicheng = URLEncoder.encode(nicheng, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/nichengsousu.php?nicheng=" + bianmanicheng);
                        } catch (Exception e) {
                        }
                        //删首尾空
                        getjieguo = getjieguo.trim();
                        Log.e("uri", getjieguo);
                        //网络问题
                        if (!(getjieguo == null || getjieguo == "")) {
                            if (getjieguo.equals("0")) {
                            } else {
                                try {
                                    JSONArray arr = new JSONArray(getjieguo);
                                    JSONObject jsonObject = (JSONObject) arr.get(0);
                                    SharedPreferences sharedPreferences = getSharedPreferences("tarenzhanghu", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("shoujihao", "");
                                    editor.putString("userid", jsonObject.getString("userid"));
                                    editor.putString("youjishu", jsonObject.getString("youjishu"));
                                    editor.putString("rijishu", jsonObject.getString("rijishu"));
                                    editor.putString("nicheng", jsonObject.getString("nicheng"));
                                    editor.putString("guanzhushu", jsonObject.getString("guanzhushu"));
                                    editor.putString("beiguanzhushu", jsonObject.getString("beiguanzhushu"));
                                    editor.putString("icon", jsonObject.getString("icon"));
                                    editor.commit();
                                } catch (JSONException ex) {
                                    Log.e("11", getjieguo);
                                }


                                RijiDetailActivity.this.startActivity(new Intent(RijiDetailActivity.this, FriendAcountActivity.class));
                            }
                        } else {
                        }
                    }
                }.start();

            }

        });

//显示数据
        TextView textView = (TextView) findViewById(R.id.shijian);
        textView.setText(shijian);
        TextView textVieww = (TextView) findViewById(R.id.nicheng);
        textVieww.setText(nicheng);
        TextView textView1 = (TextView) findViewById(R.id.woderijineirong);
        textView1.setText(neirong);
        zan1 = (TextView) findViewById(R.id.woderijizanshu);
        zan1.setText(zanshu);
        TextView textView2 = (TextView) findViewById(R.id.woderijididian);
        textView2.setText(didian);
        shou = (ImageButton) findViewById(R.id.zan);
        Button button = (Button) findViewById(R.id.guanzhu);
        button.setText("日记");
        //显示对话框


//url编码
        try {
            nicheng2 = URLEncoder.encode(nicheng1, "utf-8");
            nicheng3 = URLEncoder.encode(nicheng, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

//图片处理显示
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
            new LoadAndSaveImage(RijiDetailActivity.this, imageView, tupian + "-yasuo.jpg", "http://wode123123-test.stor.sinaapp.com/" + tupianbianmaming + "-yasuo.jpg", "/sdcard/map/");
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
                Intent intent = new Intent(RijiDetailActivity.this, PhotoActivity.class);
                intent.putExtra("photo", uri);
                intent.putExtra("thumbnail", uri);
                RijiDetailActivity.this.startActivity(intent);

            }
        });

//评论处理
        linearLayout1 = (LinearLayout) findViewById(R.id.list);
        if (Integer.parseInt(pinglunshu) == 0)
            linearLayout1.setVisibility(View.GONE);
//赞处理
        zan = (TextView) findViewById(R.id.juedehenzan);
        if (Integer.parseInt(zanshu) == 0) {
            //没有人赞
            zanderen = "暂时没有人赞";
            zan.setText(zanderen);
        }


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
                Intent intent = new Intent(RijiDetailActivity.this, GoMapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//发表评论按钮
        final EditText editText = (EditText) findViewById(R.id.xiepinglun);
        editText.clearFocus();
        ImageButton fabiaopinglun = (ImageButton) findViewById(R.id.fabiaopinglun);
        fabiaopinglun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //获取内容
                pinglunneirong = editText.getText().toString().trim().replaceAll(" ", "");
                if (!pinglunneirong.equals("")) {
                    editText.setText("");
                    editText.clearFocus();
                    //过滤 emoj
                    pinglunneirong = IshaveemojiUtil.filterEmoji(pinglunneirong);
                    new Thread() {
                        public void run() {
                            try {
                                //url编码
                                try {
                                    pinglunneirong1 = URLEncoder.encode(pinglunneirong, "utf-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/pinglunriji.php?rijiid=" + rijiid + "&shoujihao=" + shoujihao1 + "&nicheng=" + nicheng2 + "&shoujihao1=" + shoujihao + "&nicheng1=" + nicheng3 + "&neirong=" + pinglunneirong1 + "&shanchu=0");
                            } catch (Exception e) {
                            }
                            handler.sendEmptyMessageDelayed(3, 1000);
                        }
                    }.start();
                } else {
                    ToastUtil.show(RijiDetailActivity.this, "不能为空！");
                }
            }
        });


//分享按钮
        //微信
        wxApi = WXAPIFactory.createWXAPI(this, appid, true);
        wxApi.registerApp(appid);
        //腾讯
        mTencent = Tencent.createInstance("1105170987", this.getApplicationContext());
        myListener = new QQShareListener(RijiDetailActivity.this);
        ImageButton imageButton11 = (ImageButton) findViewById(R.id.fenxiang);
        imageButton11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheet = new BottomSheet.Builder(RijiDetailActivity.this).icon(GetRoundedBitmapUtil.getRoundedBitmap(R.drawable.app_icon_white_big, RijiDetailActivity.this)).title("分享到:").sheet(R.menu.list_five_share).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.QQ:
                                final Bundle params = new Bundle();
                                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                                params.putString(QQShare.SHARE_TO_QQ_TITLE, "日记分享");
                                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, nicheng + "\n" + "\n" + shijian);
                                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=riji&id=" + rijiid);
                                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://1.wode123123.sinaapp.com/photo/egl.png");
                                mTencent.shareToQQ(RijiDetailActivity.this, params, myListener);
                                break;
                            case R.id.QQZONE:
                                final Bundle params1 = new Bundle();
                                params1.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                                params1.putString(QQShare.SHARE_TO_QQ_TITLE, "日记分享");
                                params1.putString(QQShare.SHARE_TO_QQ_SUMMARY, nicheng + "\n" + "\n" + shijian);
                                params1.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=riji&id=" + rijiid);
                                params1.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://1.wode123123.sinaapp.com/photo/egl11.png");
                                params1.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                                mTencent.shareToQQ(RijiDetailActivity.this, params1, myListener);
                                break;
                            case R.id.PENGYOUQUAN:
                                WXWebpageObject webpage = new WXWebpageObject();
                                webpage.webpageUrl = "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=riji&id=" + rijiid;
                                WXMediaMessage msg = new WXMediaMessage(webpage);
                                msg.title = "日记分享";
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
                                webpage1.webpageUrl = "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=riji&id=" + rijiid;
                                WXMediaMessage msg1 = new WXMediaMessage(webpage1);
                                msg1.title = "日记分享";
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
                                bundle.putString("wangzhi", "http://wode123123.sinaapp.com/wangye/fenxiang.php?ku=riji&id=" + rijiid);
                                bundle.putString("biaoti", "故事地图-日记分享");
                                Intent intent = new Intent(RijiDetailActivity.this, WebActivity.class);
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
//获取账户信息
        SharedPreferences sharedPreferences1 = getSharedPreferences("zhanghu", MODE_PRIVATE);
        shoujihao1 = sharedPreferences1.getString("shoujihao", null);
        nicheng1 = sharedPreferences1.getString("nicheng", null);
        //获取传入数据
        Intent intent = getIntent();
        rijiid = intent.getStringExtra("rijiid");
        shoujihao = intent.getStringExtra("shoujihao");
        nicheng = intent.getStringExtra("nicheng");
        gongkai = intent.getStringExtra("gongxiang");
        shijian = intent.getStringExtra("shijian");
        didian = intent.getStringExtra("didian");
        jinwei = intent.getStringExtra("jinwei");
        tupian = intent.getStringExtra("tupian");
        zanshu = intent.getStringExtra("zanshu");
        pinglunshu = intent.getStringExtra("pinglunshu");
        neirong = intent.getStringExtra("neirong");
    }


    //返回键回调
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
            Intent intent = getIntent();
            intent.putExtra("keyword", "");
            RijiDetailActivity.this.setResult(0, intent);
            RijiDetailActivity.this.finish();
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
                        getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/huoqurijizan.php?rijiid=" + rijiid);

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
                                    if (nicheng1.equals(jsonObject.getString("nicheng"))) {
                                        shifouzan = true;
                                        Log.e("uri", jsonObject.getString("nicheng"));
                                        Log.e("uri", nicheng1);
                                    } else {
                                        zanderen = zanderen + jsonObject.getString("nicheng") + ".";
                                    }
                                } else {
                                    if (nicheng1.equals(jsonObject.getString("nicheng"))) {
                                        shifouzan = true;
                                        Log.e("uri", jsonObject.getString("nicheng"));
                                        Log.e("uri", nicheng1);
                                    } else {
                                        zanderen = zanderen + jsonObject.getString("nicheng") + ".";
                                    }
                                }
                                j++;
                            }

                            if (shifouzan == true) {
                                if (Integer.parseInt(zanshu) > 1) {
                                    zanderen = zanderen + "等觉得很赞";
                                    zanderen1 = zanderen;
                                    zanderen = nicheng1 + "." + zanderen;
                                } else {
                                    zanderen = nicheng1 + "等觉得很赞";
                                }
                            } else {
                                zanderen = zanderen + "等觉得很赞";
                                zanderen1 = zanderen;
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
                        getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/huoqurijipinglun.php?rijiid=" + rijiid);
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
                            SharedPreferences sharedPreferences1 = getSharedPreferences("tarenrijipinglun", MODE_PRIVATE);
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
                    //显示赞的按钮
                    if (shifouzan == true) {
                        shou.setImageResource(R.drawable.button_zan_done);
                    } else {
                        shou.setImageResource(R.drawable.button_zan);
                    }
                    //显示评论
                    SharedPreferences sharedPreferences2 = getSharedPreferences("tarenrijipinglun", MODE_PRIVATE);
                    for (int i = 0; i < Integer.parseInt(pinglunshu); i++) {
                        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(RijiDetailActivity.this)
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
                    ToastUtil.show(RijiDetailActivity.this, "获取数据失败！检查网络");

                    break;
                case 3:
                    //评论 成功
                    ToastUtil.show(RijiDetailActivity.this, "评论成功!");
                    LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(RijiDetailActivity.this)
                            .inflate(R.layout.comment_white, null);
                    TextView textView = (TextView) linearLayout.findViewById(R.id.pinglunnicheng);
                    textView.setText(nicheng1);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    TextView textView1 = (TextView) linearLayout.findViewById(R.id.pinglunshujian);
                    textView1.setText(str);
                    TextView textView2 = (TextView) linearLayout.findViewById(R.id.pinglunneirong);
                    textView2.setText(pinglunneirong);
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout1.addView(linearLayout);
                    break;
                case 4:
                    //赞 成功
                    if (Integer.parseInt(zanshu) == 0) {
                        zanderen = nicheng1 + "等觉得很赞";
                    } else {
                        zanderen = nicheng1 + "." + zanderen;
                    }
                    zan.setText(zanderen);
                    zanshu = Integer.toString(Integer.parseInt(zanshu) + 1);
                    zan1.setText(zanshu);
                    shou.setImageResource(R.drawable.button_zan_done);
                    ToastUtil.show(RijiDetailActivity.this, "赞成功！");
                    break;
                case 5:
                    //取消赞
                    if (Integer.parseInt(zanshu) - 1 == 0) {
                        zan.setText("暂时没有人赞");
                    } else {
                        zanderen = zanderen1;
                        zan.setText(zanderen);
                    }
                    zanshu = Integer.toString(Integer.parseInt(zanshu) - 1);
                    zan1.setText(zanshu);
                    ToastUtil.show(RijiDetailActivity.this, "取消赞成功！");
                    shou.setImageResource(R.drawable.button_zan);
                    break;
            }
        }
    };


    //QQ分享回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        QQShareListener myListener = new QQShareListener(RijiDetailActivity.this);
        Tencent.onActivityResultData(requestCode, resultCode, data, myListener);
    }


}
