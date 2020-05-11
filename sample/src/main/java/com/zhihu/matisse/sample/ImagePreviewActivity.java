//package com.zhihu.matisse.sample;
//
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.transition.AutoTransition;
//import android.view.ViewGroup;
//import android.view.Window;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.graphics.ColorUtils;
//
//import com.zhihu.matisse.internal.ui.widget.MagicalImageView;
//
//import it.sephiroth.android.library.imagezoom.ImageViewTouch;
//
///**
// * Created by rae on 2020/4/28.
// * Copyright (c) https://github.com/raedev All rights reserved.
// */
//public class ImagePreviewActivity extends AppCompatActivity {
//
//    ImageViewTouch mImageView1;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        initWindowTransition();
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_preview);
//        mImageView1 = findViewById(R.id.imageView);
//        mImageView1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.banner));
//        init();
//
//    }
//
//    private void init() {
//        ViewGroup rootView = findViewById(R.id.rootView);
//        if (!(mImageView1 instanceof MagicalImageView)) return;
//        ((MagicalImageView) mImageView1).setOnDismissListener(new MagicalImageView.onMagicalImageViewDismissListener() {
//            @Override
//            public void onImageViewSliding(float ratio) {
//                int hexAlpha = Math.min(255, (int) (255 * (1.0F - ratio)));
//                int color = ColorUtils.setAlphaComponent(Color.BLACK, hexAlpha);
//                rootView.setBackgroundColor(color);
//            }
//
//            @Override
//            public void onImageViewDismiss() {
//                ActivityCompat.finishAfterTransition(ImagePreviewActivity.this);
//            }
//        });
//    }
//
//
//    private void initWindowTransition() {
//        Window window = getWindow();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // 是否同时执行
//            window.setAllowEnterTransitionOverlap(false);
//            window.setAllowReturnTransitionOverlap(false);
//            window.setEnterTransition(new AutoTransition()
//                    .excludeTarget(android.R.id.statusBarBackground, true));
//        }
//    }
//
//}
