package com.weichu.youdianpu.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weichu.youdianpu.R;
import com.weichu.youdianpu.model.Goods;

import java.util.List;

/**
 * Created by tangxingchu on 2018/4/14.
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {

    private List<Goods> mGoodses;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView goodsImage;
        TextView goodsName;

        public ViewHolder(View itemView) {
            super(itemView);
            goodsImage = itemView.findViewById(R.id.goods_image);
            goodsName = itemView.findViewById(R.id.goods_name);
        }
    }

    public GoodsAdapter(List<Goods> goodses) {
        this.mGoodses = goodses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goods_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Goods goods = mGoodses.get(position);
        holder.goodsImage.setImageResource(goods.getId());
        holder.goodsName.setText(goods.getName());
    }

    @Override
    public int getItemCount() {
        return mGoodses.size();
    }
}
