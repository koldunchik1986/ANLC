package com.neverlands.anlc.abproxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для хранения набора cookies, аналог CookiePack.cs
 */
public class CookiePack {
    private final Map<String, CookiePackItem> cookies;

    /**
     * Конструктор набора cookies
     */
    public CookiePack() {
        cookies = new HashMap<>();
    }

    /**
     * Добавление cookie
     * @param cookie строка cookie
     */
    public void add(String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            return;
        }

        int equalsIndex = cookie.indexOf('=');
        if (equalsIndex <= 0) {
            return;
        }

        String name = cookie.substring(0, equalsIndex).trim();
        String value = cookie.substring(equalsIndex + 1).trim();
        
        // Проверка на expires
        if (name.equalsIgnoreCase("expires")) {
            return;
        }

        // Проверка на path
        if (name.equalsIgnoreCase("path")) {
            return;
        }

        // Проверка на domain
        if (name.equalsIgnoreCase("domain")) {
            return;
        }

        // Проверка на secure
        if (name.equalsIgnoreCase("secure")) {
            return;
        }

        // Проверка на httponly
        if (name.equalsIgnoreCase("httponly")) {
            return;
        }

        // Проверка на max-age
        if (name.equalsIgnoreCase("max-age")) {
            return;
        }

        // Проверка на samesite
        if (name.equalsIgnoreCase("samesite")) {
            return;
        }

        // Добавление cookie
        cookies.put(name, new CookiePackItem(name, value));
    }

    /**
     * Удаление cookie
     * @param name имя cookie
     */
    public void remove(String name) {
        if (name == null || name.isEmpty()) {
            return;
        }

        cookies.remove(name);
    }

    /**
     * Получение значения cookie по имени
     * @param name имя cookie
     * @return значение cookie или null, если cookie не найдена
     */
    public String getValue(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        CookiePackItem item = cookies.get(name);
        return item != null ? item.getValue() : null;
    }

    /**
     * Установка значения cookie
     * @param name имя cookie
     * @param value значение cookie
     */
    public void setValue(String name, String value) {
        if (name == null || name.isEmpty()) {
            return;
        }

        cookies.put(name, new CookiePackItem(name, value));
    }

    /**
     * Проверка наличия cookie
     * @param name имя cookie
     * @return true, если cookie существует
     */
    public boolean exists(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return cookies.containsKey(name);
    }

    /**
     * Очистка всех cookies
     */
    public void clear() {
        cookies.clear();
    }

    /**
     * Получение количества cookies
     * @return количество cookies
     */
    public int getCount() {
        return cookies.size();
    }

    /**
     * Получение всех cookies
     * @return карта cookies (имя -> значение)
     */
    public Map<String, String> getAllCookies() {
        Map<String, String> result = new HashMap<>();
        
        for (Map.Entry<String, CookiePackItem> entry : cookies.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getValue());
        }
        
        return result;
    }

    /**
     * Получение строкового представления набора cookies
     * @return строковое представление набора cookies
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (CookiePackItem item : cookies.values()) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(item.getName()).append("=").append(item.getValue());
        }
        
        return sb.toString();
    }
}