package com.map.wulimap.Activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.andexert.library.RippleView;
import com.andtinder.model.CardModel;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.map.wulimap.util.DownloadUtil;
import com.map.wulimap.R;
import com.map.wulimap.util.FileUtil;
import com.map.wulimap.util.ToastUtil;
import com.readystatesoftware.viewbadger.BadgeView;

import java.net.URLEncoder;

public class CardActivity extends AppCompatActivity {
    //初始化控件
    CardContainer mCardContainer;
    BadgeView badge;
    BadgeView badge1;
    ImageView imageView;
    TranslateAnimation anim;
    Button button;
    //初始化变量
    int dangqianshu;
    int start;
    int end;
    int biaojijishi = 0;
    int shoucangshu;
    int zongshu;
    String tupianming;
    String bianmatupianming;
    String yemian = "2";
    String index22;
    String diyici;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        initsetting();
        initdate();
        initview();
    }


    public void initview() {
//标题
        button = (Button) findViewById(R.id.bt3);
//返回
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                CardActivity.this.finish();
            }

        });
//添加容器
        mCardContainer = (CardContainer) findViewById(R.id.layoutview);
//初始化 加入内容
        dangqianshu = 2;//当前页面数
        start = 0;
        end = 2;
        mCardContainer.setAdapter(getadpter(start, end, id));

//数字动态效果初始化
        imageView = (ImageView) findViewById(R.id.target);
        badge = new BadgeView(CardActivity.this, imageView);
        badge.setText("+1");
        badge.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
        badge.setBadgeMargin(15, 10);

        badge1 = new BadgeView(CardActivity.this, imageView);
        badge1.setText("-1");
        badge1.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
        badge1.setBadgeMargin(15, 10);

//初始化动画
        anim = new TranslateAnimation(-100, 0, 0, 0);
        anim.setInterpolator(new BounceInterpolator());
        anim.setDuration(500);

//收藏按钮
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.shoucang);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yemian.equals("1")) {
                } else {
                    button.setText("收藏");
                    yemian = "1";
                    if (shoucangshu == 0) {
                        mCardContainer.setAdapter(getadpter1(0, 0));
                        ToastUtil.show(CardActivity.this, "还没有收藏！");
                    } else {
                        start = 0;
                        end = shoucangshu;
                        mCardContainer.setAdapter(getadpter1(start, end));
                    }
                }
            }
        });

//卡库按钮
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.kaku);
        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yemian.equals("2")) {
                } else {
                    button.setText("卡库");
                    yemian = "2";
                    dangqianshu = 2;
                    start = 0;
                    end = 2;
                    mCardContainer.setAdapter(getadpter(start, end, id));
                }
            }
        });

//第一次打开页面
        if ("1".equals(diyici)) {
        } else {
            //显示第一次提示对话框
            show();
        }

