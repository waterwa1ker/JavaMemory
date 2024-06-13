package com.example.java.memory.pojo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Leak {

    private static final int ITERATION_COUNT = 10_000_000;

    private static final List<Double> leaks = new ArrayList<>();

    private Map<LeakKey, Double> map = new HashMap<>();

    public void addValues() {
        for (int i = 0; i < ITERATION_COUNT; ++i) {
            map.put(new LeakKey("Value"), Math.random());
        }
    }

    public void addNumbers() {
        for (int i = 0; i < ITERATION_COUNT; ++i) {
            leaks.add(Math.random());
        }
    }



    public InnerLeak createInnerLeak() {
        return new InnerLeak();
    }

    public class InnerLeak {

    }

}
