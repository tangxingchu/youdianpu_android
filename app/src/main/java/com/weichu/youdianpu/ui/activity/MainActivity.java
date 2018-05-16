package com.weichu.youdianpu.ui.activity;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.weichu.youdianpu.R;
import com.weichu.youdianpu.ui.fragment.HomeFragment;
import com.weichu.youdianpu.ui.fragment.MyFragment;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    private HomeFragment mHomeFragment;
    private MyFragment mMyFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        this.mContext = this.getApplicationContext();
    }

    private void initUI() {
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_eighth, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_fifth, "Books"))
                .addItem(new BottomNavigationItem(R.drawable.ic_fourth, "Music"))
                .addItem(new BottomNavigationItem(R.drawable.ic_third, "Movies & TV"))
                .addItem(new BottomNavigationItem(R.drawable.ic_sixth, "Games"))
                .setFirstSelectedPosition(0)
                .initialise();


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
            fragmentTransaction.replace(R.id.fragmentContainer, mHomeFragment);
        }
        fragmentTransaction.commit();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (position) {
                    case 0:
                        if(mHomeFragment == null) {
                            mHomeFragment = new HomeFragment();
                        }
                        fragmentTransaction.replace(R.id.fragmentContainer, mHomeFragment);
                        break;
                    case 1:
                        if(mHomeFragment == null) {
                            mHomeFragment = new HomeFragment();
                        }
                        fragmentTransaction.replace(R.id.fragmentContainer, mHomeFragment);
                        break;
                    case 2:
                        if(mHomeFragment == null) {
                            mHomeFragment = new HomeFragment();
                            fragmentTransaction.replace(R.id.fragmentContainer, mHomeFragment);
                        }
                        break;
                    case 3:
                        if(mHomeFragment == null) {
                            mHomeFragment = new HomeFragment();
                        }
                        fragmentTransaction.replace(R.id.fragmentContainer, mHomeFragment);
                        break;
                    case 4:
                        if(mMyFragment == null) {
                            mMyFragment = new MyFragment();
                        }
                        fragmentTransaction.replace(R.id.fragmentContainer, mMyFragment);
                        break;
                }
                fragmentTransaction.commit();
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

}