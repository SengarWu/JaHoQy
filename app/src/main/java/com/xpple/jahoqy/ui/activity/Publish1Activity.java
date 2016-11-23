package com.xpple.jahoqy.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.adapter.wheelPopWindow;
import com.xpple.jahoqy.addimages.PublishProjectImagesAdater;
import com.xpple.jahoqy.addimages.ShowImageActivity;
import com.xpple.jahoqy.addimages.scanImageActivity;
import com.xpple.jahoqy.bean.Question;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.view.DeletableEditText;
import com.xpple.jahoqy.view.HeaderLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class Publish1Activity extends BaseActivity implements View.OnClickListener {

    private DeletableEditText et_title;
    private Button btn_type;
    private ImageView iv_type;
    private EditText et_Award;
    private ImageView iv_city;
    private TextView tv_city;
    private EditText et_details;

    private int picturesNum = 0; //用户上传照片计数
    private int award = 0;

    private ProgressDialog progressDialog;

    private wheelPopWindow mywheel; //类型选择滚动

    GridView myGridView;
    private PopupWindow popWindow;
    private LayoutInflater layoutInflater;
    private ArrayList<String> imageArray=new ArrayList<String>(); //照片存储位置
    private TextView photograph,albums;
    private LinearLayout cancel;
    private PublishProjectImagesAdater imagesAdapt;
    private static int MAX_NUMBER=3;
    /**拍照的照片存储位置 */
    /**拍照的照片存储位置*/
    private String photoSavePath;//保存路径
    private String photoSaveName;//图片名

    public final static int SELECT_PHOTO=10;
    public final static int photo_graph=2;
    public final static int SCAN_IMAGE=12;
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish1);
        initView();
        initTopBarForBoth("发布问题", R.mipmap.ic_publish, R.mipmap.actionbar_publish, Color.BLACK,
                new HeaderLayout.onRightImageButtonClickListener() {
            @Override
            public void onClick() { //问题发布操作


                if (TextUtils.isEmpty(et_title.getText())) {
                    showToast("请输入标题");
                    return;
                }
                if (TextUtils.isEmpty(btn_type.getText()))
                {
                    showToast("请选择类型");
                    return;
                }
                if (TextUtils.isEmpty(tv_city.getText())) {
                    showToast("请选择地区");
                    return;
                }
                if (TextUtils.isEmpty(et_details.getText())) {
                    showToast("请输入描述信息");
                    return;
                }
                if (!TextUtils.isEmpty(et_Award.getText()))
                {
                    award = Integer.parseInt(et_Award.getText().toString());
                    BmobQuery<User> query=new BmobQuery<User>();
                    query.addWhereEqualTo("objectId",CurrentUser.getObjectId());
                    query.findObjects(mContext, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if(list!=null&list.size()>0){
                                if(award > list.get(0).getUserIntegral())
                                {
                                    showToast("您当前的积分不足");
                                    return;
                                }else{
                                    progressDialog = new ProgressDialog(Publish1Activity.this);
                                    progressDialog.setMessage("正在上传发布信息...");
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();

                                    question = new Question();

                                    if (imageArray.size() > 1) //有图片上传
                                    {
                                        picturesNum = imageArray.size() - 1; //上传的图片个数
                                    }
                                    else  //无图片上传
                                    {
                                        NoPictureSave();
                                        return;
                                    }

                                    //图片本地路径转化为BmobFile格式上传
                                    final String[] filePaths = new String[picturesNum];
                                    for (int i = 0; i < picturesNum; i++) {
                                        filePaths[i] = imageArray.get(i);
                                    }
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Bmob.uploadBatch(mContext, filePaths, new UploadBatchListener() {
                                                @Override
                                                public void onSuccess(List<BmobFile> files, List<String> urls) {
                                                    if (urls.size() == picturesNum)
                                                    {
                                                        switch (urls.size())
                                                        {
                                                            case 0:
                                                                break;
                                                            case 1:
                                                                question.setPicture1(files.get(0));
                                                                break;
                                                            case 2:
                                                                question.setPicture1(files.get(0));
                                                                question.setPicture2(files.get(1));
                                                                break;
                                                            case 3:
                                                                question.setPicture1(files.get(0));
                                                                question.setPicture2(files.get(1));
                                                                question.setPicture3(files.get(2));
                                                                break;
                                                        }
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                NoPictureSave();
                                                            }
                                                        });
                                                    }
                                                    else {
                                                        //有可能上传不完整，中间可能会存在未上传成功的情况
                                                    }
                                                }

                                                @Override
                                                public void onProgress(int i, int i1, int i2, int i3) {
                                                }

                                                @Override
                                                public void onError(int i, String s) {
                                                    progressDialog.dismiss();
                                                    showToast("图片上传失败，请重新发布");
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });

                }



            }
            //发布
        });


        //图片上传功能
        //缓冲图片位置
        File mfile = new File(Environment.getExternalStorageDirectory(), "JaHoQy/Camera");
        if (!mfile.exists())
            mfile.mkdirs();
        photoSavePath=Environment.getExternalStorageDirectory()+"/JaHoQy/Camera/";
        photoSaveName =System.currentTimeMillis()+ ".png";
        myGridView=(GridView)findViewById(R.id.gridview_child);
        imageArray.add("");
        imagesAdapt=new PublishProjectImagesAdater(this, imageArray, myGridView);
        myGridView.setAdapter(imagesAdapt);
        imagesAdapt.notifyDataSetChanged();
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 == imageArray.size() - 1) {
                    if (arg2 > MAX_NUMBER - 1) {
                        Toast.makeText(Publish1Activity.this, "只能上传" + MAX_NUMBER + "张图片！！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showPopupWindow(myGridView);
                } else {
                    String path = imageArray.get(arg2);
                    Intent myIntent = new Intent(Publish1Activity.this, scanImageActivity.class);
                    myIntent.putExtra("path", path);
                    myIntent.putExtra("number", arg2 + "");
                    startActivityForResult(myIntent, SCAN_IMAGE);
                }
            }
        });
    }

    private void initView() {
        et_title = $(R.id.et_title);
        iv_type = $(R.id.iv_type);
        iv_type.setOnClickListener(this);
        btn_type = $(R.id.btn_type);
        btn_type.setOnClickListener(this);
        et_Award = $(R.id.et_Award);
        iv_city = $(R.id.iv_city);
        iv_city.setOnClickListener(this);
        tv_city = $(R.id.tv_city);
        et_details = $(R.id.et_details);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_type: //选择类型
            case R.id.iv_type:
                String[] items = getResources().getStringArray(R.array.style);
                mywheel=new wheelPopWindow(Publish1Activity.this, items, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        btn_type.setText(mywheel.getSelectDate());
                        switch (mywheel.getSelectDate())
                        {
                            case "寻人启示":
                                iv_type.setImageResource(R.mipmap.lostpeople);
                                break;
                            case "找人维修":
                                iv_type.setImageResource(R.mipmap.repair);
                                break;
                            case "寻物启示":
                                iv_type.setImageResource(R.mipmap.lostthing);
                                break;
                            case "生活帮带":
                                iv_type.setImageResource(R.mipmap.helpcarry);
                                break;
                            case "学习互助":
                                iv_type.setImageResource(R.mipmap.study);
                                break;
                            case "江湖眼线":
                                iv_type.setImageResource(R.mipmap.jhsign);
                                break;
                            case "江湖救急":
                                iv_type.setImageResource(R.mipmap.emergency);
                                break;
                            case "其他":
                                iv_type.setImageResource(R.mipmap.other);
                                break;
                            default:
                                iv_type.setImageResource(R.mipmap.other);
                                break;

                        }
                        mywheel.dismiss();
                    }
                });
                mywheel.showAtLocation(btn_type, Gravity.BOTTOM,
                        0, 0);
                break;
            case R.id.iv_city://选择城市
                Intent intent =new Intent(mContext,SelectCityActivity.class);
                startActivityForResult(intent,99);
                break;
        }
    }

    public void NoPictureSave()
    {
        question.setUser(BmobUser.getCurrentUser(mContext, User.class));
        question.setState("0");
        question.setTitle(et_title.getText().toString());
        question.setType(btn_type.getText().toString());
        question.setAward(award);
        question.setCity(tv_city.getText().toString());
        question.setDetails(et_details.getText().toString());
        question.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                showToast("发布成功！");
                if (!TextUtils.isEmpty(et_Award.getText()))
                {
                    //发布成功后扣除用户积分
                    User user = new User();
                    user.setUserIntegral(CurrentUser.getUserIntegral() - award);
                    //user.setExperience(CurrentUser.getExperience());
                    //user.setGangsPosition(CurrentUser.getGangsPosition());
                    user.update(mContext, CurrentUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            showToast("用户积分已扣除");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            showToast("用户积分扣除失败");
                            //扣除积分失败的处理
                        }
                    });
                }
                finish();
                startAnimActivity(MyPublishActivity.class);
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
                showToast("发布失败" + s);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case photo_graph:
                    //Toast.makeText(aaaaActivity.this, "photo", 1000).show();
                    if (resultCode==Activity.RESULT_OK) {
                        //Toast.makeText(aaaaActivity.this, "photo1", 1000).show();
                        String path=photoSavePath+photoSaveName;
                        imageArray.add(imageArray.size()-1, path);
                        imagesAdapt.notifyDataSetChanged();
                    }
                    break;
                case SELECT_PHOTO:
                    //Toast.makeText(aaaaActivity.this, "select", 1000).show();
                    ArrayList<String> mylist= data.getStringArrayListExtra("images");
                    imageArray.addAll(imageArray.size()-1, mylist);
                    imagesAdapt.notifyDataSetChanged();
                    break;
                case SCAN_IMAGE:
                    //Toast.makeText(aaaaActivity.this, "scan", 1000).show();
                    String num=data.getStringExtra("number");
                    if (!num.equals("")) {
                        int number=Integer.parseInt(num);
                        imageArray.remove(number);
                        imagesAdapt.notifyDataSetChanged();
                    }
                    break;
                case 99:
                    String receive=data.getStringExtra("lngCityName");
                    tv_city.setText(receive);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void showPopupWindow(View parent){

        if (popWindow == null) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.pop_select_photo,null);
            popWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,true);
            initPop(view);
        }
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }
    public void initPop(View view) {
        photograph = (TextView) view.findViewById(R.id.photograph);//拍照
        albums = (TextView) view.findViewById(R.id.albums);//相册
        cancel= (LinearLayout) view.findViewById(R.id.cancel);//取消
        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                Uri imageUri = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoSaveName = System.currentTimeMillis() + ".png";
                String path = photoSavePath + photoSaveName;
                imageUri = Uri.fromFile(new File(path));
                openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, photo_graph);
            }
        });
        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                Intent myIntent = new Intent(Publish1Activity.this, ShowImageActivity.class);
                myIntent.putExtra("num", MAX_NUMBER - imageArray.size() + 1 + "");
                startActivityForResult(myIntent, SELECT_PHOTO);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();

            }
        });
    }

}
