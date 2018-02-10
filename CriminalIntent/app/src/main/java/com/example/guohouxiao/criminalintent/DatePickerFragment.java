package com.example.guohouxiao.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by guohouxiao on 2017/4/17.
 */

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "com.example.guohouxiao.criminalintent.date";

    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;

    //newInstance()方法用来附加argument bundle给fragment
    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    //该方法用于创建AlertDialog，返回一个Dialog
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //获取DatePickerFragment的argument中保存的date数据
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        //创建一个calendar实例
        Calendar calendar = Calendar.getInstance();
        //用date数据配置calendar
        calendar.setTime(date);
        //获得整数形式的年月日
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date,null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        //用获得的年月日初始化DatePicker
        mDatePicker.init(year,month,day,null);

        return new AlertDialog.Builder(getActivity())//传入Context参数
                .setView(v)//添加DatePicker组件
                .setTitle(R.string.date_picker_title)//设置标题
/*                //对话框按钮有三种:positive，negative，neutral，决定按钮在对话框上显示的位置
                .setPositiveButton(android.R.string.ok,null)//设置一个OK按钮，第一个参数为字符串资源，第二个参数是实现DialogInterface.OnClickListener接口的对象*/
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        //用选定的新日期生成date数据
                        Date date = new GregorianCalendar(year,month,day).getTime();
                        //回传数据
                        sendResult(Activity.RESULT_OK,date);
                    }
                })
                .create();
    }

    //sendResult()方法用来回调目标fragment
    public void sendResult(int resultCode,Date date){

        if (getTargetFragment() == null){
            return;
        }

        //date数据作为extra附加给intent
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);

        //Fragment.onActivityResult()方法用来发送intent信息给目标fragment
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

}
