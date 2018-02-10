package com.example.guohouxiao.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Created by guohouxiao on 2017/4/15.
 * Finished on 2017/4/25.
 */

public class CrimeListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";//用来标记子标题状态

    private int itemPosition;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;//用来跟踪记录子标题状态

    private TextView mEmptyTextView;
    private Button mNewCriemButton;

    private Callbacks mCallbacks;

    /**
     * Required interfacec for hosting activities;
     */
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    //在fragment附加给activity时调用
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //将托管activity强转为Callbacks对象并赋值
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fragment.onCreatOptionsMenu()方法由FragmentManager调用
        //当activity接受到操作系统的onCreateOptionsMenu()方法回调请求时，应通知FragmentManager，其管理的fragment应接收调用指令
        //用setHasOptionMenu()方法进行这一操作
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        //RecyclerView通过LayoutManager对屏幕上的TextView（列表项）进行定位，LayoutManager还负责定义屏幕滚动行为
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//LinearLayoutManager类支持以竖直列表的形式展示列表项

        mEmptyTextView = (TextView) view.findViewById(R.id.empty_list_text_view);
        mNewCriemButton = (Button) view.findViewById(R.id.new_crime_button);
        mNewCriemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPad(getActivity())) {
                    Crime crime = new Crime();
                    CrimeLab.get(getActivity()).addCrime(crime);
                    updateUI();
                    mCallbacks.onCrimeSelected(crime);
                } else {
                    newCrime();
                }
            }
        });

        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);//保存子标题状态
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    //创建菜单
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);//也可以不调用超类的onCreateOptionsMenu()方法，但是调用的话，任何超类定义的选项菜单功能在子类方法中也能获得应用
        inflater.inflate(R.menu.fragment_crime_list,menu);//传入资源ID并填充到menu中

        //更新Show Subtitle菜单项
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_sbutitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    //响应菜单单项选择事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //根据菜单项的资源ID来确定选中的菜单项
            case R.id.menu_item_new_crime:
                if (isPad(getActivity())) {
                    Crime crime = new Crime();
                    CrimeLab.get(getActivity()).addCrime(crime);
/*                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                startActivity(intent);*/
                    updateUI();
                    mCallbacks.onCrimeSelected(crime);
                } else {
                    newCrime();
                }
                return true;//注意方法返回的是布尔值，因此完成菜单项事件处理时应返回true值表明全部任务已完成
            case R.id.menu_item_show_sbutitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();//在运行时更改菜单选项
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void newCrime(){
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
        startActivity(intent);
    }

    //用来响应新增菜单项的单击事件
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        //getString()方法接受字符串资源中占位符的替换值，用来产生子标题字符串
        //String subtitle = getString(R.string.subtitle_format,crimeCount);
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural,crimeCount,crimeCount);

        //实现菜单项标题与子标题的联动
        if (!mSubtitleVisible){
            subtitle = null;
        }

        //将托管CrimeListFragment的activity强制类型转换为AppCompatActivity
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        //设置工具栏子标题，显示crime数目
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (crimes.isEmpty()){
            mCrimeRecyclerView.setVisibility(View.GONE);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mNewCriemButton.setVisibility(View.VISIBLE);
        } else {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mEmptyTextView.setVisibility(View.GONE);
            mNewCriemButton.setVisibility(View.GONE);
        }

        //如果Adapter没配置好
        if (mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            //将Adapter和RecyclerView关联
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            //刷新crime显示
            mAdapter.setCrimes(crimes);

            //如果配置好就用notifyDataSetChanged()方法修改updateUI()方法
            mAdapter.notifyDataSetChanged();
/*            mAdapter.setCrimes(crimes);
            mAdapter.notifyItemChanged(itemPosition);//刷新一个Crime实例*/
        }

        updateSubtitle();
    }

    //ViewHolder内部类，用于容纳View视图
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Crime mCrime;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
        }

        public void bindCrime(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View v) {
/*            Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getId());//传递crime实例
            //itemPosition = mCrimeRecyclerView.getChildPosition(v);
            //itemPosition = mCrimeRecyclerView.getChildLayoutPosition(v);
            itemPosition = mCrimeRecyclerView.getChildAdapterPosition(v);//获取点击的item实例的位置
            startActivity(intent);*/
            itemPosition = mCrimeRecyclerView.getChildAdapterPosition(v);
            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    //Adapter内部类，创建必要的ViewHolder，绑定ViewHolder至模型层数据
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        //创建新的View视图来显示列表项
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
/*            LayoutInflater作用类似于findViewById，不同的是LayoutInflater用来查找并实例化xml布局文件，findViewById用来找xml布局文件下的具体widget控件
            对于一个没有被载入或者想要动态载入的界面，都需要使用LayoutInflater.inflate()来载入*/
            //首先用LayoutInflater.from()实例化一个LayoutInflater
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            //然后用LayoutInflater.inflate()方法创建View视图
            View view = layoutInflater.inflate(R.layout.list_item_crime,parent,false);
            //最后将View封装到ViewHolder中
            return new CrimeHolder(view);
        }

        @Override
        //把ViewHolder的View视图和模型层数据绑定起来
        public void onBindViewHolder(CrimeHolder holder, int position) {
            //通过ViewHolder和列表项在数据集中的索引位置找到显示的数据
            Crime crime = mCrimes.get(position);
/*            //将数据和View进行绑定（绑定就是使用模型数据填充视图）
            holder.mTitleTextView.setText(crime.getTitle());*/
            holder.bindCrime(crime);
        }

        @Override
        //获取数据数量
        public int getItemCount() {
            return mCrimes.size();
        }

        //setCrimes()方法用于后续刷新crime显示
        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }
}
