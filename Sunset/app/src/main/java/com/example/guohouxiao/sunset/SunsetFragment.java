package com.example.guohouxiao.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by guohouxiao on 2017/7/1.
 */

public class SunsetFragment extends Fragment{

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = view;
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);

        //取出日落色彩资源
        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });

        return view;
    }

    private void startAnimation() {
        //获取视图的顶部坐标位置
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight();

        //模拟太阳的animator对象
        //新建ObjectAnimator一旦启动，就会以从0开始递增的参数值反复调用mSunView.setY(float)方法，直到调用mSunView.setY(1)为止
        //0-1区间参数值的确定过程称为interpolation，形成动画效果
        ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(mSunView, "y", sunYStart, sunYEnd)
                                                      .setDuration(3000);

        //添加加速特效
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        //模拟天空色彩变化的animator对象
        ObjectAnimator sunsetSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                                                         .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());//使用ArgbEvaluator使色彩变化自然

        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                                                        .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

/*        heightAnimator.start();
        sunsetSkyAnimator.start();*/

        //创建动画集
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        animatorSet.start();
    }
}