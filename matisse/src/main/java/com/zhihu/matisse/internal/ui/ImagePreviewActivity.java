package com.zhihu.matisse.internal.ui;

import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zhihu.matisse.R;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.internal.utils.Platform;

import java.util.ArrayList;

/**
 * 图片预览
 *
 * @author rae
 * Created by rae on 2020/4/26.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class ImagePreviewActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private int mLastPagePosition;
    protected SelectionSpec mSpec;
    protected final SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);
    private ViewPager mViewPager;
    private int mViewPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initWindowTransition();
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
            mSelectedCollection.onCreate(getIntent().getBundleExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE));
        } else {
            mSelectedCollection.onCreate(savedInstanceState);
        }

        ArrayList<Item> items = mSelectedCollection.asList();
        mViewPosition = getIntent().getIntExtra("position", 0);
        mViewPager = findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(this);
        PreviewFragmentPagerAdapter adapter = new PreviewFragmentPagerAdapter(getSupportFragmentManager(), items);
        adapter.setCurrentPosition(mViewPosition);
        mViewPager.setAdapter(adapter);
        if (mViewPosition != 0) {
            mViewPager.setCurrentItem(mViewPosition);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PreviewImageFragment fragment = findFragment(position);
        if (fragment != null && position == mViewPosition) {
            fragment.attachTransitionName();
        }
        if (position != mLastPagePosition) {
            PreviewImageFragment lastFragment = findFragment(mLastPagePosition);
            if (lastFragment != null && lastFragment.isVisible()) {
                lastFragment.detachTransitionName();
            }
            mLastPagePosition = position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private PreviewImageFragment findFragment(int position) {
        String tagName = "android:switcher:" + mViewPager.getId() + ":" + position;
        return (PreviewImageFragment) getSupportFragmentManager().findFragmentByTag(tagName);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mSelectedCollection.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void initWindowTransition() {
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

    private static final class PreviewFragmentPagerAdapter extends FragmentPagerAdapter {

        private final ArrayList<Item> mItems;
        private int mViewPosition;

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
            return PreviewImageFragment.newInstance(mItems.get(position), position == mViewPosition);
        }

        void setCurrentPosition(int viewPosition) {
            mViewPosition = viewPosition;
        }
    }
}
