package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_Explorer {
    private int id,price;
    private String coursename;
    private String img;

    public DataHandler_Explorer(int id, int price, String coursename, String img) {
        this.id = id;
        this.price = price;
        this.coursename = coursename;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public String getCoursename() {
        return coursename;
    }

    public String getImg() {
        return img;
    }
}
