package com.map.wulimap.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.map.wulimap.R;
import com.map.wulimap.util.Constant;
import com.map.wulimap.util.GetRoundedBitmapUtil;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.ToastUtil;

import java.net.URLEncoder;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity {
    //初始化控件
    MaterialDialog progDialog;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;
    EditText editText6;
    Button button;
    Button button1;
    //初始化变量

    String yanzhengma;
    String getjieguo;
    String shoujihao;
    String mima;
    String querenmima;
    String anquanyouxiang;
    String nicheng;
    String userid;
    int jishi = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_register);

        userid = GetRoundedBitmapUtil.getRandomCode();
        initsetting();
        initview();

//初始化短信
        SMSSDK.initSDK(this, "12c58839e4a5c", "c51317979cd1eb35bf51e4a556204907");
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功  注册
                        new Thread() {
                            public void run() {
                                try {
                                    HtmlService.getHtml(Constant.PHP_URL + "gushiditu/zhuce.php?shoujihao=" + shoujihao + "&nicheng=" + nicheng + "&mima=" + mima + "&anquanyouxiang=" + anquanyouxiang + "&userid=" + userid + "&icon=0&guanzhushu=0&beiguanzhushu=0&youjishu=0&rijishu=0");
                                } catch (Exception e) {
                                }

                                //注册成功
                                handler.sendEmptyMessageDelayed(3, 0);
                            }
                        }.start();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功  倒计时
                        ToastUtil.show(RegisterActivity.this, "获取成功！请及时输入");
                        jishi = 60;
                        handler.sendEmptyMessageDelayed(5, 1000);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

    }


    public void initview() {
//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                RegisterActivity.this.finish();
            }

        });
//手机号监听
        editText1 = (EditText) findViewById(R.id.shoujihao);
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                shoujihao = editText1.getText().toString().trim().replaceAll(" ", "");
                if (shoujihao.length() == 11) {
                    button1.setEnabled(true);
                } else {
                    button1.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText2 = (EditText) findViewById(R.id.mima);
        editText3 = (EditText) findViewById(R.id.querenmima);
        editText4 = (EditText) findViewById(R.id.nicheng);
        editText5 = (EditText) findViewById(R.id.anquanyouxiang);
        editText6 = (EditText) findViewById(R.id.yanzhengma);
        button = (Button) findViewById(R.id.zhuce);
//获取按钮
        button1 = (Button) findViewById(R.id.huoqu);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shoujihao = editText1.getText().toString().trim().replaceAll(" ", "");
                //空
                if (shoujihao.equals("") || shoujihao.length() != 11) {
                    ToastUtil.show(RegisterActivity.this, "请输入正确的手机号！");
                } else {
                    //获取验证码
                    SMSSDK.getVerificationCode("+86", shoujihao);
                }
            }
        });

//注册按钮
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                shoujihao = editText1.getText().toString().trim().replaceAll(" ", "");
                mima = editText2.getText().toString().trim().replaceAll(" ", "");
                querenmima = editText3.getText().toString().trim().replaceAll(" ", "");
                nicheng = editText4.getText().toString().trim().replaceAll(" ", "");
                anquanyouxiang = editText5.getText().toString().trim().replaceAll(" ", "");
                yanzhengma = editText6.getText().toString().trim().replaceAll(" ", "");
                //空
                if (shoujihao.equals("") || mima.equals("") || querenmima.equals("") || nicheng.equals("") || anquanyouxiang.equals("")) {
                    ToastUtil.show(RegisterActivity.this, "手机号不能为空！");
                    dissmissProgressDialog();
                } else {
                    //密码长度短
                    if (shoujihao.length() == 11) {
                        if (mima.length() > 5) {
                            //两次密码不一致
                            if (mima.equals(querenmima)) {
                                new Thread() {
                                    public void run() {
                                        try {
                                            //url编码
                                            try {
                                                nicheng = URLEncoder.encode(nicheng, "utf-8");
                                                mima = URLEncoder.encode(mima, "utf-8");
                                                anquanyouxiang = URLEncoder.encode(anquanyouxiang, "utf-8");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            getjieguo = HtmlService.getHtml(Constant.PHP_URL + "gushiditu/shifouzhuce.php?shoujihao=" + shoujihao + "&nicheng=" + nicheng);
                                        } catch (Exception e) {
                                        }
                                        //获取数据是否成功
                                        if (!(getjieguo == null || getjieguo == "")) {
                                            //删首尾空
                                            getjieguo = getjieguo.trim();
                                            if (getjieguo.equals("1")) {
                                                //手机号已注册
                                                handler.sendEmptyMessageDelayed(1, 0);
                                            } else {
                                                if (getjieguo.equals("2")) {
                                                    //昵称已用
                                                    handler.sendEmptyMessageDelayed(2, 0);
                                                } else {
                                                    //一切ok
                                                    //提交验证码
                                                    SMSSDK.submitVerificationCode("+86", shoujihao, yanzhengma);
                                                }
                                            }
                                        } else {
                                            handler.sendEmptyMessageDelayed(4, 0);
                                        }
                                    }
                                }.start();
                            } else {
                                dissmissProgressDialog();
                                ToastUtil.show(RegisterActivity.this, "两次密码不一致！");
                            }
                        } else {
                            dissmissProgressDialog();
                            ToastUtil.show(RegisterActivity.this, "密码长度小于6！");
                        }
                    } else {
                        dissmissProgressDialog();
                        ToastUtil.show(RegisterActivity.this, "手机号格式不正确！");

                    }
                }
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


    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .content("正在注册!")
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


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ToastUtil.show(RegisterActivity.this, "该手机号已经注册过！请登录");
                    dissmissProgressDialog();
                    break;
                case 2:
                    ToastUtil.show(RegisterActivity.this, "该昵称已经被占用！请修改昵称");
                    dissmissProgressDialog();
                    break;
                case 3:
                    //注册成功
                    //保存到shar
                    dissmissProgressDialog();
                    SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("shoujihao", shoujihao);
                    editor.putString("mima", mima);
                    editor.putString("nicheng", nicheng);
                    editor.putString("anquanyouxiang", anquanyouxiang);
                    editor.putString("youjishu", "0");
                    editor.putString("rijishu", "0");
                    editor.putString("guanzhushu", "0");
                    editor.putString("beiguanzhushu", "0");
                    editor.putString("icon", "0");
                    editor.putString("biaoji", "0");
                    editor.commit();
                    ToastUtil.show(RegisterActivity.this, "注册成功！");
                    RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    RegisterActivity.this.finish();
                    break;
                case 4:
                    dissmissProgressDialog();
                    ToastUtil.show(RegisterActivity.this, "获取数据失败！检查网络");
                    break;
                case 5:
                    //倒计时
                    if (jishi == 0) {
                        button1.setText("获取");
                        button1.setEnabled(true);
                    } else {
                        button1.setEnabled(false);
                        button1.setText(Integer.toString(jishi));
                        jishi = jishi - 1;
                        handler.sendEmptyMessageDelayed(5, 1000);
                    }
                    break;
            }
        }
    };
}
