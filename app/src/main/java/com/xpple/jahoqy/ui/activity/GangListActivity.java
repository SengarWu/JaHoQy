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
import com.xpple.jahoqy.adapter.GangAdapt;
import com.xpple.jahoqy.bean.Gangs;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by caolin on 2015/10/4.
 */
public class GangListActivity extends BaseActivity {
    private ListView listView;
    private GangAdapt adapter;

    private List<Gangs> gangsList=new LinkedList<Gangs>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gangs_table);
        initView();
        //初始化进度条
        progressDialog=new ProgressDialog(mContext);
        progressDialog.setMessage("正在加载帮派...");
        progressDialog.show();
        //获取及显示数据
        loadAndShowData();
    }

    private void initView() {

        initTopBarForLeft("帮派", R.mipmap.actionbar_gangs, R.color.color_transparent_bg);
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
        //设置item点击事件.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(mContext, "进入详情页面", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(mContext, GangDetailActivity.class);
                myIntent.putExtra("gang", adapter.getItem(position));
                startActivityForResult(myIntent,0);
            }
        });
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
                pageNumber = 0;
                //清空数据
                gangsList.clear();
                searchText = search.getText().toString();
                loadAndShowData();
            }
        });
    }

    private void loadAndShowData(){
        new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
                    List<Gangs> cuurGangs;
                    BmobQuery<Gangs> query=new BmobQuery<Gangs>();
                    query.addWhereContains("gangsName",searchText);
                    query.order("gangsName");
                    query.setSkip(pageNumber * pagesize); // 忽略前10条数据（即第一页数据结果）
                    query.setLimit(pagesize);// 限制最多pagesize条数据结果作为一页
                    query.findObjects(mContext, new FindListener<Gangs>() {
                        @Override
                        public void onSuccess(final List<Gangs> list) {
                            gangsList.addAll(list);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if(list.size() != 0){
                                        footLoadingLayout.setVisibility(View.VISIBLE);
                                    }
                                    if (isFirstLoading) {
                                        isFirstLoading = false;
                                        //设置adapter
                                        adapter = new GangAdapt(GangListActivity.this, R.layout.row_gangs_item, gangsList);
                                        listView.setAdapter(adapter);
                                    } else {
                                        if (list.size() < pagesize) {
                                            hasMoreData = false;
                                            footLoadingLayout.setVisibility(View.VISIBLE);
                                            footLoadingPB.setVisibility(View.GONE);
                                            footLoadingText.setText("No more data");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 16:
                setResult(16);
                finish();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
