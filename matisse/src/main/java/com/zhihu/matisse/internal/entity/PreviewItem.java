package com.zhihu.matisse.internal.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * Created by rae on 2020/5/12.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class PreviewItem implements Parcelable {

    @NonNull
    private Item mItem;
    @NonNull
    private String mTransitionName = "preview_item";
    @Nullable
    private View mTransitionView;

    public PreviewItem(@NonNull Item item) {
        mItem = item;
    }

    public PreviewItem(@NonNull Item item, View view) {
        mItem = item;
        String transitionName = ViewCompat.getTransitionName(view);
        if (transitionName != null) {
            mTransitionName = transitionName;
        }
    }

    @NonNull
    public Item getItem() {
        return mItem;
    }

    public void setItem(@NonNull Item item) {
        mItem = item;
    }

    @NonNull
    public String getTransitionName() {
        return mTransitionName;
    }

    public void setTransitionName(@NonNull String transitionName) {
        mTransitionName = transitionName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mItem, flags);
        dest.writeString(this.mTransitionName);
    }

    protected PreviewItem(Parcel in) {
        this.mItem = in.readParcelable(Item.class.getClassLoader());
        this.mTransitionName = in.readString();
    }

    public static final Creator<PreviewItem> CREATOR = new Creator<PreviewItem>() {
        @Override
        public PreviewItem createFromParcel(Parcel source) {
            return new PreviewItem(source);
        }

        @Override
        public PreviewItem[] newArray(int size) {
            return new PreviewItem[size];
        }
    };

    @Nullable
    public androidx.core.util.Pair<View, String> toShareElements() {
        if (mTransitionView == null) return null;
        return new androidx.core.util.Pair<>(mTransitionView, mTransitionName);
    }
}
