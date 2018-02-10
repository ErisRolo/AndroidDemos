package com.example.guohouxiao.criminalintent.database;

/**
 * Created by guohouxiao on 2017/4/20.
 */

public class CrimeDbSchema {
    //定义CrimeTable内部类，用来定义描述数据表元素的String常量
    public static final class CrimeTable {
        //定义表名
        public static final String NAME = "crimes";

        //定义数据表字段
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
