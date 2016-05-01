package com.map.wulimap.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.map.wulimap.QQshareListener.ShareListener;
import com.map.wulimap.R;
import com.map.wulimap.util.GetRoundedBitmapUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WebActivity extends AppCompatActivity {
    //初始化控件
    WebView webView;
    MaterialDialog progDialog;
    Bitmap bw, bm;
    BottomSheet sheet;
    Tencent mTencent;
    ShareListener myListener;
    //初始化变量
    String wangzhi;
    String tupian;
    String biaoji = "0";
    String biaoji2 = "0";
    String biaoti;
    private String appid = "wxc7cc4ae85bebdd30"; // 官网获得的appId
    private IWXAPI wxApi;// 第三方app和微信通讯的openapi接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        initsetting();
        initdate();
        //显示对话框
        showProgressDialog();
        initview();

    }


    public void initview() {
//标题显示
        Button button = (Button) findViewById(R.id.bt3);
        button.setText(biaoti);

//webview
        webView = (WebView) findViewById(R.id.WebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public void onPageFinished(WebView view, String url) {
                                         super.onPageFinished(view, url);
                                         handler.sendEmptyMessageDelayed(1, 2000);
                                     }
                                 }
        );

//载入网址
        webView.loadUrl(wangzhi);
        webView.setDrawingCacheEnabled(true);


//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                WebActivity.this.finish();
            }

        });

