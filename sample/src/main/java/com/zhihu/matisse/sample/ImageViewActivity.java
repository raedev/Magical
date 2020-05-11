package com.zhihu.matisse.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.zhihu.matisse.internal.ui.ImagePreviewActivity;

/**
 * Created by rae on 2020/4/28.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class ImageViewActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        findViewById(R.id.imageView).setOnClickListener(this);
        findViewById(R.id.imageView1).setOnClickListener(this);
        findViewById(R.id.imageView2).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "pic");
        ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
    }


}
