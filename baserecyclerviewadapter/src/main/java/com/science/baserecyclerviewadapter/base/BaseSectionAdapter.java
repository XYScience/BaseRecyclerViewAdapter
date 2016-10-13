package com.science.baserecyclerviewadapter.base;

import android.content.Context;
import android.view.ViewGroup;

import com.science.baserecyclerviewadapter.entity.SectionEntity;

import java.util.List;

/**
 * @author 幸运Science
 * @description
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/10/13
 */

public abstract class BaseSectionAdapter<T extends SectionEntity> extends BaseAdapter {

    public static final int TYPE_COMMON_SECTION_ITEM_VIEW = 100011; // 普通数据中的头部item
    private Context mContext;

    public abstract int getItemHeaderLayoutId(); // 设置普通Item头部布局

    public abstract void convert(ViewHolder viewHolder, T data); // 设置普通Item数据

    public abstract void convertHeader(ViewHolder viewHolder, T data); // 设置普通Item头部数据

    public BaseSectionAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getDefItemViewType(int position) {
        return ((SectionEntity) mData.get(position)).isHeader ? TYPE_COMMON_SECTION_ITEM_VIEW : BaseAdapter.TYPE_COMMON_ITEM_VIEW;
    }

    @Override
    public ViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_COMMON_SECTION_ITEM_VIEW) {
            return ViewHolder.create(mContext, getItemHeaderLayoutId(), parent);
        }
        return super.onCreateDefViewHolder(parent, viewType);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Object data, final int position) {
        if (viewHolder.getItemViewType() == TYPE_COMMON_SECTION_ITEM_VIEW) {
            convertHeader(viewHolder, (T) data);
        } else if (viewHolder.getItemViewType() == TYPE_COMMON_ITEM_VIEW) {
            convert(viewHolder, (T) data);
        }
    }

    /**
     * 设置数据
     *
     * @param isLoadMore 是否是新数据
     * @param data       要设置的数据
     */
    public void setData(boolean isLoadMore, List data) {
        if (isLoadMore) {
            setLoadMoreData(data);
        } else {
            setNewData(data);
        }
    }

    /**
     * 刷新加载更多的数据
     *
     * @param data
     */
    private void setLoadMoreData(List data) {
        int size = mData.size();
        mData.addAll(data);
        notifyItemInserted(size);
        isLoadMore = true; // 在一次的数据加载完成后，才可以再次加载
    }

    /**
     * 初次加载、或下拉刷新时，要替换全部旧数据时刷新数据
     *
     * @param data
     */
    private void setNewData(List data) {
        if (data != null && !data.isEmpty()) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
            isDataEmpty = false;
            currentPage = 1;
        }
    }
}
