package com.example.guohouxiao.sharedpreferencestest;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button saveData = (Button) findViewById(R.id.save_data);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //指定文件名并得到Editor对象
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                //添加数据
                editor.putString("name","Tom");
                editor.putInt("age",28);
                editor.putBoolean("married",false);
                //提交
                editor.apply();
            }
        });

        Button restoreData = (Button)findViewById(R.id.restore_data);
        restoreData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                String name = pref.getString("name","");
                int age = pref.getInt("age",0);
                boolean married = pref.getBoolean("married",false);
                Log.d("Mainactivity","name is " + name);
                Log.d("Mainactivity","age is " + age);
                Log.d("Mainactivity","married is " + married);
            }
        });
    }
}
