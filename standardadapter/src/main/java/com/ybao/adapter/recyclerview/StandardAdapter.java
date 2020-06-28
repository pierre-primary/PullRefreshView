/**
 * @Title: StandardAdapter.java
 * @Package appframe.widget.adapter.recyclerview
 * @Description: TODO
 * @author Ybao
 * @date 2015年6月8日 下午4:23:15
 * @version V1.0
 */
package com.ybao.adapter.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author Ybao
 * @ClassName: StandardAdapter
 * @Description: TODO Recyclerview 通用的适配器
 * @date 2015年6月8日 下午4:23:15
 */
public class StandardAdapter extends RecyclerView.Adapter<StandardAdapter.ItemViewHolder> {

    private List<?> mList;

    public StandardAdapter() {
    }

    public StandardAdapter(List<?> mList) {
        this.mList = mList;
    }

    public void setData(List<?> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.setItemClickListener(thisOnItemClickListener);
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public Object getItem(int arg0) {
        if (mList != null && mList.size() > arg0 && arg0 >= 0) {
            return mList.get(arg0);
        }
        return null;
    }

    public List<?> getData() {
        return mList;
    }


    StandardAdapter.OnItemClickListener thisOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, position);
            }

        }
    };

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ItemViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        protected ItemViewHolder(View view, boolean clickAble) {
            super(view);
            if (clickAble) {
                view.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, pos);
            }
        }

        private OnItemClickListener onItemClickListener;

        public void setItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
