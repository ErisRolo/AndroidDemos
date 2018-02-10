package com.example.guohouxiao.mvvmtest;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.guohouxiao.mvvmtest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        ActivityMainBinding binding =
                DataBindingUtil.setContentView(this,R.layout.activity_main);
        final User user = new User();
        user.setUsername("Juhezi");
        user.setPassword("123456");
        binding.setUser(user);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Toast.makeText(MainActivity.this,user.getUsername() + " " +
                        user.getPassword(),Toast.LENGTH_SHORT).show();
                user.setUsername(user.getUsername() + "-debug");*/
                user.setVisible(!user.isVisible());

            }
        });
    }
}
