package com.example.guohouxiao.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.guohouxiao.criminalintent.database.CrimeBaseHelper;
import com.example.guohouxiao.criminalintent.database.CrimeCursorWrapper;
import com.example.guohouxiao.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by guohouxiao on 2017/4/15.
 * 创建一个单例，类中带有私有构造方法和get()方法，确保仅拥有一份数据，方便控制层类间的数据传递
 * CrimeLab作为一个数据集中存储池，用来存储Crime对象
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;

    //private List<Crime> mCrimes;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if (sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    //私有构造方法使其他类无法创建CrimeLab对象，因为其他类无法调用get()方法
    private CrimeLab(Context context){
        mContext = context.getApplicationContext();//将Context设为应用的上下文（场景），则生命周期是整个应用，应用摧毁，它才摧毁
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();//打开一个可写的数据库
/*        mCrimes = new ArrayList<>();//用一个空的List保存Crime对象*/
/*        //生成100个crime
        for (int i = 0; i <100 ; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0 );
            mCrimes.add(crime);
        }*/
    }

    public void addCrime(Crime c){
        //mCrimes.add(c);
        ContentValues values = getContentValues(c);
        //插入记录，insert()方法第一个参数为数据库表名，第二个为nullColumnHack，第三个为写入的数据
        //关于nullColumnHack的作用
        //为避免values为空导致insert()调用失败的情况，指定一个列名，如果发现要插入的行为空时，就会将你指定的这个列名的值设为null，然后再向数据库中插入
        mDatabase.insert(CrimeTable.NAME ,null ,values);
    }

    public void deleteCrime(Crime crime){
        //mCrimes.remove(c);
        String uuidString = crime.getId().toString();
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID + " = ? ",new String[] { uuidString });
    }

    //返回数组列表
    //mCrimes已废弃不用，此时getCrimes()方法返回的List<Crime>是Crime对象的快照
    //如果要刷新CrimeListActivity界面，首先要更新这个快照
    public List<Crime> getCrimes(){
        //return mCrimes;
        //return new ArrayList<>();
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            //cursor内部总是指向查询的某个地方，moveToFirst()方法移动光标到第一行，即指向第一个元素
            cursor.moveToFirst();
            //isAfterLast()判断是否已指向最后一个元素
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();//读取下一行记录
            }
        } finally {
            cursor.close();//注意最后一定要调用close()方法
        }

        return crimes;

    }

    //返回带有指定ID的Crime对象
    public Crime getCrime(UUID id){
/*        for (Crime crime: mCrimes){
            if (crime.getId().equals(id)){
                return crime;
            }
        }*/
        //return null;

        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[]{ id.toString() }
        );

        try {
            //如果Cursor中的行数（元素个数）为0
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    //返回指向某个具体位置的File对象
    public File getPhotoFile(Crime crime) {
        //getExternalFilesDir()方法获取主外部存储上存放常规文件的文件目录
        //通过String参数，可访问特定内容类型的子目录
        //Environment.DIRECTORY_PICTURES 图像文件目录
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //确认外部存储是否可用
        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFilename());
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        //更新记录，update()方法第一个参数为表名，第二个参数为数据，第三个参数为where子句，第四个参数用来指定where子句中的参数值(String[]数组参数)
        //最后两个参数用来确定更新哪些记录，注意不能直接在where子句中放入uuidString
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?",new String[]{ uuidString });
    }

    //ContentValues类是一个键值存储类，用来处理数据库写入和更新操作
    //创建ContentValues实例，将Crime记录转换为ContentValues
    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        //ContentValues的键是数据表字段，除了_id由数据库自动创建，其他数据表字段都要编码指定
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());

        return values;
    }

    //读取数据库的方法
    //private Cursor queryCrimes(String whereClause,String[] whereArgs){
    //使用cursor封装方法，让queryCrimes()方法返回CrimeCursorWrapper对象
    private CrimeCursorWrapper queryCrimes(String whereClause,String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,//Columns - null selects all columns
                whereClause,
                whereArgs,
                null,//groupBy
                null,//having
                null//orderBy
        );
       // return cursor;
        return new CrimeCursorWrapper(cursor);
    }

}
