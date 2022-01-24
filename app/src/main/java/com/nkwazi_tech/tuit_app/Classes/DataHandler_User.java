package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_User {
    private int id, courseid,schoolId;
    private String  email, phone, name, profilepicture, coverpicture, accounttype;
    private String practisingnumber,lecturercourse;

    public DataHandler_User(int id, int courseid, String email,  String phone, String name
            ,  String profilepicture, String coverpicture, String accounttype, String practisingnumber, String lecturercourse,int schoolId) {
        this.id = id;
        this.schoolId = schoolId;
        this.courseid = courseid;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.profilepicture = profilepicture;
        this.coverpicture = coverpicture;
        this.accounttype = accounttype;
        this.practisingnumber = practisingnumber;
        this.lecturercourse = lecturercourse;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseid() {
        return courseid;
    }

    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public String getCoverpicture() {
        return coverpicture;
    }

    public void setCoverpicture(String coverpicture) {
        this.coverpicture = coverpicture;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getPractisingnumber() {
        return practisingnumber;
    }

    public void setPractisingnumber(String practisingnumber) {
        this.practisingnumber = practisingnumber;
    }

    public String getLecturercourse() {
        return lecturercourse;
    }

    public void setLecturercourse(String lecturercourse) {
        this.lecturercourse = lecturercourse;
    }
}
