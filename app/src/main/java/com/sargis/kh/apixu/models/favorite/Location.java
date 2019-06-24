package com.sargis.kh.apixu.models.favorite;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {

    @SerializedName("name")
    public String name;

    @SerializedName("region")
    public String region;

    @SerializedName("country")
    public String country;

    @SerializedName("lat")
    public String lat;

    @SerializedName("lon")
    public String lon;

    @SerializedName("tz_id")
    public String tzId;

    @SerializedName("localtime_epoch")
    public String localTimeEpoch;

    @SerializedName("localtime")
    public String localTime;
}
