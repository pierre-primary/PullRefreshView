package com.ybao.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author Ybao
 * @ClassName: tandardAdapter
 * @Description: TODO AbsListView 通用适配器
 * @date 2015年6月8日 下午5:10:18
 */
public class StandardAdapter extends BaseAdapter {
    private List<?> mList;

    public StandardAdapter(List<?> mList) {
        this.mList = mList;
    }

    public StandardAdapter() {
    }

    public void setData(List<?> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        if (mList != null) {
            return mList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface OnCreateViewListener {
        View CreateView(int position, View convertView, ViewGroup parent, List<?> mList);
    }

    private OnCreateViewListener mCreateViewListener;

    public void setOnCreateViewListener(OnCreateViewListener mCreateViewListener) {
        this.mCreateViewListener = mCreateViewListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mCreateViewListener != null) {
            return mCreateViewListener.CreateView(position, convertView, parent, mList);
        }
        return new View(parent.getContext());
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    public List<?> getData() {
        return mList;
    }
}
