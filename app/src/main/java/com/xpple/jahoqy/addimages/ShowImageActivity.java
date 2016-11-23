package com.xpple.jahoqy.addimages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.ChildAdapter.ListCallback;
import com.xpple.jahoqy.addimages.ChildAdapter.TextCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

@SuppressLint({ "ResourceAsColor", "HandlerLeak" })
@SuppressWarnings("unused")
public class ShowImageActivity extends Activity implements OnItemClickListener ,OnClickListener{
	/**拍照的照片存储位�? */
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory()
			+ "/qebb/Camera");
	/** 用来标识请求照相功能的activity */
	public static final int CAMERA_WITH_DATA = 168;
	private static  File mCurrentPhotoFile;// 照相机拍照得到的图片
	private GridView mGridView;
	private LinkedList<String> list,chileList,mPauseList;
	private LinkedList<ImageBean> mBeenList;
	private ChildAdapter adapter;
	private HashMap<String, LinkedList<String>> mGruopMap = new HashMap<String, LinkedList<String>>();
	private final static int SCAN_OK = 1;
	private Context mContext;
	private Button upButton,imageview_select;
	private PopupWindow mPopupWindow;
	private int windowWitdh,windowHeight;
	private RelativeLayout mLayout;
	private Button mOkButton;
	private ArrayList<File> fileList;
	private ImageLoader imageLoader;
	private PreferenceUtil preferenceUtil;

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				list.addFirst("");
				mBeenList = subGroupOfImage(mGruopMap);
				upButton.setText("全部图片("+(list.size()-1)+")");
				adapter = new ChildAdapter(mContext, list, mGridView);
				//				alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
				//				alphaInAnimationAdapter.setAbsListView(mGridView);
				mGridView.setAdapter(adapter);
				adapter.setListCallback(new ListCallback() {

					@Override
					public void onListener(List<String> path,String pathStr) {
						// TODO Auto-generated method stub
						if(path != null && path.size() > 0){
							mPauseList.add(path.get(0));
						}else{
							mPauseList.remove(pathStr);
						}
					}
				});
				adapter.setTextCallback(new TextCallback() {

					@Override
					public void onListen(int count) {
						// TODO Auto-generated method stub
						if(count > 0){
							mOkButton.setEnabled(true);
							mOkButton.setBackgroundColor(Color.TRANSPARENT);
							if(ChildAdapter.Max_number-count>0)
								mOkButton.setText("("+count+"/"+ChildAdapter.Max_number+") 确定");
							else
								mOkButton.setText("("+count+"/"+ChildAdapter.Max_number+") 完成");

						}else{
							mOkButton.setEnabled(false);
							mOkButton.setBackgroundColor(Color.TRANSPARENT);
							mOkButton.setText("确定");
						}
					}
				});
				//关闭进度
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		setContentView(R.layout.show_image_activity);
		if(imageLoader != null){
			imageLoader.clearMemoryCache();
			System.gc();
		}
		String number=getIntent().getStringExtra("num");
		try {
			Log.e("caolin", number);
			ChildAdapter.Max_number=Integer.parseInt(number);
			
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(ShowImageActivity.this, number, Toast.LENGTH_SHORT);
		}
		initViews();
	}
	private void initViews() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		Bundle bun  = intent.getExtras();
		imageview_select = (Button) findViewById(R.id.imageview_select);
		preferenceUtil = PreferenceUtil.getInstance(mContext);
		preferenceUtil.saveString("path_showImage", "0");
		fileList = new ArrayList<File>();
		mPauseList = new LinkedList<String>();
		mGridView = (GridView) findViewById(R.id.gridview_child);
		upButton = (Button) findViewById(R.id.imageview_up_select);
		mLayout = (RelativeLayout) findViewById(R.id.bottom_nav_relative);
		mOkButton = (Button) findViewById(R.id.imageview_ok_select);
		mOkButton.setEnabled(false);
		mOkButton.setBackgroundColor(Color.TRANSPARENT);
		mOkButton.setText("确定");
		mGridView.setOnItemClickListener(this);
		list = new LinkedList<String>();
		//		list.add("");
		getImages();
		upButton.setOnClickListener(this);
		mOkButton.setOnClickListener(this);
		imageview_select.setOnClickListener(this);

	}

	/*.
	 * 点击事件
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageview_up_select:
			getPopWindows();
			break;
		case R.id.imageview_ok_select:
			//确定按钮的事�?
			if(ChildAdapter.mHashMap != null){
				ChildAdapter.mHashMap.clear();
			}
			Intent mIntent =new Intent();
			ArrayList<String> list1=new ArrayList<String>();
			list1.addAll(mPauseList);
			mIntent.putExtra("images", list1);
			setResult(10, mIntent);
			finish();
			break;
//		case R.id.tv_cancel_navigation:
//			if(Bimp.mHashMap != null){
//				Bimp.mHashMap.clear();
//			}
//			finish();
//			ImageLoader.getInstance().clearMemoryCache();
//			System.gc();
//			break;
		case R.id.imageview_select:
			if(mPauseList == null || mPauseList.size() <= 0){
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putSerializable("list", mPauseList);
			Intent intent = new Intent(mContext,ViewPagerActivity.class) ;
			intent.putExtras(bundle);
			startActivity(intent);
			break;

		}
	}
	@SuppressWarnings("deprecation")
	private void getPopWindows(){
		View contentView = setPopWindowViews();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		windowWitdh = dm. widthPixels;
		windowHeight = dm.heightPixels;
		mPopupWindow = new PopupWindow(contentView, windowWitdh, windowHeight*2/3);
		//getWindow().getDecorView()
		mPopupWindow.setFocusable(true);  
		mPopupWindow.setOutsideTouchable(true);  
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());  
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		int[] location = new int[2];  
		mLayout.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(mLayout, Gravity.NO_GRAVITY, location[0], location[1]-mPopupWindow.getHeight());  
	}
	private View setPopWindowViews() {
		// TODO Auto-generated method stub
		View contentView = getLayoutInflater().inflate(R.layout.popwindow_layout, null);
		ListView popWindowListView = (ListView) contentView.findViewById(R.id.listview_popwindow);
		if(mGruopMap != null && mGruopMap.size() > 0){
			//			Log.d("TAG", "size: "+mBeenList.size());

			GroupImageAdapter imageAdapter = new GroupImageAdapter(mBeenList, mContext, popWindowListView);
			popWindowListView.setAdapter(imageAdapter);
		}else{
			Log.e("","image is null");
		}
//		if("0".equals(preferenceUtil.getString("path_showImage", "")) && mBeenList != null &&  mBeenList.size() > 0){
//			preferenceUtil.saveString("path_showImage", mBeenList.get(0).getTopImagePath());
//		}
		popWindowListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String path = mBeenList.get(arg2).getTopImagePath();
				preferenceUtil.saveString("path_showImage", arg2+"");
//				ImageView imageOK = (ImageView) arg1.findViewById(R.id.imageViewOk);
//				imageOK.setVisibility(View.VISIBLE);
				String fileName = mBeenList.get(arg2).getFolderName();

				if("全部图片".equals(fileName)){
//										list.clear();
//										list.add("");
					getImages();
					//					upButton.setText(fileName+"("+list.size()+")");
					//					Log.d("TAG", "size :"+list.size());
					adapter.setList(list);
					adapter.notifyDataSetChanged();
					//list.clear();
				}else if("最近图片".equals(fileName)){
					list = mGruopMap.get("Camera");
					//					upButton.setText(fileName+"("+list.size()+")");
					adapter.setList(list);
					adapter.notifyDataSetChanged();
				}else{
					list = mGruopMap.get(fileName);

					adapter.setList(list);
					adapter.notifyDataSetChanged();
				}
				upButton.setText(fileName+"("+list.size()+")");
				mPopupWindow.dismiss();
			}
		});
		return contentView;
	}
	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(ShowImageActivity.this, "暂无外部存储", 2000);
			return;
		}

		//显示进度�?

		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = mContext.getContentResolver();
				//只查询jpeg和png的图�?
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaColumns.MIME_TYPE + "=? or "
								+ MediaColumns.MIME_TYPE + "=? or "+ MediaColumns.MIME_TYPE + "=?",
								new String[] {  "image/jpg","image/jpeg", "image/png" }, MediaColumns.DATE_MODIFIED);
				mGruopMap.clear();

				while (mCursor.moveToNext()) {
					//获取图片的路�?
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaColumns.DATA));
					//获取该图片的父路径名
					String parentName = new File(path).getParentFile().getName();
					if (!mGruopMap.containsKey(parentName)) {
						Log.e("caolin", path);
						chileList = new LinkedList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						if(!chileList.contains(path)){
							mGruopMap.get(parentName).add(path);
						}
					}
				}

				mCursor.close();
				mGruopMap.keySet();
				Iterator<Entry<String, LinkedList<String>>> it = mGruopMap.entrySet().iterator();
				List<ImageSort> sortList = new LinkedList<ImageSort>();
				List<String> pathArray = new LinkedList<String>();
				while (it.hasNext()) {
					Entry<String, LinkedList<String>> entry = it.next();
					String key = entry.getKey();
					List<String> value = entry.getValue();
					list.addAll(value);
				}

				//通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);

			}
		}).start();

	}
	public class ImageSort  implements Comparator<ImageSort>{
		public String path;
		public long lastModified;
	        public int compare(ImageSort file1, ImageSort file2) {  
	            if(file1.lastModified > file2.lastModified)  
	            {  
	                return -1;  
	            }else  
	            {  
	                return 1;  
	            }  
	        }
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub//&& SDK16.getAndroidSDKVersion() >= 11
		if(arg2 == 0 && "".equals(list.get(arg2))){
			doTakePhoto(); 
		}else{
			Bundle bundle = new Bundle();
			bundle.putSerializable("list", list);
			//			bundle.putStringArrayList("list", list);
			bundle.putInt("position", arg2-1);
			Intent intent = new Intent(mContext,ViewPagerActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}

	}
	/** 
	 * 获取原图片存储路�? 
	 * @return 
	 */  
	private String getPhotopath() {  
		// 文件夹路�?  
		if(!PHOTO_DIR.exists()){
			PHOTO_DIR.mkdirs();
		}
		mCurrentPhotoFile = new File(PHOTO_DIR,getPhotoFileName());
		String pathUrl = mCurrentPhotoFile.getAbsolutePath();  
		try {
			mCurrentPhotoFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pathUrl;  
	}  
	@SuppressLint("SimpleDateFormat")
	private  String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		return "/"+dateFormat.format(date) + ".jpg";
	}
	/**
	 * 拍照获取图片
	 * 
	 */
	private  void doTakePhoto() {
		      // if(mBeenList != null){
					try {
						if (!PHOTO_DIR.exists())
							PHOTO_DIR.mkdirs();// 创建照片的存储目�?
						mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
						Intent it = new Intent("android.media.action.IMAGE_CAPTURE"); 
						it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile)); 
						startActivityForResult(it, 2000); 
					} catch (Exception e) {
						//showShortToast("没有发现图片");
					}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data); 
		if(requestCode == 2000 && resultCode == Activity.RESULT_OK) {  
			String sdStatus = Environment.getExternalStorageState();  
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // �?测sd是否可用  
			
				return;  
			}  
			String fileName = mCurrentPhotoFile.getAbsolutePath();  
			FileOutputStream b = null;  
