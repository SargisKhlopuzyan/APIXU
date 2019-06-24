package com.sargis.kh.apixu.helpers;

import com.sargis.kh.apixu.utils.Constants;

public class UrlHelper {

    public static String getDataBySearchQuery(String query) {
        String url = Constants.UrlConstants.SEARCH_URL + query;
        return url;
    }

    public static String getDataByCurrentWeatherQuery(String query) {
        String url = Constants.UrlConstants.CURRENT_WEATHER_URL + query;
        return url;
    }

}
