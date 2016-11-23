package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.SeekHelp;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.util.CommonUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

public class ShowSeekMap extends BaseActivity implements View.OnClickListener{
    private MapView mMapView;
    BaiduMap mBaiduMap=null;
    Double Lo;
    Double La;
    private Marker mMarker[];//存储要显示到地图的所有标记
    private String sex="";
    private String ty="所有类型";
    private Spinner type;
    private DrawerLayout mdrawlayout;
    private ImageView cer,rest;
    private RadioGroup rg;
    private ImageView bk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_help_map);
        init();
    }

    private void init() {
        bk=(ImageView)findViewById(R.id.back);
        bk.setOnClickListener(this);
        rg=(RadioGroup)findViewById(R.id.sexgroup);
        mdrawlayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        cer=(ImageView)findViewById(R.id.certain);
        rest=(ImageView)findViewById(R.id.reset);
        cer.setOnClickListener(this);
        rest.setOnClickListener(this);
        initSelectdrawer();
        mMapView = (MapView)findViewById(R.id.seekmap);
        mBaiduMap=mMapView.getMap();
        Lo = CustomApplication.lastPoint.getLongitude();
        La = CustomApplication.lastPoint.getLatitude();
        float f = mBaiduMap.getMaxZoomLevel();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(new LatLng(La, Lo), f - 2);//设置到100米的大小
        mBaiduMap.animateMapStatus(u);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < mMarker.length; i++) {
                    if (marker == mMarker[i]) {
                        Intent item = new Intent(ShowSeekMap.this, ShowSeekHelp.class);
                        item.putExtra("item", marker.getTitle());
                        item.putExtra("E_or_Not", "map");
                        startActivity(item);
                    }
                }
                return true;
            }
        });
        Querysh();
    }

    private void Querysh(){
        BmobQuery<SeekHelp> query = new BmobQuery<SeekHelp>();
        query.addQueryKeys("user,address,type,objectId");
        query.include("user");
        BmobQuery<User> userquery=new BmobQuery<User>();

        if(!sex.equals("")) {
            userquery.addWhereEqualTo("sex", sex);
            query.addWhereMatchesQuery("user", "_User", userquery);
        }

        if(!ty.equals("所有类型")) {
            query.addWhereEqualTo("type", ty);
        }

        query.addWhereWithinRadians("address", new BmobGeoPoint(Lo,La), 3);
        query.setLimit(10);
        query.addWhereEqualTo("state", "0");
        Date Now=new Date(System.currentTimeMillis());
        query.addWhereGreaterThan("limittime", new BmobDate(Now));

        query.findObjects(this, new FindListener<SeekHelp>() {//查询多条数据
            @Override
            public void onSuccess(List<SeekHelp> object) {
                if (object.size() != 0) {
                if (CommonUtils.isNotNull(object)) {
                    mBaiduMap.clear();
                    mMarker = new Marker[object.size()];
                    for (int i = 0; i < object.size(); i++) {
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.other);
                        switch (object.get(i).getType()){
                            case "寻人启示":
                                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.lostpeople);
                                break;
                            case "找人维修":
                                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.repair);
                                break;
                            case "寻物启示":
                                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.lostthing);
                                break;
                            case "生活帮带":
                                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.helpcarry);
                                break;
                            case "学习互助":
                                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.study);
                                break;
                            case "江湖眼线":
                                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.jhsign);
                                break;
                            case "江湖救急":
                                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.emergency);
                                break;
                            case "其它":
                                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.other);
                                break;
                            default:
                                bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.other);
                                break;
                        }

                        LatLng pl = new LatLng(object.get(i).getAddress().getLatitude(), object.get(i).getAddress().getLongitude());
                        MarkerOptions option = new MarkerOptions().icon(bitmap).position(pl);
                        if (object.get(i).getType() != null) {
                            OverlayOptions ooText = new TextOptions().fontSize(20).text(object.get(i).getType())
                                    .position(pl);
                            mBaiduMap.addOverlay(ooText);
                        }
                        mMarker[i] = (Marker) mBaiduMap.addOverlay(option);
                        mMarker[i].setTitle(object.get(i).getObjectId());
                    }
                }else{
                    showToast("暂无数据！");
                }
                } else {
                    showToast("抱歉，没搜到任何结果！");
                }
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }
    private void initSelectdrawer() {
        type=(Spinner)findViewById(R.id.type);
        List<String> data_list2 = new ArrayList<String>();
        data_list2.add("所有类型");
        data_list2.add("寻人启示");
        data_list2.add("找人维修");
        data_list2.add("代取东西");
        data_list2.add("生活帮带");
        data_list2.add("学习互助");
        data_list2.add("江湖眼线");
        data_list2.add("其它");
        ArrayAdapter<String> adapter2=new ArrayAdapter<String>(this,
                R.layout.spinner_check,data_list2);
        type.setAdapter(adapter2);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.certain:
                switch (rg.getCheckedRadioButtonId()){
                    case R.id.onlyboy:
                        sex="男";
                        break;
                    case R.id.onlygirl:
                        sex="女";
                        break;
                    case R.id.nolimit:
                        sex="";
                        break;
                    default:
                        sex="";
                        break;
                }
                ty=type.getSelectedItem().toString();
                mdrawlayout.closeDrawer(GravityCompat.START);
                mBaiduMap.clear();
                Querysh();
                break;
            case R.id.reset:
                rg.clearCheck();
                type.setSelection(0);
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
