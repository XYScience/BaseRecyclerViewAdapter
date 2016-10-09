package com.science.baserecyclerviewadapter.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.science.baserecyclerviewadapter.R;
import com.science.baserecyclerviewadapter.interfaces.OnItemClickListener;
import com.science.baserecyclerviewadapter.interfaces.OnLoadMoreListener;
import com.science.baserecyclerviewadapter.util.AdapterUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2016/9/28
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_COMMON_ITEM_VIEW = 10001;
    public static final int TYPE_FOOTER_ITEM_VIEW = 10002;
    public static final int TYPE_EMPTY_ITEM_VIEW = 10003;
    private Context mContext;
    private List<T> mDatas;
    private View mEmptyView;
    private View mFooterView;
    private boolean isAutoLoadMore = true; // 是否自动加载，即当数据不满一屏幕会自动加载
    private boolean isDataEmpty = true; // 数据是否为空
    private boolean isLoadMore = true; // 是否加载更多
    private int currentPage = 0;

    public abstract int getItemLayoutId(); // 设置普通Item布局

    public abstract void convert(ViewHolder viewHolder, T data); // 设置普通Item数据

    private OnItemClickListener<T> mOnItemClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;


    public BaseAdapter(Context context) {
        mContext = context;
        mDatas = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_COMMON_ITEM_VIEW:
                viewHolder = ViewHolder.create(mContext, getItemLayoutId(), parent);
                break;
            case TYPE_FOOTER_ITEM_VIEW:
                if (mFooterView == null) {
                    mFooterView = AdapterUtil.inflate(mContext, R.layout.item_footer, parent);
                }
                viewHolder = ViewHolder.create(mFooterView);
                break;
            case TYPE_EMPTY_ITEM_VIEW:
                if (mEmptyView == null) {
                    mEmptyView = AdapterUtil.inflate(mContext, R.layout.item_empty, parent);
                }
                viewHolder = ViewHolder.create(mEmptyView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_COMMON_ITEM_VIEW:
                bindCommonItem(holder, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.isEmpty()) {
            return TYPE_EMPTY_ITEM_VIEW;
        }
        if (isFooterView(position)) {
            return TYPE_FOOTER_ITEM_VIEW;
        }
        return TYPE_COMMON_ITEM_VIEW;
    }

    @Override
    public int getItemCount() {
        if (mDatas.isEmpty()) {
            return 1; // 数据为空，则显示“暂时没有数据”
        }
        return mDatas.size() + getFooterViewCount();
    }

    /**
     * 如果没有设置了加载更多监听，则没有footer。
     *
     * @param position
     * @return
     */
    private boolean isFooterView(int position) {
        return mOnLoadMoreListener != null && position >= getItemCount() - 1;
    }

    /**
     * 前提：数据不为空。
     * 如果设置了加载更多监听，则列表长度+1，增加的item是footer。
     * 否则，列表长度不变，没有footer。
     *
     * @return
     */
    private int getFooterViewCount() {
        return mOnLoadMoreListener != null && !mDatas.isEmpty() ? 1 : 0;
    }

    /**
     * StaggeredGridLayoutManager模式时，FooterView可占据一行
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isFooterView(holder.getLayoutPosition())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    /**
     * GridLayoutManager模式时， FooterView可占据一行，判断RecyclerView是否到达底部
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isFooterView(position)) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
        startLoadMore(recyclerView, layoutManager);
    }

    /**
     * 普通item设置数据
     *
     * @param holder
     * @param position
     */
    private void bindCommonItem(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        convert(viewHolder, mDatas.get(position));
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(viewHolder, mDatas.get(position), position);
                }
            }
        });
    }

    /**
     * 判断列表是否滑动到底部
     *
     * @param recyclerView
     * @param layoutManager
     */
    private void startLoadMore(RecyclerView recyclerView, final RecyclerView.LayoutManager layoutManager) {
        if (mOnLoadMoreListener == null) {
            return;
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isDataEmpty && !isAutoLoadMore &&
                            findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                        scrollLoadMore();
                        // 在一次数据加载没有完成时，不能再次加载（因为此回调方法会因SCROLL_STATE_IDLE人为的多次执行）
                        isLoadMore = false;
                    }
                }
            }

            // 当列表滚动结束后会回调。如果初始item不满一屏幕，则可在该方法中加载更多数据，直到item占满一屏幕，也就自动加载更多。
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isDataEmpty && isAutoLoadMore) {
                    if (findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                        scrollLoadMore();
                        isAutoLoadMore = true;
                    } else {
                        isAutoLoadMore = false;
                    }
                }
            }
        });
    }

    /**
     * 到达底部开始刷新
     */
    private void scrollLoadMore() {
        if (isLoadMore) {
            if (mOnLoadMoreListener != null) {
                currentPage++;
                mOnLoadMoreListener.onLoadMore(currentPage);
            }
        }
    }

    private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            return findMax(lastVisibleItemPositions);
        }
        return -1;
    }

    /**
     * StaggeredGridLayoutManager时，查找position最大的列
     *
     * @param lastVisiblePositions
     * @return
     */
    private int findMax(int[] lastVisiblePositions) {
        int max = lastVisiblePositions[0];
        for (int value : lastVisiblePositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 设置数据
     *
     * @param isLoadMore 是否是新数据
     * @param data       要设置的数据
     */
    public void setData(boolean isLoadMore, List<T> data) {
        if (isLoadMore) {
            setLoadMoreData(data);
        } else {
            setNewDatas(data);
        }
    }

    /**
     * 刷新加载更多的数据
     *
     * @param datas
     */
    private void setLoadMoreData(List<T> datas) {
        int size = mDatas.size();
        mDatas.addAll(datas);
        notifyItemInserted(size);
        isLoadMore = true; // 在一次的数据加载完成后，才可以再次加载
    }

    /**
     * 初次加载、或下拉刷新时，要替换全部旧数据时刷新数据
     *
     * @param datas
     */
    private void setNewDatas(List<T> datas) {
        if (datas != null && !datas.isEmpty()) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
            isDataEmpty = false;
            currentPage = 1;
        }
    }

    /**
     * 当没有数据时,显示"暂无数据",并关闭加载控件
     */
    private void showEmptyViewNoData(int drawableRes, int stringRes) {
        if (mEmptyView != null) {
            final View viewProgress = mEmptyView.findViewById(R.id.progress);
            ViewCompat.animate(viewProgress).alpha(0).start();
            viewProgress.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewProgress.setVisibility(View.GONE);
                }
            }, 300);
            TextView textNoData = (TextView) mEmptyView.findViewById(R.id.tv_no_data);
            Drawable drawable = mContext.getResources().getDrawable(drawableRes);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            textNoData.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            textNoData.setCompoundDrawablePadding(16);
            textNoData.setText(stringRes);
            ViewCompat.animate(textNoData).alpha(1).start();
            mEmptyView.findViewById(R.id.rl_empty).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        showEmptyViewProgress();
                        mOnItemClickListener.onItemEmptyClick();
                    }
                }
            });
        }
    }

    /**
     * 当无数据并且点击继续记载数据时，显示加载动画，并隐藏“暂无数据”
     */
    private void showEmptyViewProgress() {
        if (mEmptyView != null) {
            View viewProgress = mEmptyView.findViewById(R.id.progress);
            viewProgress.setVisibility(View.VISIBLE);
            ViewCompat.animate(viewProgress).alpha(1).start();
            final View viewNoData = mEmptyView.findViewById(R.id.tv_no_data);
            ViewCompat.animate(viewNoData).alpha(0).start();
            viewNoData.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewNoData.setVisibility(View.GONE);
                }
            }, 300);
        }
    }

    /**
     * 当数据全部记载完成时，底部显示“无更多数据！”
     */
    public void showFooterNoMoreData() {
        showFooterNoMoreData(R.string.no_more_data);
    }

    public void showFooterNoMoreData(int stringRes) {
        if (mFooterView != null) {
            final View viewProgress = mFooterView.findViewById(R.id.progress);
            ViewCompat.animate(viewProgress).alpha(0).start();
            viewProgress.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewProgress.setVisibility(View.GONE);
                }
            }, 300);
            TextView viewResult = (TextView) mFooterView.findViewById(R.id.tv_load_result);
            viewResult.setOnClickListener(null);
            ViewCompat.animate(viewResult).alpha(1).start();
            viewResult.setText(stringRes);
        }
    }

    /**
     * 当无网络等原因加载失败时
     */
    public void showLoadFailed() {
        showLoadFailed(R.drawable.empty, R.string.no_data, R.string.load_failed);
    }

    public void showLoadFailed(int noDataDrawableRes, int noDataStringRes, int loadFailedStringRes) {
        // 有数据，列表footer加载失败
        if (!mDatas.isEmpty()) {
            if (mFooterView != null) {
                isLoadMore = false;
                final View viewProgress = mFooterView.findViewById(R.id.progress);
                ViewCompat.animate(viewProgress).alpha(0).start();
                viewProgress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewProgress.setVisibility(View.GONE);
                    }
                }, 300);
                final TextView viewResult = (TextView) mFooterView.findViewById(R.id.tv_load_result);
                viewResult.setText(loadFailedStringRes);
                ViewCompat.animate(viewResult).alpha(1).start();
                viewResult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLoadMore = true;
                        viewProgress.setVisibility(View.VISIBLE);
                        ViewCompat.animate(viewProgress).alpha(1).start();
                        ViewCompat.animate(viewResult).alpha(0).start();
                        scrollLoadMore();
                    }
                });
            }
        }
        // 无数据，全屏显示暂无数据
        else {
            showEmptyViewNoData(noDataDrawableRes, noDataStringRes);
            isDataEmpty = true;
        }
    }

    public T getItem(int position) {
        if (mDatas.isEmpty()) {
            return null;
        }
        return mDatas.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }
}
