package com.zhihu.matisse.internal.ui;

import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.widget.MagicalImageView;
import com.zhihu.matisse.internal.utils.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片预览
 *
 * @author rae
 * Created by rae on 2020/4/26.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class ImagePreviewActivity extends AppCompatActivity {

    ArrayList<Item> mItems;
    protected SelectionSpec mSpec;
    private int mSelectedPosition; // 当前选择的索引
    private ViewPager mViewPager;
    private boolean mDisableFinish; // 不允许关闭

    private void setupWindowTransition() {
        Window window = getWindow();
        window.setGravity(Gravity.FILL);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 是否同时执行
            window.setAllowEnterTransitionOverlap(false);
            window.setAllowReturnTransitionOverlap(false);
            window.setEnterTransition(new AutoTransition()
                    .excludeTarget(android.R.id.statusBarBackground, true));
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setupWindowTransition();
        super.onCreate(savedInstanceState);
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        setContentView(R.layout.activity_image_preview);
        if (Platform.hasKitKat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mSpec = SelectionSpec.getInstance();
        if (mSpec.needOrientationRestriction()) {
            setRequestedOrientation(mSpec.orientation);
        }

        if (savedInstanceState == null) {
            mItems = getIntent().getParcelableArrayListExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE);
        } else {
            mItems = savedInstanceState.getParcelableArrayList(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE);
        }


        mSelectedPosition = getIntent().getIntExtra("position", 0);
        mViewPager = findViewById(R.id.pager);
        PreviewFragmentPagerAdapter adapter = new PreviewFragmentPagerAdapter(getSupportFragmentManager(), mItems);

        mViewPager.setAdapter(adapter);
        if (mSelectedPosition != 0) {
            mViewPager.setCurrentItem(mSelectedPosition);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mSpec.selectedPosition = position;
                if (mSpec.onPageSelectedListener != null) {
                    mSpec.onPageSelectedListener.onImagePageSelected(position);
                }
            }
        });

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                // 重新设置共享元素
                int currentItem = mViewPager.getCurrentItem();
                String tagName = "android:switcher:" + mViewPager.getId() + ":" + currentItem;
                ImagePreviewItemFragment fragment = (ImagePreviewItemFragment) getSupportFragmentManager().findFragmentByTag(tagName);
                if (fragment == null || currentItem == mSelectedPosition) return;
                sharedElements.clear();
                names.clear();
                String transitionName = "preview";
                MagicalImageView view = fragment.getMagicalImageView();
                names.add(transitionName);
                sharedElements.put(transitionName, view);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mItems);
        super.onSaveInstanceState(outState);
    }

    private final class PreviewFragmentPagerAdapter extends FragmentPagerAdapter {

        private final ArrayList<Item> mItems;

        PreviewFragmentPagerAdapter(@NonNull FragmentManager fm, ArrayList<Item> items) {
            super(fm);
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            String transitionName = "preview";
            if (position != mSelectedPosition) {
                transitionName += position;
            }
            return ImagePreviewItemFragment.newInstance(mItems.get(position), transitionName);
        }
    }
}