//分享按钮
        //微信
        wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), appid, true);
        wxApi.registerApp(appid);
        //腾讯
        mTencent = Tencent.createInstance("1105170987", this.getApplicationContext());
        myListener = new ShareListener(WebActivity.this);
        final RippleView rippleView = (RippleView) findViewById(R.id.fenxiang);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                sheet = new BottomSheet.Builder(WebActivity.this).icon(GetRoundedBitmapUtil.getRoundedBitmap(R.drawable.app_icon_white_big, WebActivity.this)).title("分享方式:").sheet(R.menu.list_two_share).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case R.id.lianjie:
                                sheet = new BottomSheet.Builder(WebActivity.this).icon(GetRoundedBitmapUtil.getRoundedBitmap(R.drawable.app_icon_white_big, WebActivity.this)).title("分享到:").sheet(R.menu.list_four_share).listener(new DialogInterface.OnClickListener() {
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
                                                mTencent.shareToQQ(WebActivity.this, params, myListener);
                                                break;
                                            case R.id.QQZONE:
                                                final Bundle params1 = new Bundle();
                                                params1.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                                                params1.putString(QQShare.SHARE_TO_QQ_TITLE, "故事地图");
                                                params1.putString(QQShare.SHARE_TO_QQ_SUMMARY, "\n" + biaoti);
                                                params1.putString(QQShare.SHARE_TO_QQ_TARGET_URL, wangzhi);
                                                params1.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://1.wode123123.sinaapp.com/photo/egl11.png");
                                                params1.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                                                mTencent.shareToQQ(WebActivity.this, params1, myListener);
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

                                break;
                            case R.id.quantu:
                                //截取网页全图
                                if (biaoji.equals("0")) {
                                    showProgressDialog();
                                    Toast.makeText(WebActivity.this, "保存图片中", Toast.LENGTH_SHORT).show();
                                    handler.sendEmptyMessageDelayed(3, 5000);
                                } else {
                                    handler.sendEmptyMessageDelayed(2, 0);
                                }
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
//获取数据 显示数据
        Intent intent = getIntent();
        wangzhi = intent.getStringExtra("wangzhi");
        biaoti = intent.getStringExtra("biaoti");

    }


    //qq分享回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ShareListener myListener = new ShareListener(WebActivity.this);
        Tencent.onActivityResultData(requestCode, resultCode, data, myListener);
    }


    //保存截图
    public void saveBitmap(Bitmap bitmap, String bitName) throws IOException {
        File folder = new File("/sdcard/map/");
        if (!folder.exists()) {
            folder.mkdir();
        }
        tupian = "/sdcard/map/" + bitName + ".jpg";
        File file = new File("/sdcard/map/" + bitName + ".jpg");

        FileOutputStream out;
        if (!file.exists()) {

            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 70, out)) {
                    Toast.makeText(WebActivity.this, "成功存入map目录",
                            Toast.LENGTH_SHORT).show();
                    dissmissProgressDialog();
                    biaoji = "1";
                    out.flush();
                    out.close();
                    handler.sendEmptyMessageDelayed(2, 1000);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (biaoji2.equals("0")) {
                        biaoji2 = "1";
                        //网页bitma
                        webView.setWebChromeClient(new WebChromeClient() {
                            @SuppressWarnings("deprecation")
                            public void onProgressChanged(WebView view, int newProgress) {
                                if (newProgress == 100) {
                                    bw = Bitmap.createBitmap(webView.getWidth(), webView.getHeight(),
                                            Bitmap.Config.RGB_565);
                                    Canvas c = new Canvas(bw);
                                    webView.draw(c);

                                }
                            }
                        });

                        webView.loadUrl(wangzhi);
                        webView.setDrawingCacheEnabled(true);
                        dissmissProgressDialog();
                    }
                    break;
                case 2:
                    sheet = new BottomSheet.Builder(WebActivity.this).icon(GetRoundedBitmapUtil.getRoundedBitmap(R.drawable.app_icon_white_big, WebActivity.this)).title("分享到:").sheet(R.menu.list_four_share).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.QQ:
                                    Bundle params = new Bundle();
                                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, tupian);
                                    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "故事地图");
                                    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);

                                    mTencent.shareToQQ(WebActivity.this, params, myListener);
                                    break;
                                case R.id.QQZONE:
                                    Bundle params1 = new Bundle();
                                    params1.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, tupian);
                                    params1.putString(QQShare.SHARE_TO_QQ_APP_NAME, "故事地图");
                                    params1.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);

                                    params1.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                                    mTencent.shareToQQ(WebActivity.this, params1, myListener);
                                    break;
                                case R.id.PENGYOUQUAN:
                                    WXImageObject imgObj = new WXImageObject();
                                    imgObj.setImagePath(tupian);
                                    WXMediaMessage msg = new WXMediaMessage();
                                    msg.mediaObject = imgObj;
                                    msg.description = "故事地图";
                                    Bitmap bmp = BitmapFactory.decodeFile(tupian);
                                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
                                    bmp.recycle();
                                    msg.thumbData = GetRoundedBitmapUtil.bmpToByteArray(thumbBmp, true);
                                    msg.title = "故事地图";
                                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                                    req.transaction = "img" + String.valueOf(System.currentTimeMillis());
                                    req.message = msg;
                                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                                    wxApi.sendReq(req);
                                    break;
                                case R.id.WEIXIN:
                                    WXImageObject imgObj1 = new WXImageObject();
                                    imgObj1.setImagePath(tupian);
                                    WXMediaMessage msg1 = new WXMediaMessage();
                                    msg1.mediaObject = imgObj1;
                                    msg1.description = "故事地图";
                                    Bitmap bmp1 = BitmapFactory.decodeFile(tupian);
                                    Bitmap thumbBmp1 = Bitmap.createScaledBitmap(bmp1, 150, 150, true);
                                    bmp1.recycle();
                                    msg1.thumbData = GetRoundedBitmapUtil.bmpToByteArray(thumbBmp1, true);
                                    msg1.title = "故事地图";
                                    SendMessageToWX.Req req1 = new SendMessageToWX.Req();
                                    req1.transaction = "img" + String.valueOf(System.currentTimeMillis());
                                    req1.message = msg1;
                                    req1.scene = SendMessageToWX.Req.WXSceneSession;
                                    wxApi.sendReq(req1);
                                    break;
                            }
                        }
                    }).build();
                    sheet.show();
                    Log.e("uri", "www");
                    break;
                case 3:
                    try {
                        String num = GetRoundedBitmapUtil.getRandomCode();
                        saveBitmap(bw, "imagePic" + num);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)

                    .content("稍等!")
                    .progress(true, 0);

            progDialog = builder.build();
            progDialog.setCancelable(false);
        }

        progDialog.show();
    }


    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


}