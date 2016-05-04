package com.map.wulimap.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.map.wulimap.R;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.util.HtmlService;

import java.net.URLEncoder;


public class LoginActivity extends AppCompatActivity {
    MaterialDialog progDialog;
    //初始化变量
    String wangzhi;
    String getjieguo;
    String mima;
    String zhanghu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        initsetting();
        initview1();
    }


    public void initview1() {
//初始化控件
        final EditText editText1 = (EditText) findViewById(R.id.zhanghukuang);
        final EditText editText2 = (EditText) findViewById(R.id.mimakuang);
        Button button = (Button) findViewById(R.id.denglu);
        TextView textView1 = (TextView) findViewById(R.id.xinyonghu);
        TextView textView2 = (TextView) findViewById(R.id.wangjimima);

//登录按钮
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                zhanghu = editText1.getText().toString().trim().replaceAll(" ", "");
                mima = editText2.getText().toString().trim().replaceAll(" ", "");
                //手机号格式
                if (zhanghu.length() == 11) {
                    //密码是否为空
                    if (!mima.equals("")) {
                        //是否注册
                        new Thread() {
                            public void run() {
                                try {
                                    getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/shifouzhuce.php?shoujihao=" + zhanghu);

                                } catch (Exception e) {
                                }
                                //获取数据是否失败
                                if (!(getjieguo == null || getjieguo == "")) {
                                    //删首尾空
                                    getjieguo = getjieguo.trim();
                                    if (getjieguo.equals("0")) {
                                        //没有注册
                                        handler.sendEmptyMessageDelayed(1, 0);
                                    } else {
                                        //注册过，验证密码
                                        new Thread() {
                                            public void run() {
                                                try {
                                                    //url编码
                                                    try {
                                                        mima = URLEncoder.encode(mima, "utf-8");

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/denglu.php?shoujihao=" + zhanghu + "&mima=" + mima);
                                                    //删首尾空
                                                    getjieguo = getjieguo.trim();
                                                    Log.e("uri", getjieguo);
                                                } catch (Exception e) {
                                                }
                                                if (getjieguo.equals("1")) {
                                                    //验证密码通过，获取账户信息
                                                    new Thread() {
                                                        public void run() {
                                                            try {
                                                                getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/huquzhanghu.php?shoujihao=" + zhanghu);
                                                                //删首尾空
                                                                getjieguo = getjieguo.trim();
                                                            } catch (Exception e) {
                                                            }
                                                            //删首尾空
                                                            getjieguo = getjieguo.trim();
                                                            //josn解析 写入shar  登录成功
                                                            Log.e("uri", getjieguo);
                                                            try {
                                                                JSONArray arr = new JSONArray(getjieguo);
                                                                JSONObject jsonObject = (JSONObject) arr.get(0);
                                                                SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                editor.putString("shoujihao", jsonObject.getString("shoujihao"));
                                                                editor.putString("userid", jsonObject.getString("userid"));
                                                                editor.putString("mima", jsonObject.getString("mima"));
                                                                editor.putString("anquanyouxiang", jsonObject.getString("anquanyouxiang"));
                                                                editor.putString("youjishu", jsonObject.getString("youjishu"));
                                                                editor.putString("rijishu", jsonObject.getString("rijishu"));
                                                                editor.putString("nicheng", jsonObject.getString("nicheng"));
                                                                editor.putString("guanzhushu", jsonObject.getString("guanzhushu"));
                                                                editor.putString("beiguanzhushu", jsonObject.getString("beiguanzhushu"));
                                                                editor.putString("icon", jsonObject.getString("icon"));
                                                                editor.putString("biaoji", "1");
                                                                editor.commit();
                                                            } catch (JSONException ex) {
                                                                Log.e("11", getjieguo);
                                                            }
                                                            handler.sendEmptyMessageDelayed(2, 500);
                                                        }
                                                    }.start();
                                                } else {
                                                    //密码错误
                                                    handler.sendEmptyMessageDelayed(3, 0);
                                                }
                                            }
                                        }.start();
                                    }
                                } else {
                                    //获取数据失败
                                    handler.sendEmptyMessageDelayed(4, 0);
                                }
                            }
                        }.start();
                    } else {
                        dissmissProgressDialog();
                        ToastUtil.show(LoginActivity.this, "密码不能为空！");
                    }
                } else {
                    dissmissProgressDialog();
                    ToastUtil.show(LoginActivity.this, "请输入正确的手机号！");
                }
            }
        });

//注册按钮
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
//忘记密码按钮
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, FindPasswordActivity.class));
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


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dissmissProgressDialog();
                    ToastUtil.show(LoginActivity.this, "该手机号还未注册！请注册");
                    break;
                case 2:
                    dissmissProgressDialog();
                    ToastUtil.show(LoginActivity.this, "登录成功");
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    LoginActivity.this.finish();
                    break;
                case 3:
                    dissmissProgressDialog();
                    ToastUtil.show(LoginActivity.this, "密码错误！请重新登录");
                    break;
                case 4:
                    dissmissProgressDialog();
                    ToastUtil.show(LoginActivity.this, "获取数据失败！检查网络");
                    break;
            }
        }
    };


    //返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


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
