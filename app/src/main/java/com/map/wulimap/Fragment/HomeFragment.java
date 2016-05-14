package com.map.wulimap.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;

import com.map.wulimap.Activity.FocusOnMeActivity;
import com.map.wulimap.Activity.MyAccountActivity;
import com.map.wulimap.Activity.My_FocusActivity;
import com.map.wulimap.Activity.My_newsActivity;
import com.map.wulimap.Activity.My_rijiActivity;
import com.map.wulimap.Activity.My_youjiActivity;
import com.map.wulimap.Activity.NoticeActivity;
import com.map.wulimap.Activity.SearchFriendActivity;
import com.map.wulimap.Activity.Write_rijiActivity;
import com.map.wulimap.Activity.Write_youjiActivity;
import com.map.wulimap.R;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.view.Myanimotion;


public class HomeFragment extends Fragment {
    //初始化控件
    static HomeFragment fragment;
    static Context context1;
    View view;
    private OnFragmentInteractionListener mListener;
    RelativeLayout view1;
    LinearLayout linearLayoutq;
    //初始化变量
    int in = 0;
    String mima;

    public static HomeFragment newInstance(Context context) {
        fragment = new HomeFragment();
        context1 = context;
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_my_home, container, false);
        linearLayoutq = (LinearLayout) inflater.inflate(R.layout.riji_password, null);

        initview();

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
        //显示信息
        SharedPreferences sharedPreferences = context1.getSharedPreferences("zhanghu", Context.MODE_PRIVATE);
        String nicheng = sharedPreferences.getString("nicheng", null);
        String shoujihao = sharedPreferences.getString("shoujihao", null);
        mima = sharedPreferences.getString("mima", null);
        TextView textView = (TextView) view.findViewById(R.id.shoujihao);
        textView.setText(shoujihao);
        TextView textView1 = (TextView) view.findViewById(R.id.nicheng);
        textView1.setText(nicheng);

//隐藏按钮
        view1 = (RelativeLayout) view.findViewById(R.id.tianjia);
        Myanimotion.StartAnimationOUT(view1, 0, 0);


//广播
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

        //搜素按钮
        final RippleView rippleView = (RippleView) view.findViewById(R.id.sousu);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                context1.startActivity(new Intent(context1, SearchFriendActivity.class));
            }

        });

//写操作
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (in == 0) {
                    view1.setVisibility(View.VISIBLE);
                    Myanimotion.StartAnimationIN(view1, 500);
                    in = 1;

                } else {
                    in = 0;
                    Myanimotion.StartAnimationOUT(view1, 500, 0);
                }
            }


        });

//写游记
        final FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context1.startActivity(new Intent(context1, Write_youjiActivity.class));


            }


        });

//写日记
        final FloatingActionButton fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context1.startActivity(new Intent(context1, Write_rijiActivity.class));

            }


        });

//个人主页
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.yonghu);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context1.startActivity(new Intent(context1, MyAccountActivity.class));
            }
        });
//我的关注
        LinearLayout linearLayout2 = (LinearLayout) view.findViewById(R.id.wodeguanzhu);
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context1.startActivity(new Intent(context1, My_FocusActivity.class));
            }
        });
//关注我的
        LinearLayout linearLayout3 = (LinearLayout) view.findViewById(R.id.guanzhuwode);
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context1.startActivity(new Intent(context1, FocusOnMeActivity.class));
            }
        });
//添加关注
        LinearLayout linearLayout4 = (LinearLayout) view.findViewById(R.id.tianjiaguanzhu);
        linearLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context1.startActivity(new Intent(context1, SearchFriendActivity.class));
            }
        });

//我的游记
        LinearLayout linearLayout5 = (LinearLayout) view.findViewById(R.id.wodeyouji);
        linearLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context1.startActivity(new Intent(context1, My_youjiActivity.class));
            }
        });

//我的日记
        final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(context1).create();
        final EditText editText = (EditText) linearLayoutq.findViewById(R.id.mima);
        final TextView textViewq = (TextView) linearLayoutq.findViewById(R.id.yanzheng);
        textViewq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shurumima = editText.getText().toString().trim();
                if (mima.equals(shurumima)) {
                    ToastUtil.show(context1, "验证通过！");
                    editText.setText("");
                    dialog.hide();
                    context1.startActivity(new Intent(context1, My_rijiActivity.class));
                } else {
                    dialog.hide();
                    ToastUtil.show(context1, "验证失败！");
                }
            }
        });

        dialog.setIcon(R.drawable.app_icon);
        dialog.setTitle("安全验证");
        dialog.setView(linearLayoutq); // 自定义dialog
        LinearLayout linearLayout6 = (LinearLayout) view.findViewById(R.id.woderiji);

        linearLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

            }
        });


//我的消息
        LinearLayout linearLayout9 = (LinearLayout) view.findViewById(R.id.wodexiaoxi);
        linearLayout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context1.startActivity(new Intent(context1, My_newsActivity.class));
            }
        });

    }



}
