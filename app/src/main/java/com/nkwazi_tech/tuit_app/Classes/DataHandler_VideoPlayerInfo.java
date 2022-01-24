package com.nkwazi_tech.tuit_app.Classes;

import android.content.Context;

public class DataHandler_VideoPlayerInfo {

    private String title;
    private String description;
    private String video;
    private String username;
    private String img;
    private int views;
    private int videoid;

    private static DataHandler_VideoPlayerInfo mInstance;

    private DataHandler_VideoPlayerInfo(Context context) {
    }

    public static synchronized DataHandler_VideoPlayerInfo getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataHandler_VideoPlayerInfo(context);
        }
        return mInstance;
    }

    public void DataHandler_VideoPlayerInfo(String title, String description, String video,
                                            String username, String img, int likes,
                                            int comments, int views, int videoid, String tags) {
        this.title = title;
        this.description = description;
        this.video = video;
        this.username = username;
        this.img = img;
        this.views = views;
        this.videoid = videoid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVideo() {
        return video;
    }

    public String getUsername() {
        return username;
    }

    public String getImg() {
        return img;
    }

    public int getViews() {
        return views;
    }

    public int getVideoid() {
        return videoid;
    }

}
