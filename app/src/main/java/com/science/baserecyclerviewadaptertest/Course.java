package com.science.baserecyclerviewadaptertest;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2016/10/16
 */

public class Course {

    private String name;
    private int age;

    public Course(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
