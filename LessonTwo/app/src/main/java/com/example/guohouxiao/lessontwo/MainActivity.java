package com.example.guohouxiao.lessontwo;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity";

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnSignIn;

    private final static String USERNAME = "juhezi";
    private final static String PASSWORD = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEtUsername=(EditText) findViewById(R.id.et_username);
        mEtPassword=(EditText) findViewById(R.id.et_password);
        mBtnSignIn=(Button) findViewById(R.id.btn_sign_in);
        String username= mEtUsername.getText() + "";
        String password= mEtPassword.getText() + "";
        mBtnSignIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String username=mEtUsername.getText() + "";
                String passwd =mEtPassword.getText() + "";
                username = username.trim();
                passwd = passwd.trim();
                if(username.equals(USERNAME)&&
                        passwd.equals(PASSWORD)){
                    //可以登录
                    Intent intent = new Intent(MainActivity.this,HelloActivity.class);
                    intent.putExtra("Username",username);//键值对
                    startActivity(intent);
                    finish();//结束当前Activity
                } else{
                    //不可以登录
                    Toast.makeText(MainActivity.this,"用户名或者密码不正确",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
