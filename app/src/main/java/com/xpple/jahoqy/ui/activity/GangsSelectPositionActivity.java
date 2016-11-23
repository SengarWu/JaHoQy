package com.xpple.jahoqy.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.GangSelectPositionAdapter;
import com.xpple.jahoqy.bean.GangsPost;
import com.xpple.jahoqy.bean.User;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by caolin on 2015/10/7.
 */
public class GangsSelectPositionActivity extends BaseActivity {
    private ListView listView;
    private GangSelectPositionAdapter adapter;

    private List<GangsPost> postList=new LinkedList<>();
    private boolean isLoading;
    private boolean isFirstLoading = true;
    private boolean hasMoreData = true;
    private int pageNumber=0;
    private final int pagesize = 10;


    private EditText search;
    private ProgressDialog progressDialog;
    private String searchText="";

    private LinearLayout footLoadingLayout;
    private ProgressBar footLoadingPB;
    private TextView footLoadingText;

    private int start;
    private int end;
    private int grade;

    int positionGrade;
    String updateuserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gangs_table);
        initView();
        initData();
        //初始化进度条
        progressDialog=new ProgressDialog(mContext);
        progressDialog.setMessage("正在加载帮派...");
        progressDialog.show();

        //获取及显示数据
        loadAndShowData();
    }

    private void initData() {
        //获取修改权利的人
        Intent myIntent=getIntent();
        positionGrade =myIntent.getIntExtra("positionGrade", -1);
        updateuserId=myIntent.getStringExtra("updateuserId");

        //初始化搜索范围
        int num=CurrentUser.getGangsPosition();
        int num1=num/10;
        start=num;
        if(num1<2){end=100;}
        else{
            int num2=num%10;
            if(num2==0){
                end=num+10;
            }
            else{
                end=num+3;
            }
        }

        //初始化列表点击事件
        //设置item点击事件.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(mContext, "进入页面", Toast.LENGTH_SHORT).show();
                final int number=((GangsPost)listView.getAdapter().getItem(position)).getPositionGrade();
                String gangsName=CurrentUser.getGangsName();
                int num2=number%10;
                if(num2==0||(num2-1)%3==0){
                    BmobQuery<User> query=new BmobQuery<>();
                    query.addWhereEqualTo("gangsPosition", number);
                    query.addWhereEqualTo("gangsName",gangsName);
                    query.findObjects(mContext, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (list != null && list.size() > 0) {
                                showToast("此职位有人,任命此职位许跟换此职位的人！！！");
                            } else {
                                final Intent myIntent=new Intent();
                                myIntent.putExtra("positionGrade", number);
                                myIntent.putExtra("updateuserId",updateuserId);
                                setResult(0,myIntent);
                                finish();
                            }
                        }
                        @Override
                        public void onError(int i, String s) {
                            showToast("网络出错,请检查网络设置！！！");
                        }
                    });
                }else{
                    final Intent myIntent=new Intent();
                    myIntent.putExtra("positionGrade", number);
                    myIntent.putExtra("updateuserId", updateuserId);
                    setResult(0, myIntent);
                    finish();
                }
            }
        });
    }
    private void initView() {

        initTopBarForLeft("帮派职位选择", R.mipmap.actionbar_gangs, R.color.color_transparent_bg);
        listView = (ListView) findViewById(R.id.gang_list);
        View mview = (View) findViewById(R.id.search_gang);
        //搜索
        search=(EditText)mview.findViewById(R.id.query);
        //列表尾部  加载跟多
        View footView = getLayoutInflater().inflate(R.layout.listview_footer_view, null);
        footLoadingLayout = (LinearLayout) footView.findViewById(R.id.loading_layout);
        footLoadingPB = (ProgressBar)footView.findViewById(R.id.loading_bar);
        footLoadingText = (TextView) footView.findViewById(R.id.loading_text);
        listView.addFooterView(footView, null, false);
        footLoadingLayout.setVisibility(View.GONE);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (listView.getCount() != 0) {
                        int lasPos = view.getLastVisiblePosition();
                        if (hasMoreData && !isLoading && lasPos == listView.getCount() - 1) {
                            loadAndShowData();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //初始化初页
                pageNumber=0;
                //清空数据
                postList.clear();
                searchText=search.getText().toString();
                loadAndShowData();
            }
        });
    }

    private void loadAndShowData(){

        new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
                    List<GangsPost> users;

                    BmobQuery<GangsPost> query=new BmobQuery<GangsPost>();
                    query.addWhereNotEqualTo("positionGrade",positionGrade);
                    query.addWhereContains("positionName", searchText);
                    query.addWhereGreaterThan("positionGrade", start);
                    query.addWhereLessThan("positionGrade", end);
                    query.setSkip(pageNumber * pagesize); // 忽略前10条数据（即第一页数据结果）
                    query.setLimit(pagesize);// 限制最多pagesize条数据结果作为一页
                    query.order("positionGrade");
                    query.findObjects(mContext, new FindListener<GangsPost>() {
                        @Override
                        public void onSuccess(final List<GangsPost> list) {
                            postList.addAll(list);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if(list.size() != 0){
                                        footLoadingLayout.setVisibility(View.VISIBLE);
                                    }
                                    if (isFirstLoading) {
                                        isFirstLoading = false;
                                        //设置adapter
                                        adapter = new GangSelectPositionAdapter(GangsSelectPositionActivity.this, R.layout.row_gangs_select_position,postList);
                                        listView.setAdapter(adapter);
                                    } else {
                                        if (list.size() < pagesize) {
                                            hasMoreData = false;
                                            footLoadingLayout.setVisibility(View.VISIBLE);
                                            footLoadingPB.setVisibility(View.GONE);
                                            footLoadingText.setText("成员加载完毕");
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                    pageNumber++;
                                }
                            });
                            if (progressDialog!=null){
                                progressDialog.dismiss();
                            }
                        }
                        @Override
                        public void onError(int i, String s) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog!=null){
                                        progressDialog.dismiss();
                                    }
                                    showToast("加载数据失败，请检查网络或稍后重试");
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            footLoadingLayout.setVisibility(View.GONE);
                            if (progressDialog!=null){
                                progressDialog.dismiss();
                            }
                            showToast("加载数据失败，请检查网络或稍后重试");
                        }
                    });
                }finally {
                    isLoading = false;
                }
            }
        }).start();
    }
}
