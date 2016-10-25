package com.science.baserecyclerviewadapter.interfaces;

import com.science.baserecyclerviewadapter.base.ViewHolder;

/**
 * @author 幸运Science
 * @description
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/9/30
 */

public abstract class OnItemClickListener<T> {

    /**
     * 普通item点击事件
     *
     * @param viewHolder
     * @param data
     * @param position
     */
    public abstract void onItemClick(ViewHolder viewHolder, T data, int position);

    /**
     * 数据为空时，点击继续加载事件
     */
    public void onItemEmptyClick() {

    }

    /**
     * section部分头部点击事件
     */
    public void onItemSectionHeaderClick(ViewHolder viewHolder, T data, int position) {

    }

    /**
     * section部分尾部点击事件
     */
    public void onItemSectionFooterClick(ViewHolder viewHolder, T data, int position) {

    }
}
