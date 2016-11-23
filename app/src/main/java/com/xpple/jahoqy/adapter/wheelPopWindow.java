package com.xpple.jahoqy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xpple.jahoqy.R;
import com.xpple.jahoqy.view.WheelView;
import com.xpple.jahoqy.view.WheelView.OnWheelChangedListener;


public class wheelPopWindow  extends PopupWindow implements OnWheelChangedListener {
	private TextView btnCancel;
	private TextView btnConfirm;
	private WheelView Wheel; // �����
	private String[] mDatas; 
	private View popup;
	private View popView;
	private String mCurrentDateString;
	private Context mycontent;
	public String getSelectDate(){
		if (mCurrentDateString==null) {
			return null;
		}else {
			return mCurrentDateString;
		}
	}
	public wheelPopWindow(Context context,String[] datas,OnClickListener listener) {
		super(context);
		mycontent=context;
		mDatas=datas;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popView = inflater.inflate(R.layout.pop_wheel, null);
		initContentView();
		
		this.setContentView(popView);
		this.setWidth(-1);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
	    popView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = popView.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	    btnConfirm.setOnClickListener(listener);
	}
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		mCurrentDateString =  mDatas[newValue];
	}
	 protected void initContentView() {
		    Wheel = (WheelView) popView.findViewById(R.id.wheel1);
			btnCancel = (TextView) popView.findViewById(R.id.cancel);
			btnConfirm = (TextView) popView.findViewById(R.id.confirm);
			Wheel.setViewAdapter(new ArrayWheelAdapter<String>(
					mycontent, mDatas));
			Wheel.addChangingListener(this);
			Wheel.setCurrentItem(3);
			Wheel.setVisibleItems(7);
			btnCancel.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					if (popup != null) {
						dismiss();
					}
				}
			});
			
		}
}
