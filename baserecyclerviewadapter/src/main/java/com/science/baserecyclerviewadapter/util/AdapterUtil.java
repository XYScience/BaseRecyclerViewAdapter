package com.science.baserecyclerviewadapter.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2016/9/30
 */

public class AdapterUtil {

    public static View inflate(Context context, int layoutId, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }
}