//获取提示按钮
        final RippleView rippleView = (RippleView) findViewById(R.id.xiangqing);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                show();
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


    public void initdate() {
//获取收藏数量
        SharedPreferences sharedPreferences = getSharedPreferences("shoucang", MODE_PRIVATE);
        shoucangshu = sharedPreferences.getInt("shuliang", 0);
//是否第一次打开页面
        diyici = sharedPreferences.getString("diyici", null);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("diyici", "1");
        editor.commit();
//获取传入数据
        Intent intent = getIntent();
        id = intent.getStringExtra("id");//来源
        zongshu = Integer.parseInt(intent.getStringExtra("zongshu"));//总数
    }


    //卡库 获取适配器
    public SimpleCardStackAdapter getadpter(int zhi1, int zhi, final String weizhi) {
        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(CardActivity.this);
        int jj = zhi1;//tag
        for (int i = zhi - 1; i >= zhi1; i--) {
            //图片 昵称 时间
            final SharedPreferences sharedPreferences = getSharedPreferences(weizhi, MODE_PRIVATE);
            tupianming = sharedPreferences.getString("tupian" + i, null);
            final CardModel cardModel;
//图片存在
            if (FileUtil.wenjianshifoucunzai("/sdcard/map/" + tupianming + "-yasuo.jpg")) {
                Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/map/" + tupianming + "-yasuo.jpg");
                cardModel = new CardModel(sharedPreferences.getString("shijian" + i, null), "TA:" + sharedPreferences.getString("nicheng" + i, null), bitmap);
                //赞数 评论数
                cardModel.setzanshu(sharedPreferences.getString("zanshu" + i, null));
                cardModel.setpinglushu(sharedPreferences.getString("pinglunshu" + i, null));
                cardModel.settag(Integer.toString(jj));
//提前加载图片
                int zongshu1 = zongshu;
                if (zhi < zongshu1 - 3) {
                    int j = zhi + 2;
                    tupianming = sharedPreferences.getString("tupian" + j, null);
                    if (!FileUtil.wenjianshifoucunzai("/sdcard/map/" + tupianming + "-yasuo.jpg")) {
                        //url编码
                        try {
                            bianmatupianming = URLEncoder.encode(tupianming, "utf-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DownloadUtil down = new DownloadUtil();
                        down.downloadApk(tupianming + "-yasuo.jpg", "http://wode123123-test.stor.sinaapp.com/" + bianmatupianming + "-yasuo.jpg", "/sdcard/map/");
                    }
                    j = j + 1;
                    tupianming = sharedPreferences.getString("tupian" + j, null);
                    if (!FileUtil.wenjianshifoucunzai("/sdcard/map/" + tupianming + "-yasuo.jpg")) {
                        //url编码
                        try {
                            bianmatupianming = URLEncoder.encode(tupianming, "utf-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DownloadUtil down1 = new DownloadUtil();
                        down1.downloadApk(tupianming + "-yasuo.jpg", "http://wode123123-test.stor.sinaapp.com/" + bianmatupianming + "-yasuo.jpg", "/sdcard/map/");
                    }
                } else {
                    if (zhi == zongshu1 - 3) {
                        int j = zongshu - 1;
                        tupianming = sharedPreferences.getString("tupian" + j, null);
                        if (!FileUtil.wenjianshifoucunzai("/sdcard/map/" + tupianming + "-yasuo.jpg")) {
                            //url编码
                            try {
                                bianmatupianming = URLEncoder.encode(tupianming, "utf-8");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            DownloadUtil down = new DownloadUtil();
                            down.downloadApk(tupianming + "-yasuo.jpg", "http://wode123123-test.stor.sinaapp.com/" + bianmatupianming + "-yasuo.jpg", "/sdcard/map/");
                        }
                    }
                }
                //图片不存在
            } else {
                cardModel = new CardModel(sharedPreferences.getString("shijian" + i, null), "TA:" + sharedPreferences.getString("nicheng" + i, null), getResources().getDrawable(R.drawable.card_defalt_image));
                //赞数 评论数
                cardModel.setzanshu(sharedPreferences.getString("zanshu" + i, null));
                cardModel.setpinglushu(sharedPreferences.getString("pinglunshu" + i, null));
                cardModel.settag(Integer.toString(jj));
            }

            //双击进入
            cardModel.setOnClickListener(new CardModel.OnClickListener() {
                @Override
                public void OnClickListener() {
                    String index = cardModel.gettag();
                    index22 = index;
                    if (biaojijishi == 1) {
                        Bundle bundle = new Bundle();
                        SharedPreferences sharedPreferences5 = getSharedPreferences(weizhi, MODE_PRIVATE);
                        bundle.putString("rijiid", sharedPreferences5.getString("rijiid" + index, null));
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
                        if (weizhi.equals("weiguanzhuriji")) {
                            Intent intent = new Intent(CardActivity.this, RijiDetailActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(CardActivity.this, YoujiDetailActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    } else {
                        //双击控制
                        biaojijishi = 1;
                        handler.sendEmptyMessageDelayed(1, 300);
                    }
                }
            });

            //右滑  跳过
            cardModel.setOnCardDismissedListener(new CardModel.OnCardDismissedListener() {
                @Override
                public void onLike() {
                    //除 双击 bug
                    biaojijishi = 0;
                    //也米啊书减一
                    dangqianshu = dangqianshu - 1;
                    //页面数为0，自动获取新的适配器
                    if (dangqianshu == 0) {
                        dangqianshu = 2;
                        int zhi1 = end + 2;
                        if (zhi1 > zongshu) {
                            start = end;
                            end = zongshu;
                            ToastUtil.show(CardActivity.this, "到底了！");
                        } else {
                            start = end;
                            end = end + 2;
                        }
                        mCardContainer.setAdapter(getadpter(start, end, weizhi));
                    }
                }

                //左滑 加入收藏
                @Override
                public void onDislike() {
                    //除双击bug
                    biaojijishi = 0;
                    //页面数减一
                    dangqianshu = dangqianshu - 1;
                    //页面数为0，自动获取新的适配器
                    if (dangqianshu == 0) {
                        dangqianshu = 2;
                        int zhi1 = end + 2;
                        if (zhi1 > zongshu) {
                            ToastUtil.show(CardActivity.this, "到底了！");
                            start = end;
                            end = zongshu;
                        } else {
                            start = end;
                            end = end + 2;
                        }
                        mCardContainer.setAdapter(getadpter(start, end, weizhi));
                    }
//判断是否重复
                    String chongfu = "0";
                    SharedPreferences sharedPreferences5 = getSharedPreferences(weizhi, MODE_PRIVATE);
                    SharedPreferences sharedPreferences2 = getSharedPreferences("shoucang", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences2.edit();
                    if (shoucangshu > 0) {
                        for (int a = 0; a < shoucangshu; a++) {
                            if (sharedPreferences2.getString("tupian" + a, null).equals(sharedPreferences5.getString("tupian" + index22, null))) {
                                Log.e("uri", Integer.toString(a));
                                Log.e("uri", index22);
                                chongfu = "1";
                            }
                        }
                    }
//没有 重复 加入收藏
                    if (chongfu.equals("0")) {
                        if (weizhi.equals("weiguanzhuriji")) {
                            editor.putString("ji" + shoucangshu, "riji");
                            editor.commit();
                        } else {
                            editor.putString("ji" + shoucangshu, "youji");
                            editor.commit();
                        }
                        editor.putString("shanchu" + shoucangshu, "0");
                        editor.putString("rijiid" + shoucangshu, sharedPreferences5.getString("rijiid" + index22, null));
                        editor.putString("youjiid" + shoucangshu, sharedPreferences5.getString("youjiid" + index22, null));
                        editor.putString("shoujihao" + shoucangshu, sharedPreferences5.getString("shoujihao" + index22, null));
                        editor.putString("nicheng" + shoucangshu, sharedPreferences5.getString("nicheng" + index22, null));
                        editor.putString("shijian" + shoucangshu, sharedPreferences5.getString("shijian" + index22, null));
                        editor.putString("didian" + shoucangshu, sharedPreferences5.getString("didian" + index22, null));
                        editor.putString("jinwei" + shoucangshu, sharedPreferences5.getString("jinwei" + index22, null));
                        editor.putString("tupian" + shoucangshu, sharedPreferences5.getString("tupian" + index22, null));
                        editor.putString("zanshu" + shoucangshu, sharedPreferences5.getString("zanshu" + index22, null));
                        editor.putString("pinglunshu" + shoucangshu, sharedPreferences5.getString("pinglunshu" + index22, null));
                        editor.putString("neirong" + shoucangshu, sharedPreferences5.getString("neirong" + index22, null));
                        shoucangshu = shoucangshu + 1;
                        editor.putInt("shuliang", shoucangshu);
                        editor.commit();
                        //动态显示数字
                        badge.toggle(anim, null);
                        //一秒消失
                        handler.sendEmptyMessageDelayed(3, 1500);
                    } else {
                        ToastUtil.show(CardActivity.this, "不能重复收藏！");
                    }
                }
            });
            //加入适配器
            adapter.add(cardModel);
            jj = jj + 1;
        }
        return adapter;
    }


    //收藏  获取适配器
    public SimpleCardStackAdapter getadpter1(int zhi1, int zhi) {
        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(CardActivity.this);
        for (int i = zhi1; i < zhi; i++) {
            //图片 昵称 时间
            SharedPreferences sharedPreferences = getSharedPreferences("shoucang", MODE_PRIVATE);
            tupianming = sharedPreferences.getString("tupian" + i, null);
            final CardModel cardModel;
            //图片存在
            if (FileUtil.wenjianshifoucunzai("/sdcard/map/" + tupianming + "-yasuo.jpg")) {
                Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/map/" + tupianming + "-yasuo.jpg");
                cardModel = new CardModel(sharedPreferences.getString("shijian" + i, null), "TA:" + sharedPreferences.getString("nicheng" + i, null), bitmap);
                //赞数 评论数 tag
                cardModel.setzanshu(sharedPreferences.getString("zanshu" + i, null));
                cardModel.setpinglushu(sharedPreferences.getString("pinglunshu" + i, null));
                cardModel.settag(Integer.toString(zhi - 1 - i));
                //图片不存在
            } else {
                cardModel = new CardModel(sharedPreferences.getString("shijian" + i, null), "TA:" + sharedPreferences.getString("nicheng" + i, null), getResources().getDrawable(R.drawable.card_defalt_image));
                //赞数 评论数 tag
                cardModel.setzanshu(sharedPreferences.getString("zanshu" + i, null));
                cardModel.setpinglushu(sharedPreferences.getString("pinglunshu" + i, null));
                cardModel.settag(Integer.toString(zhi - 1 - i));
            }
            //双击进入
            cardModel.setOnClickListener(new CardModel.OnClickListener() {
                @Override
                public void OnClickListener() {
                    //准确获取tag
                    String index = cardModel.gettag();
                    index22 = index;
                    if (biaojijishi == 1) {
                        Log.e("uri", index22);
                        Bundle bundle = new Bundle();
                        SharedPreferences sharedPreferences5 = getSharedPreferences("shoucang", MODE_PRIVATE);
                        bundle.putString("rijiid", sharedPreferences5.getString("rijiid" + index22, null));
                        bundle.putString("youjiid", sharedPreferences5.getString("youjiid" + index22, null));
                        bundle.putString("shoujihao", sharedPreferences5.getString("shoujihao" + index22, null));
                        bundle.putString("nicheng", sharedPreferences5.getString("nicheng" + index22, null));
                        bundle.putString("shijian", sharedPreferences5.getString("shijian" + index22, null));
                        bundle.putString("didian", sharedPreferences5.getString("didian" + index22, null));
                        bundle.putString("jinwei", sharedPreferences5.getString("jinwei" + index22, null));
                        bundle.putString("tupian", sharedPreferences5.getString("tupian" + index22, null));
                        bundle.putString("zanshu", sharedPreferences5.getString("zanshu" + index22, null));
                        bundle.putString("pinglunshu", sharedPreferences5.getString("pinglunshu" + index22, null));
                        bundle.putString("neirong", sharedPreferences5.getString("neirong" + index22, null));
                        if (sharedPreferences5.getString("ji" + index22, null).equals("riji")) {
                            Intent intent = new Intent(CardActivity.this, RijiDetailActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(CardActivity.this, YoujiDetailActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    } else {
                        //双击控制
                        biaojijishi = 1;
                        //变为0操作
                        handler.sendEmptyMessageDelayed(1, 300);
                    }
                }
            });
            //右滑  跳过
            cardModel.setOnCardDismissedListener(new CardModel.OnCardDismissedListener() {
                @Override
                public void onLike() {
                    //除双击bug
                    biaojijishi = 0;
                }

                //左滑  删除
                @Override
                public void onDislike() {
                    //除双击bug
                    biaojijishi = 0;
                    //收藏 shar 减一更新
                    SharedPreferences sharedPreferences = getSharedPreferences("shoucang", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("shanchu" + index22, "1");
                    editor.commit();
                    int j = 0;
                    for (int i = 0; i < shoucangshu; i++) {
                        if (sharedPreferences.getString("shanchu" + i, null).equals("0")) {
                            editor.putString("shanchu" + j, "0");
                            editor.putString("rijiid" + j, sharedPreferences.getString("rijiid" + i, null));
                            editor.putString("youjiid" + j, sharedPreferences.getString("youjiid" + i, null));
                            editor.putString("shoujihao" + j, sharedPreferences.getString("shoujihao" + i, null));
                            editor.putString("nicheng" + j, sharedPreferences.getString("nicheng" + i, null));
                            editor.putString("shijian" + j, sharedPreferences.getString("shijian" + i, null));
                            editor.putString("didian" + j, sharedPreferences.getString("didian" + i, null));
                            editor.putString("jinwei" + j, sharedPreferences.getString("jinwei" + i, null));
                            editor.putString("tupian" + j, sharedPreferences.getString("tupian" + i, null));
                            editor.putString("zanshu" + j, sharedPreferences.getString("zanshu" + i, null));
                            editor.putString("pinglunshu" + j, sharedPreferences.getString("pinglunshu" + i, null));
                            editor.putString("neirong" + j, sharedPreferences.getString("neirong" + i, null));
                            editor.putString("ji" + j, sharedPreferences.getString("ji" + i, null));
                            editor.commit();
                            j++;
                        }

                    }
                    //收藏数减一更新
                    shoucangshu = shoucangshu - 1;
                    editor.putInt("shuliang", shoucangshu);
                    editor.commit();
                    //数字动态显示
                    badge1.toggle(anim, null);
                    handler.sendEmptyMessageDelayed(4, 1500);
                }

            });
            //加入适配器
            adapter.add(cardModel);

        }
        return adapter;
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //双击  变为0
                    biaojijishi = 0;
                    break;
                case 3:
                    //数字显示小时
                    badge.toggle(anim, null);
                    break;
                case 4:
                    //数字显示消失
                    badge1.toggle(anim, null);
                    break;

            }
        }
    };


    //提示对话框
    public void show() {
        String[] items = new String[]{"左滑加入,取消收藏", "右滑跳过,双击进入"};
        //对话框选择
        new android.support.v7.app.AlertDialog.Builder(CardActivity.this)
                .setTitle("温馨提示")
                .setIcon(R.drawable.app_icon)
                .setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        }).create().show();
    }

}
