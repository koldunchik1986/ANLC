package com.neverlands.anlc;

/**
 * Класс для представления закладки, аналог Bookmark.cs
 */
public class Bookmark {
    /**
     * Название закладки
     */
    private String name;
    public String regNum;
    
    /**
     * URL закладки
     */
    private String url;

    /**
     * Конструктор по умолчанию
     */
    public Bookmark() {
        this.name = "";
        this.url = "";
    }

    /**
     * Конструктор с параметрами
     * @param name название закладки
     * @param url URL закладки
     */
    public Bookmark(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Получение названия закладки
     * @return название закладки
     */
    public String getName() {
        return name;
    }

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    /**
     * Установка названия закладки
     * @param name название закладки
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получение URL закладки
     * @return URL закладки
     */
    public String getUrl() {
        return url;
    }

    /**
     * Установка URL закладки
     * @param url URL закладки
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Bookmark bookmark = (Bookmark) obj;
        
        if (name != null ? !name.equals(bookmark.name) : bookmark.name != null) return false;
        return url != null ? url.equals(bookmark.url) : bookmark.url == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}