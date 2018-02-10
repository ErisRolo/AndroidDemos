package com.example.guohouxiao.criminalintent;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by guohouxiao on 2017/4/15.
 * 代码复用
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    //该抽象方法用于实例化新的fragment，SingleFragmentActivity的子类会实现该方法，来返回由activity托管的fragment实例
    protected abstract Fragment createFragment();

    @LayoutRes
    //LayoutRes注解表示任何时候该实现方法都应该返回有效的布局资源ID
    //增加SingleFragmentActivity类的灵活性
    //如果不想使用固定不变的activity_fragment.xml布局，它的子类可以选择覆盖这个方法返回所需布局
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_fragment);//从activity_fragment.xml布局里实例化activity视图*/
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();//获得FragmentManager，因为用的V4库，所以用Support的方法
        //在容器中查找FragmentManager里的fragment
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);//使用容器视图资源ID从FragmentManager中获取CrimeFragment
        //如果找不到，新建fragment并将其添加到容器中
        if (fragment == null){
            fragment = createFragment();
            //开启事务回退栈，创建一个新的fragment事务
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)//加入一个添加操作，第一个参数为容器视图资源ID，第二个参数为fragment
                    .commit();
        }
    }
}
