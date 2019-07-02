package com.sargis.kh.apixu.favorite_weather.utils;

public interface Constants {

    interface UrlConstants {
        String BASE_URL = "https://api.apixu.com/v1/";
        String SEARCH_URL = BASE_URL + "search.json?key=" + ApiConstants.API_KEY + "&q=";
        String CURRENT_WEATHER_URL = BASE_URL + "current.json?key=" + ApiConstants.API_KEY + "&q=";
    }

    interface ApiConstants {
        String API_KEY = "65c8401892bc4738b3d104240192006";
    }

    interface BundleConstants {
        String STATE_MODE = "STATE_MODE";
        String DELETE_MODE_SELECTED_STATE = "DELETE_MODE_SELECTED_STATE";
        String SELECTED_ITEMS_ORDER_INDEXES = "SELECTED_ITEMS_ORDER_INDEXES";

        String IS_ERROR_VISIBLE = "IS_ERROR_VISIBLE";
        String ERROR_MESSAGE = "ERROR_MESSAGE";

        String QUERY_SEARCH = "QUERY_SEARCH";
        String SEARCH_DATA_MODEL = "SEARCH_DATA_MODEL";

        String SEARCH_STATE_MODE = "SEARCH_STATE_MODE";
        String IS_FAVORITE_WEATHER_LOADING = "IS_FAVORITE_WEATHER_LOADING";
    }

}