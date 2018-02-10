package com.example.guohouxiao.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.guohouxiao.criminalintent.Crime;
import com.example.guohouxiao.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by guohouxiao on 2017/4/21.
 * Cursor是个表数据处理工具，其任务是封装数据表中的原始字段值
 * 使用CursorWrapper可快速方便地创建Cursor子类，CursorWrapper能够封装一个个Cursor的对象，并允许在其上添加新的有用方法
 */

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        //获取数据表中的数据
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
