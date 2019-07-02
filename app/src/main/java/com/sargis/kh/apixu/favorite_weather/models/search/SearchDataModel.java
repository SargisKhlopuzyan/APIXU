package com.sargis.kh.apixu.favorite_weather.models.search;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SearchDataModel implements Serializable {

    @SerializedName("id")
    public Integer id;

    @SerializedName("name")
    public String name;

    @SerializedName("region")
    public String region;

    @SerializedName("country")
    public String country;

    @SerializedName("lat")
    public Float lat;

    @SerializedName("lon")
    public Float lon;

    @SerializedName("url")
    public String url;

}
