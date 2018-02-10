package com.example.who;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by guohouxiao on 2017/1/8.
 */

public class WhoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "WhoAdapter";

    //放数据
    public ArrayList<String> data = new ArrayList<String>();

    /**
     * 当创建ViewHolder的时候调用
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //要加载布局(R.layout.item_who)  XML -> View  ->  ViewHolder
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_who,parent,false);
        return new ViewHolder(view);
    }

    /**
     * 当绑定ViewHolder的时候调用，这个时候就要为ViewHolder设置内容
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //1.找到TextView  ->  holder
        //2.找到内容  ->  data.get(position)
        //3、setText()
        //向下转型
        ((ViewHolder)holder).mTvshow.setText(data.get(position));
    }

    /**
     * 获取item的个数 ->由ArrayList<String>的长度决定
     * 共有多少个字符串
     * @return\
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTvshow;

        /**
         * 代表每一个item，要有一个显示字符串的控件TextView，假设ViewHolder已经把item.xml加载上去了
         *(共有多少个字符串)
         * @param itemView
         */
        public ViewHolder(View itemView){
            super(itemView);
            mTvshow=(TextView) itemView.findViewById(R.id.tv_show);
        }
    }

}
