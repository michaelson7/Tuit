package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_programs {
    private int prog_id,course_id;
    private String course_name,img,prog_name;

    public DataHandler_programs(int prog_id, int course_id, String course_name, String img, String prog_name) {
        this.prog_id = prog_id;
        this.course_id = course_id;
        this.course_name = course_name;
        this.img = img;
        this.prog_name = prog_name;
    }

    public int getProg_id() {
        return prog_id;
    }

    public void setProg_id(int prog_id) {
        this.prog_id = prog_id;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getProg_name() {
        return prog_name;
    }

    public void setProg_name(String prog_name) {
        this.prog_name = prog_name;
    }
}
