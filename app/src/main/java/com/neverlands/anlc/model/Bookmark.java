package com.neverlands.anlc.model;

public class Bookmark {
    public String title;
    public String url;
    public String regNum;
    // Для Android: SmallIcon можно реализовать через Drawable или Bitmap при необходимости
    // public Drawable smallIcon;

    public Bookmark() {}

    public Bookmark(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }
}
