package com.map.wulimap.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.andexert.library.RippleView;
import com.baidu.speech.VoiceRecognitionService;
import com.map.wulimap.R;
import com.map.wulimap.util.AMapUtil;
import com.map.wulimap.util.BaiduyuyingUtil;
import com.map.wulimap.util.FileUtil;
import com.map.wulimap.util.GetRoundedBitmapUtil;
import com.map.wulimap.util.Sdkversionutil;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.util.UploadUtil;
import com.map.wulimap.util.UritopathUtil;
import com.map.wulimap.util.HtmlService;
import com.map.wulimap.util.IshaveemojiUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Write_rijiActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener, RecognitionListener {
    //初始化变量
    String capturePath = null;
    String shoujihao;
    String wangzhi;
    String tupianming;
    String gongkai = "1";
    String jingweidu;
    String dizhi;
    String neirong;
    String nicheng;
    Boolean youwutupian = false;
    Boolean diweichengong = false;
    String shangcibaocun = "";
    int rijishu;
    //初始化控件
    TextView textView2;//地点
    MaterialDialog progDialog;
    MapView mapView;
    AMap aMap;
    UiSettings mUiSettings;
    LocationSource.OnLocationChangedListener mListener;
    AMapLocationClient mlocationClient;
    AMapLocationClientOption mLocationOption;
    ImageView imageView;
    EditText editText;

    //语音
    private static final String TAG = "Touch";
    private static final int EVENT_ERROR = 11;
    private SpeechRecognizer speechRecognizer;
    private long speechEndTime = -1;
    View speechTips;
    View speechWave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_riji);


        initsetting();
        initdate();
        //定位
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        initview();

    }


    public void initview() {
        if (aMap == null)
            aMap = mapView.getMap();
        aMap.setLocationSource(this);// 设置定位监听
        setUpMap();

//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Write_rijiActivity.this.finish();
            }

        });

//时间显示
        TextView textView1 = (TextView) findViewById(R.id.shijian1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        textView1.setText("时间:" + str);

//配图按钮
        imageView = (ImageView) findViewById(R.id.tupian1);
        imageView.setOnClickListener(new View.OnClickListener() {
            String[] items = new String[]{"手机拍照", "手机相册"};

            public void onClick(View view) {
                //对话框选择
                new AlertDialog.Builder(Write_rijiActivity.this)
                        .setTitle("获取图片方式")
                        .setIcon(R.drawable.app_icon)
                        .setItems(items,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:
                                                //拍照
                                                getImageFromCamera();
                                                break;
                                            case 1:
                                                //相册
                                                Intent intent = new Intent();
                                                intent.setData(Uri
                                                        .parse("content://media/internal/images/media"));
                                                intent.setAction(Intent.ACTION_PICK);
                                                startActivityForResult(Intent
                                                                .createChooser(intent,
                                                                        "Select Picture"),
                                                        1);
                                                break;
                                        }
                                    }
                                }).create().show();
            }
        });


//地点显示
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.didian);
        textView2 = (TextView) findViewById(R.id.didian1);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


//权限按钮
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.quanxian);
        final TextView textView3 = (TextView) findViewById(R.id.quanxian1);
        linearLayout1.setOnClickListener(new View.OnClickListener() {
            String[] items = new String[]{"公开", "不公开"};

            public void onClick(View view) {
                //对话框选择
                new AlertDialog.Builder(Write_rijiActivity.this)
                        .setTitle("日记是否公开")
                        .setIcon(R.drawable.app_icon)
                        .setItems(items,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0:
                                                textView3.setText("是否公开日记（公开）");
                                                gongkai = "1";
                                                break;
                                            case 1:
                                                textView3.setText("是否公开日记（不公开）");
                                                gongkai = "0";
                                                break;
                                        }
                                    }
                                }).create().show();
            }
        });

//日记内容
        editText = (EditText) findViewById(R.id.neirong);

//发表按钮
        final RippleView rippleView = (RippleView) findViewById(R.id.fabiao);
        rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                if (diweichengong == false || jingweidu.length() < 3) {
                    Toast.makeText(Write_rijiActivity.this, "定位失败，请检查网络！", Toast.LENGTH_LONG).show();
                } else {
                    if (youwutupian == false) {
                        Toast.makeText(Write_rijiActivity.this, "没有漂亮的图片哦！", Toast.LENGTH_LONG).show();
                    } else {
                        //获取内容
                        neirong = editText.getText().toString();
                        //过滤 emoj
                        neirong = IshaveemojiUtil.filterEmoji(neirong);

                        // Toast.makeText(Write_rijiActivity.this, neirong, Toast.LENGTH_LONG).show();
                        //显示进度条
                        showProgressDialog();
                        //上传图片
                        new Thread() {
                            public void run() {
                                try {
                                    //上传监听
                                    UploadUtil uploadUtil = UploadUtil.getInstance();
                                    uploadUtil.setOnUploadProcessListener(new UploadUtil.OnUploadProcessListener() {
                                        @Override
                                        public void onUploadDone(int responseCode, String message) {
                                            handler.sendEmptyMessageDelayed(1, 1000);
                                        }

                                        @Override
                                        public void onUploadProcess(int uploadSize) {
                                        }

                                        @Override
                                        public void initUpload(int fileSize) {
                                        }
                                    });
                                    //上传
                                    Map<String, String> map = new HashMap<String, String>();
                                    map = null;
                                    uploadUtil.uploadFile(Environment.getExternalStorageDirectory().getPath() + "/map/" + tupianming + "-yasuo.jpg", "myfile", "http://wode123123.sinaapp.com/gushiditu/shangchan.php", map);
                                } catch (Exception e) {
                                }
                            }
                        }.start();
                    }
                }


            }

        });

