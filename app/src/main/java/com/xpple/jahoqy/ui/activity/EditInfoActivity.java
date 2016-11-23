package com.xpple.jahoqy.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xpple.jahoqy.BaseClass.BaseActivity;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.ImageDownLoader;
import com.xpple.jahoqy.bean.User;
import com.xpple.jahoqy.view.RoundImageView;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class EditInfoActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_username;
    private TextView tv_phone;
    private RoundImageView iv_photo;
    private TextView tv_gender;
    private TextView tv_city;
    private TextView tv_introduce;
    private RelativeLayout rl_photo;
    private RelativeLayout rl_gender;
    private RelativeLayout rl_city;
    private RelativeLayout rl_introduce;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private User user;

    private PopupWindow popWindow;
    private LayoutInflater layoutInflater;
    private String path;//照片位置
    private TextView photograph, albums;
    private LinearLayout cancel;
    /**拍照的照片存储位置 */
    /**
     * 拍照的照片存储位置
     */
    private String photoSavePath;//保存路径
    private String photoSaveName;//图片名

    public final static int SELECT_PHOTO = 10;
    public final static int photo_graph = 2;
    public final static int INTRODUCE = 3;
    public final static int CROP_IMAGE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        initView();
        initData();
    }
    private void initData() {
        tv_username.setText(CurrentUser.getUsername());
        tv_phone.setText(CurrentUser.getMobilePhoneNumber());
        if (CurrentUser.getUserPhoto() != null) {
            ImageDownLoader.showNetImage(mContext, CurrentUser.getUserPhoto(), iv_photo, R.mipmap.ic_photo);
            //imageLoader.displayImage(CurrentUser.getUserPhoto().getUrl(), iv_photo);
        } else {

        }
        if (CurrentUser.getGender() != null) {
            tv_gender.setText(CurrentUser.getGender());
        } else {
            tv_gender.setText("");
        }
        if (CurrentUser.getCity() != null) {
            tv_city.setText(CurrentUser.getCity());
        } else {
            tv_city.setText("");
        }
        if (CurrentUser.getJahoAnnounce() != null) {
            tv_introduce.setText(CurrentUser.getJahoAnnounce());
        } else {
            tv_introduce.setText("");
        }
    }


    private void initView() {
        initTopBarForLeft("编辑资料", R.mipmap.actionbar_me, Color.BLACK); //标题栏
        tv_username = $(R.id.tv_username);
        tv_phone = $(R.id.tv_phone);
        iv_photo = $(R.id.iv_photo);
        tv_gender = $(R.id.tv_gender);
        tv_city = $(R.id.tv_city);
        tv_introduce = $(R.id.tv_introduce);
        rl_photo = $(R.id.rl_photo);
        rl_photo.setOnClickListener(this);
        rl_gender = $(R.id.rl_gender);
        rl_gender.setOnClickListener(this);
        rl_city = $(R.id.rl_city);
        rl_city.setOnClickListener(this);
        rl_introduce = $(R.id.rl_introduce);
        rl_introduce.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_photo: //上传个人头像
                //缓冲图片位置
                File mfile = new File(Environment.getExternalStorageDirectory(), "JaHoQy/Camera");
                if (!mfile.exists())
                    mfile.mkdirs();
                photoSavePath = Environment.getExternalStorageDirectory() + "/JaHoQy/Camera/";
                photoSaveName = System.currentTimeMillis() + ".png";
                showPopupWindow(iv_photo);

                break;
            case R.id.rl_gender:  //选择性别
                int checkId = 0;
                if (!TextUtils.isEmpty(CurrentUser.getGender())) {
                    if (CurrentUser.getGender().equals("男")) {
                        checkId = 0;
                    } else {
                        checkId = 1;
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(EditInfoActivity.this);
                builder.setTitle("选择性别");
                builder.setSingleChoiceItems(new String[]{"男", "女"}, checkId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        dialog.dismiss();

                        BmobQuery<User> query=new BmobQuery<>();
                        query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
                        query.findObjects(mContext, new FindListener<User>() {
                            @Override
                            public void onSuccess(List<User> list) {
                                if (list != null && list.size() > 0) {
                                    //list.get(0);
                                    String gender = "";
                                    switch (which) {
                                        case 0:
                                            tv_gender.setText("男");
                                            gender = "男";
                                            break;
                                        case 1:
                                            tv_gender.setText("女");
                                            gender = "女";
                                            break;
                                    }
                                    user = new User();
                                    user.setGender(gender);
                                    user.setUserIntegral(list.get(0).getUserIntegral());
                                    user.setExperience(list.get(0).getExperience());
                                    user.setGangsPosition(list.get(0).getGangsPosition());
                                    user.update(mContext, CurrentUser.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            showToast("更新成功");
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            showToast("更新失败" + s);
                                        }
                                    });

                                } else {
                                    showToast("请检查网络设置");
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                showToast("请检查网络设置");
                            }
                        });

                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.rl_city: //选择城市
                Intent intent = new Intent(EditInfoActivity.this, SelectCityActivity.class);
                startActivityForResult(intent, 99);
                break;
            case R.id.rl_introduce:
                Intent intent1 = new Intent(EditInfoActivity.this, IntroduceEditActivity.class);
                intent1.putExtra("introduce", tv_introduce.getText());
                startActivityForResult(intent1, INTRODUCE);
                break;
        }
    }

    private void showPopupWindow(View parent) {

        if (popWindow == null) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.pop_select_photo, null);
            popWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
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
        cancel = (LinearLayout) view.findViewById(R.id.cancel);//取消
        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                Uri imageUri = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoSaveName = System.currentTimeMillis() + ".png";
                path = photoSavePath + photoSaveName;
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
                Intent myIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case photo_graph:
                    if (resultCode == Activity.RESULT_OK) {
                        //showToast(path);
                        File file = new File(path);
                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(uri, "image/*");
                        startActivityForResult(intent, CROP_IMAGE);
                    }
                    break;
                case SELECT_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri uri = data.getData();
                        Cursor cursor = getContentResolver().query(uri, null,
                                null, null, null);
                        cursor.moveToFirst();
                        path = cursor.getString(1); // 图片文件路径
                        cursor.close();
                        //showToast(path);
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(uri, "image/*");
                        startActivityForResult(intent, CROP_IMAGE);
                    }
                    break;
                case CROP_IMAGE:
                    if (resultCode== Activity.RESULT_OK) {
                        final BmobFile bmobFile = new BmobFile(new File(path));
                        bmobFile.uploadblock(this, new UploadFileListener() {
                            @Override
                            public void onSuccess() {
                                BmobQuery<User> query=new BmobQuery<>();
                                query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
                                query.findObjects(mContext, new FindListener<User>() {
                                    @Override
                                    public void onSuccess(List<User> list) {
                                        if (list != null && list.size() > 0) {
                                            //list.get(0);
                                            User user = new User();
                                            user.setUserIntegral(list.get(0).getUserIntegral());
                                            user.setExperience(list.get(0).getExperience());
                                            user.setGangsPosition(list.get(0).getGangsPosition());
                                            user.setUserPhoto(bmobFile);
                                            user.update(mContext, CurrentUser.getObjectId(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    iv_photo.setImageURI(Uri.fromFile(new File(path)));
                                                    showToast("图片更新成功");
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                    showToast("更新图片失败" + s);
                                                }
                                            });

                                        } else {
                                            showToast("请检查网络设置");
                                        }
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        showToast("请检查网络设置");
                                    }
                                });

                            }

                            @Override
                            public void onFailure(int i, String s) {
                                showToast("图片上传失败" + s);
                            }
                        });
                    }
                case INTRODUCE:
                    if (data != null) {
                        tv_introduce.setText(data.getStringExtra("introduce_et"));
                    }
                    break;
                case 99:
                    BmobQuery<User> query=new BmobQuery<>();
                    query.addWhereEqualTo("objectId", CurrentUser.getObjectId());
                    query.findObjects(mContext, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (list != null && list.size() > 0) {
                                //list.get(0);
                                String receive = data.getStringExtra("lngCityName");
                                tv_city.setText(receive);
                                user = new User();
                                user.setCity(receive);
                                user.setUserIntegral(list.get(0).getUserIntegral());
                                user.setExperience(list.get(0).getExperience());
                                user.setGangsPosition(list.get(0).getGangsPosition());
                                user.update(mContext, CurrentUser.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        showToast("更新成功");
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        showToast("更新失败" + s);
                                    }
                                });

                            } else {
                                showToast("请检查网络设置");
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            showToast("请检查网络设置");
                        }
                    });

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void onBackPressed() {
//        setResult(13);
//        super.onBackPressed();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
}
