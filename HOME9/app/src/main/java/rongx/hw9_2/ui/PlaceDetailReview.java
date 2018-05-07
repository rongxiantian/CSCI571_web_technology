package rongx.hw9_2.ui;

import java.io.Serializable;

public class PlaceDetailReview  implements Serializable {
    private String profile_photo_url;
    private String author_name;
    private int rating;
    private long time;
    private String text;
    private String author_url;

    public PlaceDetailReview(String profile_photo_url, String author_name, int rating, long time, String text, String author_url) {
        this.profile_photo_url = profile_photo_url;
        this.author_name = author_name;
        this.rating = rating;
        this.time = time;
        this.text = text;
        this.author_url = author_url;
    }

    public String getProfile_photo_url() {

        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }
}
