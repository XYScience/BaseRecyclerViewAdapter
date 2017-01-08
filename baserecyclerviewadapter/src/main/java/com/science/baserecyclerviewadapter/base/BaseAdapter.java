package com.science.baserecyclerviewadapter.base;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.science.baserecyclerviewadapter.R;
import com.science.baserecyclerviewadapter.animation.AlphaInAnimation;
import com.science.baserecyclerviewadapter.interfaces.OnClickListener;
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

    public static final int TYPE_COMMON_ITEM_VIEW = 10001; // 普通数据item
    public static final int TYPE_FOOTER_ITEM_VIEW = 10002; // 整个列表的底部item（显示正在加载or加载结束等）
    public static final int TYPE_EMPTY_ITEM_VIEW = 10003; // 无任何数据时的item
    private Context mContext;
    private View mEmptyView;
    private View mFooterView;
    private boolean isAutoLoadMore = true; // 是否自动加载，即当数据不满一屏幕会自动加载
    protected boolean isDataEmpty = true; // 数据是否为空
    protected boolean isLoadMore = true; // 是否加载更多
    protected int currentPage = 0;
    private int mLastPosition = -1;
    protected List<T> mData;
    private AlphaInAnimation mAlphaInAnimation;
    protected OnItemClickListener<T> mOnItemClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView mRecyclerView;

    public abstract int getItemLayoutId(); // 设置普通Item布局

    public abstract void convert(ViewHolder viewHolder, List<T> dataList, int position); // 设置普通Item数据

    public BaseAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        mRecyclerView = recyclerView;
        mData = new ArrayList<>();
        mAlphaInAnimation = new AlphaInAnimation();
        mFooterView = AdapterUtil.inflate(mContext, R.layout.item_footer, (ViewGroup) recyclerView.getParent());
        mEmptyView = AdapterUtil.inflate(mContext, R.layout.item_empty, (ViewGroup) recyclerView.getParent());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_FOOTER_ITEM_VIEW:
                viewHolder = ViewHolder.create(mFooterView);
                break;
            case TYPE_EMPTY_ITEM_VIEW:
                viewHolder = ViewHolder.create(mEmptyView);
                break;
            default:
                viewHolder = onCreateDefViewHolder(parent, viewType);
                break;
        }
        return viewHolder;
    }

    protected ViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent);
    }

    protected ViewHolder createBaseViewHolder(ViewGroup parent) {
        return ViewHolder.create(mContext, getItemLayoutId(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_COMMON_ITEM_VIEW:
                convert((ViewHolder) holder, mData, position);
                break;
            case TYPE_EMPTY_ITEM_VIEW:
                break;
            case TYPE_FOOTER_ITEM_VIEW:
                break;
            default:
                convert((ViewHolder) holder, mData, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.isEmpty() && mEmptyView != null) {
            return TYPE_EMPTY_ITEM_VIEW;
        }
        if (isFooterView(position)) {
            return TYPE_FOOTER_ITEM_VIEW;
        }
        return getDefItemViewType(position);
    }

    protected int getDefItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mData.isEmpty() && mEmptyView != null) {
            return 1; // 数据为空，则显示“暂时没有数据”
        }
        return mData.size() + getFooterViewCount();
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
    protected int getFooterViewCount() {
        return mOnLoadMoreListener != null && !mData.isEmpty() ? 1 : 0;
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
        } else {
            addAnimation(holder);
        }
    }

    /**
     * add animation when you want to show time
     *
     * @param holder
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (holder.getLayoutPosition() > mLastPosition) {
            for (Animator anim : mAlphaInAnimation.getAnimators(holder.itemView)) {
                startAnim(anim, holder.getLayoutPosition());
            }
            mLastPosition = holder.getLayoutPosition();
        }
    }

    /**
     * set anim to start when loading
     *
     * @param anim
     * @param index
     */
    protected void startAnim(Animator anim, int index) {
        anim.setDuration(300).start();
        anim.setInterpolator(new LinearInterpolator());
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
                // 在一次数据加载没有完成时，不能再次加载（因为此回调方法会因SCROLL_STATE_IDLE人为的多次执行）
                isLoadMore = false;
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
     * 如果当前的Activity的组合是Toolbar+TabLayout+RecyclerView，则满足item没有占满一屏幕时，toolbar禁止伸缩
     *
     * @param layoutManager
     * @param toolbar
     * @param appBarLayout
     */
    public void turnOffToolbarCollapse(RecyclerView.LayoutManager layoutManager, Toolbar toolbar, AppBarLayout appBarLayout) {
        AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        if (findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
            //turn off scrolling
            toolbarLayoutParams.setScrollFlags(0);
            appBarLayoutParams.setBehavior(null);
        } else {
            // turn on scrolling
            toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                    | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
            appBarLayoutParams.setBehavior(new AppBarLayout.Behavior());
        }
        toolbar.setLayoutParams(toolbarLayoutParams);
        appBarLayout.setLayoutParams(appBarLayoutParams);
    }

    /**
     * 自定义无数据时空白view
     */
    public void setCustomEmptyView() {
        mEmptyView = null;
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
            setNewData(data);
        }
    }

    /**
     * 刷新加载更多的数据
     *
     * @param data
     */
    private void setLoadMoreData(List<T> data) {
        int size = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(size, data.size());
        isLoadMore = true; // 在一次的数据加载完成后，才可以再次加载
    }

    /**
     * 初次加载、或下拉刷新时，要替换全部旧数据时刷新数据
     *
     * @param data
     */
    protected void setNewData(List<T> data) {
        if (data != null && !data.isEmpty()) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
            isDataEmpty = false;
            currentPage = 1;
            mLastPosition = -1;
        }
    }

    /**
     * 删除单条item
     * 注：不能直接使用notifyItemRemoved(position)，参数要使用getLayoutPosition()或者getAdapterPosition()，
     * 因为函数里面的传入的参数position，它是在进行onBind操作时确定的，在删除单项后，
     * 已经出现在画面里的项不会再有调用onBind机会，这样它保留的position一直是未进行删除操作前的position值。
     *
     * @param viewHolder
     * @param position
     */
    public void removeData(ViewHolder viewHolder, int position) {
        mData.remove(viewHolder.getLayoutPosition()); // 把数据从list中remove掉
        notifyItemRemoved(viewHolder.getLayoutPosition()); // 显示动画效果
        notifyItemRangeChanged(position, mData.size() - position); // 对于被删掉的位置及其后range大小范围内的view进行重新onBindViewHolder
        if (findLastVisibleItemPosition(mRecyclerView.getLayoutManager()) + 1 == getItemCount()) {
            scrollLoadMore();
        }
    }

    /**
     * 自定义无数据时空白view 更新单条item数据
     * @param position
     * @param data
     */
    public void updateItem(int position, T data) {
        mData.set(position, data);
        notifyItemChanged(position);
    }

    /**
     * 当没有数据时,显示"暂无数据",并关闭加载控件
     */
    private void showEmptyViewNoData(int drawableRes, String stringRes) {
        if (mEmptyView != null) {
            final View viewProgress = mEmptyView.findViewById(R.id.progress);
            ViewCompat.animate(viewProgress).alpha(0).start();
            viewProgress.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewProgress.setVisibility(View.GONE);
                }
            }, 300);
            final TextView textNoData = (TextView) mEmptyView.findViewById(R.id.tv_no_data);
            textNoData.setVisibility(View.VISIBLE);
            Drawable drawable = mContext.getResources().getDrawable(drawableRes);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            textNoData.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            textNoData.setCompoundDrawablePadding(16);
            textNoData.setText(stringRes);
            ViewCompat.animate(textNoData).alpha(1).start();
            mEmptyView.findViewById(R.id.rl_empty).setOnClickListener(new OnClickListener() {
                @Override
                public void onClicks(View v) {
                    if (mOnItemClickListener != null) {
                        showEmptyViewProgress(mOnItemClickListener, viewProgress, textNoData);
                    }
                }
            });
        }
    }

    /**
     * 当无数据并且点击继续记载数据时，显示加载动画，并隐藏“暂无数据”
     *
     * @param onItemClickListener
     * @param viewProgress
     * @param textNoData
     */
    private void showEmptyViewProgress(final OnItemClickListener<T> onItemClickListener, final View viewProgress, final TextView textNoData) {
        if (mEmptyView != null) {
            mEmptyView.findViewById(R.id.rl_empty).setOnClickListener(null);
            ViewCompat.animate(viewProgress).alpha(1).start();
            ViewCompat.animate(textNoData).alpha(0).start();
            textNoData.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textNoData.setVisibility(View.GONE);
                    viewProgress.setVisibility(View.VISIBLE);
                    onItemClickListener.onItemEmptyClick();
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
        showLoadFailed(R.drawable.empty, mContext.getResources().getString(R.string.no_data), mContext.getResources().getString(R.string.load_failed));
    }

    public void showLoadFailed(int noDataDrawableRes, String noDataStringRes, final String loadFailedStringRes) {
        // 有数据，列表footer加载失败
        if (!mData.isEmpty()) {
            if (mFooterView != null) {
                isLoadMore = false;
                final View viewProgress = mFooterView.findViewById(R.id.progress);
                ViewCompat.animate(viewProgress).alpha(0).start();
                final TextView viewResult = (TextView) mFooterView.findViewById(R.id.tv_load_result);
                viewResult.setText(mContext.getString(R.string.load_failed_custom, loadFailedStringRes));
                viewResult.setVisibility(View.VISIBLE);
                ViewCompat.animate(viewResult).alpha(1).start();
                viewProgress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewProgress.setVisibility(View.GONE);

                        viewResult.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClicks(View v) {
                                isLoadMore = true;
                                viewProgress.setVisibility(View.VISIBLE);
                                ViewCompat.animate(viewProgress).alpha(1).start();
                                ViewCompat.animate(viewResult).alpha(0).start();
                                viewResult.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewResult.setVisibility(View.GONE);
                                        scrollLoadMore();
                                    }
                                }, 300);
                            }
                        });
                    }
                }, 300);

            }
        }
        // 无数据，全屏显示暂无数据
        else {
            showEmptyViewNoData(noDataDrawableRes, noDataStringRes);
            isDataEmpty = true;
        }
    }

    public T getItem(int position) {
        if (mData.isEmpty()) {
            return null;
        }
        return mData.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }
}
