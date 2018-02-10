package com.example.guohouxiao.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by guohouxiao on 2017/4/16.
 */

public class CrimePagerActivity extends AppCompatActivity
    implements CrimeFragment.Callbacks{
    private static final String EXTRA_CRIME_ID = "com.example.guohouxiao.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    //为intent附加extra信息，传入crimeID，使CrimeFragment知道该显示哪个Crime
    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

        mCrimes = CrimeLab.get(this).getCrimes();
        //获取FragmentManager实例，用于创建FragmentStatePagerAdapter实例
        FragmentManager fragmentManager = getSupportFragmentManager();
        //设置adapter为FragmentStatePagerAdapter的一个匿名实例，FragmentStatePagerAdpater负责管理与ViewPager的对话并协同工作
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);//获取数据集中指定位置的Crime实例
                return CrimeFragment.newInstance(crime.getId());//利用该Crime实例的ID创建并返回一个有效配置的CrimeFragment
            }

            @Override
            //返回数组列表中包含的列表项数目
            public int getCount() {
                return mCrimes.size();
            }
        });

        //解决ViewPager默认只显示PageAdapter中的第一个列表项的问题
        //循环检查crime的ID，找到所选crime在数组中的索引位置
        for (int i = 0; i < mCrimes.size(); i++) {
            //如果Crime实例的mId与intent extra的crimeId相匹配
            if (mCrimes.get(i).getId().equals(crimeId)){
                //serCurrentItem()方法用来设置item初始显示的页面，要注意不是在数据适配器中设置，而是在完成数据适配后设置
                mViewPager.setCurrentItem(i);
                break;

            }
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
    }
}
