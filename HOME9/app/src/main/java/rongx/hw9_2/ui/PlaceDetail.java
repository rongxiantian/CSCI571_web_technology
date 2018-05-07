package rongx.hw9_2.ui;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaceDetail implements Serializable {
    private String formatted_address;
    private String getFormatted_phone_number;
    private int price_level;
    private double rating;
    private String url;
    private String website;
    private String name;
    private double[] location;

    public PlaceDetail(String formatted_address, String getFormatted_phone_number, int price_level, double rating, String url, String website, String name, double[] location) {
        this.formatted_address = formatted_address;
        this.getFormatted_phone_number = getFormatted_phone_number;
        this.price_level = price_level;
        this.rating = rating;
        this.url = url;
        this.website = website;
        this.name = name;
        this.location = location;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //private ArrayList<String> photos;

    public String getFormatted_address() {

        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getGetFormatted_phone_number() {
        return getFormatted_phone_number;
    }

    public void setGetFormatted_phone_number(String getFormatted_phone_number) {
        this.getFormatted_phone_number = getFormatted_phone_number;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
}
