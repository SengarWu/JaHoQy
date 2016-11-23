package com.xpple.jahoqy.addimages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.xpple.jahoqy.R;

import java.util.ArrayList;


public class PublishProjectImagesAdater  extends BaseAdapter {
	public static ArrayList<String> listMap=new ArrayList<String>();
	/**
	 * 用来存储图片的�?�中情况
	 */
	private GridView mGridView;
	private Context context;
	protected LayoutInflater mInflater;

	public PublishProjectImagesAdater(Context context, ArrayList<String> list, GridView mGridView) {
		this.mGridView = mGridView;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		listMap=list;
	}

	public PublishProjectImagesAdater(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return listMap.size();
	}

	@Override
	public Object getItem(int position) {
		return listMap.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {

		final ViewHolder viewHolder;
		final String path = listMap.get(position);
		View rootView = convertView;
		if(rootView == null){
			viewHolder = new ViewHolder();
			rootView = mInflater.inflate(R.layout.publish_project_images_item, null);
			viewHolder.mImageView = (ImageView) rootView.findViewById(R.id.image_item);
			rootView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) rootView.getTag();
		}
        if (path=="") {
			ImageDownLoader.showLocationImage
					(path, viewHolder.mImageView, R.mipmap.ic_add_picture);//无图片时的默认图?
		}
        else {
    		ImageDownLoader.showLocationImage
    		(path, viewHolder.mImageView, R.drawable.default_picture);//无图片时的默认图?
		}
		return rootView;
	}
	public static class ViewHolder{
		public ImageView mImageView;
	}
}
