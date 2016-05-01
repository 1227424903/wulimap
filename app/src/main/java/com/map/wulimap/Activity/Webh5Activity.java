package com.map.wulimap.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.map.wulimap.QQshareListener.ShareListener;
import com.map.wulimap.R;
import com.map.wulimap.util.GetRoundedBitmapUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;


public class Webh5Activity extends AppCompatActivity {
    //初始化变量
    String biaoji = "0";
    String biaoji2 = "0";
    String tupian;
    String wangzhi;
    String biaoti;
    //初始化控件
    BottomSheet sheet;
    Tencent mTencent;
    ShareListener myListener;
    WebView webView;
    MaterialDialog progDialog;
    Bitmap bw, bm;

    private String appid = "wxc7cc4ae85bebdd30"; // 官网获得的appId
    private IWXAPI wxApi;// 第三方app和微信通讯的openapi接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webh5);

        initsetting();
        initdate();
        initview();


    }


    public void initview() {
//标题显示
        Button button = (Button) findViewById(R.id.bt3);
        button.setText(biaoti);

//载入网址
        webView = (WebView) findViewById(R.id.WebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(wangzhi);
        webView.setDrawingCacheEnabled(true);


//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Webh5Activity.this.finish();
            }

        });


//分享按钮
        //微信
        wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), appid, true);
        wxApi.registerApp(appid);
        //腾讯
        mTencent = Tencent.createInstance("1105170987", this.getApplicationContext());
        myListener = new ShareListener(Webh5Activity.this);

        final RippleView rippleView = (RippleView) findViewById(R.id.fenxiang);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                sheet = new BottomSheet.Builder(Webh5Activity.this).icon(GetRoundedBitmapUtil.getRoundedBitmap(R.drawable.app_icon_white_big, Webh5Activity.this)).title("分享到:").sheet(R.menu.list_four_share).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.QQ:
                                final Bundle params = new Bundle();
                                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                                params.putString(QQShare.SHARE_TO_QQ_TITLE, "故事地图");
                                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "\n" + biaoti);
                                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, wangzhi);
                                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://1.wode123123.sinaapp.com/photo/egl.png");
                                mTencent.shareToQQ(Webh5Activity.this, params, myListener);
                                break;
                            case R.id.QQZONE:
                                final Bundle params1 = new Bundle();
                                params1.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                                params1.putString(QQShare.SHARE_TO_QQ_TITLE, "故事地图");
                                params1.putString(QQShare.SHARE_TO_QQ_SUMMARY, "\n" + biaoti);
                                params1.putString(QQShare.SHARE_TO_QQ_TARGET_URL, wangzhi);
                                params1.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://1.wode123123.sinaapp.com/photo/egl11.png");
                                params1.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                                mTencent.shareToQQ(Webh5Activity.this, params1, myListener);
                                break;
                            case R.id.PENGYOUQUAN:
                                WXWebpageObject webpage = new WXWebpageObject();
                                webpage.webpageUrl = wangzhi;
                                WXMediaMessage msg = new WXMediaMessage(webpage);
                                msg.title = "故事地图";
                                msg.description = "\n" + biaoti;
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
                                webpage1.webpageUrl = wangzhi;
                                WXMediaMessage msg1 = new WXMediaMessage(webpage1);
                                msg1.title = "故事地图";
                                msg1.description = "\n" + biaoti;
                                //这里替换一张自己工程里的图片资源
                                Bitmap thumb1 = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon_white_small);
                                msg1.setThumbImage(thumb1);
                                SendMessageToWX.Req req1 = new SendMessageToWX.Req();
                                req1.transaction = String.valueOf(System.currentTimeMillis());
                                req1.message = msg1;
                                req1.scene = SendMessageToWX.Req.WXSceneSession;
                                wxApi.sendReq(req1);
                                break;
                        }
                    }
                }).build();
                sheet.show();
                Log.e("uri", "www");


            }
        });

    }


    public void initdate() {
//获取数据 显示数据
        Intent intent = getIntent();
        wangzhi = intent.getStringExtra("wangzhi");
        biaoti = intent.getStringExtra("biaoti");
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


    //qq分享回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ShareListener myListener = new ShareListener(Webh5Activity.this);
        Tencent.onActivityResultData(requestCode, resultCode, data, myListener);
    }


}