package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_GeneralResearch {
    private int id;
    private String header;
    private String subheader;
    private String notes;
    private String pdffile;

    public DataHandler_GeneralResearch(int id, String header, String subheader, String notes, String pdffile) {
        this.id = id;
        this.header = header;
        this.subheader = subheader;
        this.notes = notes;
        this.pdffile = pdffile;
    }

    public int getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getSubheader() {
        return subheader;
    }

    public String getNotes() {
        return notes;
    }

    public String getPdffile() {
        return pdffile;
    }
}
