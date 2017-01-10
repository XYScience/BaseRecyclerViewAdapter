package com.science.baserecyclerviewadapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.science.baserecyclerviewadapter.interfaces.OnClickListener;
import com.science.baserecyclerviewadapter.widget.StickyHeaderItemDecoration;

import java.util.List;

/**
 * @author 幸运Science
 * @description 粘性头部
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/10/13
 */

public abstract class BaseStickyAdapter<T> extends BaseAdapter
        implements StickyHeaderItemDecoration.StickyHeaderAdapter {

    public static final int TYPE_COMMON_SECTION_HEADER_ITEM_VIEW = 100011; // 普通数据中的头部item
    private Context mContext;
    /**
     * Holds the calculated values of @{link getSectionForPosition}
     */
    private SparseArray<Integer> mSectionCache;
    /**
     * Holds the calculated values of @{link getPositionInSectionForPosition}
     */
    private SparseArray<Integer> mSectionPositionCache;
    /**
     * Holds the calculated values of @{link getCountForSection}
     */
    private SparseArray<Integer> mSectionCountCache;
    /**
     * Caches the section count
     */
    private int mSectionCount = -1;
    /**
     * Caches the item count
     */
    private int mCount = -1;
    private boolean isLoading = false;

    public abstract int getItemHeaderLayoutId(); // 设置普通Item头部布局

    public abstract void convertCommon(ViewHolder viewHolder, T data, int section, int position); // 设置普通Item数据

    public abstract void convertHeader(ViewHolder viewHolder, T data, int section); // 设置普通Item头部数据

    public BaseStickyAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        mContext = context;
        mSectionCountCache = new SparseArray<>();
        mSectionCache = new SparseArray<>();
        mSectionPositionCache = new SparseArray<>();
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
        if (cachedSectionCount != null && isLoading) {
            return cachedSectionCount;
        }
        int sectionCount = getCountOfSection(section);
        mSectionCountCache.put(section, sectionCount);
        return sectionCount;
    }

    private int internalGetSectionCount() {
        if (mSectionCount >= 0 && isLoading) {
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
    public void convert(final ViewHolder viewHolder, final List dataList, final int position) {
        if (viewHolder.getItemViewType() == TYPE_COMMON_SECTION_HEADER_ITEM_VIEW) {
            convertHeader(viewHolder, (T) dataList, getSectionForPosition(position));
        } else if (viewHolder.getItemViewType() == TYPE_COMMON_ITEM_VIEW) {
            convertCommon(viewHolder, (T) dataList, getSectionForPosition(position),
                    getPositionInSectionForPosition(position));
            viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClicks(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(dataList.get(getSectionForPosition(position)),
                                getPositionInSectionForPosition(position));
                    }
                }
            });
        }
    }

    public final int getSectionForPosition(int position) {
        // first try to retrieve values from cache
        Integer cachedSection = mSectionCache.get(position);
        if (cachedSection != null && isLoading) {
            return cachedSection;
        }
        int sectionStart = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            int sectionCount = internalGetCountForSection(i);
            int sectionEnd = sectionStart + sectionCount + 1;
            if (position >= sectionStart && position < sectionEnd) {
                mSectionCache.put(position, i);
                return i;
            }
            sectionStart = sectionEnd;
        }
        return 0;
    }

    public int getPositionInSectionForPosition(int position) {
        // first try to retrieve values from cache
        Integer cachedPosition = mSectionPositionCache.get(position);
        if (cachedPosition != null && isLoading) {
            return cachedPosition;
        }
        int sectionStart = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            int sectionCount = internalGetCountForSection(i);
            int sectionEnd = sectionStart + sectionCount + 1;
            if (position >= sectionStart && position < sectionEnd) {
                int positionInSection = position - sectionStart - 1;
                mSectionPositionCache.put(position, positionInSection);
                return positionInSection;
            }
            sectionStart = sectionEnd;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if (mData.isEmpty()) {
            return 1; // 数据为空，则显示“暂时没有数据”
        }
        if (mCount >= 0 && isLoading) {
            return mCount;
        }
        int count = 0;
        for (int i = 0; i < internalGetSectionCount(); i++) {
            count += internalGetCountForSection(i);
            count++; // for the header view
        }
        mCount = count + getFooterViewCount();
        return mCount;
    }

    /**
     * 设置数据
     *
     * @param isLoadMore 是否是新数据
     * @param data       要设置的数据
     */
    public void setData(boolean isLoadMore, List data) {
        isLoading = true;
        if (isLoadMore) {
            updateData(isLoadMore, (T) data); // 更新section头部和子item的位置
            setLoadMoreData(data);
        } else {
            if (data != null && !data.isEmpty()) {
                updateData(isLoadMore, (T) data); // 更新section头部和子item的位置
                mSectionCount = -1;
                mCount = -1;
                mSectionCountCache.clear();
                mSectionCache.clear();
                mSectionPositionCache.clear();
                setNewData(data);
                isLoading = false; // 表示加载完成
            }
        }
    }

    /**
     * 刷新加载更多的数据
     *
     * @param data
     */
    private void setLoadMoreData(List<T> data) {
        mData.addAll(data);
        notifyDataSetChanged(); // notifyItemInserted(size);-->数据量变大时，会变卡，暂时不清楚原因
        isLoadMore = true; // 在一次的数据加载完成后，才可以再次加载
        isLoading = false; // 表示加载完成
    }

    @Override
    public boolean isStickyViewType(int viewType) {
        if (viewType == TYPE_COMMON_SECTION_HEADER_ITEM_VIEW) {
            return true;
        } else {
            return false;
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

    public abstract void updateData(boolean isLoadMore, T list);
}
