package com.zhihu.matisse.internal.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rae on 2020/5/12.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class PreviewItem implements Parcelable {

    private Item mItem;
    private String name = "preview";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mItem, flags);
        dest.writeString(this.name);
    }

    public PreviewItem() {
    }

    public PreviewItem(Item item) {
        this.mItem = item;
    }

    public PreviewItem(Item item, String name) {
        this.mItem = item;
        this.name = name;
    }

    protected PreviewItem(Parcel in) {
        this.mItem = in.readParcelable(Item.class.getClassLoader());
        this.name = in.readString();
    }

    public Item getItem() {
        return mItem;
    }

    public void setItem(Item item) {
        mItem = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final Parcelable.Creator<PreviewItem> CREATOR = new Parcelable.Creator<PreviewItem>() {
        @Override
        public PreviewItem createFromParcel(Parcel source) {
            return new PreviewItem(source);
        }

        @Override
        public PreviewItem[] newArray(int size) {
            return new PreviewItem[size];
        }
    };
}
