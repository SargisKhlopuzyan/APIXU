package com.sargis.kh.apixu.favorite_weather.database.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "items")
public class Item {

    @PrimaryKey
    @NonNull
    private Long id;

    private Integer order_index;

    private String name;

    private String region;

    private String country;

    private String condition_text;

    private Float temp_c;

    private Float wind_kph;

    private String wind_dir;

    private String icon;

    @NonNull
    public Long getId() {
        return id;
    }

    public Integer getOrder_index() {
        return order_index;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public String getCondition_text() {
        return condition_text;
    }

    public Float getTemp_c() {
        return temp_c;
    }

    public Float getWind_kph() {
        return wind_kph;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public String getIcon() {
        return icon;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public void setOrder_index(Integer order_index) {
        this.order_index = order_index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCondition_text(String condition_text) {
        this.condition_text = condition_text;
    }

    public void setTemp_c(Float temp_c) {
        this.temp_c = temp_c;
    }

    public void setWind_kph(Float wind_kph) {
        this.wind_kph = wind_kph;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
