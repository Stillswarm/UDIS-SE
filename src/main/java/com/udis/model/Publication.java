package com.udis.model;

public class Publication {
    private int pubId;
    private String title;
    private String authors;
    private String journal;
    private Integer year;
    private String doi;

    public Publication() { }

    public int getPubId() { return pubId; }
    public void setPubId(int pubId) { this.pubId = pubId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public String getJournal() { return journal; }
    public void setJournal(String journal) { this.journal = journal; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }
}
