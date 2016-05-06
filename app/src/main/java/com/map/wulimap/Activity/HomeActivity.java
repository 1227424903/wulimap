package com.map.wulimap.Activity;


import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.map.wulimap.Fragment.HomeFragment;
import com.map.wulimap.Fragment.PopFragment;
import com.map.wulimap.Fragment.RijiFragment;
import com.map.wulimap.Fragment.YoujiFragment;
import com.map.wulimap.R;
import com.map.wulimap.listener.ConversationBehaviorListener;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.ToastUtil;
import com.umeng.update.UmengUpdateAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, PopFragment.OnFragmentInteractionListener, YoujiFragment.OnFragmentInteractionListener, RijiFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener {

    //四个下标  图片 文字组成
    ImageView mBut1, mBut2, mBut3, mBut4;
    TextView mCateText1, mCateText2, mCateText3, mCateText4;
    LinearLayout linearLayouttuijian, linearLayoutyouji, linearLayoutriji, linearLayouthome;
    //  Fragment
    Fragment popfragment;
    Fragment Youjifragment;
    Fragment Rijifragment;
    Fragment Homefragment;
    Fragment mContent;
    FragmentManager fm;
    ConversationBehaviorListener conversationBehaviorListener;
    static private DrawerLayout drawer;
    private String mima;

    private String userid;
    private String nicheng;
    private String getjieguo;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initdate();
        initsetting();

        initview();
    }


    public void initview() {
//初始化操作
        prepareView();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//左侧界面
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//滑动监听
        navigationView.setNavigationItemSelectedListener(this);
//友盟更新
        SharedPreferences sharedPreferencesgx = getSharedPreferences("shezhi", MODE_PRIVATE);
        if ("1".equals(sharedPreferencesgx.getString("genxin", null))) {
            UmengUpdateAgent.setUpdateOnlyWifi(false);
            UmengUpdateAgent.update(this);
        }

        //启动地图
        LinearLayout ditu = (LinearLayout) findViewById(R.id.ditu);
        ditu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.this.startActivity(new Intent(HomeActivity.this, MapActivity.class));
                HomeActivity.this.overridePendingTransition(R.anim.open, R.anim.stay);
            }
        });
    }


    public void initdate() {
//获取信息
        SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
        mima = sharedPreferences.getString("mima", null);
        userid = sharedPreferences.getString("userid", null);
        nicheng = sharedPreferences.getString("nicheng", null);

        //融云初始化
        new Thread() {
            public void run() {
                inittoken();
            }
        }.start();
    }


    public void initsetting() {
        //沉浸模式
        Window window = getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }


    //调取view
    private void prepareView() {
        mBut1 = (ImageView) findViewById(R.id.imageView1);
        mBut2 = (ImageView) findViewById(R.id.imageView2);
        mBut3 = (ImageView) findViewById(R.id.imageView3);
        mBut4 = (ImageView) findViewById(R.id.imageView4);

        linearLayouttuijian = (LinearLayout) findViewById(R.id.tuijian);
        linearLayouttuijian.setOnClickListener(this);
        linearLayoutyouji = (LinearLayout) findViewById(R.id.youji);
        linearLayoutyouji.setOnClickListener(this);
        linearLayoutriji = (LinearLayout) findViewById(R.id.rriji);
        linearLayoutriji.setOnClickListener(this);
        linearLayouthome = (LinearLayout) findViewById(R.id.Zzhuye);
        linearLayouthome.setOnClickListener(this);

        mCateText1 = (TextView) findViewById(R.id.textView1);
        mCateText2 = (TextView) findViewById(R.id.textView2);
        mCateText3 = (TextView) findViewById(R.id.textView3);
        mCateText4 = (TextView) findViewById(R.id.textView4);

        //初始framgment
        popfragment = PopFragment.newInstance(HomeActivity.this);
        Youjifragment = YoujiFragment.newInstance(HomeActivity.this);
        Rijifragment = RijiFragment.newInstance(HomeActivity.this);
        Homefragment = HomeFragment.newInstance(HomeActivity.this);
        mContent = popfragment;
        fm = getFragmentManager();
        fm.beginTransaction().add(R.id.fragmentcontent, mContent, Integer.toString(0)).commit();
        icon1();
    }


    //下标监听器
    @Override
    public void onClick(View v) {
        int checkedId = v.getId();
        switch (checkedId) {
            case R.id.tuijian:
                icon1();
                switchContent(mContent, popfragment, 0);
                break;
            case R.id.youji:
                icon2();
                switchContent(mContent, Youjifragment, 1);
                break;
            case R.id.rriji:
                icon3();
                switchContent(mContent, Rijifragment, 2);
                break;
            case R.id.Zzhuye:
                icon4();
                switchContent(mContent, Homefragment, 3);
                break;
            default:
                break;
        }
    }


    //左侧菜单单击事件
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.wodezhuye) {


            HomeActivity.this.startActivity(new Intent(HomeActivity.this, MyAccountActivity.class));
        } else if (id == R.id.woderiji) {


            LinearLayout linearLayoutq = (LinearLayout) getLayoutInflater().inflate(R.layout.riji_password, null);
            final EditText editText = (EditText) linearLayoutq.findViewById(R.id.mima);
            final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(HomeActivity.this).create();
            final TextView textViewq = (TextView) linearLayoutq.findViewById(R.id.yanzheng);
            textViewq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String shurumima = editText.getText().toString().trim();
                    if (mima.equals(shurumima)) {
                        ToastUtil.show(HomeActivity.this, "验证通过！");
                        dialog.hide();
                        HomeActivity.this.startActivity(new Intent(HomeActivity.this, My_rijiActivity.class));
                    } else {
                        dialog.hide();
                        ToastUtil.show(HomeActivity.this, "验证失败！");
                    }
                }
            });
            dialog.setIcon(R.drawable.app_icon);
            dialog.setTitle("安全验证");
            dialog.setView(linearLayoutq); // 自定义dialog
            dialog.show();
        } else if (id == R.id.wodeyouji) {


            HomeActivity.this.startActivity(new Intent(HomeActivity.this, My_youjiActivity.class));
        } else if (id == R.id.ditulvxing) {


            HomeActivity.this.startActivity(new Intent(HomeActivity.this, NoticeActivity.class));
        } else if (id == R.id.shezhi) {


            HomeActivity.this.startActivity(new Intent(HomeActivity.this, SetActivity.class));
        }
        return true;
    }


    //接受广播
    static public class myreceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // String a= intent.getStringExtra("fanhui");
            drawer.openDrawer(GravityCompat.START);
        }
    }

    //回调
    @Override
    public void onFragmentInteraction(Uri uri) {
    }


    public void icon1() {
        mCateText1.setTextColor(getResources().getColor(R.color.bulu));
        mBut1.setImageResource(R.drawable.icon_tuijian_2);
        linearLayouttuijian.setEnabled(false);
        mCateText2.setTextColor(getResources().getColor(R.color.white2));
        mBut2.setImageResource(R.drawable.icon_youji_1);
        linearLayoutyouji.setEnabled(true);
        mCateText3.setTextColor(getResources().getColor(R.color.white2));
        mBut3.setImageResource(R.drawable.icon_riji_1);
        linearLayoutriji.setEnabled(true);
        mCateText4.setTextColor(getResources().getColor(R.color.white2));
        mBut4.setImageResource(R.drawable.icon_home_1);
        linearLayouthome.setEnabled(true);
    }

    public void icon2() {
        mCateText1.setTextColor(getResources().getColor(R.color.white2));
        mBut1.setImageResource(R.drawable.icon_tuijian_1);
        linearLayouttuijian.setEnabled(true);
        mCateText2.setTextColor(getResources().getColor(R.color.bulu));
        mBut2.setImageResource(R.drawable.icon_youji_2);
        linearLayoutyouji.setEnabled(false);
        mCateText3.setTextColor(getResources().getColor(R.color.white2));
        mBut3.setImageResource(R.drawable.icon_riji_1);
        linearLayoutriji.setEnabled(true);
        mCateText4.setTextColor(getResources().getColor(R.color.white2));
        mBut4.setImageResource(R.drawable.icon_home_1);
        linearLayouthome.setEnabled(true);
    }

    public void icon3() {
        mCateText1.setTextColor(getResources().getColor(R.color.white2));
        mBut1.setImageResource(R.drawable.icon_tuijian_1);
        linearLayouttuijian.setEnabled(true);
        mCateText2.setTextColor(getResources().getColor(R.color.white2));
        mBut2.setImageResource(R.drawable.icon_youji_1);
        linearLayoutyouji.setEnabled(true);
        mCateText3.setTextColor(getResources().getColor(R.color.bulu));
        mBut3.setImageResource(R.drawable.icon_riji_2);
        linearLayoutriji.setEnabled(false);
        mCateText4.setTextColor(getResources().getColor(R.color.white2));
        mBut4.setImageResource(R.drawable.icon_home_1);
        linearLayouthome.setEnabled(true);
    }

    public void icon4() {
        mCateText1.setTextColor(getResources().getColor(R.color.white2));
        mBut1.setImageResource(R.drawable.icon_tuijian_1);
        linearLayouttuijian.setEnabled(true);
        mCateText2.setTextColor(getResources().getColor(R.color.white2));
        mBut2.setImageResource(R.drawable.icon_youji_1);
        linearLayoutyouji.setEnabled(true);
        mCateText3.setTextColor(getResources().getColor(R.color.white2));
        mBut3.setImageResource(R.drawable.icon_riji_1);
        linearLayoutriji.setEnabled(true);
        mCateText4.setTextColor(getResources().getColor(R.color.bulu));
        mBut4.setImageResource(R.drawable.icon_home_2);
        linearLayouthome.setEnabled(false);
    }


    public void inittoken() {
        new Thread() {
            public void run() {
                try {
                    //url编码
                    try {
                        nicheng = URLEncoder.encode(nicheng, "utf-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/gettoken.php?shoujihao=" + userid + "&nicheng=" + nicheng);
                } catch (Exception e) {
                }
                //删首尾空
                getjieguo = getjieguo.trim();
                Log.e("uri", getjieguo);
                //网络问题
                if (!(getjieguo == null || getjieguo == "")) {
                    token = getjieguo.substring(43);
                    token = token.substring(0, 96);
                    Log.e("uri", token);

                    // 建立与融云服务器的连接
                    connect(token);
                }

            }
        }.start();
    }


    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    Log.e("uri", "--onTokenIncorrect");
                }
                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.e("uri", "--onSuccess" + userid);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.e("uri", "--onError" + errorCode);
                }
            });
        }


        conversationBehaviorListener = new ConversationBehaviorListener();
        RongIM.setConversationBehaviorListener(conversationBehaviorListener);


        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String userId) {
                return findUserById(userId);

            }
        }, true);
    }


    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    //高效率且换frament
    public void switchContent(Fragment from, Fragment to, int position) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = fm.beginTransaction();
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(from)
                        .add(R.id.fragmentcontent, to, Integer.toString(position)).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }


    //获取用户信息
    public UserInfo findUserById(final String userId) {
        String name = "未知";
        String icon = "http://1.wode123123.sinaapp.com/photo/egl11.png";
        try {
            getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/getuseinfo.php?userid=" + userId);
        } catch (Exception e) {
        }
        //删首尾空
        getjieguo = getjieguo.trim();
        Log.e("uri", getjieguo);
        //网络问题
        if (!(getjieguo == null || getjieguo == "")) {
            try {
                JSONArray arr = new JSONArray(getjieguo);
                JSONObject jsonObject;
                jsonObject = (JSONObject) arr.get(0);
                name = jsonObject.getString("nicheng");
                Log.e("uri", name);
                //icon=jsonObject.getString("icon");
            } catch (JSONException ex) {
            }
        }
        return new UserInfo(userId, name,
                Uri.parse(icon));
    }

}
