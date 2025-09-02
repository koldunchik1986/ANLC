package com.neverlands.anlc.abproxy;

/**
 * Класс для представления заголовка HTTP, аналог HttpHeaderItem.cs
 */
public class HttpHeaderItem {
    private final String name;
    private final String value;

    /**
     * Конструктор заголовка HTTP
     * @param name имя заголовка
     * @param value значение заголовка
     */
    public HttpHeaderItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Получение имени заголовка
     * @return имя заголовка
     */
    public String getName() {
        return name;
    }

    /**
     * Получение значения заголовка
     * @return значение заголовка
     */
    public String getValue() {
        return value;
    }

    /**
     * Получение строкового представления заголовка
     * @return строковое представление заголовка
     */
    @Override
    public String toString() {
        return name + ": " + value;
    }

    /**
     * Проверка равенства заголовков
     * @param obj объект для сравнения
     * @return true, если заголовки равны
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        HttpHeaderItem that = (HttpHeaderItem) obj;
        
        if (name != null ? !name.equalsIgnoreCase(that.name) : that.name != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    /**
     * Получение хеш-кода заголовка
     * @return хеш-код заголовка
     */
    @Override
    public int hashCode() {
        int result = name != null ? name.toLowerCase().hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}