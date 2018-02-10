package com.liyi.loadmorelistview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/12.
 */
public class LoadMoreListViewAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<String> list;
    private int currPosition;

    public LoadMoreListViewAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.itemText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(position==currPosition){
            holder.textView.setBackgroundResource(R.color.colorAccent);
        }else {
            holder.textView.setBackgroundResource(Color.alpha(0));
        }


        holder.textView.setText(list.get(position));

        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }

    void add(String str) {
        list.add(str);
        notifyDataSetChanged();
    }

    void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void setCurrPosition(int currPosition){
        this.currPosition=currPosition;
        notifyDataSetChanged();
    }


}
