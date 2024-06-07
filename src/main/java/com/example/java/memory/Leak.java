package com.example.java.memory;

import java.util.ArrayList;
import java.util.List;

public class Leak {

    private final List<Double> leaks = new ArrayList<>();

    public void addNumbers() {
        for (int i = 0; i < 10_000_00; ++i) {
            leaks.add(Math.random());
        }
    }

}
