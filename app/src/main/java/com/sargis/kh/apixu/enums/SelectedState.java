package com.sargis.kh.apixu.enums;

public enum SelectedState {
    Unselected(0),
    Selected(1),
    AllSelected(2);

    private int index;

    SelectedState(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
