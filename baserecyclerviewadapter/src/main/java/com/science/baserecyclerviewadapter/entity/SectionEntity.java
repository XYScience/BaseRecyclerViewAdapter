package com.science.baserecyclerviewadapter.entity;

/**
 * @author 幸运Science
 * @description
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/10/13
 */

public abstract class SectionEntity<T> {

    public boolean isHeader;
    public T data;

    public SectionEntity(boolean isHeader, T data) {
        this.isHeader = isHeader;
        this.data = data;
    }
}
