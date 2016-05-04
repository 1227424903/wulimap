package com.map.wulimap.adpter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.map.wulimap.Fragment.CommentOnMeFragment;
import com.map.wulimap.Fragment.ZanMeFragment;

import io.rong.imkit.fragment.ConversationListFragment;

/**
 * Created by Administrator on 2016/5/1.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    public final int COUNT = 3;
    private String[] titles = new String[]{"消息", "评论", "赞我"};
    private Context context;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return CommentOnMeFragment.newInstance(context);
        } else {
            if (position == 2) {
                return ZanMeFragment.newInstance(context);
            } else {
                return ConversationListFragment.getInstance();
            }
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
