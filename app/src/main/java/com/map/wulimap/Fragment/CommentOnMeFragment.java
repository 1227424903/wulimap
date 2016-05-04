package com.map.wulimap.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.map.wulimap.R;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.view.RefreshLayout;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentOnMeFragment extends Fragment {
    static CommentOnMeFragment fragment;
    static Context context1;
    View view;

    //初始化数据
    String tupianming;
    String getjieguo;
    String shoujihao;
    String wangzhi;
    String shanchuyoujiid;
    int zhi = 8;
    int zongshu;
    int biaoji = 0;
    //初始化控件
    RefreshLayout swipeRefreshLayout;

    AnimationAdapter mAnimAdapter;
    BaseAdapter baseAdapter;
    ListView listView;
    private OnFragmentInteractionListener mListener;

    public static CommentOnMeFragment newInstance(Context context) {
        fragment = new CommentOnMeFragment();
        context1 = context;
        return fragment;
    }

    public CommentOnMeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_comment_on_me, container, false);

        initdate();
        initview();

        //获取数据
        huoqushuju();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
    }


    public void initdate() {
//获取数据
        final SharedPreferences sharedPreferences = context1.getSharedPreferences("zhanghu", Context.MODE_PRIVATE);
        shoujihao = sharedPreferences.getString("shoujihao", null);
    }


    public void initview() {


//初始化组件
        listView = (ListView) view.findViewById(R.id.listview);
        //下拉组件设置
        swipeRefreshLayout = (RefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        // 设置下拉多少距离之后开始刷新数据
        swipeRefreshLayout.setDistanceToTriggerSync(100);
        // 设置进度条背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(this.getResources().getColor(android.R.color.holo_blue_light));
        // 设置刷新动画的颜色，可以设置1或者更多.
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_green_light));
        //下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //刷新操作
                        LongTimeOperationTask task = new LongTimeOperationTask();
                        task.execute();
                    }
                });
        swipeRefreshLayout.post(new Runnable() {
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        swipeRefreshLayout.postDelayed(new Runnable() {
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);


        swipeRefreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                ////加载更多操作
                swipeRefreshLayout.post(new Runnable() {
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                swipeRefreshLayout.postDelayed(new Runnable() {
                    public void run() {
                        swipeRefreshLayout.setLoading(false);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
                LongTimeOperationTask1 task = new LongTimeOperationTask1();
                task.execute();

            }
        });

    }


    //单线程任务监听器   加载
    private class LongTimeOperationTask1 extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            //每次加二
            int zhi1;
            zhi1 = zhi + 2;
            if (zhi1 > zongshu) {
                zhi = zongshu;
            } else {
                zhi = zhi + 2;
            }
            SystemClock.sleep(1000);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //关闭对话框
            if (!(zongshu == 0)) {
                baseAdapter.notifyDataSetChanged();
                ToastUtil.show(context1, "已加载");
            } else {
                ToastUtil.show(context1, "还没有消息!");
            }

        }
    }


    //单线程任务监听器  下拉刷新
    private class LongTimeOperationTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            //获取数据
            huoqushuju();
            SystemClock.sleep(2000);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //关闭对话框
            swipeRefreshLayout.setRefreshing(false);
            if (!(zongshu == 0)) {
                ToastUtil.show(context1, "已刷新");
            }
        }
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //适配器适配
                    biaoji = 1;
                    baseAdapter = new BaseAdapter() {
                        @Override
                        public int getCount() {
                            if (zongshu > 8) {
                                return zhi;
                            } else {
                                return zongshu;
                            }
                        }

                        @Override
                        public Object getItem(int i) {
                            return null;
                        }

                        @Override
                        public long getItemId(int i) {
                            return i;
                        }

                        @Override
                        public View getView(int i, View view, ViewGroup viewGroup) {
                            int j = zongshu - 1 - i;
                            SharedPreferences sharedPreferences = context1.getSharedPreferences("pinglunwo", Context.MODE_PRIVATE);
                            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context1)
                                    .inflate(R.layout.comment_bule, null);
                            TextView textView = (TextView) linearLayout.findViewById(R.id.pinglunnicheng);
                            textView.setText(sharedPreferences.getString("nicheng" + j, null));
                            TextView textView1 = (TextView) linearLayout.findViewById(R.id.pinglunshujian);
                            textView1.setText(sharedPreferences.getString("shijian" + j, null));
                            TextView textView2 = (TextView) linearLayout.findViewById(R.id.pinglunneirong);
                            textView2.setText(sharedPreferences.getString("neirong" + j, null));
                            return linearLayout;
                        }
                    };
                    //下面出现动画
                    // SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(baseAdapter,PopActivity.this));
                    // swingBottomInAnimationAdapter.setAbsListView(listView);
                    // assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    //  swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
//右边出现动画

                    if (!(mAnimAdapter instanceof SwingRightInAnimationAdapter)) {
                        mAnimAdapter = new SwingRightInAnimationAdapter(baseAdapter);
                        mAnimAdapter.setAbsListView(listView);
                        listView.setAdapter(mAnimAdapter);
                    }


                    break;
                case 2:
                    ToastUtil.show(context1, "获取数据失败！检查网络");

                    break;
                case 3:
                    ToastUtil.show(context1, "还没有消息！");

                    break;
                case 4:
                    //第一次获取数据后，每一次  通知
                    baseAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    //获取数据
    public void huoqushuju() {
        new Thread() {
            public void run() {
                try {
                    getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/pinglunwoyouji.php?shoujihao=" + shoujihao);
                } catch (Exception e) {
                }

                if (!(getjieguo == null || getjieguo == "")) {
                    //删首尾空
                    getjieguo = getjieguo.trim();
                    //josn解析 写入shar
                    if (!getjieguo.equals("0")) {
                        //josn解析 写入shar
                        try {
                            JSONArray arr = new JSONArray(getjieguo);
                            SharedPreferences sharedPreferences = context1.getSharedPreferences("pinglunwo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            int zongshu1 = arr.length();

                            JSONObject jsonObject;
                            int j = 0;
                            for (int i = zongshu1 - 1; i >= 0; i--) {
                                jsonObject = (JSONObject) arr.get(j);

                                editor.putString("shijian" + j, jsonObject.getString("shijian"));
                                editor.putString("nicheng" + j, jsonObject.getString("nicheng"));
                                editor.putString("neirong" + j, jsonObject.getString("neirong"));
                                editor.commit();
                                j++;
                            }
                            zongshu = j;
                            editor.putInt("zongshu", j);
                            editor.commit();
                        } catch (JSONException ex) {
                            Log.e("11", wangzhi);
                        }
                        //第1次获取数据
                        if (biaoji == 0) {
                            //适配
                            handler.sendEmptyMessageDelayed(1, 1000);
                        } else {
                            //通知适配器更新
                            handler.sendEmptyMessageDelayed(4, 1000);
                        }
                    } else {
                        //没有日记
                        zhi = 0;
                        zongshu = 0;
                        if (biaoji == 1) {
                            handler.sendEmptyMessageDelayed(4, 1000);
                        }
                        handler.sendEmptyMessageDelayed(3, 1000);
                    }
                } else {
                    //网络失败
                    handler.sendEmptyMessageDelayed(2, 1000);
                }
            }
        }.start();
    }


}
