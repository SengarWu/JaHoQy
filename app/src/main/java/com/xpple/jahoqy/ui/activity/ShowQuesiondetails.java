package com.xpple.jahoqy.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.Answeradapter;
import com.xpple.jahoqy.bean.Answer;
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.util.CommonUtils;
import com.xpple.jahoqy.view.RoundImageView;
import com.xpple.jahoqy.view.xlistview.SimpleFooter;
import com.xpple.jahoqy.view.xlistview.SimpleHeader;
import com.xpple.jahoqy.view.xlistview.ZrcListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ShowQuesiondetails extends BaseActivity implements View.OnClickListener {
    private String url,uname;
    private String obid;
    private TextView una,tit;
    private ImageView ima;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private int curpage=0;
    private ZrcListView listView;
    private Answeradapter adapter;
    private ArrayList<Answer> items = new ArrayList<>();
    private Handler handler;//用于接收子线程的信息以刷新主线程
    private Button rep;
    private EditText ed;
    private RelativeLayout ll;
    private ImageView pic1,pic2,pic3;
    private String url1,url2,url3;
    private TextView con;
    //private  MyPopupWindow myPopwindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_quesiondetails);
        init();
    }

    private void init() {
        initTopBarForLeft("详情", R.mipmap.actionbar_xxzy, R.color.black_deep);
        con=(TextView)findViewById(R.id.content);
        pic1=(ImageView)findViewById(R.id.p1);
        pic2=(ImageView)findViewById(R.id.p2);
        pic3=(ImageView)findViewById(R.id.p3);

        ll=(RelativeLayout)findViewById(R.id.answerview);
        ed=(EditText)findViewById(R.id.comment);
        rep=(Button)findViewById(R.id.reply);
        rep.setOnClickListener(this);

        listView = (ZrcListView)findViewById(R.id.answerlist);
        una=(TextView)findViewById(R.id.username);
        ima=(RoundImageView)findViewById(R.id.image);
        tit=(TextView)findViewById(R.id.title);
        Intent intent =getIntent();
        con.setText(intent.getStringExtra("content"));
        obid=intent.getStringExtra("item");
        if(intent.hasExtra("url")) {
            url = intent.getStringExtra("url");
            imageLoader.displayImage("http://file.bmob.cn/" + url, ima, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
        uname=intent.getStringExtra("username");
        una.setText(uname);


        BmobQuery<Question> query=new BmobQuery<>();
        query.addQueryKeys("title,picture1,picture2,picture3");
        query.addWhereEqualTo("objectId", obid);
        query.findObjects(this, new FindListener<Question>() {//查询多条数据

            @Override
            public void onSuccess(List<Question> object) {
                Question x = object.get(0);
                tit.setText(x.getTitle());
                if(x.getPicture1()!=null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture1().getUrl(),pic1);
                    url1=x.getPicture1().getUrl();
                    pic1.setOnClickListener(ShowQuesiondetails.this);
                }
                if(x.getPicture2()!=null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture1().getUrl(),pic2);
                    url2=x.getPicture1().getUrl();
                    pic2.setOnClickListener(ShowQuesiondetails.this);
                }
                if(x.getPicture3()!=null) {
                    imageLoader.displayImage("http://file.bmob.cn/" + x.getPicture1().getUrl(),pic3);
                    url3=x.getPicture1().getUrl();
                    pic3.setOnClickListener(ShowQuesiondetails.this);
                }
            }

            @Override
            public void onError(int i, String s) {
                showToast("出现异常");
            }
        });

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
        //listView.setItemAnimForTopIn(R.anim.top_item_in);
        listView.setItemAnimForBottomIn(R.anim.bottom_item_in);

        adapter = new Answeradapter(this, items);
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
                if(!items.get(position).getUser().getObjectId().equals(BmobUser.getCurrentUser(mContext).getObjectId())) {
                    Intent item = new Intent(ShowQuesiondetails.this, Shownearuser.class);
                    item.putExtra("objectId", items.get(position).getUser().getObjectId());
                    startActivity(item);
                }
            }
        });
    }

    private void refresh() {
        curpage=0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                queryAnswer();//queryProxy为自定义的查询类
            }
        });
    }
    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                queryCountAnswer();
            }
        });
    }

    private void queryAnswer() {
          BmobQuery<Answer> query=new BmobQuery<>();
          query.include("Question,user");
          query.order("-updatedAt");
          query.setLimit(10);
          BmobQuery<Question> questionquery=new BmobQuery<>();
          questionquery.addWhereEqualTo("objectId", obid);
          query.addWhereMatchesQuery("Question","Question",questionquery);
          query.findObjects(this, new FindListener<Answer>() {
              @Override
              public void onSuccess(List<Answer> list) {
                  if (list.size() != 0) {
                      if (CommonUtils.isNotNull(list)) {//监测网络等是否可用
                          items.clear();
                          adapter.addAll(list);
                          if (list.size() < 10) {
                              listView.setRefreshSuccess("加载完成"); // 通知加载完成
                              listView.stopLoadMore();
                          } else {
                              //listView.setSelection(0);
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
              public void onError(int i, String s) {

              }
          });
    }

    private void querymoreAnswer(int page){
        BmobQuery<Answer> query=new BmobQuery<>();
        query.include("Question,user");
        query.order("-updatedAt");
        query.setLimit(10);
        query.setSkip(page*10);
        BmobQuery<Question> questionquery=new BmobQuery<>();
        questionquery.addWhereEqualTo("objectId", obid);
        query.addWhereMatchesQuery("Question","Question",questionquery);
        query.findObjects(this, new FindListener<Answer>() {
            @Override
            public void onSuccess(List<Answer> object) {
                if (CommonUtils.isNotNull(object)) {
                    adapter.addAll(object);
                }
                adapter.notifyDataSetChanged();
                listView.setLoadMoreSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(ShowQuesiondetails.this, msg, Toast.LENGTH_LONG).show();
                listView.stopLoadMore();
            }
        });
    }

    private void queryCountAnswer(){
        BmobQuery<Answer> query=new BmobQuery<>();
        query.include("Question,user");
        BmobQuery<Question> questionquery=new BmobQuery<>();
        questionquery.addWhereEqualTo("objectId", obid);
        query.addWhereMatchesQuery("Question","Question",questionquery);
        query.count(this, Answer.class, new CountListener() {
            @Override
            public void onSuccess(int count) {
                if (count > items.size()) {
                    curpage++;
                    querymoreAnswer(curpage);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reply:
                String c=ed.getText().toString();
                if(!c.equals("")) {
                    Answer n = new Answer();
                    User u = new User();
                    u.setObjectId(BmobUser.getCurrentUser(mContext).getObjectId());
                    n.setUser(u);
                    Question q = new Question();
                    q.setObjectId(obid);
                    n.setQuestion(q);
                    n.setContent(c);
                    n.setResult("0");
                    n.save(mContext, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            listView.setSelection(0);
                            listView.refresh();
                            ed.setText("");
                            ll.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            //为用户信息添加对问题信息的关联
                            User u = new User();
                            Question q = new Question();
                            q.setObjectId(obid);
                            BmobRelation relation = new BmobRelation();
                            relation.add(q);
                            u.setAnswerQuestion(relation);
                            u.setUserIntegral(CurrentUser.getUserIntegral());
                            u.setGangsPosition(CurrentUser.getGangsPosition());
                            u.setExperience(CurrentUser.getExperience());
                            u.update(mContext, CurrentUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    showToast("发布成功");
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    showToast("发布失败" + s);
                                }
                            });
/*                        User uu=CurrentUser;
                        //uu.setObjectId(CurrentUser.getObjectId());
                        Question q=new Question();
                        q.setObjectId(obid);
                        BmobRelation relation=new BmobRelation();
                        relation.add(q);
                        uu.setAnswerQuestion(relation);
*//*                        uu.setUserIntegral(CurrentUser.getUserIntegral());
                        uu.setGangsPosition(CurrentUser.getGangsPosition());
                        uu.setExperience(CurrentUser.getExperience());*//*
                        uu.update(mContext,CurrentUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                showToast("发布成功");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                    showToast("发布失败");
                            }
                        });*/
                        }

                        @Override
                        public void onFailure(int code, String arg0) {
                            showToast("发布失败");
                        }
                    });
                }else{
                    showToast("请输入内容");
                }
                break;
            case R.id.p1:
                Intent intent1=new Intent(mContext,ShowImage.class);
                intent1.putExtra("url",url1);
                startActivity(intent1);
                break;
            case R.id.p2:
                Intent intent2=new Intent(mContext,ShowImage.class);
                intent2.putExtra("url",url2);
                startActivity(intent2);
                break;
            case R.id.p3:
                Intent intent3=new Intent(mContext,ShowImage.class);
                intent3.putExtra("url",url3);
                startActivity(intent3);
                break;
        }
    }
}
