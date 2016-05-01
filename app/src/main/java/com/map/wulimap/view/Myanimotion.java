package com.map.wulimap.view;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;

/**
 * 动画
 */
public class Myanimotion {

    // 入动画
    public static void StartAnimationIN(ViewGroup viewGroup, int duration) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setVisibility(View.VISIBLE);
            viewGroup.getChildAt(i).setClickable(true);// 可以点击
            viewGroup.getChildAt(i).setFocusable(true);// 可以获得焦点
        }

        Animation animation;
        /*
         * fromDegrees 初始角度 toDegrees 结束角度 pivotXType x轴的参照物 pivotXValue
		 * 参照x轴的参照物 的哪个位置进行旋转 pivotYType Y轴的参照物 pivotYValue 参照Y轴的参照物 的哪个位置进行旋转
		 */
        animation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        animation.setFillAfter(true);// 停留在 动画运行结束的位置
        animation.setDuration(duration);// 动画运行时间

        viewGroup.startAnimation(animation);
    }

    // 出动画
    public static void StartAnimationOUT(final ViewGroup viewGroup, int duration, int startOffSet) {
        Animation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        animation.setFillAfter(true);// 停留在 动画运行结束的位置
        animation.setDuration(duration);// 动画运行时间
        animation.setStartOffset(startOffSet);// 动画延迟启动时间
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    viewGroup.getChildAt(i).setVisibility(View.GONE);
                    viewGroup.getChildAt(i).setClickable(false);// 不可以点击
                    viewGroup.getChildAt(i).setFocusable(false);// 不可以获得焦点

                }

                viewGroup.setVisibility(View.GONE);
            }
        });

        viewGroup.startAnimation(animation);
    }

}
