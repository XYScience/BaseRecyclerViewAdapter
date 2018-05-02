package com.science.baserecyclerviewadapter.entity;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2016/10/13
 */

public abstract class SectionEntity<T, K, V> {

    public boolean isHeader;
    public boolean isFooter;
    public T data;
    public K header;
    public V footer;

    public SectionEntity(K header, V footer) {
        this.isHeader = header != null;
        this.isFooter = footer != null;
        this.header = header;
        this.footer = footer;
    }

    public SectionEntity(T data) {
        this.isHeader = false;
        this.isFooter = false;
        this.data = data;
    }
}
