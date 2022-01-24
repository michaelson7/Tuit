package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_Group_all_list {
    private String GroupName;
    private String NewGroupName;
    private String GroupImg;
    private String GroupDiscription;
    private String Admin;
    private int Members;

    public DataHandler_Group_all_list(String groupName, String groupImg, String groupDiscription, String admin, int members,String newGroupName) {
        GroupName = groupName;
        GroupImg = groupImg;
        GroupDiscription = groupDiscription;
        Admin = admin;
        Members = members;
        NewGroupName= newGroupName;
    }

    public String getGroupName() {
        return GroupName;
    }

    public String getGroupImg() {
        return GroupImg;
    }

    public String getGroupDiscription() {
        return GroupDiscription;
    }

    public String getAdmin() {
        return Admin;
    }

    public String getNewGroupName() {
        return NewGroupName;
    }

    public int getMembers() {
        return Members;
    }
}
