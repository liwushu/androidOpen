package com.flying.test.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flying.test.R;
import com.flying.test.utils.LogUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by liwu.shu on 2017/5/23.
 */

public class ActAdapter extends RecyclerView.Adapter<ActViewHolder> {


    Context mc;
    List<Class<?>> actList;
    LayoutInflater layoutInflater;

    public ActAdapter(Context mc, List<Class<?>> actList){
        this.mc = mc;
        this.actList = actList;
        layoutInflater = LayoutInflater.from(mc);
    }

    @Override
    public ActViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View main = layoutInflater.inflate(R.layout.act_recycler_item,null,false);
        //View main = View.inflate(mc,R.layout.act_recycler_item,null);
        LogUtils.logd("flying","layoutParams: "+main.getLayoutParams());
        return new ActViewHolder(main);
    }

    @Override
    public void onBindViewHolder(ActViewHolder holder, int position) {
        final Class<?> actClz  =getItem(position);
        holder.tvTitle.setText(actClz.getSimpleName());
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mc,actClz);
                mc.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return actList==null?0:actList.size();
    }

    private Class<?> getItem(int position){
        return actList.get(position);
    }

    /**
     * 移动Item
     *
     * @param fromPosition
     * @param toPosition
     */
    public void moveItem(int fromPosition, int toPosition) {
        //做数据的交换
        if (fromPosition < toPosition) {
            for (int index = fromPosition; index < toPosition; index++) {
                Collections.swap(actList, index, index + 1);
            }
        } else {
            for (int index = fromPosition; index > toPosition; index--) {
                Collections.swap(actList, index, index - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 滑动Item
     *
     * @param position
     */
    public void removeItem(int position) {
        actList.remove(position);//删除数据
        notifyItemRemoved(position);
    }
}
