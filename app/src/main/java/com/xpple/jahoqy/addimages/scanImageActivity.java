package com.xpple.jahoqy.addimages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.xpple.jahoqy.R;

public class scanImageActivity extends Activity {
	ImageView imageView;
	Button button;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanimageactivity);
        button=(Button)findViewById(R.id.imagebutton);
        imageView=(ImageView)findViewById(R.id.imageView);
        String pathString=getIntent().getStringExtra("path");
        ImageDownLoader.showLocationImage(pathString, imageView, R.mipmap.ic_launcher);//预览图片
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent=new Intent(scanImageActivity.this,scanImageActivity.class);
				String numberString=getIntent().getStringExtra("number");
				myIntent.putExtra("number", numberString);
				//Toast.makeText(scanImageActivity.this, numberString, Toast.LENGTH_SHORT).show();
				setResult(2, myIntent);
				finish();
			}
		});
    }
}
