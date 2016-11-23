package com.xpple.jahoqy.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.wheelPopWindow;
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.view.DeletableEditText;
import com.xpple.jahoqy.view.HeaderLayout;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class Publish2Activity extends BaseActivity implements View.OnClickListener{
    private MyLocationListener mMyLocationListener;
    private LocationClient mLocationClient;

    private DeletableEditText et_title;
    private Button btn_type;
    private ImageView iv_type;
    private EditText et_awardInteral;
    private EditText et_giveMoney;
    private EditText et_needNum;
    private TextView tv_total;
    private String Lo,La;
    private int total = 0;

    private wheelPopWindow mywheel; //类型选择滚动
    private MapView mMapView;
    BaiduMap mBaiduMap=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish2);
        initTopBarForBoth("发布任务", R.mipmap.ic_next, R.mipmap.actionbar_publish, Color.BLACK,
                new HeaderLayout.onRightImageButtonClickListener() {
                    @Override
                    public void onClick() { //下一步
                        if (TextUtils.isEmpty(et_title.getText())) {
                            showToast("请输入标题");
                            return;
                        }
                        if (TextUtils.isEmpty(btn_type.getText())) {
                            showToast("请选择发布类型");
                            return;
                        }
                        if (TextUtils.isEmpty(et_needNum.getText())) {
                            showToast("请输入需要人数");
                            return;
                        }
                        if (TextUtils.isEmpty(La) && TextUtils.isEmpty(Lo)) {
                            showToast("请选择发布地点");
                            return;
                        }
                        BmobQuery<User> query=new BmobQuery<User>();
                        query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
                        query.findObjects(mContext, new FindListener<User>() {
                            @Override
                            public void onSuccess(List<User> list) {
                                if(list!=null&list.size()>0){
                                    if (total > list.get(0).getUserIntegral()) {
                                        showToast("您的积分不足");
                                        return;
                                    }

                                    Intent intent = new Intent(Publish2Activity.this, Publish3Activity.class);
                                    intent.putExtra("title", et_title.getText().toString());
                                    intent.putExtra("type", btn_type.getText().toString());
                                    if (TextUtils.isEmpty(et_awardInteral.getText()))
                                    {
                                        intent.putExtra("awardInteral", 0);
                                    }
                                    else
                                    {
                                        intent.putExtra("awardInteral", et_awardInteral.getText().toString());
                                    }
                                    if (TextUtils.isEmpty(et_needNum.getText()))
                                    {
                                        intent.putExtra("needNum",0);
                                    }
                                    else
                                    {
                                        intent.putExtra("needNum", et_needNum.getText().toString());
                                    }
                                    if (TextUtils.isEmpty(et_giveMoney.getText()))
                                    {
                                        intent.putExtra("giveMoney", 0);
                                    }
                                    else
                                    {
                                        intent.putExtra("giveMoney", et_giveMoney.getText().toString());
                                    }
                                    intent.putExtra("Lo", Lo);
                                    intent.putExtra("La", La);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });

                    }
                });
        initView();
    }

    private void initView() {
        initBaiduLocClient();
        et_title = $(R.id.et_title);
        btn_type = $(R.id.btn_type);
        btn_type.setOnClickListener(this);
        iv_type = $(R.id.iv_type);
        iv_type.setOnClickListener(this);
        et_giveMoney = $(R.id.et_giveMoney);
        et_awardInteral = $(R.id.et_awardInteral);
        et_needNum = $(R.id.et_needNum);
        tv_total = $(R.id.tv_total);
        et_awardInteral.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(TextUtils.isEmpty(et_awardInteral.getText())) && !(TextUtils.isEmpty(et_needNum.getText())))
                {
                    int a = Integer.parseInt(et_awardInteral.getText().toString());
                    int b = Integer.parseInt(et_needNum.getText().toString());
                    total = a * b ;
                    tv_total.setText(String.valueOf(total));
                }
                else{
                    tv_total.setText(String.valueOf(""));
                }
            }
        });

        et_needNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(TextUtils.isEmpty(et_awardInteral.getText())) && !(TextUtils.isEmpty(et_needNum.getText())))
                {
                    int a = Integer.parseInt(et_awardInteral.getText().toString());
                    int b = Integer.parseInt(et_needNum.getText().toString());
                    total = a * b ;
                    tv_total.setText(String.valueOf(total));
                }
                else{
                    tv_total.setText(String.valueOf(""));
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_type:
            case R.id.iv_type:
                String[] items = getResources().getStringArray(R.array.style);
                mywheel = new wheelPopWindow(Publish2Activity.this, items, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        btn_type.setText(mywheel.getSelectDate());
                        switch (mywheel.getSelectDate())
                        {
                            case "寻人启示":
                                iv_type.setImageResource(R.mipmap.lostpeople);
                                break;
                            case "找人维修":
                                iv_type.setImageResource(R.mipmap.repair);
                                break;
                            case "寻物启示":
                                iv_type.setImageResource(R.mipmap.lostthing);
                                break;
                            case "生活帮带":
                                iv_type.setImageResource(R.mipmap.helpcarry);
                                break;
                            case "学习互助":
                                iv_type.setImageResource(R.mipmap.study);
                                break;
                            case "江湖眼线":
                                iv_type.setImageResource(R.mipmap.jhsign);
                                break;
                            case "江湖救急":
                                iv_type.setImageResource(R.mipmap.emergency);
                                break;
                            case "其他":
                                iv_type.setImageResource(R.mipmap.other);
                                break;
                            default:
                                iv_type.setImageResource(R.mipmap.other);
                                break;

                        }
                        mywheel.dismiss();
                    }
                });
                mywheel.showAtLocation(btn_type, Gravity.BOTTOM,
                        0, 0);
                break;
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            double latitude = location.getLatitude();
            double longtitude = location.getLongitude();
            if (CustomApplication.lastPoint != null) {
                if (CustomApplication.lastPoint.getLatitude() == location.getLatitude()
                        && CustomApplication.lastPoint.getLongitude() == location.getLongitude()) {
                    // BmobLog.i("两次获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
                    mLocationClient.stop();
                    Lo=String.valueOf(location.getLongitude());
                    La=String.valueOf(location.getLatitude());
                    mMapView = (MapView)findViewById(R.id.mapview);
                    mBaiduMap=mMapView.getMap();
                    float f = mBaiduMap.getMaxZoomLevel();//19.0 最小比例尺
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(La), Double.valueOf(Lo)), f - 2);//设置到100米的大小
                    mBaiduMap.animateMapStatus(u);

                    //在地图上标记自己的位置
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.myplace);
                    MarkerOptions option1 = new MarkerOptions().icon(bitmap).position(new LatLng(CustomApplication.lastPoint.getLatitude(), CustomApplication.lastPoint.getLongitude()));
                    OverlayOptions ooText1 = new TextOptions().fontSize(20).text("我的位置")
                            .position(new LatLng(CustomApplication.lastPoint.getLatitude(), CustomApplication.lastPoint.getLongitude()));
                    mBaiduMap.addOverlay(option1);
                    mBaiduMap.addOverlay(ooText1);

                    mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            mBaiduMap.clear();
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.mar3);
                            MarkerOptions option = new MarkerOptions().icon(bitmap).position(latLng);
                            mBaiduMap.addOverlay(option);
                            Lo = String.valueOf(latLng.longitude);
                            La = String.valueOf(latLng.latitude);
                        }
                    });
                    return;
                }
            }
            CustomApplication.lastPoint = new BmobGeoPoint(longtitude, latitude);
        }
    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
    }

    /**
     * 初始化百度定位sdk
     */
    private void initBaiduLocClient() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        initLocation();
        mLocationClient.start();
    }
}
