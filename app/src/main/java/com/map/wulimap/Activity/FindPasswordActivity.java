package com.map.wulimap.Activity;


import android.content.Intent;
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


public class FindPasswordActivity extends AppCompatActivity {
    //初始化控件
    MaterialDialog progDialog;
    EditText editText;
    Button button;
    //初始化变量
    String getjieguo;
    String wangzhi;
    String shoujihao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_find_password);

        initsetting();

        initview();

    }


    public void initview() {
//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                FindPasswordActivity.this.finish();
            }

        });

        editText = (EditText) findViewById(R.id.shoujihao);
        button = (Button) findViewById(R.id.zhaohuimima);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                shoujihao = editText.getText().toString().trim().replaceAll(" ", "");
                if (shoujihao.equals("") || shoujihao.length() != 11) {
                    dissmissProgressDialog();
                    ToastUtil.show(FindPasswordActivity.this, "请输入正确的手机号！");
                } else {
                    new Thread() {

                        public void run() {
                            try {
                                getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/shifouzhuce.php?shoujihao=" + shoujihao + "&nicheng=090g9");

                            } catch (Exception e) {
                            }
                            //数据是否获取成功
                            if (!(getjieguo == null || getjieguo == "")) {
                                getjieguo = getjieguo.trim();
                                //手机号注册过
                                if (getjieguo.equals("1")) {
                                    new Thread() {
                                        public void run() {
                                            try {
                                                HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/zhaohuimima.php?shoujihao=" + shoujihao);
                                            } catch (Exception e) {
                                            }
                                            // 找回成功
                                            handler.sendEmptyMessageDelayed(1, 0);
                                        }
                                    }.start();

                                } else {
                                    //手机号未注册过
                                    handler.sendEmptyMessageDelayed(2, 0);
                                }
                            } else {
                                handler.sendEmptyMessageDelayed(3, 0);
                            }
                        }
                    }.start();
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

                    .content("正在找回!")
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
                    dissmissProgressDialog();
                    ToastUtil.show(FindPasswordActivity.this, "找回成功！请前往邮箱查看");
                    FindPasswordActivity.this.startActivity(new Intent(FindPasswordActivity.this, LoginActivity.class));
                    FindPasswordActivity.this.finish();
                    break;
                case 2:
                    dissmissProgressDialog();
                    ToastUtil.show(FindPasswordActivity.this, "该手机号还未注册！");
                    break;
                case 3:
                    dissmissProgressDialog();
                    ToastUtil.show(FindPasswordActivity.this, "获取数据失败！检查网络");
                    break;
            }
        }
    };
}
