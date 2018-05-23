package com.weichu.youdianpu.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.weichu.youdianpu.R;
import com.weichu.youdianpu.ui.fragment.HomeFragment;
import com.weichu.youdianpu.ui.fragment.MineFragment;
import com.weichu.youdianpu.ui.fragment.NearbyFragment;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String HOME_FRAGMENT = "homeFragment";
    public static final String NEARBY_FRAGMENT = "nearbyFragment";
    public static final String MINE_FRAGMENT = "mineFragment";
    private LocationListener mLocationListener = new LocationListener();
    private LocationClient mLocationClient;
    private HomeFragment mHomeFragment;
    private NearbyFragment mNearbyFragment;
    private MineFragment mMineFragment;
    private static final int REQUESTCODE_MAP = 1;
    private double lon;
    private double lat;

    private int mBNBPostion = 0;//默认是选中首页
    private Stack<Fragment> mStack = new Stack<Fragment>();
    private LocatedCity mLocatedCity;
    private FloatingActionButton mFaButton;
    private DrawerLayout mDrawerLayout;

    private IProfile profile = null;//登录用户信息
    private Drawer result = null;//嵌套抽屉
    private AccountHeader headerResult = null;//head头布局
    private Toolbar mToolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(savedInstanceState != null) {
            //当activity被系统销毁，获取到之前的fragment，并且移除之前的fragment的状态
            mHomeFragment = (HomeFragment) fragmentManager.findFragmentByTag(HOME_FRAGMENT);
            mNearbyFragment = (NearbyFragment) fragmentManager.findFragmentByTag(NEARBY_FRAGMENT);
            mMineFragment = (MineFragment) fragmentManager.findFragmentByTag(MINE_FRAGMENT);
//            fragmentManager.beginTransaction().remove(mHomeFragment).commit();
//            fragmentManager.beginTransaction().remove(mNearbyFragment).commit();
//            fragmentManager.beginTransaction().remove(mMineFragment).commit();
            mBNBPostion = savedInstanceState.getInt("BNBPostion", 0);
            switch (mBNBPostion) {
                case 0:
                    mStack.push(mHomeFragment);
                    break;
                case 1:
                    mStack.push(mNearbyFragment);
                    break;
                case 4:
                    mStack.push(mMineFragment);
                    break;
            }
        }
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        super.setSupportActionBar(mToolbar);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerLayout);
        profile = getUserProfile();
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withTranslucentStatusBar(true) //半透明效果
                .addProfiles(profile)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        switch ((int) profile.getIdentifier()) {
                            case 101://根据不同标识符监听不同对象
                                Toast.makeText(MainActivity.this, "头像被点击", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();//至此头布局head构建完成
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .build();
//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
//        }

        mFaButton = (FloatingActionButton) findViewById(R.id.main_fab);
        //百度定位
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(mLocationListener);
        initUI();
        List<String> permissonList = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissonList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissonList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissonList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissonList.isEmpty()) {
            String[] permissons = permissonList.toArray(new String[permissonList.size()]);
            ActivityCompat.requestPermissions(this, permissons, REQUESTCODE_MAP);
        } else {
            //请求位置信息
            requestLocation();
        }

    }

    private void requestLocation() {
        LocationClientOption locationClientOption = new LocationClientOption();
//        locationClientOption.setScanSpan(1000 * 60 * 2);//2分钟定位1次
//        locationClientOption.setScanSpan(1000);//1秒定位1次
        locationClientOption.setOpenGps(true);
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setIsNeedAddress(true);
//        locationClientOption.setIsNeedAltitude(true);
//        locationClientOption.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(locationClientOption);
        mLocationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUESTCODE_MAP:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "同意位置权限能够享受更好的体验", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "申请权限出现了错误!", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private IProfile<ProfileDrawerItem> getUserProfile() {
        String userLogin = getSharedPreferences("login", 0)
                .getString("user", getResources().getString(R.string.txt_anonymous));

        return new ProfileDrawerItem()
                .withName(userLogin)
                .withIcon(R.drawable.img_home)
                .withIdentifier(101);
    }

    private void initUI() {
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home, "首页"))
                .addItem(new BottomNavigationItem(R.drawable.ic_nearby, "附近"))
                .addItem(new BottomNavigationItem(R.drawable.ic_shopping_cart, "购物袋"))
                .addItem(new BottomNavigationItem(R.drawable.ic_order, "订单"))
