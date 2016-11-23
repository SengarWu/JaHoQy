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
import com.xpple.jahoqy.adapter.GangAppointmentAdapter;
import com.xpple.jahoqy.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by caolin on 2015/10/6.
 */
public class GangsAppointmentActivity extends BaseActivity {
    private static final int SELECTED_POSITION=1;
    private ListView listView;
    private GangAppointmentAdapter adapter;

    private List<User> userList=new LinkedList<User>();
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
        BmobQuery<User> query=new BmobQuery<User>();
        query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
        query.findObjects(mContext, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if(list!=null&&list.size()>0){
                    int num=list.get(0).getGangsPosition();
                    int num1=num/10;
                    if(num1==0){start=0;end=100;}
                    else if(num1==1){start=10;end=100;}
                    else{
                        int num2=num%10;
                        start=num;
                        if(num2==0){
                            end=num+10;
                        }
                        else{
                            end=num+3;
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    private void initView() {

        initTopBarForLeft("帮派任命", R.mipmap.actionbar_gangs, R.color.color_transparent_bg);
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
                //Toast.makeText(mContext, "进入页面", Toast.LENGTH_SHORT).show();
                User myuser=(User)listView.getAdapter().getItem(position);
                String updateuserId=myuser.getObjectId();
                int positionGrade=myuser.getGangsPosition();
                Intent myIntent=new Intent(mContext,GangsSelectPositionActivity.class);
                myIntent.putExtra("positionGrade", positionGrade);
                myIntent.putExtra("updateuserId",updateuserId);
                startActivityForResult(myIntent, SELECTED_POSITION);

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
                pageNumber=0;
                //清空数据
                userList.clear();
                searchText=search.getText().toString();
                loadAndShowData();
            }
        });
    }
  @Override
  protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
      switch (requestCode){
          case SELECTED_POSITION:
              if(data!=null){
                  BmobQuery<User> query=new BmobQuery<User>();
                  query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
                  query.findObjects(mContext, new FindListener<User>() {
                      @Override
                      public void onSuccess(List<User> list) {
                          if(list!=null&&list.size()>0){
                              int num=list.get(0).getGangsPosition();
                              int num1=num/10;
                              if(num1==0){start=0;end=100;}
                              else if(num1==1){start=10;end=100;}
                              else{
                                  int num2=num%10;
                                  start=num;
                                  if(num2==0){
                                      end=num+10;
                                  }
                                  else{
                                      end=num+3;
                                  }
                              }
                          }
                          final int positionGrade=data.getIntExtra("positionGrade", -1);
                          if(positionGrade!=-1){
                              final String updateuserId=data.getStringExtra("updateuserId");
                              JSONObject myJesn=new JSONObject();
                              try{
                                  myJesn.put("objectId", updateuserId);
                                  myJesn.put("gangsPosition", positionGrade);
                                  myJesn.put("gangsName", list.get(0).getGangsName());

                                  AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
                                  //第一个参数是上下文对象，第二个参数是云端代码的方法名称，第三个参数是上传到云端代码的参数列表（JSONObject cloudCodeParams），第四个参数是回调类
                                  ace.callEndpoint(mContext, "updateGangs", myJesn,
                                          new CloudCodeListener() {
                                              @Override
                                              public void onSuccess(Object object) {
                                                  String str = object.toString();
                                                  if (str.equals("yes")) {
                                                      showToast("更新成功");
                                                      //初始化初页
                                                      pageNumber = 0;
                                                      //清空数据
                                                      userList.clear();
                                                      progressDialog = new ProgressDialog(mContext);
                                                      progressDialog.setMessage("更新成功,正在加载数据...");
                                                      progressDialog.show();
                                                      loadAndShowData();
                                                  } else {
                                                      showToast(object.toString());
                                                  }
                                              }

                                              @Override
                                              public void onFailure(int code, String msg) {
                                                  // TODO Auto-generated method stub
                                                  //检查网络设置
                                              }
                                          });
                              }catch (JSONException e){
                                  showToast("josn化失败");
                              }
                          }
                      }

                      @Override
                      public void onError(int i, String s) {

                      }
                  });

              }
              break;
          default:
              break;
      }
      super.onActivityResult(requestCode, resultCode, data);
  }
    private void loadAndShowData(){

        new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
                    BmobQuery<User> query=new BmobQuery<User>();
                    query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
                    query.findObjects(mContext, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if(list!=null&&list.size()>0){
                                int num=list.get(0).getGangsPosition();
                                int num1=num/10;
                                if(num1==0){start=0;end=100;}
                                else if(num1==1){start=10;end=100;}
                                else{
                                    int num2=num%10;
                                    start=num;
                                    if(num2==0){
                                        end=num+10;
                                    }
                                    else{
                                        end=num+3;
                                    }
                                }
                                BmobQuery<User> query=new BmobQuery<User>();
                                query.addWhereContains("username", searchText);
                                query.addWhereEqualTo("gangsName", list.get(0).getGangsName());
                                query.addWhereGreaterThan("gangsPosition", start);
                                query.addWhereLessThan("gangsPosition", end);
                                query.setSkip(pageNumber * pagesize); // 忽略前10条数据（即第一页数据结果）
                                query.setLimit(pagesize);// 限制最多pagesize条数据结果作为一页
                                query.order("gangsPosition");
                                query.findObjects(mContext, new FindListener<User>() {
                                    @Override
                                    public void onSuccess(final List<User> list) {
                                        userList.addAll(list);
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                if(list.size() != 0){
                                                    footLoadingLayout.setVisibility(View.VISIBLE);
                                                }
//                                                if (isFirstLoading) {
//                                                    isFirstLoading = false;
                                                    //设置adapter
                                                    adapter = new GangAppointmentAdapter(GangsAppointmentActivity.this,
                                                            R.layout.row_gangs_appointment,userList);
                                                    listView.setAdapter(adapter);
//                                                } else {
                                                    if (list.size() < pagesize) {
                                                        hasMoreData = false;
                                                        footLoadingLayout.setVisibility(View.VISIBLE);
                                                        footLoadingPB.setVisibility(View.GONE);
                                                        footLoadingText.setText("成员加载完毕");
                                                    }
                                                    adapter.notifyDataSetChanged();
//                                                }
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
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

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