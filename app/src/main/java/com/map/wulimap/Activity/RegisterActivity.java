package com.map.wulimap.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.map.wulimap.R;
import com.map.wulimap.util.GetRoundedBitmapUtil;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.ToastUtil;

import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {
    //初始化控件
    MaterialDialog progDialog;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;
    Button button;
    //初始化变量
    String wangzhi;
    String getjieguo;
    String shoujihao;
    String mima;
    String querenmima;
    String anquanyouxiang;
    String nicheng;
    String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_register);

        userid = GetRoundedBitmapUtil.getRandomCode();
        initsetting();
        initview();

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
        editText1 = (EditText) findViewById(R.id.shoujihao);
        editText2 = (EditText) findViewById(R.id.mima);
        editText3 = (EditText) findViewById(R.id.querenmima);
        editText4 = (EditText) findViewById(R.id.nicheng);
        editText5 = (EditText) findViewById(R.id.anquanyouxiang);
        button = (Button) findViewById(R.id.zhuce);
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
                //空
                if (shoujihao.equals("") || mima.equals("") || querenmima.equals("") || nicheng.equals("") || anquanyouxiang.equals("")) {
                    ToastUtil.show(RegisterActivity.this, "不能为空！");
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


                                            getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/shifouzhuce.php?shoujihao=" + shoujihao + "&nicheng=" + nicheng);

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
                                                    //没有注册过，注册
                                                    new Thread() {
                                                        public void run() {
                                                            try {
                                                                HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/zhuce.php?shoujihao=" + shoujihao + "&nicheng=" + nicheng + "&mima=" + mima + "&anquanyouxiang=" + anquanyouxiang + "&userid=" + userid + "&icon=0&guanzhushou=0&beiguanzhushou=0&youjishu=0&rijishou=0");
                                                            } catch (Exception e) {
                                                            }

                                                            //注册成功
                                                            handler.sendEmptyMessageDelayed(3, 0);
                                                        }
                                                    }.start();
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
//保存到shar           dissmissProgressDialog();
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
            }
        }
    };
}
