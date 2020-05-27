package com.zhihu.matisse.listener;

/**
 * 当页面被选中的时候回调
 * Created by rae on 2020/5/27.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public interface OnPageSelectedListener {

    /**
     * 当前浏览图片的页面发生改变了
     *
     * @param position 页面所在索引
     */
    void onImagePageSelected(int position);
}
