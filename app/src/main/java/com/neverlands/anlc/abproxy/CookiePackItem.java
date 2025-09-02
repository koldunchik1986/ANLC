package com.neverlands.anlc.abproxy;

/**
 * Класс для представления отдельной cookie, аналог CookiePackItem.cs
 */
public class CookiePackItem {
    private final String name;
    private final String value;

    /**
     * Конструктор cookie
     * @param name имя cookie
     * @param value значение cookie
     */
    public CookiePackItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Получение имени cookie
     * @return имя cookie
     */
    public String getName() {
        return name;
    }

    /**
     * Получение значения cookie
     * @return значение cookie
     */
    public String getValue() {
        return value;
    }

    /**
     * Получение строкового представления cookie
     * @return строковое представление cookie
     */
    @Override
    public String toString() {
        return name + "=" + value;
    }

    /**
     * Проверка равенства cookies
     * @param obj объект для сравнения
     * @return true, если cookies равны
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CookiePackItem that = (CookiePackItem) obj;
        
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    /**
     * Получение хеш-кода cookie
     * @return хеш-код cookie
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}