package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_MyCourse3 {

    private int id;
    private String coursename;

    public DataHandler_MyCourse3(int id, String coursename, String img) {
        this.coursename = coursename;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCoursename() {
        return coursename;
    }

}
