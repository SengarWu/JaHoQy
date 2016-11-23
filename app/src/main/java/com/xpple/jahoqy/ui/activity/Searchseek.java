package com.xpple.jahoqy.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.SeekHelpAdapter;
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.bean.SeekHelp;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.util.CommonUtils;
import com.xpple.jahoqy.view.xlistview.SimpleFooter;
import com.xpple.jahoqy.view.xlistview.SimpleHeader;
import com.xpple.jahoqy.view.xlistview.ZrcListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class Searchseek extends BaseActivity implements View.OnClickListener {
    private String sex="";
    private String ty="所有类型";
    private ZrcListView listView;
    private Handler handler;//用于接收子线程的信息以刷新主线程
    private SeekHelpAdapter adapter;
    private ArrayList<SeekHelp> items = new ArrayList<>();
    int curPage = 0;
    private ImageView bk;
    private RadioGroup rg;
    private Spinner type;
    private DrawerLayout mdrawlayout;
    private ImageView changemap;
    private MyLocationListener mMyLocationListener;
    private LocationClient mLocationClient;
    private ImageView cer,rest;
    private ProgressDialog p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_seekk);
        p=new ProgressDialog(mContext);
        p.setMessage("正在定位....");
        p.setCanceledOnTouchOutside(false);
        p.show();
        initBaiduLocClient();
    }

    private void initLoc() {

    }

    private void init() {
        //获得各个控件
        bk=(ImageView)findViewById(R.id.bk);
        bk.setOnClickListener(this);
        rg=(RadioGroup)findViewById(R.id.sexgroup);
        type=(Spinner)findViewById(R.id.type);
        mdrawlayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        cer=(ImageView)findViewById(R.id.certain);
        rest=(ImageView)findViewById(R.id.reset);
        cer.setOnClickListener(this);
        rest.setOnClickListener(this);
        changemap=(ImageView)findViewById(R.id.change);
        changemap.setOnClickListener(this);

        initSelectdrawer();

        listView = (ZrcListView)findViewById(R.id.seeklist);
        handler = new Handler();
        // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
        SimpleHeader header = new SimpleHeader(this);
        header.setTextColor(0xffee71a1);
        header.setCircleColor(0xffee71a1);
        listView.setHeadable(header);

        // 设置加载更多的样式（可选）
        SimpleFooter footer = new SimpleFooter(this);
        footer.setCircleColor(0xffee71a1);
        listView.setFootable(footer);

        // 设置列表项出现动画（可选）
        listView.setItemAnimForTopIn(R.anim.top_item_in);
        listView.setItemAnimForBottomIn(R.anim.bottom_item_in);

        adapter = new SeekHelpAdapter(this, items);
        listView.setAdapter(adapter);

        if (items.size() <= 0)
        {
            listView.refresh(); // 主动下拉刷新
        }

        // 下拉刷新事件回调（可选）
        listView.setOnRefreshStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                refresh();
            }
        });

        // 加载更多事件回调（可选）
        listView.setOnLoadMoreStartListener(new ZrcListView.OnStartListener() {
            @Override
            public void onStart() {
                loadMore();
            }
        });
        listView.setOnItemClickListener(new ZrcListView.OnItemClickListener() {
            @Override
            public void onItemClick(ZrcListView parent, View view, int position, long id) {
                Intent item = new Intent(Searchseek.this, ShowSeekHelp.class);
                item.putExtra("E_or_Not","list");
                item.putExtra("item", items.get(position).getObjectId());
                item.putExtra("username",items.get(position).getUser().getUsername());
                item.putExtra("title",items.get(position).getTitle());
                item.putExtra("time",items.get(position).getUpdatedAt());
                item.putExtra("gn",String.valueOf(items.get(position).getGiveHelpNum()));
                item.putExtra("nn",String.valueOf(items.get(position).getNeedNum()));
                item.putExtra("award",String.valueOf(items.get(position).getAwardInteral()));
                item.putExtra("money",String.valueOf(items.get(position).getGivemoney()));
                if(items.get(position).getUser().getUserPhoto()!=null)
                {
                    item.putExtra("url", items.get(position).getUser().getUserPhoto().getUrl());
                }
                startActivity(item);
            }
        });
    }

    private void refresh() {
        curPage=0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Querysh();//queryProxy为自定义的查询类
            }
        });
    }
    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                QueryCountsh();
            }
        });
    }

    private void Querysh(){
        BmobQuery<SeekHelp> query = new BmobQuery<SeekHelp>();
        query.addQueryKeys("user,address,state,giveHelpNum,needNum,title,type,awardInteral,limittime,givemoney,-updatedAt");
        query.include("user");
        BmobQuery<User> userquery=new BmobQuery<User>();
        if(!sex.equals("")) {
            userquery.addWhereEqualTo("gender", sex);
        }
        userquery.addWhereNotEqualTo("objectId",CurrentUser.getObjectId());

        if(!ty.equals("所有类型")) {
            query.addWhereEqualTo("type", ty);
        }
        query.order("-updatedAt");//时间排序
        query.addWhereMatchesQuery("user", "_User", userquery);
        query.addWhereWithinRadians("address", new BmobGeoPoint(CustomApplication.lastPoint.getLongitude(), CustomApplication.lastPoint.getLatitude()), 3);
        query.setLimit(10);
        query.addWhereEqualTo("state", "0");
        Date Now=new Date(System.currentTimeMillis());
        query.addWhereGreaterThan("limittime", new BmobDate(Now));

        query.findObjects(this, new FindListener<SeekHelp>() {//查询多条数据
            @Override
            public void onSuccess(List<SeekHelp> object) {
                if (object.size() != 0) {
                    if (CommonUtils.isNotNull(object)) {//监测网络等是否可用
                        items.clear();
                        adapter.addAll(object);
                        if (object.size() < 10) {
                            listView.setRefreshSuccess("加载完成"); // 通知加载完成
                            listView.stopLoadMore();
                        } else {
                            listView.setSelection(0);
                            listView.setRefreshSuccess("加载成功"); // 通知加载成功
                            listView.startLoadMore(); // 开启LoadingMore功能
                        }
                    } else {
                        listView.setRefreshSuccess("暂无数据");
                        listView.stopLoadMore();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    listView.setRefreshFail("抱歉，没搜到任何结果");
                    items.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int code, String msg) {
                listView.setRefreshFail("加载失败");
                items.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void QueryCountsh(){
        BmobQuery<SeekHelp> query=new BmobQuery<>();
        query.include("user");
        BmobQuery<User> userquery=new BmobQuery<User>();
        userquery.addWhereNotEqualTo("objectId",CurrentUser.getObjectId());
        if(!sex.equals("")) {
            userquery.addWhereEqualTo("sex", sex);
        }
        query.addWhereMatchesQuery("user", "_User", userquery);
        if(!ty.equals("所有类型")) {
            query.addWhereEqualTo("type", ty);
        }

        query.count(this, Question.class, new CountListener() {
            @Override
            public void onSuccess(int count) {
                if (count > items.size()) {
                    curPage++;
                    Querymore(curPage);
                } else {
                    listView.stopLoadMore();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                listView.stopLoadMore();
            }
        });
    }

    private void Querymore(int page){
        BmobQuery<SeekHelp> query = new BmobQuery<SeekHelp>();
        query.addQueryKeys("address,state,giveHelpNum,needNum,title,type,awardInteral,limittime,givemoney,-updatedAt");
        query.include("user");
        BmobQuery<User> userquery=new BmobQuery<User>();
        userquery.addWhereNotEqualTo("objectId",CurrentUser.getObjectId());
        if(!sex.equals("")) {
            userquery.addWhereEqualTo("sex", sex);
        }
        query.addWhereMatchesQuery("user", "_User", userquery);
        if(!ty.equals("所有类型")) {
            query.addWhereEqualTo("type", ty);
        }
        query.order("-updatedAt");//时间排序
        query.setLimit(10);
        query.setSkip(page * 10);
        query.addWhereMatchesQuery("user", "_User", userquery);
        // query.addQueryKeys("title,details,type,Award");
        query.findObjects(this, new FindListener<SeekHelp>() {//查询多条数据
            @Override
            public void onSuccess(List<SeekHelp> object) {
                if (CommonUtils.isNotNull(object)) {
                    adapter.addAll(object);
                }
                adapter.notifyDataSetChanged();
                listView.setLoadMoreSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                listView.stopLoadMore();
            }
        });
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
                listView.setSelection(0);
                listView.refresh();
                break;
            case R.id.reset:
                rg.clearCheck();
                type.setSelection(0);
                break;
            case R.id.change:
                Intent ii = new Intent(Searchseek.this, ShowSeekMap.class);
                startActivity(ii);
                break;
            case R.id.bk:
                this.finish();
                break;
        }
    }

    private void initSelectdrawer() {
        Spinner type = (Spinner) findViewById(R.id.type);
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

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            double latitude = location.getLatitude();
            double longtitude = location.getLongitude();
            if (CustomApplication.lastPoint != null) {
                if (CustomApplication.lastPoint.getLatitude() == latitude
                        && CustomApplication.lastPoint.getLongitude() == longtitude) {
                    // BmobLog.i("两次获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
                    mLocationClient.stop();
                    p.dismiss();
                    init();
                    return;
                }
            }
            CustomApplication.lastPoint = new BmobGeoPoint(longtitude, latitude);
        }
    }
    private void initBaiduLocClient() {
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        initLocation();
        mLocationClient.start();
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
}
