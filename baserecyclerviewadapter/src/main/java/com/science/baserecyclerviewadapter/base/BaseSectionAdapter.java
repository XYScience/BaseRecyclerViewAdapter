package com.science.baserecyclerviewadapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.science.baserecyclerviewadapter.entity.SectionEntity;
import com.science.baserecyclerviewadapter.interfaces.OnClickListener;

import java.util.List;

/**
 * @author SScience
 * @description section头部和尾部
 * @email chentushen.science@gmail.com
 * @data 2016/10/13
 */

public abstract class BaseSectionAdapter<T extends SectionEntity> extends BaseAdapter {

    public static final int TYPE_COMMON_SECTION_HEADER_ITEM_VIEW = 100011; // 普通数据中的头部item
    public static final int TYPE_COMMON_SECTION_FOOTER_ITEM_VIEW = 100012; // 普通数据中的尾部item
    private Context mContext;

    public abstract int getItemHeaderLayoutId(); // 设置普通Item头部布局

    public abstract int getItemFooterLayoutId(); // 设置普通Item尾部布局

    public abstract void convert(ViewHolder viewHolder, T data); // 设置普通Item数据

    public abstract void convertHeader(ViewHolder viewHolder, T data); // 设置普通Item头部数据

    public abstract void convertFooter(ViewHolder viewHolder, T data); // 设置普通Item尾部数据

    public BaseSectionAdapter(Context context, RecyclerView recyclerView, boolean showStartView) {
        super(context, recyclerView, showStartView);
        mContext = context;
    }

    @Override
    public int getDefItemViewType(int position) {
        SectionEntity sectionEntity = ((SectionEntity) mData.get(position));
        if (sectionEntity.isHeader) {
            return TYPE_COMMON_SECTION_HEADER_ITEM_VIEW;
        }
        if (sectionEntity.isFooter) {
            return TYPE_COMMON_SECTION_FOOTER_ITEM_VIEW;
        }
        return TYPE_COMMON_ITEM_VIEW;
    }

    @Override
    public ViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_COMMON_SECTION_HEADER_ITEM_VIEW) {
            return ViewHolder.create(mContext, getItemHeaderLayoutId(), parent);
        }
        if (viewType == TYPE_COMMON_SECTION_FOOTER_ITEM_VIEW) {
            return ViewHolder.create(mContext, getItemFooterLayoutId(), parent);
        }
        return super.onCreateDefViewHolder(parent, viewType);
    }

    @Override
    public void convert(ViewHolder viewHolder, List dataList, int position) {
        if (viewHolder.getItemViewType() == TYPE_COMMON_SECTION_HEADER_ITEM_VIEW) {
            convertHeader(viewHolder, (T) dataList.get(position));
        } else if (viewHolder.getItemViewType() == TYPE_COMMON_SECTION_FOOTER_ITEM_VIEW) {
            convertFooter(viewHolder, (T) dataList.get(position));
        } else if (viewHolder.getItemViewType() == TYPE_COMMON_ITEM_VIEW) {
            convert(viewHolder, (T) dataList.get(position));
        }
    }

    @Override
    public void convertItemClick(final ViewHolder viewHolder, final List dataList, int position) {
        if (viewHolder.getItemViewType() == TYPE_COMMON_SECTION_HEADER_ITEM_VIEW) {
            viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClicks(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemSectionHeaderClick((T) dataList.get(viewHolder.getAdapterPosition()),
                                viewHolder.getAdapterPosition());
                    }
                }
            });
        } else if (viewHolder.getItemViewType() == TYPE_COMMON_SECTION_FOOTER_ITEM_VIEW) {
            viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClicks(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemSectionFooterClick((T) dataList.get(viewHolder.getAdapterPosition()),
                                viewHolder.getAdapterPosition());
                    }
                }
            });
        } else if (viewHolder.getItemViewType() == TYPE_COMMON_ITEM_VIEW) {
            viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClicks(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick((T) dataList.get(viewHolder.getAdapterPosition()),
                                viewHolder.getAdapterPosition());
                    }
                }
            });

            viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemLongClick((T) dataList.get(viewHolder.getAdapterPosition()),
                                viewHolder.getAdapterPosition());
                    }
                    return true;
                }
            });
        }
    }
}
