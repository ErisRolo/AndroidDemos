package com.example.guohouxiao.bottomnavigationviewdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private HomeFragment mHomeFragment;
    private FindFragment mFindFragment;
    private NotificationFragment mNotificationFragment;
    private MyFragment mMyFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        try {
            Field shiftMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftMode.setAccessible(true);
            shiftMode.setBoolean(menuView, false);
            shiftMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                itemView.setShiftingMode(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < menuView.getChildCount(); i++) {
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
            itemView.setShiftingMode(false);
            itemView.setChecked(itemView.getItemData().isChecked());
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        mHomeFragment = new HomeFragment();
                        createFragment(mHomeFragment);
                        break;
                    case R.id.find:
                        bottomNavigationView.getMenu().getItem(0).setChecked(false);
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        mFindFragment = new FindFragment();
                        createFragment(mFindFragment);
                        break;
                    case R.id.add:
                        startActivity(new Intent(MainActivity.this,AddActivity.class));
                        break;
                    case R.id.notification:
                        bottomNavigationView.getMenu().getItem(0).setChecked(false);
                        bottomNavigationView.getMenu().getItem(3).setChecked(true);
                        mNotificationFragment = new NotificationFragment();
                        createFragment(mNotificationFragment);
                        break;
                    case R.id.my:
                        bottomNavigationView.getMenu().getItem(0).setChecked(false);
                        bottomNavigationView.getMenu().getItem(4).setChecked(true);
                        mMyFragment = new MyFragment();
                        createFragment(mMyFragment);
                        break;
                }
                return false;
            }
        });
    }

    private void createFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

}
