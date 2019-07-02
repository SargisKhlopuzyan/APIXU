package com.sargis.kh.apixu.favorite_weather.models.favorite;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Condition implements Serializable {

    @SerializedName("text")
    public String text;

    @SerializedName("icon")
    public String icon;

    @SerializedName("code")
    public Integer code;

}
