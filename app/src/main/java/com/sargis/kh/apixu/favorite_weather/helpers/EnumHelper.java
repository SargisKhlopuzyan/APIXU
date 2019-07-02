package com.sargis.kh.apixu.favorite_weather.helpers;

import com.sargis.kh.apixu.favorite_weather.enums.DeleteModeSelectedState;
import com.sargis.kh.apixu.favorite_weather.enums.SearchStateMode;
import com.sargis.kh.apixu.favorite_weather.enums.StateMode;


public class EnumHelper {

    public static StateMode getStateMode(int index) {

        for (StateMode stateMode: StateMode.values()) {
            if (index == stateMode.getIndex())
                return stateMode;
        }
        return null;
    }

    public static DeleteModeSelectedState getSelectedState(int index) {

        for (DeleteModeSelectedState deleteModeSelectedState : DeleteModeSelectedState.values()) {
            if (index == deleteModeSelectedState.getIndex())
                return deleteModeSelectedState;
        }
        return null;
    }

    public static SearchStateMode getSearchStateMode(int index) {

        for (SearchStateMode searchStateMode : SearchStateMode.values()) {
            if (index == searchStateMode.getIndex())
                return searchStateMode;
        }
        return null;
    }
}
