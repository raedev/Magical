package com.zhihu.matisse.sample;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * 拖拽的ImageView
 * Created by rae on 2020/4/29.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class MagicalImageView extends ImageViewTouch {

    private float mRawDownY;
    private float mRawDownScale = 1.0F;
    private float mDiffScrollY;
    private float mMoveRatio; // 移动比例[0,1]

    private final Runnable mMoveRunnable = new Runnable() {
        @Override
        public void run() {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mMoveRatio);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.setDuration(300);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = mMoveRatio - (float) animation.getAnimatedValue();
                    if (mOnDismissListener != null) {
                        mOnDismissListener.onImageViewSliding(value);
                    }
                }
            });
            valueAnimator.start();
        }
    };

    public interface onMagicalImageViewDismissListener {

        /**
         * 图片在滑动的时候触发
         *
         * @param ratio 滑动比例
         */
        void onImageViewSliding(float ratio);

        void onImageViewDismiss();
    }

    // 满足最小滑动Y坐标距离关闭图片
    private final int mMinDismissY = 400;

    private onMagicalImageViewDismissListener mOnDismissListener;

    public MagicalImageView(Context context) {
        super(context);
    }

    public MagicalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MagicalImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected GestureDetector.OnGestureListener getGestureListener() {
        return new MagicalImageViewGestureListener();
    }

    public void setOnDismissListener(onMagicalImageViewDismissListener dismissListener) {
        mOnDismissListener = dismissListener;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mDiffScrollY = e2.getRawY() - e1.getRawY();
        if (getScale() == 1f) {
            scrollBy(-distanceX, -distanceY);
            return true;
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mRawDownY = e.getRawY();
        mRawDownScale = getScale();
        return super.onDown(e);
    }


    @Override
    public boolean onUp(MotionEvent e) {
        super.onUp(e);
        // 检查是否满足关闭图片要求
        float diffY = mRawDownY - e.getRawY();
        if (diffY < 0 && Math.abs(diffY) > mMinDismissY) {
            onDismiss();
        } else {
            // 释放之后图片居中位置
            center(true, true);
            // 回调原来的移动比例
            animSourceLocationRatio();
        }


        return true;
    }

    /**
     * 动画回调原来比例
     */
    private void animSourceLocationRatio() {
        Log.i("rae", "最后的比例：" + mMoveRatio);
        removeCallbacks(mMoveRunnable);
        post(mMoveRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mMoveRunnable);
    }

    @Override
    public void scrollBy(float dx, float dy) {
        RectF rect = getBitmapRect();
        mScrollRect.set(dx, dy, 0, 0);
        updateRect(rect, mScrollRect);
        postTranslate(dx, dy);

        // 计算移动的缩放比例
        rect = getBitmapRect();
        float oldScale = getScale();
        float ratio = calcMoveRatio();
        // 移动的缩放比例最小是一半
        float scale = mRawDownScale - mRawDownScale * Math.min(ratio, 0.5F);
        float centerX = rect.centerX();
        float centerY = rect.centerY();
        postScale(scale / oldScale, centerX, centerY);

        if (mOnDismissListener != null) {
            mMoveRatio = ratio;
            mOnDismissListener.onImageViewSliding(ratio);
        }
    }

    /**
     * 计算移动时候的缩放比例
     */
    private float calcMoveRatio() {
        // 比例 =  滑动距离 / 半屏幕高度
        if (mDiffScrollY < 0) {
            // 向上滑动的时候不执行缩放
            return 0F;
        }
        float dy = Math.abs(mDiffScrollY);
        int height = getResources().getDisplayMetrics().heightPixels / 2; // 半屏高度
        float ratio = dy / height;

        return Math.max(0, Math.min(1, ratio)); //  范围到[0,1]
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        // 单击返回
        onDismiss();
        return super.onSingleTapConfirmed(e);
    }

    /**
     * 触发返回
     */
    protected void onDismiss() {
        if (mOnDismissListener != null) {
            mOnDismissListener.onImageViewDismiss();
        }
    }


    private class MagicalImageViewGestureListener extends ImageViewTouch.GestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = super.onFling(e1, e2, velocityX, velocityY);
            if (mScaleDetector.isInProgress()) return false; // 在缩放的过程不处理
            float diffY = e2.getY() - e1.getY(); // Y坐标距离
            // 处理快速滑动返回，满足下面的阻尼即可
            if (velocityY > 800 && diffY > MagicalImageView.this.mMinDismissY) {
                MagicalImageView.this.onDismiss();
            }
            return result;
        }


    }
}
