package com.map.wulimap.Activity;

import android.Manifest;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.BusRouteOverlay;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.andexert.library.RippleView;
import com.map.wulimap.R;
import com.map.wulimap.util.Sdkversionutil;
import com.map.wulimap.util.ToastUtil;
import com.map.wulimap.util.AMapUtil;


public class GoMapActivity extends AppCompatActivity
        implements LocationSource,
        AMapLocationListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {
    //初始化变量
    String index;
    String nicheng;
    String shijian;
    String didian;
    String tupian;
    String neirong;
    String jinwei;

//初始化地图

    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private UiSettings mUiSettings;
    private String keyWord = "";// 要输入的poi搜索关键字
    private LatLonPoint endPoint = null;
    private LatLonPoint startPoint = null;
    private int busMode = RouteSearch.BusDefault;// 公交默认模式
    private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
    private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
    private BusRouteResult busRouteResult;// 公交模式查询结果
    private DriveRouteResult driveRouteResult;// 驾车模式查询结果
    private WalkRouteResult walkRouteResult;// 步行模式查询结果
    private int routeType = 3;// 1代表公交模式，2代表驾车模式，3代表步行模式
    private RouteSearch routeSearch;
    MaterialDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_map);


        initsetting();
        //获取地图组件
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initview();
        initdate();
    }


    public void initdate() {
//获取传入数据
        Intent intent = getIntent();
        index = intent.getStringExtra("index");
        SharedPreferences sharedPreferences = getSharedPreferences("ditu", MODE_PRIVATE);
        didian = sharedPreferences.getString("didian", null);
        jinwei = sharedPreferences.getString("jinwei", null);
        tupian = sharedPreferences.getString("tupian", null);
        neirong = sharedPreferences.getString("neirong", null);
//显示所加图标
        String[] jinwei1 = jinwei.split("-");
        endPoint = AMapUtil.convertToLatLonPoint(new LatLng(Double.parseDouble(jinwei1[0]), Double.parseDouble(jinwei1[1])));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(Double.parseDouble(jinwei1[0]), Double.parseDouble(jinwei1[1])));
        markerOptions.title("");
        markerOptions.snippet("");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.app_icon));
        markerOptions.draggable(true);
        aMap.clear();// 清理地图上的所有覆盖物
        Marker marker = aMap.addMarker(markerOptions);
        marker.showInfoWindow();
    }


    public void initview() {
        if (aMap == null)
            aMap = mapView.getMap();
//设置地图监听
        aMap.setLocationSource(this);// 设置定位监听
        // aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
        setUpMap();//地图设置
//路线查询监听
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
//按钮监听
        final Button button1 = (Button) findViewById(R.id.bt1);
        final Button button2 = (Button) findViewById(R.id.bt2);
        final Button button3 = (Button) findViewById(R.id.bt3);
        final Button button4 = (Button) findViewById(R.id.bt4);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                button1.setBackground(getResources().getDrawable(R.drawable.yuanjiao_white));
                button1.setTextColor(getResources().getColor(R.color.bulu));
                button2.setBackground(getResources().getDrawable(R.drawable.yuanjiao_bule));
                button2.setTextColor(getResources().getColor(R.color.white));

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                button2.setBackground(getResources().getDrawable(R.drawable.yuanjiao_white));
                button2.setTextColor(getResources().getColor(R.color.bulu));
                button1.setBackground(getResources().getDrawable(R.drawable.yuanjiao_bule));
                button1.setTextColor(getResources().getColor(R.color.white));

            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
                button3.setBackground(getResources().getDrawable(R.drawable.yuanjiao_white));
                button3.setTextColor(getResources().getColor(R.color.bulu));
                button4.setBackground(getResources().getDrawable(R.drawable.yuanjiao_bule));
                button4.setTextColor(getResources().getColor(R.color.white));

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
                button4.setBackground(getResources().getDrawable(R.drawable.yuanjiao_white));
                button4.setTextColor(getResources().getColor(R.color.bulu));
                button3.setBackground(getResources().getDrawable(R.drawable.yuanjiao_bule));
                button3.setTextColor(getResources().getColor(R.color.white));

            }
        });

//返回按钮
        final RippleView rippleView1 = (RippleView) findViewById(R.id.fanhui);
        rippleView1.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                GoMapActivity.this.finish();
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

