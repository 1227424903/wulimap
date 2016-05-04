package com.map.wulimap.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.speech.VoiceRecognitionService;
import com.map.wulimap.R;
import com.map.wulimap.util.BaiduyuyingUtil;
import com.map.wulimap.util.Sdkversionutil;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.util.HtmlService;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFriendActivity extends AppCompatActivity implements RecognitionListener {
    //初始化变量
    String getjieguo;
    String jieguo;
    String jiegou;
    int shuliang;
    List<String> listString1;
    //初始化控件
    AnimationAdapter mAnimAdapter;
    ListView listView;
    SharedPreferences sharedPreferences1;
    MaterialDialog progDialog;
    EditText editText;
    //初始化分享常量
    private static final String TAG = "Touch";
    private static final int EVENT_ERROR = 11;
    View speechTips;
    View speechWave;
    private SpeechRecognizer speechRecognizer;
    private long speechEndTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_search_friend);

        initsetting();
        initview();


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

//主动获取录音权限
        if (Sdkversionutil.getSDKVersionNumber() >= 23) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        0);
            }
        }
    }


    public void initview() {
//读取记录，并显示在listview
        sharedPreferences1 = getSharedPreferences("sousutarenjilu", MODE_PRIVATE);
        final SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        shuliang = sharedPreferences1.getInt("shuliang", 0);
        listString1 = new ArrayList<String>();

        if (shuliang > 0) {
            for (int i = shuliang; i > 0; i--) {
                listString1.add(sharedPreferences1.getString("jilu" + i, null));
            }
            ArrayAdapter<String> aAdapter1 = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.content_search_friend_record, listString1);


            listView = (ListView) findViewById(R.id.sousujieguo);
            //下面出现动画
            // SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(baseAdapter,PopActivity.this));
            // swingBottomInAnimationAdapter.setAbsListView(listView);
            // assert swingBottomInAnimationAdapter.getViewAnimator() != null;
            //  swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
            //右边出现动画
            if (!(mAnimAdapter instanceof SwingRightInAnimationAdapter)) {
                mAnimAdapter = new SwingRightInAnimationAdapter(aAdapter1);
                mAnimAdapter.setAbsListView(listView);
                listView.setAdapter(mAnimAdapter);
            }

//记录点击
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    jiegou = listString1.get(i);
                    showProgressDialog();
                    //url编码
                    try {
                        jieguo = URLEncoder.encode(jiegou, "utf-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //手机号
                    if (jieguo.length() == 11 && jiegou.length() == 11) {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/huoqutarenzhanghu.php?shoujihao=" + jiegou);
                                } catch (Exception e) {
                                }

                                //网络问题
                                if (!(getjieguo == null || getjieguo == "")) {
                                    //删首尾空
                                    getjieguo = getjieguo.trim();
                                    if (getjieguo.equals("0")) {
                                        //没有此人
                                        handler.sendEmptyMessageDelayed(1, 1000);
                                    } else {
                                        try {
                                            JSONArray arr = new JSONArray(getjieguo);
                                            JSONObject jsonObject = (JSONObject) arr.get(0);
                                            SharedPreferences sharedPreferences = getSharedPreferences("tarenzhanghu", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("shoujihao", jiegou);
                                            editor.putString("userid", jsonObject.getString("userid"));
                                            editor.putString("youjishu", jsonObject.getString("youjishu"));
                                            editor.putString("rijishu", jsonObject.getString("rijishu"));
                                            editor.putString("nicheng", jsonObject.getString("nicheng"));
                                            editor.putString("guanzhushu", jsonObject.getString("guanzhushu"));
                                            editor.putString("beiguanzhushu", jsonObject.getString("beiguanzhushu"));
                                            editor.putString("icon", jsonObject.getString("icon"));
                                            editor.commit();
                                        } catch (JSONException ex) {
                                        }
                                        handler.sendEmptyMessageDelayed(2, 1000);
                                    }
                                } else {
                                    //网络失
                                    handler.sendEmptyMessageDelayed(3, 1000);
                                }
                            }
                        }.start();

                        //昵称
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/nichengsousu.php?nicheng=" + jieguo);
                                } catch (Exception e) {
                                }

                                Log.e("uri", getjieguo);
                                //网络问题
                                if (!(getjieguo == null || getjieguo == "")) {
                                    //删首尾空
                                    getjieguo = getjieguo.trim();
                                    if (getjieguo.equals("0")) {
                                        //没有此人
                                        handler.sendEmptyMessageDelayed(1, 1000);
                                    } else {
                                        try {
                                            JSONArray arr = new JSONArray(getjieguo);
                                            JSONObject jsonObject = (JSONObject) arr.get(0);
                                            SharedPreferences sharedPreferences = getSharedPreferences("tarenzhanghu", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("shoujihao", "");
                                            editor.putString("userid", jsonObject.getString("userid"));
                                            editor.putString("youjishu", jsonObject.getString("youjishu"));
                                            editor.putString("rijishu", jsonObject.getString("rijishu"));
                                            editor.putString("nicheng", jsonObject.getString("nicheng"));
                                            editor.putString("guanzhushu", jsonObject.getString("guanzhushu"));
                                            editor.putString("beiguanzhushu", jsonObject.getString("beiguanzhushu"));
                                            editor.putString("icon", jsonObject.getString("icon"));
                                            editor.commit();
                                        } catch (JSONException ex) {
                                            Log.e("11", getjieguo);
                                        }
                                        handler.sendEmptyMessageDelayed(2, 1000);
                                    }
                                } else {
                                    //网络失
                                    handler.sendEmptyMessageDelayed(3, 1000);
                                }
                            }
                        }.start();

                    }
                }
            });
        } else {
            //  listView.setVisibility(View.GONE);
        }


