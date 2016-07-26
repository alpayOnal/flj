package com.muatik.flj.flj.UI.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by muatik on 24.07.2016.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private boolean loading = false; // True if we are still waiting for the last set of data to load.
    public int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int totalItemCount;
    int lastVisibleItem;

    boolean dontListen = false;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dontListen)
            return;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            // End has been reached
            // Do something
            onLoadMore();
            setLoading(true);
        }
    }

    public abstract void onLoadMore();

    public void setDontListen(boolean dontListen) {
        this.dontListen = dontListen;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}