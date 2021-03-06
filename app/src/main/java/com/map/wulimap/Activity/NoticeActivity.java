package com.map.wulimap.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.map.wulimap.R;
import com.map.wulimap.util.Constant;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.view.RefreshLayout;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NoticeActivity extends AppCompatActivity {
    //初始化控件
    RefreshLayout swipeRefreshLayout;
    MaterialDialog progDialog;
    AnimationAdapter mAnimAdapter;
    BaseAdapter baseAdapter;
    ListView listView;
    //初始化变量
    int zhi = 8;
    int zongshu;
    int biaoji = 0;
    String wangzhi;
    String shanchuyoujiid;
    String tupianming;
    String getjieguo;
    String shoujihao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        initsetting();
        initview();
//获取数据
        huoqushuju();


    }


    public void initview() {
        //返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                NoticeActivity.this.finish();
            }

        });
        listView = (ListView) findViewById(R.id.listview);

        //下拉组件设置
        swipeRefreshLayout = (RefreshLayout) findViewById(R.id.swipe_layout);
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

//加载更多
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
                ToastUtil.show(NoticeActivity.this, "已加载");
            } else {
                ToastUtil.show(NoticeActivity.this, "还没有消息!");
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
                ToastUtil.show(NoticeActivity.this, "已刷新");
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
                            SharedPreferences sharedPreferences = getSharedPreferences("tongzhi", MODE_PRIVATE);
                            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(NoticeActivity.this)
                                    .inflate(R.layout.notice_xiaomu, null);
                            TextView textView = (TextView) linearLayout.findViewById(R.id.pinglunnicheng);
                            textView.setText("小木");
                            TextView textView1 = (TextView) linearLayout.findViewById(R.id.pinglunshujian);
                            textView1.setText(sharedPreferences.getString("shijian" + j, null));
                            TextView textView2 = (TextView) linearLayout.findViewById(R.id.pinglunneirong);
                            textView2.setText(sharedPreferences.getString("nicheng" + j, null));
                            return linearLayout;
                        }
                    };

//右边出现动画

                    if (!(mAnimAdapter instanceof SwingRightInAnimationAdapter)) {
                        mAnimAdapter = new SwingRightInAnimationAdapter(baseAdapter);
                        mAnimAdapter.setAbsListView(listView);
                        listView.setAdapter(mAnimAdapter);
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            int j = zongshu - 1 - i;
                            SharedPreferences sharedPreferences = getSharedPreferences("tongzhi", MODE_PRIVATE);
                            String wangzhi = sharedPreferences.getString("neirong" + j, null);
                            String biaoti = sharedPreferences.getString("nicheng" + j, null);
                            String h5 = sharedPreferences.getString("h5" + j, null);
                            Bundle bundle = new Bundle();
                            bundle.putString("wangzhi", wangzhi);
                            bundle.putString("biaoti", biaoti);
                            if (h5.equals("0")) {
                                Intent intent = new Intent(NoticeActivity.this, WebActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(NoticeActivity.this, Webh5Activity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                        }
                    });

                    break;
                case 2:
                    ToastUtil.show(NoticeActivity.this, "获取数据失败！检查网络");

                    break;
                case 3:
                    ToastUtil.show(NoticeActivity.this, "还没有消息！");

                    break;
                case 4:
                    //第一次获取数据后，每一次  通知
                    baseAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    //Activity返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0 && resultCode == 0) {
            Bundle date = intent.getExtras();
            shanchuyoujiid = date.getString("keyword");
            if (shanchuyoujiid.equals("")) {
                //  huoqushuju();
            }

        }
    }


    //获取数据
    public void huoqushuju() {
        new Thread() {
            public void run() {
                try {
                    getjieguo = HtmlService.getHtml(Constant.PHP_URL + "gushiditu/huoquxitongxiaoxi.php");
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
                            SharedPreferences sharedPreferences = getSharedPreferences("tongzhi", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            int zongshu1 = arr.length();

                            JSONObject jsonObject;
                            int j = 0;
                            for (int i = zongshu1 - 1; i >= 0; i--) {
                                jsonObject = (JSONObject) arr.get(j);
                                editor.putString("h5" + j, jsonObject.getString("h5"));
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
