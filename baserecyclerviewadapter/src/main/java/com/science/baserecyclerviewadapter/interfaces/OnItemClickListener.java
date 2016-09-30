package com.science.baserecyclerviewadapter.interfaces;

import com.science.baserecyclerviewadapter.base.ViewHolder;

/**
 * @author 幸运Science
 * @description
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/9/30
 */

public interface OnItemClickListener<T> {

    /**
     * 普通item点击事件
     * @param viewHolder
     * @param data
     * @param position
     */
    void onItemClick(ViewHolder viewHolder, T data, int position);

    /**
     * 数据为空时，点击继续加载事件
     */
    void onItemEmptyClick();

    /**
     * 加载失败时，点击继续加载事件
     */
    void onItemLoadFailedClick();
}
