package com.map.wulimap.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.andexert.library.RippleView;
import com.kyleduo.switchbutton.SwitchButton;
import com.map.wulimap.R;

import io.rong.imkit.RongIM;

public class SetActivity extends AppCompatActivity {
    //初始化控件
    SwitchButton switchButton;
    TextView shifou;
    TextView ditu;
    //初始化变量
    String biaoji = "0";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String gengxinshifou;
    String dituzenyang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        initsetting();
        initdate();
        initview();
    }


    public void initview() {
//显示switchButton
        switchButton = (SwitchButton) findViewById(R.id.switc);
        shifou = (TextView) findViewById(R.id.shifou);
        if ("0".equals(gengxinshifou)) {
            switchButton.setChecked(false);
            shifou.setText("否");
        } else {
            switchButton.setChecked(true);
            shifou.setText("是");
        }
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                biaoji = "1";
                if (switchButton.isChecked()) {
                    shifou.setText("是");
                    gengxinshifou = "1";
                    editor.putString("genxin", gengxinshifou);
                    editor.commit();
                } else {
                    shifou.setText("否");
                    gengxinshifou = "0";
                    editor.putString("genxin", gengxinshifou);
                    editor.commit();
                }
            }
        });


//更新按钮
        final RippleView rippleView = (RippleView) findViewById(R.id.genxin);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (biaoji.equals("0")) {
                    if (switchButton.isChecked()) {
                        switchButton.setChecked(false);
                        shifou.setText("否");
                        gengxinshifou = "0";
                        editor.putString("genxin", gengxinshifou);
                        editor.commit();
                    } else {
                        shifou.setText("是");
                        switchButton.setChecked(true);
                        gengxinshifou = "1";
                        editor.putString("genxin", gengxinshifou);
                        editor.commit();
                    }
                }
                biaoji = "0";
            }
        });


//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                SetActivity.this.finish();
            }

        });


//地图显示
        ditu = (TextView) findViewById(R.id.ditulei);
        ditu.setText("普通");
        if (dituzenyang == "" || dituzenyang == null) {
            dituzenyang = "0";
        }
        switch (dituzenyang) {
            case "0":
                ditu.setText("普通");
                break;
            case "1":
                ditu.setText("3D");
                break;
            case "2":
                ditu.setText("卫星");
                break;
            case "3":
                ditu.setText("黑夜");
                break;
        }
        final RippleView rippleView2 = (RippleView) findViewById(R.id.ditu);
        rippleView2.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                String[] items = new String[]{"普通", "3D", "卫星", "黑夜"};
                new MaterialDialog.Builder(SetActivity.this)
                        .title("默认地图类型")
                        .items(items)
                        .itemsCallbackSingleChoice(Integer.parseInt(dituzenyang), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        ditu.setText("普通");
                                        dituzenyang = "0";
                                        editor.putString("leixin", dituzenyang);
                                        editor.commit();
                                        break;
                                    case 1:
                                        ditu.setText("3D");
                                        dituzenyang = "1";
                                        editor.putString("leixin", dituzenyang);
                                        editor.commit();
                                        break;
                                    case 2:
                                        ditu.setText("卫星");
                                        dituzenyang = "2";
                                        editor.putString("leixin", dituzenyang);
                                        editor.commit();
                                        break;
                                    case 3:
                                        ditu.setText("黑夜");
                                        dituzenyang = "3";
                                        editor.putString("leixin", dituzenyang);
                                        editor.commit();
                                        break;
                                }
                                return true;
                            }
                        })
                        .positiveText("确定")
                        .show();
            }

        });


//修改按钮
        final RippleView rippleView3 = (RippleView) findViewById(R.id.xiugai);
        rippleView3.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                SetActivity.this.startActivity(new Intent(SetActivity.this, ChangePasswprdActivity.class));
            }

        });


//建议按钮
        final RippleView rippleView4 = (RippleView) findViewById(R.id.jianyi);
        rippleView4.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                SetActivity.this.startActivity(new Intent(SetActivity.this, SuggestActivity.class));
            }

        });


        //聊天按钮
        final RippleView rippleView6 = (RippleView) findViewById(R.id.chuangkou);
        rippleView6.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {


                if (RongIM.getInstance() != null) {

                    RongIM.getInstance().startPrivateChat(SetActivity.this, "1111111111", "小木");
                } else {
                    SetActivity.this.startActivity(new Intent(SetActivity.this, ConversationActivity.class));
                }


                //   SetActivity.this.startActivity(new Intent(SetActivity.this, MoreActivity.class));
            }

        });


        //关于按钮
        final RippleView rippleView5 = (RippleView) findViewById(R.id.guanyu);
        rippleView5.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                SetActivity.this.startActivity(new Intent(SetActivity.this, MoreActivity.class));
            }

        });
    }


    public void initdate() {
//获取数据
        sharedPreferences = getSharedPreferences("shezhi", MODE_PRIVATE);
        gengxinshifou = sharedPreferences.getString("genxin", null);
        dituzenyang = sharedPreferences.getString("leixin", null);
        editor = sharedPreferences.edit();
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


}
