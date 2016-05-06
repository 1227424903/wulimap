package com.map.wulimap.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.cocosw.bottomsheet.BottomSheet;
import com.map.wulimap.listener.QQShareListener;
import com.map.wulimap.R;
import com.map.wulimap.util.GetRoundedBitmapUtil;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.IshaveemojiUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SuggestActivity extends AppCompatActivity {
    //初始化变量
    String pinglunneirong;
    String pinglunneirong1;
    String shoujihao1;
    String appid = "wxc7cc4ae85bebdd30"; // 官网获得的appId
    //初始化控件
    LinearLayout linearLayout1;
    BottomSheet sheet;
    Tencent mTencent;
    QQShareListener myListener;
    IWXAPI wxApi;// 第三方app和微信通讯的openapi接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);

        initdate();
        initview();
        initsetting();

    }


    public void initview() {
//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                SuggestActivity.this.finish();
            }

        });

//发表评论按钮
        final EditText editText = (EditText) findViewById(R.id.xiepinglun);
        final RippleView rippleView2 = (RippleView) findViewById(R.id.fabiaopinglun);
        rippleView2.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
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
                                HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/huifu.php?shoujihao=" + shoujihao1 + "&neirong=" + pinglunneirong1);
                            } catch (Exception e) {
                            }
                            handler.sendEmptyMessageDelayed(3, 1000);
                        }
                    }.start();
                } else {
                    ToastUtil.show(SuggestActivity.this, "不能为空！");
                }
            }

        });


//分享按钮
        //微信
        wxApi = WXAPIFactory.createWXAPI(getApplicationContext(), appid, true);
        wxApi.registerApp(appid);
        //腾讯
        mTencent = Tencent.createInstance("1105170987", this.getApplicationContext());
        myListener = new QQShareListener(SuggestActivity.this);
        final RippleView rippleView = (RippleView) findViewById(R.id.fenxiang);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                sheet = new BottomSheet.Builder(SuggestActivity.this).icon(GetRoundedBitmapUtil.getRoundedBitmap(R.drawable.app_icon_white_big, SuggestActivity.this)).title("分享到:").sheet(R.menu.list_five_share).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.QQ:
                                final Bundle params = new Bundle();
                                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                                params.putString(QQShare.SHARE_TO_QQ_TITLE, "故事地图");
                                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "好玩的游记\n" + "\n" + "都在这里");
                                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://a.app.qq.com/o/simple.jsp?pkgname=com.map.wulimap");
                                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://1.wode123123.sinaapp.com/photo/egl.png");
                                mTencent.shareToQQ(SuggestActivity.this, params, myListener);
                                break;
                            case R.id.QQZONE:
                                final Bundle params1 = new Bundle();
                                params1.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                                params1.putString(QQShare.SHARE_TO_QQ_TITLE, "故事地图");
                                params1.putString(QQShare.SHARE_TO_QQ_SUMMARY, "好玩的游记\n" + "\n" + "都在这里");
                                params1.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://a.app.qq.com/o/simple.jsp?pkgname=com.map.wulimap");
                                params1.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://1.wode123123.sinaapp.com/photo/egl11.png");
                                params1.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
                                mTencent.shareToQQ(SuggestActivity.this, params1, myListener);
                                break;
                            case R.id.PENGYOUQUAN:
                                WXWebpageObject webpage = new WXWebpageObject();
                                webpage.webpageUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.map.wulimap";
                                WXMediaMessage msg = new WXMediaMessage(webpage);
                                msg.title = "故事地图";
                                msg.description = "好玩的游记，都在这里";
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
                                webpage1.webpageUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.map.wulimap";
                                WXMediaMessage msg1 = new WXMediaMessage(webpage1);
                                msg1.title = "故事地图";
                                msg1.description = "好玩的游记，都在这里";
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
                                bundle.putString("wangzhi", "http://a.app.qq.com/o/simple.jsp?pkgname=com.map.wulimap");
                                bundle.putString("biaoti", "故事地图");
                                Intent intent = new Intent(SuggestActivity.this, WebActivity.class);
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
        linearLayout1 = (LinearLayout) findViewById(R.id.list);
    }


    //显示
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    //评论 成功
                    ToastUtil.show(SuggestActivity.this, "建议成功!");
                    LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(SuggestActivity.this)
                            .inflate(R.layout.comment_white, null);
                    TextView textView = (TextView) linearLayout.findViewById(R.id.pinglunnicheng);
                    textView.setText("我");

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

            }
        }
    };


    //分享回调
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        QQShareListener myListener = new QQShareListener(SuggestActivity.this);
        Tencent.onActivityResultData(requestCode, resultCode, data, myListener);
    }


}
