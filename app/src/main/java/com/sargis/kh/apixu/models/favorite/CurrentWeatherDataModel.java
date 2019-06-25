package com.sargis.kh.apixu.models.favorite;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CurrentWeatherDataModel implements Serializable {

    public Long id;

    public Long orderIndex;

    @SerializedName("location")
    public Location location;

    @SerializedName("current")
    public Current current;

}
