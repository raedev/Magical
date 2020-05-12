package com.zhihu.matisse.internal.ui.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * 拖拽的ImageView
 * Created by rae on 2020/4/29.
 * Copyright (c) https://github.com/raedev All rights reserved.
 */
public class MagicalImageView extends ImageViewTouch {

    public interface onMagicalImageViewDismissListener {

        /**
         * 图片在滑动的时候触发
         *
         * @param ratio 滑动比例
         */
        void onImageViewSliding(float ratio);

        void onImageViewDismiss();

    }

    private final Runnable mMoveRunnable = new Runnable() {
        @Override
        public void run() {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mMoveRatio);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.setDuration(200);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    float diffValue = mMoveRatio - value;
                    if (mOnDismissListener != null) {
                        mOnDismissListener.onImageViewSliding(diffValue);
                    }

                    if (value >= diffValue) {
                        mMoveRatio = 0;
                    }

                }
            });
            valueAnimator.start();
        }
    };
    private static final String TAG = "RAE";
    private float mRawDownY;
    private float mRawDownScale = 1.0F;
    private float mDiffScrollY;
    private float mMoveRatio; // 移动比例[0,1]
    private boolean mIsInProgress;

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
    protected void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);

    }

    public void setOnDismissListener(onMagicalImageViewDismissListener dismissListener) {
        mOnDismissListener = dismissListener;
    }

    @Override
    protected float onDoubleTapPost(float scale, float maxZoom) {
        if (mDoubleTapDirection == 1) {
            mDoubleTapDirection = -1;
            return scale + mScaleFactor;
        } else {
            mDoubleTapDirection = 1;
            return 1f;
        }
    }

    @Override
    public boolean canScroll(int direction) {
        boolean result = super.canScroll(direction);
        return result && !canMoveDismiss();
    }

    /**
     * 是否可以移动关闭
     */
    protected boolean canMoveDismiss() {
        if (getScale() > 1.5) return false;
        return !mIsInProgress || !mScaleDetector.isInProgress();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 移动过程只需要确定一次是有缩放，就认为是处于缩放状态
        if (event.getAction() == MotionEvent.ACTION_MOVE && !mIsInProgress && event.getPointerCount() > 1) {
            mIsInProgress = true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mMoveRatio = 0;
        mRawDownY = e.getRawY();
        mRawDownScale = getScale();
        removeCallbacks(mMoveRunnable);
        return super.onDown(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mDiffScrollY = e2.getRawY() - e1.getRawY();
        // 正常模式下Y轴的移动幅度大于10认为是在做图片移动操作
        if (getScale() == 1f && Math.abs(mDiffScrollY) > 20) {
            scrollBy(-distanceX, -distanceY);
            return true;
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
            mUserScaled = true;
            scrollBy(diffX * 2, diffY * 2, 400);
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public boolean onUp(MotionEvent e) {
        // 缩放模式下不处理
        if (mIsInProgress) {
            mIsInProgress = false;
            if (mOnDismissListener != null) {
                mOnDismissListener.onImageViewSliding(0);
            }
            return super.onUp(e);
        }
        float diffY = mRawDownY - e.getRawY();
        // 检查是否满足关闭图片要求
        if (canMoveDismiss() && diffY < 0 && Math.abs(diffY) > 460) {
            onDismiss();
        } else if (getScale() < getMinScale()) {
            zoomTo(getMinScale(), 300); // 超过最小缩放比例
            animSourceLocationRatio();// 回调原来的移动比例
        } else {
            // 释放之后图片居中位置
            center(true, true);
            // 回调原来的移动比例
            animSourceLocationRatio();
        }
        mIsInProgress = false;
        return true;
    }

    /**
     * 动画回调原来比例
     */
    private void animSourceLocationRatio() {
        if (mMoveRatio > 0) {
            Log.d(TAG, "动画位置：" + mMoveRatio);
            removeCallbacks(mMoveRunnable);
            post(mMoveRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mMoveRunnable);
    }

    @Override
    public void scrollBy(float dx, float dy) {
        if (!canMoveDismiss()) {
            super.scrollBy(dx, dy);
            return;
        }
        RectF rect = getBitmapRect();
        if (rect == null) return;
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
    protected float calcMoveRatio() {
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
}
