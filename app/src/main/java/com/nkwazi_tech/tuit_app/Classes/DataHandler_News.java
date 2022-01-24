package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_News {

    private String Lecturername;
    private String To;
    private String Subject;
    private String Mbody;
    private String Time;
    private String file;
    private String profilepicture;
    private int id;

    public DataHandler_News(int id, String lecturername, String to, String subject, String mbody, String time, String file, String profilepicture) {
        this.id = id;
        this.Lecturername = lecturername;
        this.To = to;
        this.Subject = subject;
        this.Mbody = mbody;
        this.Time = time;
        this.file = file;
        this.profilepicture = profilepicture;
    }

    public int getId() {
        return id;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public String getLecturername() {
        return Lecturername;
    }

    public String getTo() {
        return To;
    }

    public String getSubject() {
        return Subject;
    }

    public String getMbody() {
        return Mbody;
    }

    public String getTime() {
        return Time;
    }


    public String getFile() {
        return file;
    }
}
