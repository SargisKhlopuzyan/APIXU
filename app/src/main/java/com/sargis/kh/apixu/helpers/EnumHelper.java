package com.sargis.kh.apixu.helpers;

import com.sargis.kh.apixu.enums.SelectedState;
import com.sargis.kh.apixu.enums.StateMode;


public class EnumHelper {

    public static StateMode getStateMode(int index) {

        for (StateMode stateMode: StateMode.values()) {
            if (index == stateMode.getIndex())
                return stateMode;
        }
        return null;
    }

    public static SelectedState getSelectedState(int index) {

        for (SelectedState selectedState: SelectedState.values()) {
            if (index == selectedState.getIndex())
                return selectedState;
        }
        return null;
    }

}
