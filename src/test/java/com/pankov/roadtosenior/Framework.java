package com.pankov.roadtosenior;

import java.time.LocalDateTime;

public class Framework {

    private int id;
    private String name;
    private String language;
    private String link;
    private LocalDateTime creationDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Framework(String name, String language, String link, LocalDateTime creationDate) {
        this.name = name;
        this.language = language;
        this.link = link;
        this.creationDate = creationDate;
    }

    public Framework(int id, String name, String language, String link, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.language = language;
        this.link = link;
        this.creationDate = creationDate;
    }

    public Framework() {
    }

    @Override
    public String toString() {
        return "Framework{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", link='" + link + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}