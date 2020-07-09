package com.chip.parkpro1.data.models;

import androidx.annotation.ColorRes;

import java.io.Serializable;

public class Sample implements Serializable {

    final int color;
    private final String name;

    public Sample(@ColorRes int color, String name) {
        this.color = color;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }


}