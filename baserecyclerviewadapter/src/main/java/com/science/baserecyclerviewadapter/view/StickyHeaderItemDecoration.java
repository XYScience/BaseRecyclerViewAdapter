package com.science.baserecyclerviewadapter.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * ItemDecoration for Pinned Header.
 * <p>
 * porting from https://github.com/beworker/pinned-section-listview
 * 粘性头部
 * @author takahr@gmail.com
 */
public class StickyHeaderItemDecoration extends RecyclerView.ItemDecoration {
    private final static String TAG = StickyHeaderItemDecoration.class.getSimpleName();

    public interface StickyHeaderAdapter {
        boolean isStickyViewType(int viewType);
    }

    RecyclerView.Adapter mAdapter = null;

    // cached data
    // pinned header view
    View mStickyHeaderView = null;
    int mHeaderPosition = -1;

    Map<Integer, Boolean> mStickyViewTypes = new HashMap<Integer, Boolean>();

    private int mStickyHeaderTop;
    private Rect mClipBounds;

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        createPinnedHeader(parent);

        if (mStickyHeaderView != null) {
            // check overlap section view.
            //TODO support only vertical header currently.
            final int headerEndAt = mStickyHeaderView.getTop() + mStickyHeaderView.getHeight();
            final View v = parent.findChildViewUnder(c.getWidth() / 2, headerEndAt + 1);

            if (isHeaderView(parent, v)) {
                mStickyHeaderTop = v.getTop() - mStickyHeaderView.getHeight();
            } else {
                mStickyHeaderTop = 0;
            }

            mClipBounds = c.getClipBounds();
            mClipBounds.top = mStickyHeaderTop + mStickyHeaderView.getHeight();
            c.clipRect(mClipBounds);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mStickyHeaderView != null) {
            c.save();

            mClipBounds.top = 0;
            c.clipRect(mClipBounds, Region.Op.UNION);
            c.translate(0, mStickyHeaderTop);
            mStickyHeaderView.draw(c);

            c.restore();
        }
    }

    private void createPinnedHeader(RecyclerView parent) {
        checkCache(parent);

        // get LinearLayoutManager.
        final LinearLayoutManager linearLayoutManager;
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            linearLayoutManager = (LinearLayoutManager) layoutManager;
        } else {
            return;
        }

        final int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        final int headerPosition = findPinnedHeaderPosition(firstVisiblePosition);

        if (headerPosition >= 0 && mHeaderPosition != headerPosition) {
            mHeaderPosition = headerPosition;
            final int viewType = mAdapter.getItemViewType(headerPosition);

            final RecyclerView.ViewHolder pinnedViewHolder = mAdapter.createViewHolder(parent, viewType);
            mAdapter.bindViewHolder(pinnedViewHolder, headerPosition);
            mStickyHeaderView = pinnedViewHolder.itemView;

            // read layout parameters
            ViewGroup.LayoutParams layoutParams = mStickyHeaderView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mStickyHeaderView.setLayoutParams(layoutParams);
            }

            int heightMode = View.MeasureSpec.getMode(layoutParams.height);
            int heightSize = View.MeasureSpec.getSize(layoutParams.height);

            if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                heightMode = View.MeasureSpec.EXACTLY;
            }

            final int maxHeight = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
            if (heightSize > maxHeight) {
                heightSize = maxHeight;
            }

            // measure & layout
            final int ws = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
            final int hs = View.MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            mStickyHeaderView.measure(ws, hs);
            mStickyHeaderView.layout(0, 0, mStickyHeaderView.getMeasuredWidth(), mStickyHeaderView.getMeasuredHeight());
        }
    }

    private int findPinnedHeaderPosition(int fromPosition) {
        if (fromPosition > mAdapter.getItemCount()) {
            return -1;
        }

        for (int position = fromPosition; position >= 0; position--) {
            final int viewType = mAdapter.getItemViewType(position);
            if (isStickyViewType(viewType)) {
                return position;
            }
        }

        return -1;
    }

    private boolean isStickyViewType(int viewType) {
        if (!mStickyViewTypes.containsKey(viewType)) {
            mStickyViewTypes.put(viewType, ((StickyHeaderAdapter) mAdapter).isStickyViewType(viewType));
        }

        return mStickyViewTypes.get(viewType);
    }

    private boolean isHeaderView(RecyclerView parent, View v) {
        final int position = parent.getChildPosition(v);
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }
        final int viewType = mAdapter.getItemViewType(position);

        return isStickyViewType(viewType);
    }

    private void checkCache(RecyclerView parent) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (mAdapter != adapter) {
            disableCache();
            if (adapter instanceof StickyHeaderAdapter) {
                mAdapter = adapter;
            } else {
                mAdapter = null;
            }
        }
    }

    private void disableCache() {
        mStickyHeaderView = null;
        mHeaderPosition = -1;
        mStickyViewTypes.clear();
    }

}
