package com.xpple.jahoqy.ui.otherFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.xpple.jahoqy.BaseClass.BaseFragment;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.NearUser;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.activity.Shownearuser;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;


public class near_mapFragment extends BaseFragment implements BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener, View.OnClickListener {
    private MapView mMapView;
    private View parentView;
    BaiduMap mBaiduMap=null;
    Double Lo;
    Double La;
    private int mappage=0;
    private int maxpage;
    private Button last,later;
    private TextView pagetext;
    private Marker mMarker[];//存储要显示到地图的所有标记
    private BmobGeoPoint allplace[];//存储要显示所有附近的人的位置
    private String[] allnear;//存储所有要显示附近的人的用户名
    private String[] allid;
    public near_mapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView=inflater.inflate(R.layout.fragment_near_map, container, false);
        initView();
        return parentView;
    }

    private void initView() {
        last=(Button)parentView.findViewById(R.id.last);
        later=(Button)parentView.findViewById(R.id.later);
        pagetext=(TextView)parentView.findViewById(R.id.pageshow);
        last.setOnClickListener(this);
        later.setOnClickListener(this);

        mMapView = (MapView) parentView.findViewById(R.id.nearmap);
        mBaiduMap=mMapView.getMap();
        Lo= CustomApplication.lastPoint.getLongitude();
        La= CustomApplication.lastPoint.getLatitude();
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

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < mMarker.length; i++) {
                    if (marker == mMarker[i]) {
                        Intent item = new Intent(getActivity(),
                                Shownearuser.class);
                        item.putExtra("item", marker.getTitle());
                        startActivity(item);
                    }
                }
                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(this);
        searchmuchnear();

    }

    private void searchmuchnear() {
        BmobQuery<NearUser> query=new BmobQuery<>();

        BmobQuery<User> userquery=new BmobQuery<>();
        userquery.addWhereNotEqualTo("objectId", BmobUser.getCurrentUser(getActivity()).getObjectId());
        query.addWhereMatchesQuery("user","_User",userquery);

        query.include("user");
        query.addWhereWithinRadians("place", new BmobGeoPoint(Double.valueOf(Lo), Double.valueOf(La)), 3);
        Date Now=new Date(System.currentTimeMillis());
        Date d=new Date(Now.getTime()-600000);
        query.addWhereGreaterThan("updatedAt", new BmobDate(d));
        query.findObjects(getActivity(), new FindListener<NearUser>() {//查询多条数据
            @Override
            public void onSuccess(List<NearUser> object) {
                allnear = new String[object.size()];
                allid=new String[object.size()];
                allplace=new BmobGeoPoint[object.size()];
                if (object != null && object.size() > 0) {
                    for (int i = 0; i < object.size(); i++) {
                        allnear[i] = object.get(i).getUser().getUsername();
                        allplace[i] = object.get(i).getPlace();
                        allid[i]=object.get(i).getUser().getObjectId();
                    }
                    if(object.size()>=10) {
                        maxpage = object.size() / 10;
                        if ((object.size() % 10) != 0) {
                            maxpage = maxpage + 1;
                        }
                    }else{
                        maxpage=1;
                    }
                    pagetext.setText(String.valueOf(mappage+1)+"/"+String.valueOf(maxpage));
                    shownearuser(0);
                } else {
                    showToast("附近没有大侠");
                }
            }

            @Override
            public void onError(int code, String msg) {
                showToast("加载失败");
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getActivity(), "1", Toast.LENGTH_LONG);
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.last:
                if(mappage!=0){
                    mappage--;
                    pagetext.setText(String.valueOf(mappage+1)+"/"+String.valueOf(maxpage));
                    shownearuser(mappage);
                }
                break;
            case R.id.later:
                if(mappage<maxpage-1) {
                    mappage++;
                    pagetext.setText(String.valueOf(mappage+1)+"/"+String.valueOf(maxpage));
                    shownearuser(mappage);
                }
                break;
        }
    }

    private void shownearuser(int mappage){//把当前页码对应的信息显示到地图
        mBaiduMap.clear();
        int a=allnear.length-(mappage)*10;//剩余未显示的数目
        int s;//要显示的数目
        if(a>=10){
            s=10;
        }else{
            s=a;
        }
        mMarker = new Marker[s];
        for(int i=mappage*10;i<(mappage*10+s);i++){
            LatLng pl=new LatLng(allplace[i].getLatitude(),allplace[i].getLongitude());

            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.xiaoren);
            MarkerOptions option = new MarkerOptions().icon(bitmap).position(pl);
            OverlayOptions ooText = new TextOptions().fontSize(20).text(allnear[i])
                    .position(pl);
            mMarker[i-mappage*10]=(Marker)mBaiduMap.addOverlay(option);
            mMarker[i-mappage*10].setTitle(allid[i]);
            mBaiduMap.addOverlay(ooText);
        }
    }
}
