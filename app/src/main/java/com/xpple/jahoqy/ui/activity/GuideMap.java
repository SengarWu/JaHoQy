package com.xpple.jahoqy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;

import java.util.ArrayList;

public class GuideMap extends Activity {
    BaiduMap mBaiduMap=null;
    private MapView mMapView;
    private double Lo,La;
    private Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_map);
        initView();
    }

    private void initView() {
        Intent intent=getIntent();
        Lo=Double.valueOf(intent.getStringExtra("Lo"));
        La=Double.valueOf(intent.getStringExtra("La"));
        mMapView = (MapView)findViewById(R.id.nearmap);
        mBaiduMap=mMapView.getMap();

        float f = mBaiduMap.getMaxZoomLevel();//19.0 最小比例尺
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(new LatLng(La, Lo), f - 2);//设置到100米的大小
        mBaiduMap.animateMapStatus(u);

/*        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.midmar);
        MarkerOptions option1 = new MarkerOptions().icon(bitmap).position(new LatLng(La,Lo));
        mBaiduMap.addOverlay(option1);*/
        // 通过marker的icons设置一组图片，再通过period设置多少帧刷新一次图片资源
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        BitmapDescriptor ba = BitmapDescriptorFactory
                .fromResource(R.mipmap.miin);
        BitmapDescriptor bb = BitmapDescriptorFactory
                .fromResource(R.mipmap.midmar);
        BitmapDescriptor bc = BitmapDescriptorFactory
                .fromResource(R.mipmap.max);
        giflist.add(ba);
        giflist.add(bb);
        giflist.add(bc);
        LatLng point = new LatLng(La,Lo);
        OverlayOptions ooD = new MarkerOptions().position(point).icons(giflist)
                .zIndex(0).period(10);
        marker = (Marker) (mBaiduMap.addOverlay(ooD));

    }


}
