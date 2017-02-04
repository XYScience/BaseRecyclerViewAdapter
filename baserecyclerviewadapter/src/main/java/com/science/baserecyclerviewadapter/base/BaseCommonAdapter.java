package com.science.baserecyclerviewadapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.science.baserecyclerviewadapter.interfaces.OnClickListener;

import java.util.List;

/**
 * @author 幸运Science
 * @description
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/10/17
 */

public abstract class BaseCommonAdapter<T> extends BaseAdapter {

    public abstract void convertCommon(ViewHolder viewHolder, T data, int position); // 设置普通Item数据

    public BaseCommonAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final List dataList, final int position) {
        convertCommon(viewHolder, (T) dataList, position);
    }

    @Override
    public void convertItemClick(final ViewHolder viewHolder, final List dataList, final int position) {
        viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClicks(View v) {
                if (mOnItemClickListener != null && !dataList.isEmpty()) {
                    /**
                     * 删除单条item
                     * 注：不能直接使用notifyItemRemoved(position)，参数要使用getLayoutPosition()或者getAdapterPosition()，
                     * 因为函数里面的传入的参数position，它是在进行onBind操作时确定的，在删除单项后，
                     * 已经出现在画面里的项不会再有调用onBind机会，这样它保留的position一直是未进行删除操作前的position值。
                     *
                     */
                    mOnItemClickListener.onItemClick(dataList.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
                }
            }
        });
    }
}
