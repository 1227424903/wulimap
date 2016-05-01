package com.map.wulimap.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.map.wulimap.R;

public class MyAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);


        initsetting();
        initview();

    }


    public void initview() {
        //显示信息
        final SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
        String nicheng = sharedPreferences.getString("nicheng", null);
        String shoujihao = sharedPreferences.getString("shoujihao", null);
        String anquanyouxiang = sharedPreferences.getString("anquanyouxiang", null);
        String guanzhushu = sharedPreferences.getString("guanzhushu", null);
        String beiguanzhushu = sharedPreferences.getString("beiguanzhushu", null);
        String youjishu = sharedPreferences.getString("youjishu", null);
        String rijishu = sharedPreferences.getString("rijishu", null);


        TextView textView = (TextView) findViewById(R.id.shoujihao);
        textView.setText(shoujihao);
        TextView textView1 = (TextView) findViewById(R.id.nicheng);
        textView1.setText(nicheng);
        TextView textView2 = (TextView) findViewById(R.id.shoujihao1);
        textView2.setText(shoujihao);
        TextView textView3 = (TextView) findViewById(R.id.nicheng1);
        textView3.setText(nicheng);
        TextView textView4 = (TextView) findViewById(R.id.anquanyouxiang);
        textView4.setText(anquanyouxiang);
        TextView textView5 = (TextView) findViewById(R.id.youjishu);
        textView5.setText(youjishu);
        TextView textView6 = (TextView) findViewById(R.id.rijishu);
        textView6.setText(rijishu);
        TextView textView7 = (TextView) findViewById(R.id.guanzhushu);
        textView7.setText(guanzhushu);
        TextView textView8 = (TextView) findViewById(R.id.beiguanzhushu);
        textView8.setText(beiguanzhushu);


//退出登录
        Button button = (Button) findViewById(R.id.quxiaodenglu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("biaoji", "0");
                editor.commit();
                MyAccountActivity.this.startActivity(new Intent(MyAccountActivity.this, LoginActivity.class));
                MyAccountActivity.this.finish();
            }
        });


//返回
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                MyAccountActivity.this.finish();
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


}
