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
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.ToastUtil;

import java.net.URLEncoder;

public class ChangePasswprdActivity extends AppCompatActivity {
    //初始化控件
    MaterialDialog progDialog;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    Button button;
    //初始化变量
    String shoujihao;
    String getjieguo;
    String wangzhi;
    String jiumima;
    String xinmima;
    String zaicishuru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_change_password);


        initsetting();

        initview();

    }


    public void initview() {
        //返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ChangePasswprdActivity.this.finish();
            }

        });
        editText1 = (EditText) findViewById(R.id.shoujihao);
        editText2 = (EditText) findViewById(R.id.jiumima);
        editText3 = (EditText) findViewById(R.id.xinmima);
        editText4 = (EditText) findViewById(R.id.zaicishuru);
        button = (Button) findViewById(R.id.xiugaimima);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                shoujihao = editText1.getText().toString().trim().replaceAll(" ", "");
                jiumima = editText2.getText().toString().trim().replaceAll(" ", "");
                xinmima = editText3.getText().toString().trim().replaceAll(" ", "");
                zaicishuru = editText4.getText().toString().trim().replaceAll(" ", "");
                if (shoujihao.equals("") || xinmima.equals("") || jiumima.equals("") || zaicishuru.equals("")) {
                    ToastUtil.show(ChangePasswprdActivity.this, "不能为空！");
                    dissmissProgressDialog();
                } else {
                    if (shoujihao.length() == 11) {
                        if (xinmima.equals(zaicishuru)) {
                            if (xinmima.length() > 5) {
                                new Thread() {

                                    public void run() {
                                        try {
                                            getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/shifouzhuce.php?shoujihao=" + shoujihao + "&nicheng=090g9");

                                        } catch (Exception e) {
                                        }

                                        //数据是否获取成功
                                        if (!(getjieguo == null || getjieguo == "")) {

                                            //删首尾空
                                            getjieguo = getjieguo.trim();
                                            //手机号注册过
                                            if (getjieguo.equals("1")) {
                                                new Thread() {
                                                    public void run() {
                                                        try {

                                                            //url编码
                                                            try {
                                                                jiumima = URLEncoder.encode(jiumima, "utf-8");
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                            getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/denglu.php?shoujihao=" + shoujihao + "&mima=" + jiumima);
                                                            //删首尾空
                                                            getjieguo = getjieguo.trim();

                                                        } catch (Exception e) {
                                                        }
                                                        //验证通过
                                                        if (getjieguo.equals("1")) {
                                                            new Thread() {
                                                                public void run() {
                                                                    try {


                                                                        //url编码
                                                                        try {
                                                                            xinmima = URLEncoder.encode(xinmima, "utf-8");
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }


                                                                        HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/xiugaimima.php?shoujihao=" + shoujihao + "&mima=" + xinmima);
                                                                    } catch (Exception e) {
                                                                    }
                                                                    //修改成功
                                                                    handler.sendEmptyMessageDelayed(3, 0);
                                                                }
                                                            }.start();
                                                        } else {
                                                            //旧密码验证不通过
                                                            handler.sendEmptyMessageDelayed(1, 0);
                                                        }
                                                    }
                                                }.start();
                                            } else {
                                                //手机号未注册过
                                                handler.sendEmptyMessageDelayed(2, 0);
                                            }
                                        } else {
                                            handler.sendEmptyMessageDelayed(4, 0);
                                        }
                                    }
                                }.start();
                            } else {
                                dissmissProgressDialog();
                                ToastUtil.show(ChangePasswprdActivity.this, "密码长度小于6！");
                            }
                        } else {
                            dissmissProgressDialog();
                            ToastUtil.show(ChangePasswprdActivity.this, "两次输入新密码不一致");
                        }
                    } else {
                        dissmissProgressDialog();
                        ToastUtil.show(ChangePasswprdActivity.this, "手机号格式不正确！");
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


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dissmissProgressDialog();
                    ToastUtil.show(ChangePasswprdActivity.this, "旧密码错误！");
                    break;
                case 2:
                    dissmissProgressDialog();
                    ToastUtil.show(ChangePasswprdActivity.this, "该手机号还未注册！");
                    break;
                case 3:
                    dissmissProgressDialog();
                    SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("biaoji", "0");
                    editor.commit();
                    ToastUtil.show(ChangePasswprdActivity.this, "修改成功！");
                    ChangePasswprdActivity.this.startActivity(new Intent(ChangePasswprdActivity.this, LoginActivity.class));
                    ChangePasswprdActivity.this.finish();
                    break;
                case 4:
                    dissmissProgressDialog();
                    ToastUtil.show(ChangePasswprdActivity.this, "获取数据失败！检查网络");
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

                    .content("正在修改!")
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
