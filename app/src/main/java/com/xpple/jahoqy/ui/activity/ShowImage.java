package com.xpple.jahoqy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xpple.jahoqy.R;

public class ShowImage extends Activity {
    ImageView imageView;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        imageView=(ImageView)findViewById(R.id.imageView);
        String url=getIntent().getStringExtra("url");
        imageLoader.displayImage("http://file.bmob.cn/"+url,imageView);
    }



}
