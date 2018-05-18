package com.weichu.youdianpu.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.weichu.youdianpu.R;

/**
 * Created by tangxingchu on 2018/4/12.
 */

public class NearbyFragment extends Fragment {

    private static final String TAG = NearbyFragment.class.getSimpleName();
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater mInflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = mInflater.inflate(R.layout.fragment_nearby, container, false);
        mMapView = view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        Bundle bundle = getArguments();
        if(bundle != null) {
            double lat = bundle.getDouble("lat");
            double lon = bundle.getDouble("lon");
            LatLng latLng = new LatLng(lat, lon);
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(mapStatusUpdate);
            mapStatusUpdate = MapStatusUpdateFactory.zoomTo(19);
            mBaiduMap.animateMapStatus(mapStatusUpdate);
            MyLocationData.Builder builder = new MyLocationData.Builder();
            builder.latitude(lat);
            builder.longitude(lon);
            MyLocationData myLocationData = builder.build();
            mBaiduMap.setMyLocationData(myLocationData);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
    }
}
