package com.zhihu.matisse.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

/**
 * Created by rae on 2020/4/30.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class ImageDragLayout extends FrameLayout {

    private static final String TAG = "RAE";
//    private ViewDragHelper mViewDragHelper;

    public ImageDragLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public ImageDragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ImageDragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
//        mViewDragHelper = ViewDragHelper.create(this, new DragLayoutCallback());
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return mViewDragHelper.shouldInterceptTouchEvent(ev);
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mViewDragHelper.processTouchEvent(event);
//        return true;
//    }

    private static class DragLayoutCallback extends ViewDragHelper.Callback {
        private static final String TAG = "RAE";

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            Log.d(TAG, "tryCaptureView: " + child);
            return child instanceof MagicalImageView;
        }


        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            Log.d(TAG, "clampViewPositionHorizontal: " + left + ";" + dx);
            return left;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            Log.d(TAG, "clampViewPositionVertical: " + top + ";" + dy);
            return top;
        }

    }

}
