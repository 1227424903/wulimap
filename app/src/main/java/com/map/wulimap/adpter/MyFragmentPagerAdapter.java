package com.map.wulimap.adpter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.map.wulimap.Fragment.CommentOnMeFragment;
import com.map.wulimap.Fragment.ZanMeFragment;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

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
                ConversationListFragment fragment = new ConversationListFragment();
                Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                        .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                        .build();

                fragment.setUri(uri);
                return fragment;
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
