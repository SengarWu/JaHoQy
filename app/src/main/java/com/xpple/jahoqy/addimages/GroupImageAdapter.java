package com.xpple.jahoqy.addimages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xpple.jahoqy.R;

import java.util.HashMap;
import java.util.LinkedList;

@SuppressWarnings("unused")
public class GroupImageAdapter extends BaseAdapter {

	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对�?
	private LinkedList<ImageBean> mBeanList;
	private LayoutInflater mInflater;
	private ListView mListView;
	private Context mContext;
	private PreferenceUtil preferenceUtil;
	private HashMap<Integer, View> stateMap;
	@SuppressLint("UseSparseArrays")
	public GroupImageAdapter(LinkedList<ImageBean> mBeenList,Context context,ListView listview){
		this.mBeanList = mBeenList;
		this.mInflater = LayoutInflater.from(context);
		this.mListView = listview;
		this.mContext = context;
		this.preferenceUtil = PreferenceUtil.getInstance(mContext);
		this.stateMap = new HashMap<Integer, View>();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mBeanList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mBeanList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		View rootView = convertView;
		ViewHolder holder = null;
		if(rootView == null){
			holder = new ViewHolder();
			rootView = mInflater.inflate(R.layout.popwindow_item_layout, null);
			holder.mMyImageView = (ImageView) rootView.findViewById(R.id.imageView_parent_show);
			holder.mNameText = (TextView) rootView.findViewById(R.id.textView_parent_name);
			holder.mNumText = (TextView) rootView.findViewById(R.id.textView_nums);
			holder.mOkImage = (ImageView) rootView.findViewById(R.id.imageViewOk);
			rootView.setTag(holder);
		}else{
			holder = (ViewHolder) rootView.getTag();
		}
		ImageBean bean = mBeanList.get(position);
		String path = bean.getTopImagePath();
		if(position==Integer.parseInt(preferenceUtil.getString("path_showImage", ""))){
			holder.mOkImage.setVisibility(View.VISIBLE);
		}else{
			holder.mOkImage.setVisibility(View.GONE);
		}
		
		if("".equals(path)){
			ImageDownLoader.showLocationImage(mBeanList.get(0).getTopImagePath(),
					holder.mMyImageView, R.drawable.choice_2);//列表文件夹的全部图片
		}else{
			ImageDownLoader.showLocationImage(path,
					holder.mMyImageView, R.drawable.choice_2_1);//列表文件夹的�?近图�?
		}
		
		holder.mNameText.setText(bean.getFolderName());
		if(bean.getImageCounts() != 0){
			holder.mNumText.setText(bean.getImageCounts()+"");
		}else{
			holder.mNumText.setText("");
		}
		return rootView;
	}

	static class ViewHolder{
		private ImageView mMyImageView;
		private ImageView mOkImage;
		private TextView mNameText,mNumText;
	}

}
