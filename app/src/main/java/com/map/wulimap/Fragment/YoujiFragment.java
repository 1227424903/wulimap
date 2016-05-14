package com.map.wulimap.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.map.wulimap.Activity.YoujiCircleActivity;
import com.map.wulimap.Activity.YoujiDetailActivity;
import com.map.wulimap.R;
import com.map.wulimap.util.Constant;
import com.map.wulimap.util.DownloadUtil;
import com.map.wulimap.util.FileUtil;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.LoadAndSaveImage;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.view.RefreshLayout;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class YoujiFragment extends Fragment implements OnDismissCallback {
    //初始化控件
    static YoujiFragment fragment;
    static Context context1;
    View view;
    private OnFragmentInteractionListener mListener;
    RefreshLayout swipeRefreshLayout;
    BaseAdapter baseAdapter;
    ListView listView;
    //初始化变量
    int zhi = 8;
    int zongshu;
    int biaoji = 0;
    String wangzhi;
    String tupianming;
    String getjieguo;
    String bianmatupianming;
    String shoujihao;
    JSONArray arr1;
    JSONObject jsonObject1;

    public static YoujiFragment newInstance(Context context) {
        fragment = new YoujiFragment();
        context1 = context;
        return fragment;
    }

    public YoujiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_focus_youriji, container, false);

        initview();
        initdate();

//获取数据
        huoqushuju();

        return view;
    }


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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    public void initview() {
//游记圈按钮
        final RippleView rippleView = (RippleView) view.findViewById(R.id.weiguanzhu);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                context1.startActivity(new Intent(context1, YoujiCircleActivity.class));
            }
        });

//广播按钮
        final RippleView rippleView1 = (RippleView) view.findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent();
                intent.setAction("Main22Activitymyreceive");
                intent.putExtra("fanhui", "0");
                context1.sendBroadcast(intent);
            }

        });

