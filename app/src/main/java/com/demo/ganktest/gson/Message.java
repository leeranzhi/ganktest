package com.demo.ganktest.gson;

import com.google.gson.annotations.SerializedName;


public class Message {
    @SerializedName("createdAt")
    public String createdTime;
    public String desc;
    public String url;
    public String who;
    @SerializedName("_id")
    public String id;
    public String source;
    public String type;
    public String used;
    @SerializedName("publishedAt")
    public String publishedTime;
}
