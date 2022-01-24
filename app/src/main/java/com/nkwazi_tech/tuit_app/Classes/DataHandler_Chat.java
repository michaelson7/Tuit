package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_Chat {
    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;
    private String name;
    private String profilephoto;
    private String image;
    private String messageid;

    public DataHandler_Chat(String sender, String receiver, String message,
                            boolean isseen,String name,String profilephoto,String image,String imageid,String messageid) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.name = name;
        this.profilephoto = profilephoto;
        this.image = image;
        this.messageid = messageid;
    }

    public DataHandler_Chat() {
    }

    public String getMessageid() {
        return messageid;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public boolean isIsseen() {
        return isseen;
    }

}