//清除记录
        editText = (EditText) findViewById(R.id.sousukuang);
        Button button = (Button) findViewById(R.id.sousu);
        TextView textView = (TextView) findViewById(R.id.shanchujilu);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuliang > 0) {
                    listString1.clear();
                    editor1.putInt("shuliang", 0);
                    shuliang = 0;
                    editor1.commit();
                    listView.removeAllViews();
                    // listView.setVisibility(View.GONE);
                }


            }
        });

//搜素按钮
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jiegou = editText.getText().toString().trim().replaceAll(" ", "");
                if (jiegou.equals("")) {
                    ToastUtil.show(SearchFriendActivity.this, "不能为空！");
                } else {
                    editText.setText("");
                    showProgressDialog();
                    //url编码
                    try {
                        jieguo = URLEncoder.encode(jiegou, "utf-8");
                        Log.e("uri", jiegou);
                        Log.e("uri", jieguo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //手机号
                    if (jieguo.length() == 11 && jiegou.length() == 11) {

                        Log.e("uri", "手机号");
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/huoqutarenzhanghu.php?shoujihao=" + jiegou);
                                } catch (Exception e) {
                                }

                                //网络问题
                                if (!(getjieguo == null || getjieguo == "")) {
                                    //删首尾空
                                    getjieguo = getjieguo.trim();
                                    if (getjieguo.equals("0")) {
                                        //没有此人
                                        handler.sendEmptyMessageDelayed(1, 1000);
                                    } else {
                                        try {
                                            JSONArray arr = new JSONArray(getjieguo);
                                            JSONObject jsonObject = (JSONObject) arr.get(0);
                                            SharedPreferences sharedPreferences = getSharedPreferences("tarenzhanghu", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("shoujihao", jiegou);
                                            editor.putString("userid", jsonObject.getString("userid"));
                                            editor.putString("youjishu", jsonObject.getString("youjishu"));
                                            editor.putString("rijishu", jsonObject.getString("rijishu"));
                                            editor.putString("nicheng", jsonObject.getString("nicheng"));
                                            editor.putString("guanzhushu", jsonObject.getString("guanzhushu"));
                                            editor.putString("beiguanzhushu", jsonObject.getString("beiguanzhushu"));
                                            editor.putString("icon", jsonObject.getString("icon"));
                                            editor.commit();
                                        } catch (JSONException ex) {
                                            Log.e("11", getjieguo);
                                        }
                                        shuliang += 1;
                                        editor1.putInt("shuliang", shuliang);
                                        editor1.putString("jilu" + shuliang, jiegou);
                                        editor1.commit();
                                        listString1.add(0, jiegou);
                                        handler.sendEmptyMessageDelayed(2, 1000);
                                    }
                                } else {
                                    //网络失
                                    handler.sendEmptyMessageDelayed(3, 1000);
                                }
                            }
                        }.start();
                        //昵称
                    } else {
                        Log.e("uri", "nicheng");
                        new Thread() {
                            public void run() {
                                try {
                                    getjieguo = HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/nichengsousu.php?nicheng=" + jieguo);
                                } catch (Exception e) {
                                }

                                //网络问题
                                if (!(getjieguo == null || getjieguo == "")) {
                                    //删首尾空
                                    getjieguo = getjieguo.trim();
                                    if (getjieguo.equals("0")) {
                                        //没有此人
                                        handler.sendEmptyMessageDelayed(1, 1000);
                                    } else {
                                        try {
                                            JSONArray arr = new JSONArray(getjieguo);
                                            JSONObject jsonObject = (JSONObject) arr.get(0);
                                            SharedPreferences sharedPreferences = getSharedPreferences("tarenzhanghu", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("shoujihao", "");
                                            editor.putString("userid", jsonObject.getString("userid"));
                                            editor.putString("youjishu", jsonObject.getString("youjishu"));
                                            editor.putString("rijishu", jsonObject.getString("rijishu"));
                                            editor.putString("nicheng", jsonObject.getString("nicheng"));
                                            editor.putString("guanzhushu", jsonObject.getString("guanzhushu"));
                                            editor.putString("beiguanzhushu", jsonObject.getString("beiguanzhushu"));
                                            editor.putString("icon", jsonObject.getString("icon"));
                                            editor.commit();
                                        } catch (JSONException ex) {
                                        }
                                        shuliang += 1;
                                        editor1.putInt("shuliang", shuliang);
                                        editor1.putString("jilu" + shuliang, jiegou);
                                        editor1.commit();
                                        listString1.add(0, jiegou);
                                        handler.sendEmptyMessageDelayed(2, 1000);
                                    }
                                } else {
                                    //网络失
                                    handler.sendEmptyMessageDelayed(3, 1000);
                                }
                            }
                        }.start();
                    }

                }
            }
        });

