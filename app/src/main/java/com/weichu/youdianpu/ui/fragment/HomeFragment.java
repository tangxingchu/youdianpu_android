package com.weichu.youdianpu.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.weichu.youdianpu.R;
import com.weichu.youdianpu.model.Goods;
import com.weichu.youdianpu.ui.adapter.GoodsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangxingchu on 2018/4/12.
 * 首页Fragment
 */

public class HomeFragment extends BaseFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater mInflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        Log.i(TAG, "onCreateView");
//        View view = mInflater.inflate(R.layout.fragment_home, container, false);
//        return view;
//    }

    private List<Goods> mGoodses;

    @Override
    protected void loadData() {
        Log.i(TAG, "懒加载数据");
    }

    @Override
    protected void onRealViewLoaded(View view) {
        Log.i(TAG, "onRealViewLoaded");
        PullToRefreshRecyclerView mPtrrv = (PullToRefreshRecyclerView) view.findViewById(R.id.ptrrv);

        initGoods();
        // set PagingableListener
        mPtrrv.setPagingableListener(new PullToRefreshRecyclerView.PagingableListener() {
            @Override
            public void onLoadMoreItems() {
                //do loadmore here
            }
        });

// set OnRefreshListener
        mPtrrv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // do refresh here
            }
        });
// set loadmore String
        mPtrrv.setLoadmoreString("loading");

// set loadmore enable, onFinishLoading(can load more? , select before item)
        mPtrrv.onFinishLoading(true, false);
        mPtrrv.setLayoutManager(new LinearLayoutManager(this.getContext().getApplicationContext()));
        mPtrrv.setAdapter(new GoodsAdapter(mGoodses));
    }

    private void initGoods() {
        mGoodses = new ArrayList<>();
        Goods goods = new Goods();
        goods.setId(R.mipmap.ic_launcher);
        goods.setName("afff");
        mGoodses.add(goods);
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_home;
    }
}
