package com.jerry.moneyapp.util;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.jerry.moneyapp.R;

public class AnimUtil {

    private static int sAnimationTime = 300;

    private AnimUtil() {
    }

    public static void overridePendingTransition(Activity context) {
        context.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    public static Animation getListItemDismissAnim(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.listitem_dismiss_anim);
    }

    /**
     * @return 1s闪烁
     */
    public static AlphaAnimation getBlinkAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(500);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        return alphaAnimation;
    }

    /**
     * 放大还原控件
     *
     * @param factor 倍数
     * @param mills 时间
     */
    public static void animateZoomIn(final View v, float factor, int mills) {
        ScaleAnimation sa = new ScaleAnimation(1, factor, 1, factor, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(mills);
        v.startAnimation(sa);
    }

    public static void animateCenterRotate(View view, int fromDegree, int toDegree, int mills) {
        RotateAnimation animation = new RotateAnimation(fromDegree, toDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(mills);
        view.startAnimation(animation);
    }

    /**
     * 缩小动画
     */
    public static void animateZoomOut(final View v, float factor, int mills) {
        ScaleAnimation sa = new ScaleAnimation(factor, 1, factor, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(mills);
        v.startAnimation(sa);
    }

    /**
     * 收起view
     */
    public static void animateCollapsing(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createHeightAnimator(view, origHeight, 0);
        animator.setDuration(sAnimationTime);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(final Animator animation) {

            }

            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });
        animator.start();
    }

    /**
     * 展开view
     */
    public static void animateExpanding(final View view) {
        view.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        ValueAnimator animator = createHeightAnimator(view, 0, view.getMeasuredHeight());
        animator.setDuration(sAnimationTime);
        animator.start();
    }

    public static void animateExpanding(final View view, int time) {
        sAnimationTime = time;
        animateExpanding(view);
    }

    public static void animateCollapsing(final View view, int time) {
        sAnimationTime = time;
        animateCollapsing(view);
    }

    /**
     * 监听动画过程--高度
     */
    private static ValueAnimator createHeightAnimator(final View view, int start, int end) {
        //约束布局中，0相当于match_parent,不影响动画效果
        ValueAnimator animator = ValueAnimator.ofInt(start == 0 ? 1 : start, end == 0 ? 1 : end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = value;
                view.setLayoutParams(params);
            }
        });
        return animator;
    }

    /**
     * 多了设置时间参数
     *
     * @param time 动画时间
     */
    public static void animateAlpha(final View view, float fromAlpha, float toAlpha, final int afterVisibility, int time) {
        sAnimationTime = time;
        animateAlpha(view, fromAlpha, toAlpha, afterVisibility);
    }

    /**
     * 透明动画
     *
     * @param view 要使用动画的view
     * @param fromAlpha 开始透明度
     * @param toAlpha 最后的透明度
     * @param visibility 是否显示
     */
    private static void animateAlpha(final View view, float fromAlpha, float toAlpha, final int visibility) {
        PropertyValuesHolder pvh = PropertyValuesHolder.ofFloat("alpha", fromAlpha, toAlpha);
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(pvh);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (visibility == View.VISIBLE) {
                    view.setVisibility(visibility);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (visibility == View.GONE) {
                    view.setVisibility(visibility);
                }
            }

            @Override
            public void onAnimationCancel(final Animator animation) {

            }

            @Override
            public void onAnimationRepeat(final Animator animation) {

            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float alphaValue = (float) valueAnimator.getAnimatedValue();
                view.setAlpha(alphaValue);
            }
        });

        animator.setDuration(sAnimationTime);
        animator.start();
    }
}
