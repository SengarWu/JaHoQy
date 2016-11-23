package com.xpple.jahoqy.ui.mainFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.xpple.jahoqy.BaseClass.BaseFragment;
import com.xpple.jahoqy.CustomApplication;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.AdDomain;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.ui.activity.GangsTaskActivity;
import com.xpple.jahoqy.ui.activity.JHKTActivity;
import com.xpple.jahoqy.ui.activity.enter_jh;
import com.xpple.jahoqy.ui.activity.enter_xxzy;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;

public class IndexFragment extends BaseFragment implements View.OnClickListener,View.OnTouchListener {

    private View parentView;
    private Intent intent;
    private ViewPager adViewPager;
    private List<ImageView> imageViews;// 滑动的图片集合

    private List<View> dots; // 图片标题正文的那些点
    private List<View> dots_list;
    private int currentItem = 0; // 当前图片的索引号
    // 异步加载图片
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    // 轮播banner的数据
    private List<AdDomain> adList;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            adViewPager.setCurrentItem(currentItem);
        }
    };

    private ScheduledExecutorService scheduledExecutorService;
    public IndexFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_index, container, false);
        initView();
        // 使用ImageLoader之前初始化
        CustomApplication.initImageLoader(getActivity());
        // 获取图片加载实例
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.top_banner_android)
                .showImageForEmptyUri(R.mipmap.top_banner_android)
                .showImageOnFail(R.mipmap.top_banner_android)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();

        initAdData();
        startAd();
        return parentView;
    }

    private void initView() {
        ImageView iv_gangs_task  = (ImageView) parentView.findViewById(R.id.iv_gangs_task);
        ImageView iv_brjh = (ImageView) parentView.findViewById(R.id.iv_brjh);
        ImageView iv_xxzy = (ImageView) parentView.findViewById(R.id.iv_xxzy);
        ImageView iv_jhkt = (ImageView) parentView.findViewById(R.id.iv_jhkt);
        iv_gangs_task.setOnClickListener(this);
        iv_brjh.setOnClickListener(this);
        iv_xxzy.setOnClickListener(this);
        iv_jhkt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_gangs_task: //帮派任务
                BmobQuery<User> query = new BmobQuery<User>();
                query.getObject(getActivity(), CurrentUser.getObjectId(), new GetListener<User>() {
                    @Override
                    public void onSuccess(User user) {

                        if (user.getGangsName() != null)
                        {
                            startAnimActivity(GangsTaskActivity.class);
                        }
                        else
                        {
                            showToast("请先加入帮派");
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });

                break;
            case R.id.iv_brjh: //步入江湖
                Intent intent=new Intent(getActivity(),enter_jh.class);
                startActivity(intent);
                break;
            case R.id.iv_xxzy:  //行侠仗义
                Intent intent2=new Intent(getActivity(),enter_xxzy.class);
                startActivity(intent2);
                break;
            case R.id.iv_jhkt: //江湖课堂
                startAnimActivity(JHKTActivity.class);
                break;
        }
    }

    public void onPause() {
        super.onPause();
        // 当不可见的时候停止切换
        scheduledExecutorService.shutdown();
    }

    private void initAdData() {
        adList = getBannerAd();// 广告数据
        imageViews = new ArrayList<>();
        dots = new ArrayList<>();// 点
        dots_list = new ArrayList<>();
        View dot0 = parentView.findViewById(R.id.v_dot0);
        View dot1 = parentView.findViewById(R.id.v_dot1);
        View dot2 = parentView.findViewById(R.id.v_dot2);
        dots.add(dot0);
        dots.add(dot1);
        dots.add(dot2);
        adViewPager = (ViewPager) parentView.findViewById(R.id.vp);
        adViewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        adViewPager.setOnPageChangeListener(new MyPageChangeListener());
        addDynamicView();
    }

    private void addDynamicView() {
        // 动态添加图片和下面指示的圆点
        // 初始化图片资源
        for (int i = 0; i < adList.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            // 异步加载图片
            mImageLoader.displayImage(adList.get(i).getImgUrl(), imageView,
                    options);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
            dots.get(i).setVisibility(View.VISIBLE);
            dots_list.add(dots.get(i));
        }
    }

    private void startAd() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每10秒切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 10,
                TimeUnit.SECONDS);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int Action = event.getAction();
        if (Action == MotionEvent.ACTION_DOWN) {
            playHeartbeatAnimation(v);
        }
        return false;
    }

    private class ScrollTask implements Runnable {

        @Override
        public void run() {
            synchronized (adViewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        private int oldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            AdDomain adDomain = adList.get(position);
//	            tv_title.setText(adDomain.getTitle()); // 设置标题
//	            tv_details.setText(adDomain.getDetails()); // 设置细节
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return adList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView iv = imageViews.get(position);
            container.addView(iv);
            // 在这个方法里面设置图片的点击事件
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 处理跳转逻辑
//	                    Intent intent = new Intent();
//	                    intent.setClass(getActivity(), WebActivity.class);
//	                    intent.putExtra("url", adList.get(position).getTitle());
//	                    startActivity(intent);

                }
            });
            return iv;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    /**
     * 轮播广播模拟数据
     */
    public static List<AdDomain> getBannerAd() {
        List<AdDomain> adList = new ArrayList<>();

        AdDomain adDomain = new AdDomain();
        adDomain.setTitle("http://www.baidu.com");
        adDomain.setDetails("One");
        adDomain.setImgUrl("app/res/mipmap/top_banner_android.png");
        adList.add(adDomain);
        AdDomain adDomain1 = new AdDomain();
        adDomain.setTitle("");

        AdDomain adDomain2 = new AdDomain();
        adDomain2.setTitle("http://www.baidu.com");
        adDomain2.setDetails("Two");
        adDomain2.setImgUrl("app/res/mipmap/top_banner_android2.jpeg");
        adList.add(adDomain2);

        AdDomain adDomain3 = new AdDomain();
        adDomain3.setTitle("http://www.sina.com");
        adDomain3.setDetails("Three");
        adDomain3.setImgUrl("app/res/mipmap/top_banner_android3.png");
        adList.add(adDomain3);

        return adList;
    }

    private void changeFragment(Fragment targetFragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public Bitmap getPicture(String path){
        Bitmap bm=null;
        try{
            URL url=new URL(path);
            URLConnection connection=url.openConnection();
            connection.connect();
            InputStream inputStream=connection.getInputStream();
            bm= BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  bm;
    }
}


