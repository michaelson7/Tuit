package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_VideoInfo {
    private int id;
    private String title;
    private String description;
    private String video;
    private String name;
    private String img;
    private int likes;
    private int comments;
    private int views;
    private String timestamp;
    private String tags;
    private String approvalresponse;
    private String thumbnail;
    private String videoSize;
    private String videoDuration;

    public DataHandler_VideoInfo(int id, String title, String description, int lecturerid,
                                 int courseid, String video, String name, String img, int likes,
                                 int comments, int views, String timestamp, String tags, String approvalresponse, String thumbnail
            , String videoSize, String videoDuration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.video = video;
        this.name = name;
        this.img = img;
        this.likes = likes;
        this.comments = comments;
        this.views = views;
        this.timestamp = timestamp;
        this.tags = tags;
        this.approvalresponse = approvalresponse;
        this.thumbnail = thumbnail;
        this.videoSize = videoSize;
        this.videoDuration = videoDuration;

    }

    public String getVideoSize() {
        return videoSize;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setApprovalresponse(String approvalresponse) {
        this.approvalresponse = approvalresponse;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getApprovalresponse() {
        return approvalresponse;
    }

    public String getTags() {
        return tags;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getViews() {
        return views;
    }

    public int getId() {
        return id;
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

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }
}
