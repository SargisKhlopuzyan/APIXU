package com.sargis.kh.apixu.utils;

public interface Constants {

    interface UrlConstants {
        String BASE_URL = "https://api.apixu.com/v1/";

        String SEARCH_URL = "https://api.apixu.com/v1/search.json?key=" + ApiConstants.API_KEY + "&q=";
        String CURRENT_WEATHER_URL = "https://api.apixu.com/v1/current.json?key=" + ApiConstants.API_KEY + "&q=";
    }

    interface ApiConstants {
        String API_KEY = "65c8401892bc4738b3d104240192006";
    }

    interface BundleConstants {
        String STATE_MODE = "STATE_MODE";
        String SELECTED_STATE = "SELECTED_STATE";
        String SELECTED_ITEMS_COUNT = "SELECTED_ITEMS_COUNT";
    }

}