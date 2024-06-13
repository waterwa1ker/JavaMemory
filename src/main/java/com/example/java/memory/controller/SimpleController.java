package com.example.java.memory.controller;

import com.example.java.memory.pojo.Leak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SimpleController {

    private static final int ITERATION_COUNT = 10;
    private static final int SECOND_IN_MILLISECONDS = 1000;

    private List<Leak.InnerLeak> innerLeaks = new ArrayList<>();

    private final Leak leak;

    @Autowired
    public SimpleController(Leak leak) {
        this.leak = leak;
    }

    @GetMapping
    public void createLeak() {
        leak.addNumbers();
    }

    @GetMapping("/inner")
    public void createInnerInstance() throws InterruptedException {
        for (int i = 0; i < ITERATION_COUNT; ++i) {
            Leak leak = new Leak();
            innerLeaks.add(leak.createInnerLeak());
            Thread.sleep(SECOND_IN_MILLISECONDS);
        }
    }

    @GetMapping("/map")
    public void addValuesInMap() {
        leak.addValues();
    }

}
