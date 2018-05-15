package com.weichu.youdianpu.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weichu.youdianpu.R;

/**
 * Created by tangxingchu on 2018/4/12.
 */

public class NearbyFragment extends BaseFragment {

    private static final String TAG = NearbyFragment.class.getSimpleName();

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater mInflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        Log.i(TAG, "onCreateView");
//        View view = mInflater.inflate(R.layout.fragment_nearby, container, false);
//        return view;
//    }

    @Override
    protected void loadData() {
        Log.i(TAG, "懒加载数据");
    }

    @Override
    protected void onRealViewLoaded(View view) {
        Log.i(TAG, "onRealViewLoaded");
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_nearby;
    }

}
