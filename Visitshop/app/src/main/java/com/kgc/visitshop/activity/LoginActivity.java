package com.kgc.visitshop.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.gson.Gson;
import com.kgc.visitshop.MainActivity;
import com.kgc.visitshop.R;
import com.kgc.visitshop.bean.LoginBeanResult;
import com.kgc.visitshop.bean.User;
import com.kgc.visitshop.net.OkHttpManager;
import com.kgc.visitshop.utils.Constant;
import com.kgc.visitshop.utils.SharePreUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {

    private EditText mEtName;
    private EditText mEtPassword;
    private TextInputLayout mEtName_design;
    private TextInputLayout mEtPassword_design;
    private Button mBtnLogin;
    private String mUserName;
    private String mPassWord;
    private RelativeLayout mRelLoading;//加载等待界面
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
		//判断是否已经登录，如果登陆过直接进去主界面
        if (checkLogin()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
			//没有登录，留在登录界面
            initStatusBarColor();
            bindViews();
            initListener();
        }
    }

    /**
     * 检查是否登录
     *
     * @return
     */
    private boolean checkLogin() {
        //获取数据库用户
        List<User> list = DataSupport.findAll(User.class);
        if (null != list && list.size() > 0) {
			//数据库user表不为空，已经登录，返回true
            return true;
        }
        return false;
    }

    /**
     * 初始化控件
     */
    private void bindViews() {
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mEtName_design = (TextInputLayout) findViewById(R.id.et_name_design);
        mEtPassword_design = (TextInputLayout) findViewById(R.id.et_password_design);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mRelLoading = (RelativeLayout) findViewById(R.id.loading);

        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
				//数据框数据变化，去掉错误提示
                mEtName_design.setErrorEnabled(false);
            }
        });
        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
				//数据框数据变化，去掉错误提示
                mEtPassword_design.setErrorEnabled(false);
            }
        });
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				//登录按钮单击登录
                login();
            }
        });
    }

    /**
     * 登录
     */
    private void login() {
		//校验数据是否为空等，如果数据不和要求直接提示，避免发送错误数据到后台
        if (checkData()) {
			//显示登录加载界面
            mRelLoading.setVisibility(View.VISIBLE);
            //发送登录请求（GET方式）
//            OkHttpManager.getInstance().getNet(Constant.Login
//                            + "?userid=" + mUserName + "&password=" + mPassWord,
//                    new OkHttpManager.ResultCallback() {
//                        @Override
//                        public void onFailed(Request request, IOException e) {
//                            mRelLoading.setVisibility(View.GONE);
//                            Toast.makeText(getApplicationContext(), "服务连接异常，登录失败", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onSuccess(String response) {
//                            LoginBeanResult loginBeanResult = getDataFromJson(response);
//                            mRelLoading.setVisibility(View.GONE);
//                            if (loginBeanResult.getCode() == 0) {
//                                //用户存在，密码正确，登录成功,先保存登录信息，然后跳转至主界面
//                                SharePreUtil.SetShareString(mContext, "userid", loginBeanResult.getBody().getUserid());
//                                //先清除数据库
//                                DataSupport.deleteAll(User.class);
//                                User user = new User();
//                                user.setUserId(loginBeanResult.getBody().getUserid());
//                                user.setNickName(loginBeanResult.getBody().getNickname());
//                                user.setSex(loginBeanResult.getBody().getSex());
//                                user.setJob(loginBeanResult.getBody().getJob());
//                                user.setArea(loginBeanResult.getBody().getArea());
//                                user.setPhoneNum(loginBeanResult.getBody().getPhonenum());
//                                user.setImg(loginBeanResult.getBody().getImg());
//                                user.save();
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                finish();
//                            } else {
//                                Toast.makeText(getApplicationContext(), "用户名或密码错误，登录失败", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
            //发送登录请求（POST方式）
            OkHttpManager.getInstance().postNet(Constant.Login, new OkHttpManager.ResultCallback() {
                @Override
                public void onFailed(Request request, IOException e) {
					//登录失败，去掉加载界面，提示错误信息
                    mRelLoading.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "服务连接异常，登录失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String response) {
				    //解析服务端返回数据为java对象
                    LoginBeanResult loginBeanResult = getDataFromJson(response);
                    mRelLoading.setVisibility(View.GONE);
                    if (loginBeanResult.getCode() == 0) {
                        //用户存在，密码正确，登录成功,先保存登录信息，然后跳转至主界面
                        SharePreUtil.SetShareString(mContext, "userid", loginBeanResult.getBody().getUserid());
                        //先清除数据库
                        DataSupport.deleteAll(User.class);
                        User user = new User();
                        user.setUserId(loginBeanResult.getBody().getUserid());
                        user.setNickName(loginBeanResult.getBody().getNickname());
                        user.setSex(loginBeanResult.getBody().getSex());
                        user.setJob(loginBeanResult.getBody().getJob());
                        user.setArea(loginBeanResult.getBody().getArea());
                        user.setPhoneNum(loginBeanResult.getBody().getPhonenum());
                        user.setImg(loginBeanResult.getBody().getImg());
						//保存数据到数据库中
                        user.save();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "用户名或密码错误，登录失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new OkHttpManager.Param("userid", mUserName), new OkHttpManager.Param("password", mPassWord));
        }
    }

    /**
     * 检查登录数据是否合法
     */
    private boolean checkData() {
        mUserName = mEtName.getText().toString().trim();
        mPassWord = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mUserName.trim())) {
            mEtName_design.setError("用户名不能为空");
            return false;
        }
        if (mUserName.trim().length() < 0 || mUserName.trim().length() > 6) {
            mEtName_design.setError("请输入6位数以内的用户名");
            return false;
        }
        if (TextUtils.isEmpty(mPassWord)) {
            mEtPassword_design.setError("密码不能为空");
            return false;
        }
        return true;
    }

    /**
     * 初始化状态栏颜色透明，和背景色一致
     */
    private void initStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 返回数据组装
     *
     * @return
     */
    private LoginBeanResult getDataFromJson(String strResult) {
        Gson gson = new Gson();
        LoginBeanResult loginBeanResult = gson.fromJson(strResult, LoginBeanResult.class);
        return loginBeanResult;
    }


}
