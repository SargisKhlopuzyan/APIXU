package com.sargis.kh.apixu.favorite_weather.enums;

public enum StateMode {

    Normal(0),
    Edit(1),
    Delete(2);

    private int index;

    StateMode(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
