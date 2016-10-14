package com.science.baserecyclerviewadapter.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author 幸运Science
 * @description 粘性头部
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/10/13
 */

public abstract class BaseStickyAdapter<T> extends BaseAdapter {

    public static final int TYPE_COMMON_SECTION_HEADER_ITEM_VIEW = 100011; // 普通数据中的头部item
    private Context mContext;
    /**
     * Holds the calculated values of @{link getCountForSection}
     */
    private SparseArray<Integer> mSectionCountCache;
    /**
     * Caches the section count
     */
    private int mSectionCount = 0;

    public abstract int getItemHeaderLayoutId(); // 设置普通Item头部布局

    public abstract void convert(ViewHolder viewHolder, T data); // 设置普通Item数据

    public abstract void convertHeader(ViewHolder viewHolder, T data); // 设置普通Item头部数据

    public BaseStickyAdapter(Context context) {
        super(context);
        mContext = context;
        mSectionCountCache = new SparseArray<>();
    }

    @Override
    public int getDefItemViewType(int position) {
        return isSectionHeader(position) ? TYPE_COMMON_SECTION_HEADER_ITEM_VIEW : TYPE_COMMON_ITEM_VIEW;
    }

    private final boolean isSectionHeader(int position) {
        int sectionStart = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            if (position == sectionStart) {
                return true;
            } else if (position < sectionStart) {
                return false;
            }
            sectionStart += internalGetCountForSection(i) + 1;
        }
        return false;
    }

    private int internalGetCountForSection(int section) {
        Integer cachedSectionCount = mSectionCountCache.get(section);
        if (cachedSectionCount != null) {
            return cachedSectionCount;
        }
        int sectionCount = getCountOfSection(section);
        mSectionCountCache.put(section, sectionCount);
        return sectionCount;
    }

    private int internalGetSectionCount() {
        if (mSectionCount >= 0) {
            return mSectionCount;
        }
        mSectionCount = getSectionCount();
        return mSectionCount;
    }

    @Override
    public ViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_COMMON_SECTION_HEADER_ITEM_VIEW) {
            return ViewHolder.create(mContext, getItemHeaderLayoutId(), parent);
        }
        return super.onCreateDefViewHolder(parent, viewType);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final Object data, final int position) {
        if (viewHolder.getItemViewType() == TYPE_COMMON_SECTION_HEADER_ITEM_VIEW) {
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
            if (data != null && !data.isEmpty()) {
                mSectionCount = -1;
                mSectionCountCache.clear();
                setNewData(data);
            }
        }
    }

    /**
     * section header的数量
     */
    public abstract int getSectionCount();

    /**
     * 子item的数量
     *
     * @param section
     * @return
     */
    public abstract int getCountOfSection(int section);

    public abstract void setData(List<T> list);
}
