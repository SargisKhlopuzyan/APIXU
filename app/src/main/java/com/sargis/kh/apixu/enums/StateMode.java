package com.sargis.kh.apixu.enums;

public enum StateMode {

    Normal(0),
    Empty(1),
    Search(2),
    Edit(3),
    Delete(4);

    private int index;

    StateMode(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
