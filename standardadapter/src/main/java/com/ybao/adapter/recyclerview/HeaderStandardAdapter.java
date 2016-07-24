package com.ybao.adapter.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Ybao
 * @ClassName: HeaderStandardAdapter
 * @Description: TODO Recyclerview 轻松加头通用的适配器
 * @date 2016年7月22日 下午5:10:18
 */
public class HeaderStandardAdapter extends StandardAdapter {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private List<View> mHeaderViews;
    StandardAdapter standardAdapter;

    public HeaderStandardAdapter(StandardAdapter standardAdapter) {
        this.standardAdapter = standardAdapter;
        mHeaderViews = new ArrayList<>();
    }

    public void addHeaderView(View headerView) {
        if (headerView != null) {
            mHeaderViews.add(headerView);
            notifyItemInserted(0);
        }
    }

    public List<View> getmHeaderViews() {
        return mHeaderViews;
    }

    @Override
    public void setData(List<?> mList) {
        standardAdapter.setData(mList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public int getViewType(int position) {
        if (position < mHeaderViews.size()) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        int viewType = getViewType(position);
        ItemViewHolder holder;
        if (viewType != TYPE_NORMAL) {
            holder = new ItemViewHolder(mHeaderViews.get(position), false);
        } else {
            holder = standardAdapter.onCreateViewHolder(parent, standardAdapter.getItemViewType(position - mHeaderViews.size()));
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (getViewType(position) != TYPE_NORMAL) {
            return;
        }
        standardAdapter.onBindViewHolder(holder, position - mHeaderViews.size());
        holder.setItemClickListener(thisOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return standardAdapter.getItemCount() + mHeaderViews.size();
    }

    @Override
    public Object getItem(int arg0) {
        return standardAdapter.getItem(arg0 - mHeaderViews.size());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = (GridLayoutManager) manager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getViewType(position) != TYPE_NORMAL ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }
}
