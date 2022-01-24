package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_AdminCarePlans {
    private int id;
    private String topic;
    private String img;
    private String notes;

    public DataHandler_AdminCarePlans(int id, String header, String topic, String img, String notes) {
        this.id = id;
        this.topic = topic;
        this.img = img;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getImg() {
        return img;
    }

    public String getNotes() {
        return notes;
    }
}
