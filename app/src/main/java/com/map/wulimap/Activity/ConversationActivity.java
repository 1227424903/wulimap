package com.map.wulimap.Activity;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.map.wulimap.R;
import com.map.wulimap.listener.ConversationBehaviorListener;
import com.map.wulimap.util.Constant;
import com.map.wulimap.util.HtmlService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import io.rong.imkit.RongIM;


/**
 * Created by Bob on 2015/4/16.
 */
public class ConversationActivity extends ActionBarActivity {
    String nicheng;
    String getjieguo;
    String name;
    String bianmanicheng;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        RongIM.setConversationBehaviorListener(new ConversationBehaviorListener());
        initsetting();
        //唯一有用的代码，加载一个 layout
        initdate();
    }

    public void initdate() {
        //昵称
        new Thread() {
            public void run() {
           name= findUserById(getIntent().getData().getQueryParameter("targetId"));
                }
            }.start();

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

        //返回
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ConversationActivity.this.finish();
            }

        });



    }



    //获取用户信息
    public String findUserById(String userId){
        String name="未知";
        try {
            getjieguo = HtmlService.getHtml(Constant.PHP_URL + "gushiditu/getuseinfo.php?userid=" + userId);
        } catch (Exception e) {
        }
        //删首尾空
        getjieguo = getjieguo.trim();
        Log.e("uri", getjieguo);
        //网络问题
        if (!(getjieguo == null || getjieguo == "")) {
            try {
                JSONArray arr = new JSONArray(getjieguo);
                JSONObject jsonObject;
                jsonObject = (JSONObject) arr.get(0);
                name=jsonObject.getString("nicheng");
            } catch (JSONException ex) {
            }
        }
        handler.sendEmptyMessageDelayed(1, 0);
        return  name;
    }



    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Button button =(Button)findViewById(R.id.bt3);
                    button.setText(name);
                    if("小木".equals(name)){
                    }
                    else {
                        //账户
                        final RippleView rippleView = (RippleView) findViewById(R.id.weiguanzhu);
                        TextView textView = (TextView) findViewById(R.id.caicai);
                        textView.setVisibility(View.VISIBLE);
                        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                            @Override
                            public void onComplete(RippleView rippleView) {
                                try {
                                    bianmanicheng = URLEncoder.encode(name, "utf-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            getjieguo = HtmlService.getHtml(Constant.PHP_URL + "gushiditu/nichengsousu.php?nicheng=" + bianmanicheng);
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
                                                    editor.putString("from", "liaotian");
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
                                                ConversationActivity.this.startActivity(new Intent(ConversationActivity.this, FriendAcountActivity.class));
                                            }
                                        } else {
                                        }
                                    }
                                }.start();

                            }

                        });

                    }

                    break;

            }
        }
    };



}