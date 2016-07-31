package com.muatik.flj.flj.UI.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by muatik on 27.07.2016.
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }
    abstract public void handle(Object o );
}