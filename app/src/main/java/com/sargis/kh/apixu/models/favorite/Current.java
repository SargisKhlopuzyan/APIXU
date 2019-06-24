package com.sargis.kh.apixu.models.favorite;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Current implements Serializable {

    @SerializedName("last_updated_epoch")
    public Integer last_updated_epoch;

    @SerializedName("last_updated")
    public String last_updated;

    @SerializedName("temp_c")
    public Float temp_c;

    @SerializedName("temp_f")
    public Float temp_f;

    @SerializedName("is_day")
    public Integer is_day;

    @SerializedName("condition")
    public Condition condition;

    @SerializedName("wind_mph")
    public Float wind_mph;

    @SerializedName("wind_kph")
    public Float wind_kph;

    @SerializedName("wind_degree")
    public Integer wind_degree;

    @SerializedName("wind_dir")
    public String wind_dir;

    @SerializedName("pressure_mb")
    public Float pressure_mb;

    @SerializedName("pressure_in")
    public Float pressure_in;

    @SerializedName("precip_mm")
    public Float precip_mm;

    @SerializedName("precip_in")
    public Float precip_in;

    @SerializedName("humidity")
    public Integer humidity;

    @SerializedName("cloud")
    public Integer cloud;

    @SerializedName("feelslike_c")
    public Float feelslike_c;

    @SerializedName("feelslike_f")
    public Float feelslike_f;

    @SerializedName("vis_km")
    public Float vis_km;

    @SerializedName("vis_miles")
    public Float vis_miles;

    @SerializedName("uv")
    public Float uv;

    @SerializedName("gust_mph")
    public Float gust_mph;

    @SerializedName("gust_kph")
    public Float gust_kph;

}
