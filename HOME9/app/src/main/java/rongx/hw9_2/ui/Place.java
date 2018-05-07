package rongx.hw9_2.ui;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Place implements Serializable {
    private double[] location = new double[2];
    private String icon;
    private String name;
    private String photos;
    private String place_id;
    private int price_level;
    private double rating;
    private String vicinity;

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public int getPrice_level() {
        return price_level;
    }

    public void setPrice_level(int price_level) {
        this.price_level = price_level;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Place() {
    }

    public Place(double[] location, String icon, String name, String place_id, int price_level, double rating, String vicinity) {
        this.location = location;
        this.icon = icon;
        this.name = name;
        this.place_id = place_id;
        this.price_level = price_level;
        this.rating = rating;

        this.vicinity = vicinity;
    }
}
