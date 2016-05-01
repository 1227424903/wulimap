package com.map.wulimap.adpter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.map.wulimap.Fragment.CommentOnMeFragment;
import com.map.wulimap.Fragment.ZanMeFragment;

/**
 * Created by Administrator on 2016/5/1.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    public final int COUNT = 2;
    private String[] titles = new String[]{"评论", "赞我"};
    private Context context;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return CommentOnMeFragment.newInstance(context);
        } else {
            return ZanMeFragment.newInstance(context);
        }


    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
