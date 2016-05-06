package com.map.wulimap.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.baidu.speech.VoiceRecognitionService;
import com.map.wulimap.util.BaiduyuyingUtil;
import com.map.wulimap.util.Constant;
import com.map.wulimap.R;
import com.map.wulimap.util.Sdkversionutil;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchPositionActivity extends AppCompatActivity implements RecognitionListener {
    //初始化控件
    AnimationAdapter mAnimAdapter;
    AutoCompleteTextView searchText;
    ListView listView;
    //语音
    View speechTips;
    View speechWave;
    SpeechRecognizer speechRecognizer;
    //初始化语音常量
    private static final String TAG = "Touch";
    private static final int EVENT_ERROR = 11;
    //初始化变量
    SharedPreferences sharedPreferences;
    String keyWord = "";// 要输入的poi搜索关键字
    List<String> listString1;
    long speechEndTime = -1;
    int biaoji;
    int shuliang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_search_position);

        initsetting();
        initview();

    }


    public void initsetting() {
//主动获取录音权限
        if (Sdkversionutil.getSDKVersionNumber() >= 23) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        0);
            }
        }


// infile参数用于控制识别一个PCM音频流（或文件），每次进入程序都将该值清楚，以避免体验时没有使用录音的问题
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove(Constant.EXTRA_INFILE).commit();


//沉浸模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    public void initview() {

//读取记录，并显示在listview
        sharedPreferences = getSharedPreferences("jilu", MODE_APPEND);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final ListView listView = (ListView) findViewById(R.id.sousujieguo);
        shuliang = sharedPreferences.getInt("shuliang", 0);
        listString1 = new ArrayList<String>();
        if (shuliang > 0) {
            for (int i = shuliang; i > 0; i--) {
                listString1.add(sharedPreferences.getString("jilu" + i, null));
            }
            ArrayAdapter<String> aAdapter1 = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString1);
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

//listView点击监听
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    keyWord = listString1.get(i);
                    Intent intent = getIntent();
                    intent.putExtra("keyword", keyWord);
                    SearchPositionActivity.this.setResult(0, intent);
                    SearchPositionActivity.this.finish();
                }
            });
        } else {
            // listView.setVisibility(View.GONE);
        }


//AutoCompleteTextView输入监听器
        searchText = (AutoCompleteTextView) findViewById(R.id.sousukuang);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().trim();
                InputtipsQuery inputquery = new InputtipsQuery(newText, "");
                Inputtips inputTips = new Inputtips(SearchPositionActivity.this, inputquery);
                //监听器
                inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                    @Override
                    public void onGetInputtips(List<Tip> tipList, int rCode) {
                        if (rCode == 0) {// 正确返回
                            List<String> listString = new ArrayList<String>();
                            for (int i = 0; i < tipList.size(); i++) {
                                listString.add(tipList.get(i).getName());
                            }
                            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                                    getApplicationContext(),
                                    R.layout.search_route_record, listString);
                            searchText.setAdapter(aAdapter);
                            aAdapter.notifyDataSetChanged();
                            biaoji = 1;
                        }
                    }
                });
                inputTips.requestInputtipsAsyn();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


//搜素按钮监听
        Button button = (Button) findViewById(R.id.sousu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (biaoji == 1) {
                    keyWord = searchText.getText().toString();
                    if ("".equals(keyWord)) {
                        Toast.makeText(SearchPositionActivity.this, "请输入搜索关键字", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        shuliang += 1;
                        editor.putInt("shuliang", shuliang);
                        editor.putString("jilu" + shuliang, keyWord);
                        editor.commit();
                        listString1.add(0, keyWord);
                        ArrayAdapter<String> aAdapter1 = new ArrayAdapter<String>(
                                getApplicationContext(),
                                R.layout.route_inputs, listString1);
                        listView.setVisibility(View.VISIBLE);

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
                        //启动地图
                        Intent intent = getIntent();
                        intent.putExtra("keyword", keyWord);
                        SearchPositionActivity.this.setResult(0, intent);
                        SearchPositionActivity.this.finish();
                    }
                }
            }
        });


//删除记录按钮
        TextView textView = (TextView) findViewById(R.id.shanchujilu);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuliang > 0) {
                    listString1.clear();
                    editor.putInt("shuliang", 0);
                    shuliang = 0;
                    editor.commit();
                    // listView.removeAllViews();
                    listView.setVisibility(View.GONE);

                }


            }
        });


//语言识别按钮
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
                        BaiduyuyingUtil.bindParams(intent, SearchPositionActivity.this);
                        intent.putExtra("vad", "touch");
                        searchText.setText("");
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


    //返回键按钮回调
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = getIntent();
            intent.putExtra("keyword", "");
            SearchPositionActivity.this.setResult(0, intent);
            SearchPositionActivity.this.finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    //语音识别回调
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
        searchText.setText(nbest.get(0) + strEnd2Finish);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            searchText.setText(nbest.get(0));
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


}
