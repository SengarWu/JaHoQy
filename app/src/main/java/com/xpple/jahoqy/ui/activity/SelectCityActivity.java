package com.xpple.jahoqy.ui.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.bean.City;
import com.xpple.jahoqy.view.HeaderLayout;
import com.xpple.jahoqy.view.MyLetterListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SelectCityActivity extends BaseActivity implements View.OnClickListener {
    private ListAdapter adapter;
    private ListView personList;
    private TextView overlay; // 对话框首字母textview
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private Handler handler;
    private OverlayThread overlayThread; // 显示首字母对话框
    private ArrayList<City> allCity_lists; // 所有城市列表
    private ArrayList<City> ShowCity_lists; // 需要显示的城市列表-随搜索而改变
    private ArrayList<City> city_lists;// 城市列表
    private String lngCityName = "";//存放返回的城市名
    private LocationClient locationClient = null;
    private EditText sh;
    private TextView lng_city;
    private ProgressDialog progress;
    private static final int SHOWDIALOG = 2;
    private static final int DISMISSDIALOG = 3;
    private Button al_city;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        initTopBarForLeft("选择城市", R.mipmap.actionbar_publish, R.color.black_deep);
        al_city=(Button)findViewById(R.id.allcity);
        al_city.setOnClickListener(this);
        personList = (ListView) findViewById(R.id.list_view);
        allCity_lists = new ArrayList<>();
        MyLetterListView letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
        LinearLayout lng_city_lay = (LinearLayout) findViewById(R.id.lng_city_lay);
        sh = (EditText) findViewById(R.id.sh);
        lng_city = (TextView) findViewById(R.id.lng_city);

        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        alphaIndexer = new HashMap<>();
        handler = new Handler();
        overlayThread = new OverlayThread();
        personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent();
                intent.putExtra("lngCityName", ShowCity_lists.get(arg2).name);
                setResult(99, intent);
                finish();
            }
        });
        lng_city_lay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("lngCityName", lngCityName);
                setResult(99, intent);
                finish();
            }
        });

        initGps();
        initOverlay();
        handler2.sendEmptyMessage(SHOWDIALOG);
        Thread thread = new Thread() {
            @Override
            public void run() {
                hotCityInit();
                handler2.sendEmptyMessage(DISMISSDIALOG);
                super.run();
            }
        };
        thread.start();


        HeaderLayout actionbar = (HeaderLayout) findViewById(R.id.common_actionbar);