//初始化控件
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




    public void initdate() {
//获取数据
        final SharedPreferences sharedPreferences = context1.getSharedPreferences("zhanghu", Context.MODE_PRIVATE);
        shoujihao = sharedPreferences.getString("shoujihao", null);
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
                ToastUtil.show(context1, "还没有关注游记!");
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
                            SharedPreferences sharedPreferences = context1.getSharedPreferences("guanzhuyouji", Context.MODE_PRIVATE);
                            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context1)
                                    .inflate(R.layout.youji_detail, null);

                            TextView textView = (TextView) linearLayout.findViewById(R.id.nicheng);
                            textView.setText(sharedPreferences.getString("nicheng" + j, null));
                            TextView textView1 = (TextView) linearLayout.findViewById(R.id.shijian);
                            textView1.setText(sharedPreferences.getString("shijian" + j, null));
                            TextView textView2 = (TextView) linearLayout.findViewById(R.id.neirong);
                            textView2.setText(sharedPreferences.getString("neirong" + j, null));
                            TextView textView3 = (TextView) linearLayout.findViewById(R.id.didian);
                            textView3.setText("地点:" + sharedPreferences.getString("didian" + j, null));


                            final ImageView imageview = (ImageView) linearLayout.findViewById(R.id.tupian);
                            tupianming = sharedPreferences.getString("tupian" + j, null);
                            if (FileUtil.wenjianshifoucunzai("/sdcard/map/" + tupianming + "-yasuo.jpg")) {
                                Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/map/" + tupianming + "-yasuo.jpg");
                                imageview.setImageBitmap(bitmap);
                            } else {
                                //url编码
                                try {
                                    bianmatupianming = URLEncoder.encode(tupianming, "utf-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                new LoadAndSaveImage(context1, imageview, tupianming + "-yasuo.jpg", Constant.PICTURE_URI + bianmatupianming + "-yasuo.jpg@294w_165h_1l", "/sdcard/map/");

                            }

                            TextView textView4 = (TextView) linearLayout.findViewById(R.id.zanshu);
                            textView4.setText(sharedPreferences.getString("zanshu" + j, null));
                            TextView textView5 = (TextView) linearLayout.findViewById(R.id.pinglunshu);
                            textView5.setText(sharedPreferences.getString("pinglunshu" + j, null));
                            Button button = (Button) linearLayout.findViewById(R.id.guanzhu);
                            button.setText("游记");
                            return linearLayout;
                        }
                    };
                    //下面出现动画
                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(baseAdapter, fragment));
                    swingBottomInAnimationAdapter.setAbsListView(listView);
                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);



                    listView.setAdapter(swingBottomInAnimationAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.e("uri", "dddd");
                            Bundle bundle = new Bundle();
                            int m = zongshu - 1 - i;
                            String index = Integer.toString(m);
                            SharedPreferences sharedPreferences5 = context1.getSharedPreferences("guanzhuyouji", Context.MODE_PRIVATE);
                            bundle.putString("youjiid", sharedPreferences5.getString("youjiid" + index, null));
                            bundle.putString("shoujihao", sharedPreferences5.getString("shoujihao" + index, null));
                            bundle.putString("nicheng", sharedPreferences5.getString("nicheng" + index, null));
                            bundle.putString("shijian", sharedPreferences5.getString("shijian" + index, null));
                            bundle.putString("didian", sharedPreferences5.getString("didian" + index, null));
                            bundle.putString("jinwei", sharedPreferences5.getString("jinwei" + index, null));
                            bundle.putString("tupian", sharedPreferences5.getString("tupian" + index, null));
                            bundle.putString("zanshu", sharedPreferences5.getString("zanshu" + index, null));
                            bundle.putString("pinglunshu", sharedPreferences5.getString("pinglunshu" + index, null));
                            bundle.putString("neirong", sharedPreferences5.getString("neirong" + index, null));
                            Intent intent = new Intent(context1, YoujiDetailActivity.class);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 0);
                        }
                    });

                    break;
                case 2:
                    ToastUtil.show(context1, "获取数据失败！检查网络");

                    break;
                case 3:
                    ToastUtil.show(context1, "还没有关注游记！");

                    listView.setBackground(getResources().getDrawable(R.drawable.icon_no_news));
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
                    getjieguo = HtmlService.getHtml(Constant.PHP_URL + "gushiditu/huoquguanzhuren.php?shoujihao=" + shoujihao);
                } catch (Exception e) {
                }

                if (!(getjieguo == null || getjieguo == "")) {
                    //删首尾空
                    getjieguo = getjieguo.trim();
                    //josn解析 写入shar
                    if (getjieguo.equals("0")) {
                        handler.sendEmptyMessageDelayed(3, 1000);
                    } else {
                        try {
                            arr1 = new JSONArray(getjieguo);
                        } catch (JSONException ex) {
                        }


                        try {
                            wangzhi = HtmlService.getHtml(Constant.PHP_URL + "gushiditu/huoquyouji.php");

                        } catch (Exception e) {
                        }

                        //联网问题
                        if (!(wangzhi == null || wangzhi == "")) {
                            //删首尾空
                            wangzhi = wangzhi.trim();
                            //获取数据是否为空
                            if (!wangzhi.equals("0")) {
                                Log.e("uri", wangzhi);
                                //josn解析 写入shar
                                try {
                                    JSONArray arr = new JSONArray(wangzhi);
                                    SharedPreferences sharedPreferences = context1.getSharedPreferences("guanzhuyouji", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    int zongshu1 = arr.length();

                                    JSONObject jsonObject;
                                    int j = 0;
                                    int iii = 0;
                                    for (int i = zongshu1 - 1; i >= 0; i--) {
                                        jsonObject = (JSONObject) arr.get(j);

                                        for (int ii = 0; ii < arr1.length(); ii++) {
                                            jsonObject1 = (JSONObject) arr1.get(ii);

                                            if (jsonObject1.getString("nicheng1").equals(jsonObject.getString("nicheng"))) {
                                                editor.putString("shoujihao" + iii, jsonObject.getString("shoujihao"));
                                                editor.putString("pinglunshu" + iii, jsonObject.getString("pinglunshu"));
                                                editor.putString("zanshu" + iii, jsonObject.getString("zanshu"));
                                                editor.putString("shijian" + iii, jsonObject.getString("shijian"));
                                                editor.putString("nicheng" + iii, jsonObject.getString("nicheng"));
                                                editor.putString("didian" + iii, jsonObject.getString("didian"));
                                                editor.putString("jinwei" + iii, jsonObject.getString("jinwei"));
                                                editor.putString("tupian" + iii, jsonObject.getString("tupian"));
                                                editor.putString("neirong" + iii, jsonObject.getString("neirong"));
                                                editor.putString("youjiid" + iii, jsonObject.getString("id"));
                                                editor.commit();
                                                iii = iii + 1;
                                            }
                                        }

                                        j++;
                                    }
                                    zongshu = iii;
                                    editor.putInt("zongshu", iii);
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

                } else {
                    //网络失败
                    handler.sendEmptyMessageDelayed(2, 1000);
                }
            }
        }.start();
    }


    //消失回调
    @Override
    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            //  mGoogleCardsAdapter.remove(position);
        }
    }


}
