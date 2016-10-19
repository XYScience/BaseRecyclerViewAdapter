package com.science.baserecyclerviewadaptertest;

import java.util.List;

/**
 * @author SScience
 * @description
 * @email chentushen.science@gmail.com
 * @data 2016/10/11
 */

public class Person {

    private String name;
    private List<Score> score;

    public Person(String name, List<Score> score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Score> getCourse() {
        return score;
    }

    public void setCourse(List<Score> score) {
        this.score = score;
    }

    public static class Score {
        private String java;

        public Score(String java) {
            this.java = java;
        }

        public String getJava() {
            return java;
        }

        public void setJava(String java) {
            this.java = java;
        }
    }
}
