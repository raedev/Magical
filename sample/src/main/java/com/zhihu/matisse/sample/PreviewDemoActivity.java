package com.zhihu.matisse.sample;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rae on 2020/4/28.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class PreviewDemoActivity extends AppCompatActivity implements View.OnClickListener {

    final List<String> mImageUrls = new ArrayList<>();
    private final Map<View, String> mImageUrlMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_demo);
        mImageUrls.add("content://media/external/images/media/24");
        mImageUrls.add("content://media/external/images/media/25");
        mImageUrls.add("content://media/external/images/media/26");
        mImageUrls.add("content://media/external/images/media/27");
        mImageUrls.add("content://media/external/images/media/28");
        ViewGroup contentLayout = findViewById(R.id.wrap_content);
        int childCount = contentLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = contentLayout.getChildAt(i);
            if (view instanceof ImageView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setTransitionName("preview");
                }
                ImageView imageView = (ImageView) view;
                view.setOnClickListener(this);
                String url = mImageUrls.get(i % mImageUrls.size());
                mImageUrlMap.put(imageView, url);
                new GlideEngine().loadImage(this, 0, 0, imageView, Uri.parse(url));
            }
        }

    }

    @Override
    public void onClick(View v) {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .setCurrentPosition(mImageUrls.indexOf(mImageUrlMap.get(v)))
                .setOnLongClickListener((fragment, view, item) -> {
                    AppCompatDialogFragment dialogFragment = new AppCompatDialogFragment();
                    dialogFragment.showNow(fragment.getChildFragmentManager(), "alertdialog");
                })
                .toPreview(mImageUrls, (ImageView) v);
    }


}
