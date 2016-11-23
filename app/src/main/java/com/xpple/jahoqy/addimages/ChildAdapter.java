package com.xpple.jahoqy.addimages;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.xpple.jahoqy.R;
import com.xpple.jahoqy.addimages.LocalImageView.OnMeasureListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ChildAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对�?
	public static HashMap<String, Boolean> mHashMap = new HashMap<String, Boolean>();
	public static int Max_number=10;
	public static ArrayList<String> listMap=new ArrayList<String>();
	/**
	 * 用来存储图片的�?�中情况
	 */
	private GridView mGridView;
	private LinkedList<String> list;
	private Context context;
	protected LayoutInflater mInflater;
	//	private HashMap<String,View> mHashMap;
	private TextCallback textcallback = null;
	private ListCallback mListCallback = null;
	private List<String> mPathList;

	private HashMap<String, Boolean> mStateHashMap;
	private int intCount = 1;
	//	private Map<Integer,Integer> selected;

	public LinkedList<String> getList() {
		return list;
	}

	public void setList(LinkedList<String> list) {
		this.list = list;
	}

	public ChildAdapter(Context context, LinkedList<String> list, GridView mGridView) {
		this.list = list;
		this.mGridView = mGridView;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		mPathList = new ArrayList<String>();
		mStateHashMap = new HashMap<String, Boolean>();
		//		selected=new HashMap<Integer,Integer>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {

		final ViewHolder viewHolder;
		final String path = list.get(position);
		View rootView = convertView;
		if(rootView == null){

			//		if(mHashMap.get(path) == null){
			viewHolder = new ViewHolder();
			rootView = mInflater.inflate(R.layout.grid_child_item, null);
			//			mHashMap.put(path, rootView);
			viewHolder.mOneImageView = (ImageView) rootView.findViewById(R.id.imageview_item_one);
			viewHolder.mImageView = (LocalImageView) rootView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (ImageView) rootView.findViewById(R.id.child_checkbox);
			viewHolder.view = rootView.findViewById(R.id.view_ImageView_up);

			//用来监听ImageView的宽和高  
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {

				@Override  
				public void onMeasureSize(int width, int height) {  
					mPoint.set(width, height);  
				}  
			});  
			rootView.setTag(viewHolder);
		}else{
			//		rootView = mHashMap.get(path);
			viewHolder = (ViewHolder) rootView.getTag();
		}

		if(!"".equals(path)){
			viewHolder.mOneImageView .setVisibility(View.GONE);
			viewHolder.mImageView.setVisibility(View.VISIBLE);
			viewHolder.mCheckBox.setVisibility(View.VISIBLE);
            //listMap.contains(path);
			//		//解决checkbox选择乱序
			if (mHashMap.containsKey(path)) {
				viewHolder.mCheckBox.setImageResource(R.drawable.choice_2_1);//图片选中后的图标
				viewHolder.view.setVisibility(View.VISIBLE);
			} else {
				viewHolder.mCheckBox.setImageResource(R.drawable.choice_2);//图片未�?�中的图�?
				viewHolder.view.setVisibility(View.GONE);
			}
			viewHolder.mCheckBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!mHashMap.containsKey(path)){
						if(mHashMap.size() == Max_number){
							Toast.makeText(context, "您最多只能�?�择"+Max_number+"�?", Toast.LENGTH_SHORT).show();
							viewHolder.mCheckBox.setImageResource(R.drawable.choice_2);
							viewHolder.view.setVisibility(View.GONE);
						}else{
							mHashMap.put(path, true);
							viewHolder.mCheckBox.setImageResource(R.drawable.choice_2_1);
							viewHolder.view.setVisibility(View.VISIBLE);
							mPathList.add(path);
						}
					}else if(mHashMap.containsKey(path)){
						mHashMap.remove(path);
						viewHolder.mCheckBox.setImageResource(R.drawable.choice_2);
						viewHolder.view.setVisibility(View.GONE);
						mPathList.remove(path);
					}
					mListCallback.onListener(mPathList,path);
					mPathList.clear();
					if(Max_number-mHashMap.size() > 0){
						if (textcallback != null){
							textcallback.onListen(mHashMap.size());
						}
					}else{
						if (textcallback != null){
							textcallback.onListen(mHashMap.size());
						}
					}
				}
			});
			ImageDownLoader.showLocationImage
			(path, viewHolder.mImageView, R.drawable.default_picture);//无图片时的默认图�?
		}else{
			viewHolder.mOneImageView .setVisibility(View.VISIBLE);
			viewHolder.mImageView.setVisibility(View.GONE);
			viewHolder.mCheckBox.setVisibility(View.GONE);
			viewHolder.mOneImageView.setImageResource(R.drawable.photo);//摄像头的图标
		}
		return rootView;
	}
	public static class ViewHolder{
		public ImageView mCheckBox,mOneImageView;
		public LocalImageView mImageView;
		private View view;
	}

	public  interface TextCallback {
		public void onListen(int count);
	}

	public void setTextCallback(TextCallback listener) {
		textcallback = listener;
	}

	public  interface ListCallback{
		public void onListener(List<String> path, String pathStr);
	}

	public void setListCallback(ListCallback callback){
		this.mListCallback = callback;
	}

}
