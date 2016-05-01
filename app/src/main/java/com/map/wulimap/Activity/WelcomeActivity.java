package com.map.wulimap.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import java.util.ArrayList;

import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.map.wulimap.R;
import com.map.wulimap.adpter.ViewPagerAdapter;


public class WelcomeActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener {
    //初始化控件
    TextView textView;
    ViewPager viewPager;
    ViewPagerAdapter vpAdapter;
    ArrayList<View> views;
    ImageView[] points;
    //初始化图片变量
    int[] pics = {R.drawable.picture_1, R.drawable.picture_2,
            R.drawable.picture_3, R.drawable.picture_4};
    int currentIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }


    //获取组件
    private void initView() {
//全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.content_welcome);

//跳过
        textView = (TextView) findViewById(R.id.tiaoguo);
        textView.setVisibility(View.GONE);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                WelcomeActivity.this.finish();
            }
        });

        views = new ArrayList<View>();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        vpAdapter = new ViewPagerAdapter(views);
    }


    //获取图片
    private void initData() {
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setScaleType(ScaleType.FIT_XY);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }
        viewPager.setAdapter(vpAdapter);
        viewPager.setOnPageChangeListener(this);
        initPoint();
    }


    //获取点
    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
        points = new ImageView[pics.length];
        for (int i = 0; i < pics.length; i++) {
            points[i] = (ImageView) linearLayout.getChildAt(i);
            points[i].setEnabled(true);
            points[i].setTag(i);
        }
        currentIndex = 0;
        points[currentIndex].setEnabled(false);
    }


    //viewpage监听器
    public void onPageScrollStateChanged(int arg0) {
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void onPageSelected(int arg0) {
        setCurDot(arg0);
    }


    //设置点的颜色
    private void setCurDot(int positon) {
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);
        currentIndex = positon;
        if (positon == 3) {
            textView.setVisibility(View.VISIBLE);
        }
    }


}
