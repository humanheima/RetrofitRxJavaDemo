package com.hm.retrofitrxjavademo.model;

/**
 * Created by Administrator on 2017/1/10.
 */
public class Dog extends Animal {

    String name;

    public Dog(int head, String name) {
        super(head);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
