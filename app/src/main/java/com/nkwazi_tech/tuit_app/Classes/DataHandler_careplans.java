package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_careplans {
    private int id;
    private String Username;
    private String ProfilePicture;
    private String Title;
    private String Description;
    private String Topic;
    private String PdfFile;
    private String Response;

    public DataHandler_careplans(int Id, String username, String profilePicture, String title,
                                 String description, String topic, String pdfFile,String response) {
        id = Id;
        Username = username;
        ProfilePicture = profilePicture;
        Title = title;
        Description = description;
        Topic = topic;
        PdfFile = pdfFile;
        Response = response;
    }

    public String getResponse() {
        return Response;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return Username;
    }

    public String getProfilePicture() {
        return ProfilePicture;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getTopic() {
        return Topic;
    }

    public String getPdfFile() {
        return PdfFile;
    }
}