//			try {  
//				Log.e("TAG", mCurrentPhotoFile.getAbsolutePath());
//				Bitmap bitmap = Bimp.revitionImageSize(mCurrentPhotoFile.getAbsolutePath());
//
//				fileName = mCurrentPhotoFile.getAbsolutePath();  
//				int degree =readPictureDegree(fileName);  
//				Bitmap newbitmap =rotateBitmap( bitmap,degree); 
//							
//				if(bitmap != newbitmap && bitmap != null && !bitmap.isRecycled())  
//				{  
//					bitmap.recycle();  
//					bitmap = null;  
//				}  
//				b = new FileOutputStream(fileName);  
//				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文�?  
//			} catch (Exception e) {  
//				e.printStackTrace();  
//			} finally {  
//				try {  
//					if(b != null){
//						b.flush();  
//						b.close(); 
//					}
//				} catch (IOException e) {  
//					e.printStackTrace();  
//				}  
//			}  
			//		            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);// 将图片显示在ImageView�?  
	     list.set(0, fileName);
	     adapter.setList(list);
	     adapter.notifyDataSetChanged();
	     //Bimp.drr.add(fileName);
		}
		if(requestCode == 2000 && resultCode != Activity.RESULT_OK) {  

			String sdStatus = Environment.getExternalStorageState();  
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // �?测sd是否可用  
				return;  
			}  
			String fileName = "";
			try {  
				fileName = mCurrentPhotoFile.getAbsolutePath(); 
				File file=new File(fileName);
				file.delete();
				Toast.makeText(ShowImageActivity.this, "拍照失败！！�?", 2000);
			} catch (Exception e) {  
				e.printStackTrace();  
			} 
		}
	}

	private static int readPictureDegree(String path) {    
        int degree  = 0;    
        try {    
                ExifInterface exifInterface = new ExifInterface(path);    
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);    
                switch (orientation) {    
                case ExifInterface.ORIENTATION_ROTATE_90:    
                        degree = 90;    
                        break;    
                case ExifInterface.ORIENTATION_ROTATE_180:    
                        degree = 180;    
                        break;    
                case ExifInterface.ORIENTATION_ROTATE_270:    
                        degree = 270;    
                        break;    
                }    
        } catch (IOException e) {    
                e.printStackTrace();    
        }    
        return degree;    
    }  
	private static Bitmap rotateBitmap(Bitmap bitmap, int rotate){  
        if(bitmap == null)  
            return null ;  
          
        int w = bitmap.getWidth();  
        int h = bitmap.getHeight();  
  
        // Setting post rotate to 90  
        Matrix mtx = new Matrix();  
        mtx.postRotate(rotate);  
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);  
    }
	/**
	 * 组装分组界面GridView的数据源，因为我们扫描手机的时�?�将图片信息放在HashMap�?
	 * �?以需要遍历HashMap将数据组装成List
	 * 
	 * @param mGruopMap
	 * @return
	 */
	private LinkedList<ImageBean> subGroupOfImage(HashMap<String, LinkedList<String>> mGruopMap){
		if(mGruopMap.size() == 0){
			return null;
		}
		LinkedList<ImageBean> listLink = new LinkedList<ImageBean>();
		mGruopMap.keySet();
		Iterator<Entry<String, LinkedList<String>>> it = mGruopMap.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, LinkedList<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();

			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));//获取该组的第�?张图�?
			if("Camera".equals(mImageBean.getFolderName())){
				mImageBean.setFolderName("最近图片");
				listLink.addFirst(mImageBean);
			}else{
				listLink.add(mImageBean);
			}
		}

	    ImageBean bean = new ImageBean(list.get(1), "全部图片", list.size()-1);//这里是全部图片设�?
	    listLink.addFirst(bean);
		return listLink;

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
			if(ChildAdapter.mHashMap != null){
				ChildAdapter.mHashMap.clear();
			}
			finish();
			ImageLoader.getInstance().clearMemoryCache();
			System.gc();
			return true;  
		}  
		return true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(imageLoader != null){
			imageLoader.clearMemoryCache();
		}
		System.gc();
	}
	
}
