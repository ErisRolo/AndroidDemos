package com.example.guohouxiao.denglu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtLogin;
    private Button mBtRegister;

    private final static String USERNAME="ErisRolo";
    private final static String PASSWORD="123456";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEtUsername=(EditText) findViewById(R.id.et_username);
        mEtPassword=(EditText) findViewById(R.id.et_password);
        mBtLogin=(Button) findViewById(R.id.bt_login);
        mBtRegister=(Button) findViewById(R.id.bt_register);
        //监听事件，点击该按钮则执行onClick函数
        mBtLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String username=mEtUsername.getText()+"";
                String password=mEtPassword.getText()+"";
                username=username.trim();//字符串前后的空格去掉
                password=password.trim();
                if(username.equals(USERNAME)&&password.equals(PASSWORD)){
                    //可以登录
                    Intent intent = new Intent(MainActivity.this,HelloActivity.class);
                    intent.putExtra("Username",username);//键值对
                    startActivity(intent);
                    finish();//结束当前活动
                } else{
                    //不可以登录
                    Toast.makeText(MainActivity.this,"用户名或者密码不正确", Toast.LENGTH_SHORT).show();//Toast用来显示信息，显示时间有限
                }
            }
        });
    }
}
