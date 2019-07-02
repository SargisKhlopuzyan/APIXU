package com.sargis.kh.apixu.enums;

public enum SearchStateMode {

    Non(0),
    Empty(1),
    Loading(2),
    Normal(3);

    private int index;

    SearchStateMode(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