/*        actionbar.setTitle("选择城市");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAction(new HeaderLayout.Action() {

            @Override
            public void performAction(View view) {
                finish();
            }

            @Override
            public int getDrawable() {
                return R.mipmap.ic_launcher;
            }
        });*/

    }

    /**
     * 热门城市
     */
    public void hotCityInit() {
        City city;
        city = new City("上海", "");
        allCity_lists.add(city);
        city = new City("北京", "");
        allCity_lists.add(city);
        city = new City("广州", "");
        allCity_lists.add(city);
        city = new City("深圳", "");
        allCity_lists.add(city);
        city = new City("武汉", "");
        allCity_lists.add(city);
        city = new City("天津", "");
        allCity_lists.add(city);
        city = new City("西安", "");
        allCity_lists.add(city);
        city = new City("南京", "");
        allCity_lists.add(city);
        city = new City("杭州", "");
        allCity_lists.add(city);
        city = new City("成都", "");
        allCity_lists.add(city);
        city = new City("重庆", "");
        allCity_lists.add(city);
        city_lists = getCityList();
        allCity_lists.addAll(city_lists);
        ShowCity_lists = allCity_lists;
    }

    private ArrayList<City> getCityList() {
        ArrayList<City> list = new ArrayList<>();
        try {
            JSONArray chineseCities = new JSONArray(getApplication().getString(R.string.citys));
            for (int i = 0; i < chineseCities.length(); i++) {
                JSONObject jsonObject = chineseCities.getJSONObject(i);
                City city = new City(jsonObject.getString("name"), jsonObject.getString("pinyin"));
                list.add(city);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * a-z排序
     */
    Comparator<City> comparator = new Comparator<City>() {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyi().substring(0, 1);
            String b = rhs.getPinyi().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.allcity:
                Intent intent = new Intent();
                intent.putExtra("lngCityName", "所有城市");
                setResult(99, intent);
                finish();
                break;
        }


    }

    public class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        final int VIEW_TYPE = 3;

        public ListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
            alphaIndexer = new HashMap<>();
            sections = new String[ShowCity_lists.size()];
            for (int i = 0; i < ShowCity_lists.size(); i++) {
                // 当前汉语拼音首字母
                String currentStr = getAlpha(ShowCity_lists.get(i).getPinyi());
                // 上一个汉语拼音首字母，如果不存在为“ ”
                String previewStr = (i - 1) >= 0 ? getAlpha(ShowCity_lists.get(i - 1)
                        .getPinyi()) : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = getAlpha(ShowCity_lists.get(i).getPinyi());
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
        }

        @Override
        public int getCount() {
            return ShowCity_lists.size();
        }

        @Override
        public Object getItem(int position) {
            return ShowCity_lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            int type = 2;

            if (position == 0 && sh.getText().length() == 0) {//不是在搜索状态下
                type = 0;
            }
            return type;
        }

        @Override
        public int getViewTypeCount() {// 这里需要返回需要集中布局类型，总大小为类型的种数的下标
            return VIEW_TYPE;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.city_list_item, null);
                holder = new ViewHolder();
                holder.alpha = (TextView) convertView
                        .findViewById(R.id.alpha);
                holder.name = (TextView) convertView
                        .findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//				if (sh.getText().length()==0) {//搜所状态
//					holder.name.setText(list.get(position).getName());
//					holder.alpha.setVisibility(View.GONE);
//				}else if(position>0){
            //显示拼音和热门城市，一次检查本次拼音和上一个字的拼音，如果一样则不显示，如果不一样则显示

            holder.name.setText(ShowCity_lists.get(position).getName());
            String currentStr = getAlpha(ShowCity_lists.get(position).getPinyi());//本次拼音
            String previewStr = (position - 1) >= 0 ? getAlpha(ShowCity_lists.get(position - 1).getPinyi()) : " ";//上一个拼音
            if (!previewStr.equals(currentStr)) {//不一样则显示
                holder.alpha.setVisibility(View.VISIBLE);
                if (currentStr.equals("#")) {
                    currentStr = "热门城市";
                }
                holder.alpha.setText(currentStr);
            } else {
                holder.alpha.setVisibility(View.GONE);
            }
//				}
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }
    }

    // 初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.city_overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    private class LetterListViewListener implements
            MyLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                personList.setSelection(position);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                handler.postDelayed(overlayThread, 1500);
            }
        }

    }

    // 设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }

    }

    // 获得汉语拼音首字母
    private String getAlpha(String str) {

        if (str.equals("-")) {
            return "&";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }

    private void initGps() {
        try {
            MyLocationListenner myListener = new MyLocationListenner();
            locationClient = new LocationClient(SelectCityActivity.this);
            locationClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);
            option.setAddrType("all");
            option.setCoorType("bd09ll");
            option.setScanSpan(5000);
            locationClient.setLocOption(option);
            locationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        locationClient.stop();
    }


    private class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null)
                return;
            StringBuilder sb = new StringBuilder(256);
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
//				sb.append(location.getAddrStr());
                sb.append(location.getCity());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append(location.getCity());
            }
            if (sb.toString().length() > 0) {
                lngCityName = sb.toString().substring(0, sb.toString().length() -1);
                lng_city.setText(lngCityName);
            }

        }

    }

    private ProgressDialog showProgress(Activity activity, String hintText) {
        Activity mActivity;
        if (activity.getParent() != null) {
            mActivity = activity.getParent();
            if (mActivity.getParent() != null) {
                mActivity = mActivity.getParent();
            }
        } else {
            mActivity = activity;
        }
        final Activity finalActivity = mActivity;
        ProgressDialog window = ProgressDialog.show(finalActivity, "", hintText);
        window.getWindow().setGravity(Gravity.CENTER);

        window.setCancelable(false);
        return window;
    }

    Handler handler2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOWDIALOG:
                    progress = showProgress(SelectCityActivity.this, "正在加载数据，请稍候...");
                    break;
                case DISMISSDIALOG:
                    if (progress != null) {
                        progress.dismiss();
                    }
                    adapter = new ListAdapter(SelectCityActivity.this);
                    personList.setAdapter(adapter);

                    sh.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            //搜索符合用户输入的城市名
                            if (s.length() > 0) {
                                ArrayList<City> change_city = new ArrayList<>();
                                for (int i = 0; i < city_lists.size(); i++) {
                                    if (city_lists.get(i).name.contains(sh.getText().toString())) {
                                        change_city.add(city_lists.get(i));
                                    }
                                }
                                ShowCity_lists = change_city;
                            } else {
                                ShowCity_lists = allCity_lists;
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };
}