//语言识别
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);
        speechTips = View.inflate(this, R.layout.bd_asr_popup_speech, null);
        speechWave = speechTips.findViewById(R.id.wave);
        speechTips.setVisibility(View.GONE);
        addContentView(speechTips, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ImageButton imageButton = (ImageButton) findViewById(R.id.yuyin);
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        speechTips.setVisibility(View.VISIBLE);
                        speechRecognizer.cancel();
                        Intent intent = new Intent();
                        BaiduyuyingUtil.bindParams(intent, SearchFriendActivity.this);
                        intent.putExtra("vad", "touch");
                        editText.setText("");
                        // txtLog.setText("");
                        speechRecognizer.startListening(intent);
                        return true;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        speechTips.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //没有此人
                    dissmissProgressDialog();
                    ToastUtil.show(SearchFriendActivity.this, "没有找到此人！");
                    break;
                case 2:
                    //找到成功
                    ArrayAdapter<String> aAdapter1 = new ArrayAdapter<String>(
                            getApplicationContext(),
                            R.layout.content_search_friend_record, listString1);

                    //下面出现动画
                    // SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(baseAdapter,PopActivity.this));
                    // swingBottomInAnimationAdapter.setAbsListView(listView);
                    // assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    //  swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
//右边出现动画

                    if (!(mAnimAdapter instanceof SwingRightInAnimationAdapter)) {
                        mAnimAdapter = new SwingRightInAnimationAdapter(aAdapter1);
                        mAnimAdapter.setAbsListView(listView);
                        listView.setAdapter(mAnimAdapter);
                    }
                    listView.setVisibility(View.VISIBLE);

                    dissmissProgressDialog();
                    SearchFriendActivity.this.startActivity(new Intent(SearchFriendActivity.this, FriendAcountActivity.class));

                    break;
                case 3:
                    //没有此人
                    dissmissProgressDialog();
                    ToastUtil.show(SearchFriendActivity.this, "网络故障！");
                    break;

            }
        }
    };


    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        final int VTAG = 0xFF00AA01;
        Integer rawHeight = (Integer) speechWave.getTag(VTAG);
        if (rawHeight == null) {
            rawHeight = speechWave.getLayoutParams().height;
            speechWave.setTag(VTAG, rawHeight);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) speechWave.getLayoutParams();
        params.height = (int) (rawHeight * rmsdB * 0.01);
        params.height = Math.max(params.height, speechWave.getMeasuredWidth());
        speechWave.setLayoutParams(params);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }


    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        print("识别失败：" + sb.toString());
    }

    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        try {
            print("origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            print("origin_result=[warning: bad json]\n" + json_res);
        }
        // btn.setText("开始");
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        editText.setText(nbest.get(0) + strEnd2Finish);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            editText.setText(nbest.get(0));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                print("EVENT_ERROR, " + reason);
                break;
        }
    }

    private void print(String msg) {
        Log.e("uri", msg);
        // txtLog.append(msg + "\n");
        //  ScrollView sv = (ScrollView) txtLog.getParent();
        //sv.smoothScrollTo(0, 1000000);
        Log.d(TAG, "----" + msg);
    }


    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)

                    .content("稍等!")
                    .progress(true, 0);

            progDialog = builder.build();
            progDialog.setCancelable(false);
        }

        progDialog.show();
    }


    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


}
