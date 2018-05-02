package com.science.baserecyclerviewadapter.interfaces;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2016/9/30
 */

public interface OnLoadMoreListener {

    /**
     * 加载更多
     * @param currentPage 当前加载的页数
     */
    void onLoadMore(int currentPage);
}
