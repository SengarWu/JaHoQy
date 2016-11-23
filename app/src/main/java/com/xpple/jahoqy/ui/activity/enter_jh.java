package com.xpple.jahoqy.ui.activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.NearUser;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.otherFragment.nearFragment;
import com.xpple.jahoqy.ui.otherFragment.near_mapFragment;
import com.xpple.jahoqy.view.HeaderLayout;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class enter_jh extends BaseActivity {
    private  int framgmentstate=3;
    private User a;
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_jh);
        initView();
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

    private void initView() {
        p=new ProgressDialog(mContext);
        p.setMessage("正在定位....");
        p.setCanceledOnTouchOutside(false);
        p.show();
        initTopBarForBoth("", R.mipmap.ditu, R.mipmap.actionbar_brjh, R.color.default_title_indicator_text_color, new HeaderLayout.onRightImageButtonClickListener() {
            @Override
            public void onClick() {
                if (framgmentstate == 0||framgmentstate==3) {
                    changeFragment(new near_mapFragment());
                    framgmentstate = 1;
                } else {
                    changeFragment(new nearFragment());
                    framgmentstate = 0;
                }
            }
        });
        initBaiduLocClient();
    }


    private void changeFragment(Fragment targetFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nearresult, targetFragment, "fragment")
                .setTransitionStyle(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                .commit();
    }

    /**
     * 实现实位回调监听
     */
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
                    uploadaddress();
                    return;
                }
            }
            CustomApplication.lastPoint = new BmobGeoPoint(longtitude, latitude);
        }
    }

    private void uploadaddress(){
        BmobQuery<NearUser> query = new BmobQuery<>();
        a=new User();
        a.setObjectId(BmobUser.getCurrentUser(this).getObjectId());
        query.addWhereEqualTo("user", a);
        query.findObjects(mContext, new FindListener<NearUser>() {
            @Override
            public void onSuccess(List<NearUser> list) {
                if (list.size() == 0) {
                    NearUser nne = new NearUser();
                    nne.setUser(a);
                    nne.setPlace(CustomApplication.lastPoint);
                    nne.save(mContext, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            p.dismiss();
                            changeFragment(new nearFragment());
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            showToast("网络不稳定，搜索失败");
                            enter_jh.this.finish();
                        }
                    });
                } else {
                    String obid = list.get(0).getObjectId();
                    NearUser nne = new NearUser();
                    nne.setPlace(CustomApplication.lastPoint);
                    nne.update(mContext, obid, new UpdateListener() {

                        @Override
                        public void onSuccess() {
                            p.dismiss();
                            changeFragment(new nearFragment());
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            showToast("搜索失败");
                            enter_jh.this.finish();
                        }
                    });
                }

            }

            @Override
            public void onError(int i, String s) {
                showToast("搜索失败");
                enter_jh.this.finish();
            }
        });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
