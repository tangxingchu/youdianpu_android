package com.weichu.youdianpu.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weichu.youdianpu.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Administrator on 2018/4/21.
 */

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private List<String> goodsList;

    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    private Handler mHandler;

    private static final int RERESH_LAYOUT = 1;

    private int mLastVisibleItem;
    private LinearLayoutManager mLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initGoodsList();
        mContext = this.getContext();
        View view = inflater.inflate(R.layout.fragment_home, null);

        mRecyclerView = view.findViewById(R.id.rectclerView);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case RERESH_LAYOUT:
                        mSwipeRefreshLayout.setRefreshing(false);
                        addGoods();
                        mRecyclerViewAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断是否滚动到最后一条记录
                if(newState == RecyclerView.SCROLL_STATE_IDLE && mLastVisibleItem + 1 == mRecyclerViewAdapter.getItemCount()) {
                    if(mRecyclerViewAdapter.mLoadMoreStatus == RecyclerViewAdapter.LOADING_MORE) {
                        return;
                    }
                    mRecyclerViewAdapter.changeMoreStatus(RecyclerViewAdapter.LOADING_MORE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addMoreItem(Arrays.asList("more Item1", "more Item2", "more Item3", "more Item4", "more Item5"));
                            mRecyclerViewAdapter.changeMoreStatus(RecyclerViewAdapter.PULLUP_LOAD_MORE);
                        }
                    }, 1000);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
        return view;
    }

    private void initGoodsList() {
        goodsList = new ArrayList<String>();
        for(int i = 0; i < 30; i++) {
            goodsList.add("商品" + i);
        }
    }

    private void addGoods() {
        goodsList.add(0, "我是新商品");
    }

    private void addMoreItem(List<String> newDatas) {
        goodsList.addAll(newDatas);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = RERESH_LAYOUT;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 0; //普通的itemVIew
        private static final int TYPE_FOOTER = 1;//底部footerView

        //上拉加载更多
        public static final int PULLUP_LOAD_MORE = 0;
        //正在加载中
        public static final int LOADING_MORE     = 1;
        //没有加载更多 隐藏
        public static final int NO_LOAD_MORE     = 2;

        //上拉加载更多状态-默认为0
        private int mLoadMoreStatus = 0;

        @Override
        public int getItemViewType(int position) {
            if(position  == this.getItemCount() - 1) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_goods, parent, false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_footer, parent, false);
                view.setBackgroundColor(Color.RED);
                FooterHolder footerHolder = new FooterHolder(view);
                return footerHolder;
            }
//            View view = LayoutInflater.from(mContext).inflate(R.layout.item_goods, parent, false);
//            ViewHolder viewHolder = new ViewHolder(view);
//            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof ViewHolder) {
                String item = goodsList.get(position);
                ((ViewHolder) holder).mTextView.setText(item);
            } else if(holder instanceof FooterHolder) {
                switch (mLoadMoreStatus) {
                    case PULLUP_LOAD_MORE:
                        ((FooterHolder) holder).mTextView.setText("上拉加载更多...");
                        break;
                    case LOADING_MORE:
                        ((FooterHolder) holder).mTextView.setText("正在加载...");
                        break;
                    case NO_LOAD_MORE:
                        //隐藏加载更多
                        ((FooterHolder) holder).mTextView.setVisibility(View.GONE);
                        break;
                }
            }
//            String item = goodsList.get(position);
//            ((ViewHolder) holder).mTextView.setText(item);
        }

        @Override
        public int getItemCount() {
            //+1 做为footerView
            return goodsList.size() + 1;
        }

        public void changeMoreStatus(int status) {
            this.mLoadMoreStatus = status;
            this.notifyDataSetChanged();
        }

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.goodsName);
        }
    }

    public class FooterHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public FooterHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.footerTextView);
        }
    }

}
