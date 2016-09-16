package com.ihandy.a2014011328.bigproject;

import android.util.Log;

import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by ihandysoft on 16/8/23.
 */
public class News {
    private String image;
    private String title;
    private String origin;
    private long id;
    private String source;
    private String category;
    boolean isLiked = false;

    public News(String title, String origin, String image, long id, String category, String source) {
        this.image = image;
        //Log.d("News","The url is "+url);
        this.title = title;
        this.origin = origin;
        this.id = id;
        this.category = category;
        this.source = source;
        //this.isLiked = false;
    }

    public String getImage() {
        return image;
    }
    public String getTitle() {
        return title;
    }
    public String getOrigin() {
        return origin;
    }
    public long getId() {
        return id;
    }
    public String getSource() {
        return source;
    }
    public String getCategory() {
        return category;
    }
    public boolean getIsLiked(){
        return isLiked;
    }
    public void setIsLiked(boolean s){
        this.isLiked = s;
    }

}
