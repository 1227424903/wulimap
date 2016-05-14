package com.map.wulimap.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.map.wulimap.R;
import com.map.wulimap.util.Constant;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.util.HtmlService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import io.rong.imkit.RongIM;


public class FriendAcountActivity extends AppCompatActivity {
    MaterialDialog progDialog;
    Button button;
    //初始化变量
    String getjieguo;
    String nicheng;
    String shoujihao;
    Boolean shifouguanzhu = false;
    String nicheng11;
    String nichengg;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_account);

        initsetting();
        initdate();
        huoqushuju();


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
//显示信息
        final SharedPreferences sharedPreferences = getSharedPreferences("tarenzhanghu", MODE_PRIVATE);
        nicheng = sharedPreferences.getString("nicheng", null);
        String guanzhushu = sharedPreferences.getString("guanzhushu", null);
        String beiguanzhushu = sharedPreferences.getString("beiguanzhushu", null);
        String youjishu = sharedPreferences.getString("youjishu", null);
        String rijishu = sharedPreferences.getString("rijishu", null);
        String from = sharedPreferences.getString("from", null);
        userid = sharedPreferences.getString("userid", null);
        shoujihao = sharedPreferences.getString("shoujihao", null);
//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                FriendAcountActivity.this.finish();
            }

        });

        TextView textView1 = (TextView) findViewById(R.id.nicheng);
        textView1.setText(nicheng);
        TextView textView2 = (TextView) findViewById(R.id.shoujihao);
        if (shoujihao.equals("")) {
            if (Integer.parseInt(youjishu) + Integer.parseInt(rijishu) < 10) {
                textView2.setText("ta很懒！");
            } else {
                textView2.setText("ta很勤快！");
            }
        } else {
            textView2.setText(shoujihao);
        }
        TextView textView3 = (TextView) findViewById(R.id.nicheng1);
        textView3.setText(nicheng);
        TextView textView5 = (TextView) findViewById(R.id.youjishu);
        textView5.setText(youjishu);
        TextView textView6 = (TextView) findViewById(R.id.rijishu);
        textView6.setText(rijishu);
        TextView textView7 = (TextView) findViewById(R.id.guanzhushu);
        textView7.setText(guanzhushu);
        TextView textView8 = (TextView) findViewById(R.id.beiguanzhushu);
        textView8.setText(beiguanzhushu);
        button = (Button) findViewById(R.id.guanzhu);


//日记
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.riji);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendAcountActivity.this.startActivity(new Intent(FriendAcountActivity.this, Friend_rijiActivity.class));
            }
        });
//游记
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.youji);
        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendAcountActivity.this.startActivity(new Intent(FriendAcountActivity.this, Friend_youjiActivity.class));
            }
        });

//关注
        Button guanzhu = (Button) findViewById(R.id.guanzhu);
        guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            final SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                            String shoujihao1 = sharedPreferences.getString("shoujihao", null);
                            String nicheng1 = sharedPreferences.getString("nicheng", null);

                            //url编码
                            try {
                                nicheng11 = URLEncoder.encode(nicheng1, "utf-8");
                                nichengg = URLEncoder.encode(nicheng, "utf-8");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            getjieguo = HtmlService.getHtml(Constant.PHP_URL + "gushiditu/guanzhu.php?shoujihao=" + shoujihao1 + "&shoujihao1=" + shoujihao + "&nicheng=" + nicheng11 + "&nicheng1=" + nichengg + "&shanchu=0");
                            //删首尾空
                            getjieguo = getjieguo.trim();
                        } catch (Exception e) {
                            //网络异常
                            handler.sendEmptyMessageDelayed(2, 1000);
                        }
                        if (getjieguo.equals("1")) {
                            //关注成功
                            handler.sendEmptyMessageDelayed(4, 1000);
                        } else {
                            //取消关注
                            handler.sendEmptyMessageDelayed(5, 1000);
                        }
                    }
                }.start();
            }
        });


        if ("liaotian".equals(from)) {
            final RippleView rippleView3 = (RippleView) findViewById(R.id.fab);
            rippleView3.setVisibility(View.GONE);


        } else {

            //发送消息
            final RippleView rippleView3 = (RippleView) findViewById(R.id.fab);
            rippleView3.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {

                    if (RongIM.getInstance() != null) {

                        RongIM.getInstance().startPrivateChat(FriendAcountActivity.this, userid, nicheng);
                    } else {
                        FriendAcountActivity.this.startActivity(new Intent(FriendAcountActivity.this, ConversationActivity.class));
            }
                }

            });

        }




    }

    //获取数据
    public void huoqushuju() {
        new Thread() {
            public void run() {
                final SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                String shoujihao1 = sharedPreferences.getString("shoujihao", null);
                //关注者
                try {
                    getjieguo = HtmlService.getHtml(Constant.PHP_URL + "gushiditu/huoquguanzhuren.php?shoujihao=" + shoujihao1);
                } catch (Exception e) {
                }

                if (!(getjieguo == null || getjieguo == "")) {
                    //删首尾空
                    getjieguo = getjieguo.trim();
                    //josn解析 写入shar
                    try {
                        JSONArray arr = new JSONArray(getjieguo);
                        JSONObject jsonObject;
                        int j = 0;
                        for (int i = arr.length() - 1; i >= 0; i--) {
                            jsonObject = (JSONObject) arr.get(j);
                            if (nicheng.equals(jsonObject.getString("nicheng1"))) {
                                shifouguanzhu = true;
                            }
                            j++;
                        }
                    } catch (JSONException ex) {
                    }
                    //成功
                    handler.sendEmptyMessageDelayed(1, 1000);
                } else {
                    //联网问题
                    handler.sendEmptyMessageDelayed(2, 1000);
                }
            }
        }.start();
    }


    //显示
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //显示
                    if (shifouguanzhu == false) {
                        button.setText("未关注");
                    } else {
                        button.setText("已关注");
                    }

                    break;
                case 2:
                    //网络异常
                    ToastUtil.show(FriendAcountActivity.this, "获取数据失败！检查网络");

                    break;
                case 4:
                    //关注 成功
                    button.setText("已关注");
                    ToastUtil.show(FriendAcountActivity.this, "关注成功！");
                    SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String guanzhushu1 = sharedPreferences.getString("guanzhushu", null);
                    int guanzhushu = Integer.parseInt(guanzhushu1) + 1;
                    editor.putString("guanzhushu", Integer.toString(guanzhushu));
                    editor.commit();

                    break;
                case 5:
                    //取消关注
                    SharedPreferences sharedPreferences1 = getSharedPreferences("zhanghu", MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    String guanzhushu11 = sharedPreferences1.getString("guanzhushu", null);
                    int guanzhushu111 = Integer.parseInt(guanzhushu11) - 1;
                    editor1.putString("guanzhushu", Integer.toString(guanzhushu111));
                    editor1.commit();
                    ToastUtil.show(FriendAcountActivity.this, "取消关注成功！");
                    button.setText("未关注");
                    break;
            }
        }
    };


}
