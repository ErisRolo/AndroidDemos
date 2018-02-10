package com.liyi.loadmorelistview;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class MyChildActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{

    private Toolbar myToolBar;
    private CoordinatorLayout frameLayout;
    private FloatingActionButton floatingActionButton;

    private TextInputLayout passwordTextInputLayout;

    private BottomNavigationBar bottomNavigationBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_child);

        myToolBar= (Toolbar) findViewById(R.id.myToolBar);

        setSupportActionBar(myToolBar);

        ActionBar actionBar=getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);



        frameLayout= (CoordinatorLayout) findViewById(R.id.frameLayout);

        floatingActionButton= (FloatingActionButton) findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "floatingActionButton", Snackbar.LENGTH_LONG).setActionTextColor(Color.BLUE).setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        });


        passwordTextInputLayout= (TextInputLayout) findViewById(R.id.passwordTextInputLayout);

        passwordTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(passwordTextInputLayout.getEditText().getText().toString().length()<6){
                    passwordTextInputLayout.setErrorEnabled(true);
                    passwordTextInputLayout.setError("密码长度小于6位");
                }else {
                    passwordTextInputLayout.setErrorEnabled(false);
                }
            }
        });



        initBottomNavigationBar();

        initCardVIew();
    }

    private void initCardVIew() {
        findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initBottomNavigationBar() {
        BadgeItem badgeItem=new BadgeItem().setBorderWidth(1).setBackgroundColorResource(R.color.colorGreen).setText("2").setHideOnSelect(true);

        bottomNavigationBar= (BottomNavigationBar) findViewById(R.id.bottomNavigationBar);
        bottomNavigationBar.setFab(floatingActionButton);
        bottomNavigationBar.clearAll();

        bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.ic_favorite_white_24dp,"喜欢").setActiveColorResource(R.color.colorYellow).setBadgeItem(badgeItem))
        .addItem(new BottomNavigationItem(R.drawable.ic_find_replace_white_24dp,"发现").setActiveColorResource(R.color.colorRed))
                .addItem(new BottomNavigationItem(R.drawable.ic_location_on_white_24dp, "位置").setActiveColorResource(R.color.colorGreen))
        .setFirstSelectedPosition(0)
        .initialise();

        bottomNavigationBar.setTabSelectedListener(this);

    }


    @Override
    public void onTabSelected(int position) {

    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}
