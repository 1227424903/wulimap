package com.map.wulimap.adpter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

//介绍页面
//view page adpter
public class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> views;

    public ViewPagerAdapter(ArrayList<View> views) {
        this.views = views;
    }


    public int getCount() {
        if (views != null) {
            return views.size();
        } else return 0;
    }


    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }


    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(views.get(position));
    }


    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(views.get(position), 0);
        return views.get(position);
    }
}
