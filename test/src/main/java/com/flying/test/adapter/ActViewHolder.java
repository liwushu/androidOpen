package com.flying.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flying.test.R;

/**
 * Created by liwu.shu on 2017/5/23.
 */

public class ActViewHolder extends RecyclerView.ViewHolder {
    TextView tvTitle;

    public ActViewHolder(View itemView) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.title);
    }
}
