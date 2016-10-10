package com.science.baserecyclerviewadaptertest;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2016/10/11
 */

public class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
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