//主动获取定位权限
        if (Sdkversionutil.getSDKVersionNumber() >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        0);
            }
        }
    }


    //地图设置
    private void setUpMap() {
        mUiSettings = aMap.getUiSettings();//设置
        mUiSettings.setScaleControlsEnabled(true);//比例尺
        mUiSettings.setCompassEnabled(true);//指南针
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        //aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }


    //系统回调函数
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


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                startPoint = AMapUtil.convertToLatLonPoint(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Toast.makeText(GoMapActivity.this, errText, Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
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


    /**
     * 点击标签  监听器
     */
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    public View getInfoContents(Marker marker) {
        Log.e("uri", "dianle");
        return null;
    }

    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.poikeywordserch,
                null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(didian);
        TextView snippet = (TextView) view.findViewById(R.id.neirong);
        snippet.setText(neirong);

        ImageButton button = (ImageButton) view
                .findViewById(R.id.start_amap_app);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //驾车方式
                singleChoice();
            }
        });
        Log.e("uri", "dianle");
        return view;
    }


    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        if (routeType == 1) {// 公交路径规划
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, busMode, "北京", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == 2) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, drivingMode,
                    null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == 3) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, walkMode);
            routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }


    /**
     * 公交路线查询回调
     */
    @Override
    public void onBusRouteSearched(BusRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                busRouteResult = result;
                BusPath busPath = busRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap,
                        busPath, busRouteResult.getStartPos(),
                        busRouteResult.getTargetPos());
                routeOverlay.removeFromMap();
                routeOverlay.addToMap();
                routeOverlay.zoomToSpan();
            } else {
                ToastUtil.show(GoMapActivity.this, R.string.no_result);
                dissmissProgressDialog();
            }
        } else if (rCode == 27) {
            ToastUtil.show(GoMapActivity.this, R.string.error_network);
            dissmissProgressDialog();
        } else if (rCode == 32) {
            ToastUtil.show(GoMapActivity.this, R.string.error_key);
            dissmissProgressDialog();
        } else {
            ToastUtil.show(GoMapActivity.this, getString(R.string.error_other)
                    + rCode);
            dissmissProgressDialog();
        }
    }

    /**
     * 驾车结果回调
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                driveRouteResult = result;
                DrivePath drivePath = driveRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                        this, aMap, drivePath, driveRouteResult.getStartPos(),
                        driveRouteResult.getTargetPos());
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            } else {
                ToastUtil.show(GoMapActivity.this, R.string.no_result);
                dissmissProgressDialog();
            }
        } else if (rCode == 27) {
            ToastUtil.show(GoMapActivity.this, R.string.error_network);
            dissmissProgressDialog();
        } else if (rCode == 32) {
            ToastUtil.show(GoMapActivity.this, R.string.error_key);
            dissmissProgressDialog();
        } else {
            ToastUtil.show(GoMapActivity.this, getString(R.string.error_other)
                    + rCode);
            dissmissProgressDialog();
        }
    }

    /**
     * 步行路线结果回调
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                walkRouteResult = result;
                WalkPath walkPath = walkRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
                        aMap, walkPath, walkRouteResult.getStartPos(),
                        walkRouteResult.getTargetPos());
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
            } else {
                ToastUtil.show(GoMapActivity.this, R.string.no_result);
                dissmissProgressDialog();
            }
        } else if (rCode == 27) {
            ToastUtil.show(GoMapActivity.this, R.string.error_network);
            dissmissProgressDialog();
        } else if (rCode == 32) {
            ToastUtil.show(GoMapActivity.this, R.string.error_key);
            dissmissProgressDialog();
        } else {
            ToastUtil.show(GoMapActivity.this, getString(R.string.error_other)
                    + rCode);
            dissmissProgressDialog();
        }
    }


    /**
     * 自定义单选对话框
     */
//交通方式选择
    public void singleChoice() {
        String[] items = new String[]{"公交", "驾车", "步行"};
        //对话框选择
        new AlertDialog.Builder(GoMapActivity.this)
                .setTitle("交通方式")
                .setIcon(R.drawable.app_icon)
                .setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        //交通方式选择
                                        routeType = 1;
                                        //查询路线
                                        searchRouteResult(startPoint, endPoint);
                                        break;
                                    case 1:
                                        //交通方式选择
                                        routeType = 2;
                                        //查询路线
                                        searchRouteResult(startPoint, endPoint);
                                        break;
                                    case 2:
                                        //交通方式选择
                                        routeType = 3;
                                        //查询路线
                                        searchRouteResult(startPoint, endPoint);
                                        break;
                                }
                            }
                        }).create().show();

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
