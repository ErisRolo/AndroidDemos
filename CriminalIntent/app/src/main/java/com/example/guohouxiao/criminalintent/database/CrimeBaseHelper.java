package com.example.guohouxiao.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.guohouxiao.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by guohouxiao on 2017/4/20.
 * SQLiteOpenHelper类提供创建初始数据库的方法
 */
public class CrimeBaseHelper extends SQLiteOpenHelper{
    private static final int VERSION = 1;//版本号
    private static final String DATABASE_NAME = "crimeBase.db";//数据库名

    public CrimeBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库的表，要注意格式，创建表字段时，不需要制定表字段类型
        db.execSQL("create table " + CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED + ", " +
                CrimeTable.Cols.SUSPECT +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