//                .addItem(new BottomNavigationItem(R.drawable.ic_my, "我的"))
                .setFirstSelectedPosition(mBNBPostion)
                //.setMode(BottomNavigationBar.MODE_FIXED)
                .setMode(BottomNavigationBar.MODE_SHIFTING)
                //MODE_SHIFTING模式 默认使用 BACKGROUND_STYLE_RIPPLE
                //mode设为MODE_FIXED，默认使用BACKGROUND_STYLE_STATIC
                //in-active color：图标和文本未被激活或选中的颜色；默认颜色为Theme’s Primary Color
                //active color : 在BACKGROUND_STYLE_STATIC下，为图标和文本激活或选中的颜色；在BACKGROUND_STYLE_RIPPLE下，为整个控件的背景颜色；默认颜色为Color.LTGRAY
                //background color :在BACKGROUND_STYLE_STATIC 下，为整个空控件的背景色；在 BACKGROUND_STYLE_RIPPLE 下为图标和文本被激活或选中的颜色；默认颜色为Color.WHITE
                //.setBarBackgroundColor(android.R.color.white)
                .setBarBackgroundColor(R.color.colorPrimary)
                .setInActiveColor(R.color.colorPrimaryText)
                .setActiveColor(android.R.color.white)
                //.setActiveColor(R.color.colorPrimary)
                .initialise();

//        bottomNavigationBar.per
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
            fragmentTransaction.add(R.id.fragmentContainer, mHomeFragment, HOME_FRAGMENT);
            mStack.push(mHomeFragment);
        }
        fragmentTransaction.commit();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position) {

                switch (position) {
                    case 0:
                        if (mHomeFragment == null) {
                            mHomeFragment = new HomeFragment();
                        }
                        addFragment(mHomeFragment, HOME_FRAGMENT);
                        break;
                    case 1:
                        if (mNearbyFragment == null) {
                            mNearbyFragment = new NearbyFragment();
                        }
                        addFragment(mNearbyFragment, NEARBY_FRAGMENT);
                        break;
                    case 2:
//                        if (mHomeFragment == null) {
//                            mHomeFragment = new HomeFragment();
//                        }
                        break;
                    case 3:
//                        if (mHomeFragment == null) {
//                            mHomeFragment = new HomeFragment();
//                        }
                        break;
                    case 4:
                        if (mMineFragment == null) {
                            mMineFragment = new MineFragment();
                        }
                        addFragment(mMineFragment, MINE_FRAGMENT);
                        break;
                }
                mBNBPostion = position;
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

    }

    private void hideAllFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (mHomeFragment != null && !mHomeFragment.isHidden()) {
            fragmentTransaction.hide(mHomeFragment);
        }
        if (mNearbyFragment != null && !mNearbyFragment.isHidden()) {
            fragmentTransaction.hide(mNearbyFragment);
        }
        if (mMineFragment != null && !mMineFragment.isHidden()) {
            fragmentTransaction.hide(mMineFragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void addFragment(Fragment fragment, String tag) {
        Fragment preFragment = mStack.pop();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!fragment.isAdded() && null == fragmentManager.findFragmentByTag(tag)) {
            fragmentManager.executePendingTransactions();
            fragmentTransaction.hide(preFragment).add(R.id.fragmentContainer, fragment, tag);
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            fragmentTransaction.hide(preFragment).show(fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
        mStack.push(fragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.title_city:
//                Toast.makeText(this, "aaa", Toast.LENGTH_SHORT).show();
//                break;
        }
    }

    public void cityTVClick(View view) {
        CityPicker.getInstance()
                .setFragmentManager(getSupportFragmentManager())
                .enableAnimation(true)
                .setLocatedCity(mLocatedCity)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_toolbar:

                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    public class LocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            lon = bdLocation.getLongitude();
            lat = bdLocation.getLatitude();
            Log.i("MainActivity", "经度:" + bdLocation.getLongitude());
            Log.i("MainActivity", "纬度:" + bdLocation.getLatitude());
            if (BDLocation.TypeGpsLocation == bdLocation.getLocType()) {
                Log.i("MainActivity", "定位方式:GPS");
            } else if (BDLocation.TypeNetWorkLocation == bdLocation.getLocType()) {
                Log.i("MainActivity", "定位方式:网络");
            }
            Log.i("MainActivity", "城市:" + bdLocation.getCity() + bdLocation.getCityCode());
            Log.i("MainActivity", "区 :" + bdLocation.getDistrict());
            Log.i("MainActivity", "街道 :" + bdLocation.getStreet());
            Log.i("MainActivity", "楼 :" + bdLocation.getBuildingName());
            String cityName = bdLocation.getCity().substring(0, bdLocation.getCity().length() - 1);
//            TextView textView = (TextView) findViewById(R.id.title_city);
//            textView.setText(cityName);
            mLocationClient.unRegisterLocationListener(mLocationListener);
            mLocatedCity = new LocatedCity(cityName, bdLocation.getProvince(), bdLocation.getCityCode());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("BNBPostion", mBNBPostion);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}