package com.zhihu.matisse.sample;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.Album;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.model.AlbumMediaCollection;
import com.zhihu.matisse.internal.ui.adapter.RecyclerViewCursorAdapter;
import com.zhihu.matisse.internal.ui.widget.SquareFrameLayout;
import com.zhihu.matisse.listener.OnPageSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by rae on 2020/4/28.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class PreviewDemoActivity extends AppCompatActivity implements AlbumMediaCollection.AlbumMediaCallbacks, OnPageSelectedListener {

    private final AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
    RecyclerView mRecyclerView;
    private AlbumImageAdapter mAlbumMediaAdapter;

    private boolean mFromItem;
    private Map<String, View> mSharedElements;


    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        mAlbumMediaAdapter.swapCursor(cursor);
    }

    @Override
    public void onAlbumMediaReset() {
        mAlbumMediaAdapter.swapCursor(null);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_demo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
        SelectionSpec instance;
        if (!SelectionSpec.getInstance().hasInited) {
            instance = SelectionSpec.getCleanInstance();
            instance.imageEngine = new GlideEngine();
        }
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        ImageView imageView1 = findViewById(R.id.imageView1);
        final Uri uri = Uri.parse("https://up.sc.enterdesk.com/edpic_360_360/5d/e7/84/5de784857a74120197c05a0e6f70f1a5.jpg");
        SelectionSpec.getInstance().imageEngine.loadImage(this, 0, 0, imageView1, uri);
        imageView1.setOnClickListener(v -> {
            mFromItem = false;
            Matisse.from(PreviewDemoActivity.this)
                    .preview()
                    .toShow(uri.toString(), v);
        });


        // 过渡动画
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                if (!mFromItem) return;
                mSharedElements = sharedElements;
                Log.i("rae", "Exit - onMapSharedElements");
                int position = SelectionSpec.getInstance().selectedPosition;
                // 列表处理
                Log.d("rae", "页面选中：" + position);
                mSharedElements.clear();
                mRecyclerView.scrollToPosition(position);
                MediaViewHolder holder = (MediaViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                if (holder != null) {
                    mSharedElements.put("preview", holder.mImageView);
                }
            }

        });


        mAlbumMediaCollection.onCreate(this, this);
        mAlbumMediaCollection.load(Album.all(), false);
        mAlbumMediaAdapter = new AlbumImageAdapter();
        mRecyclerView.setAdapter(mAlbumMediaAdapter);
    }

    @Override
    public void onImagePageSelected(int position) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class MediaViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        MediaViewHolder(View itemView) {
            super(itemView);
            mImageView = new ImageView(itemView.getContext());
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.MarginLayoutParams lp = new FrameLayout.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(5, 5, 5, 5);
            mImageView.setLayoutParams(lp);
            ViewCompat.setTransitionName(mImageView, "preview");
            ((ViewGroup) itemView).addView(mImageView);
        }
    }

    class AlbumImageAdapter extends RecyclerViewCursorAdapter<MediaViewHolder> {


        AlbumImageAdapter() {
            super(null);
        }

        @Override
        protected int getItemViewType(int position, Cursor cursor) {
            return 0;
        }


        @NonNull
        @Override
        public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = new SquareFrameLayout(parent.getContext());
            return new MediaViewHolder(view);

        }

        private List<Item> getData() {
            Cursor cursor = getCursor();
            int itemCount = getItemCount();
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < itemCount; i++) {
                if (cursor.moveToPosition(i)) {
                    items.add(Item.valueOf(cursor));
                }
            }
            return items;
        }

        @Override
        protected void onBindViewHolder(MediaViewHolder holder, Cursor cursor) {
            final Item item = Item.valueOf(cursor);
            ViewCompat.setTransitionName(holder.mImageView, "preview" + holder.getAdapterPosition());
            holder.mImageView.setOnClickListener(v -> {
                mFromItem = true;
                Matisse.from((Activity) holder.itemView.getContext())
                        .preview()
                        .setCurrentPosition(holder.getAdapterPosition())
                        .setOnPageSelectedListener(PreviewDemoActivity.this)
                        .showWithItems(getData(), v);
            });
            SelectionSpec.getInstance().imageEngine.loadImage(holder.itemView.getContext(), 0, 0, holder.mImageView, item.getContentUri());
        }
    }


}
