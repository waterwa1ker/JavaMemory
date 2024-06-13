package com.example.java.memory.pojo;

public class LeakKey {

    private String value;

    public LeakKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
