package com.science.baserecyclerviewadapter.base;

import android.content.Context;
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

    public BaseCommonAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(final ViewHolder viewHolder, final List dataList, final int position) {
        convertCommon(viewHolder, (T) dataList, position);
        viewHolder.getConvertView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClicks(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(viewHolder, dataList.get(position), position);
                }
            }
        });
    }
}
