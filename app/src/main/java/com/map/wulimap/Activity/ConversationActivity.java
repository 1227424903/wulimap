package com.map.wulimap.Activity;


import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.andexert.library.RippleView;
import com.map.wulimap.R;

/**
 * Created by Bob on 2015/4/16.
 */
public class ConversationActivity extends ActionBarActivity {
    String nicheng;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initsetting();
        initdate();
        //唯一有用的代码，加载一个 layout
        setContentView(R.layout.activity_conversation);
        //继承的是ActionBarActivity，直接调用 自带的 Actionbar，下面是Actionbar 的配置，如果不用可忽略...
        getSupportActionBar().setTitle(nicheng);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void initdate() {
        final SharedPreferences sharedPreferences = getSharedPreferences("tarenzhanghu", MODE_PRIVATE);
        nicheng = sharedPreferences.getString("nicheng", null);

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