//语言识别按
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
                        BaiduyuyingUtil.bindParams(intent, Write_rijiActivity.this);
                        intent.putExtra("vad", "touch");
                        shangcibaocun = editText.getText().toString();
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


    public void initsetting() {
//沉浸模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

//主动获取定位权限 录音权限
        if (Sdkversionutil.getSDKVersionNumber() >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);
            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        0);
            }
        }
    }


    public void initdate() {
        //图片名
        SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
        shoujihao = sharedPreferences.getString("shoujihao", null);
        nicheng = sharedPreferences.getString("nicheng", null);
        tupianming = nicheng + "-riji-" + GetRoundedBitmapUtil.getRandomCode();

    }


    //选图后回调函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //拍照后
                case 0:
                    //显示图片
                    tupianxianshi();
                    youwutupian = true;
                    break;
                //选图后
                case 1:
                    //url  to  path
                    Uri uri = data.getData();
                    String lujing = UritopathUtil.getImageAbsolutePath(Write_rijiActivity.this, uri);
                    //复制文件
                    File dir = new File("/sdcard/map/");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    FileUtil.fuzhiwenjian(lujing, "/sdcard/map/" + tupianming + ".jpg");
                    //显示图片
                    tupianxianshi();
                    youwutupian = true;
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //拍照  保存本地
    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            File dir = new File("/sdcard/map/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            capturePath = "/sdcard/map/" + tupianming + ".jpg";
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
            getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(getImageByCamera, 0);
        } else {
            Toast.makeText(Write_rijiActivity.this, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }


    //地图系统回调函数
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    //地图设置
    private void setUpMap() {
        mUiSettings = aMap.getUiSettings();//设置
        mUiSettings.setScaleControlsEnabled(true);//比例尺
        mUiSettings.setCompassEnabled(true);//指南针
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                //定位成功 获取 经纬度 详细信息
                diweichengong = true;
                jingweidu = AMapUtil.format(amapLocation.getLatitude()) + "-" + AMapUtil.format(amapLocation.getLongitude());
                //Toast.makeText(Write_rijiActivity.this, jingweidu, Toast.LENGTH_LONG).show();
                jingweidu = jingweidu.trim().replaceAll(" ", "");
                String dizhi1 = amapLocation.getProvince() + amapLocation.getCity() + amapLocation.getDistrict() + amapLocation.getStreet() + amapLocation.getStreetNum();//街道门牌号信息
                String dizhi2 = amapLocation.getAddress();
                //Toast.makeText(Write_rijiActivity.this, dizhi, Toast.LENGTH_LONG).show();
                dizhi2 = dizhi2.trim().replaceAll(" ", "");
                dizhi1 = dizhi1.trim().replaceAll(" ", "");

                if (!(dizhi2 == null || dizhi2 == "")) {
                    textView2.setText(dizhi2);
                    dizhi = dizhi2;
                } else {
                    if (!(dizhi1 == null || dizhi1 == "")) {
                        textView2.setText(dizhi1);
                        dizhi = dizhi1;
                    } else {
                        textView2.setText("未知");
                        dizhi = "未知";
                    }

                }

            } else {
                Toast.makeText(Write_rijiActivity.this, "当前定位失败，再试试！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(true);
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    //上传成功
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ToastUtil.show(Write_rijiActivity.this, "发表成功");
                    new Thread() {
                        public void run() {
                            try {
                                //url编码
                                try {
                                    neirong = URLEncoder.encode(neirong, "utf-8");
                                    nicheng = URLEncoder.encode(nicheng, "utf-8");
                                    dizhi = URLEncoder.encode(dizhi, "utf-8");
                                    tupianming = URLEncoder.encode(tupianming, "utf-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                HtmlService.getHtml("http://wode123123.sinaapp.com/gushiditu/riji.php?shoujihao=" + shoujihao + "&nicheng=" + nicheng + "&gongxiang=" + gongkai + "&shanchu=0&pinglunshu=0&zanshu=0&didian=" + dizhi + "&jinwei=" + jingweidu + "&tupian=" + tupianming + "&neirong=" + neirong);
                            } catch (Exception e) {
                            }
                        }
                    }.start();

                    SharedPreferences sharedPreferences = getSharedPreferences("zhanghu", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("rijishu", Integer.toString(rijishu));
                    editor.commit();
                    dissmissProgressDialog();
                    Write_rijiActivity.this.finish();
                    break;
            }
        }
    };


    //图片压缩显示  压缩保存
    public void tupianxianshi() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/map/" + tupianming + ".jpg", options); //此时返回bm为空
        options.inJustDecodeBounds = false;
        //计算缩放比
        int be = (int) (options.outHeight / (float) 200);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile("/sdcard/map/" + tupianming + ".jpg", options);
        imageView.setImageBitmap(bitmap);
        //删除文件
        FileUtil.shanchuwenjian("/sdcard/map/" + tupianming + ".jpg");
        //写出压缩文件
        File file = new File("/sdcard/map/" + tupianming + "-yasuo.jpg");
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();

            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    //百度语音回调
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
        editText.setText(shangcibaocun + nbest.get(0) + strEnd2Finish);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            editText.setText(shangcibaocun + nbest.get(0));
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

    //语音log
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
