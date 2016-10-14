package com.science.baserecyclerviewadapter.entity;

/**
 * @author 幸运Science
 * @description
 * @email chentushen.science@gmail.com,274240671@qq.com
 * @data 2016/10/13
 */

public abstract class SectionEntity<T> {

    public boolean isHeader;
    public boolean isFooter;
    public T data;

    public SectionEntity(boolean isHeader, boolean isFooter, T data) {
        this.isHeader = isHeader;
        this.isFooter = isFooter;
        this.data = data;
    }

    public SectionEntity(T data) {
        this.isHeader = false;
        this.isFooter = false;
        this.data = data;
    }
}
