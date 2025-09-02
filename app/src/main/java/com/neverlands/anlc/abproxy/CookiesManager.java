package com.neverlands.anlc.abproxy;

import android.util.Log;
import android.webkit.CookieManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Менеджер cookies, аналог CookiesManager.cs
 */
public class CookiesManager {
    public static void initialize() {
        // TODO: реализовать инициализацию куки
    }
    private static final String TAG = "CookiesManager";
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Map<String, CookiePack> cookiePacks = new HashMap<>();

    /**
     * Получение строки cookies для домена
     * @param domain домен
     * @return строка cookies
     */
    public static String obtain(String domain) {
        if (domain == null || domain.isEmpty()) {
            return "";
        }

        // Нормализация домена
        domain = normalizeDomain(domain);

        // Получение cookies из WebView
        String cookies = CookieManager.getInstance().getCookie(domain);
        if (cookies == null) {
            cookies = "";
        }

        // Получение cookies из нашего хранилища
        lock.readLock().lock();
        try {
            CookiePack cookiePack = cookiePacks.get(domain);
            if (cookiePack != null) {
                String packCookies = cookiePack.toString();
                if (!packCookies.isEmpty()) {
                    if (cookies.isEmpty()) {
                        cookies = packCookies;
                    } else {
                        cookies += "; " + packCookies;
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }

        return cookies;
    }

    /**
     * Добавление cookie
     * @param domain домен
     * @param cookie строка cookie
     */
    public static void add(String domain, String cookie) {
        if (domain == null || domain.isEmpty() || cookie == null || cookie.isEmpty()) {
            return;
        }

        // Нормализация домена
        domain = normalizeDomain(domain);

        // Добавление cookie в WebView
        CookieManager.getInstance().setCookie(domain, cookie);

        // Добавление cookie в наше хранилище
        lock.writeLock().lock();
        try {
            CookiePack cookiePack = cookiePacks.get(domain);
            if (cookiePack == null) {
                cookiePack = new CookiePack();
                cookiePacks.put(domain, cookiePack);
            }
            cookiePack.add(cookie);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удаление cookie
     * @param domain домен
     * @param name имя cookie
     */
    public static void remove(String domain, String name) {
        if (domain == null || domain.isEmpty() || name == null || name.isEmpty()) {
            return;
        }

        // Нормализация домена
        domain = normalizeDomain(domain);

        // Удаление cookie из WebView
        CookieManager.getInstance().setCookie(domain, name + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT");

        // Удаление cookie из нашего хранилища
        lock.writeLock().lock();
        try {
            CookiePack cookiePack = cookiePacks.get(domain);
            if (cookiePack != null) {
                cookiePack.remove(name);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Очистка всех cookies
     */
    public static void clear() {
        // Очистка cookies в WebView
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        // Очистка нашего хранилища
        lock.writeLock().lock();
        try {
            cookiePacks.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Очистка cookies для домена
     * @param domain домен
     */
    public static void clear(String domain) {
        if (domain == null || domain.isEmpty()) {
            return;
        }

        // Нормализация домена
        domain = normalizeDomain(domain);

        // Получение cookies из WebView
        String cookies = CookieManager.getInstance().getCookie(domain);
        if (cookies != null && !cookies.isEmpty()) {
            // Разбор cookies
            String[] cookieArray = cookies.split(";");
            for (String cookie : cookieArray) {
                int equalsIndex = cookie.indexOf('=');
                if (equalsIndex > 0) {
                    String name = cookie.substring(0, equalsIndex).trim();
                    // Удаление cookie
                    CookieManager.getInstance().setCookie(domain, name + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT");
                }
            }
            CookieManager.getInstance().flush();
        }

        // Очистка нашего хранилища
        lock.writeLock().lock();
        try {
            cookiePacks.remove(domain);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Получение значения cookie по имени
     * @param domain домен
     * @param name имя cookie
     * @return значение cookie или null, если cookie не найдена
     */
    public static String getValue(String domain, String name) {
        if (domain == null || domain.isEmpty() || name == null || name.isEmpty()) {
            return null;
        }

        // Нормализация домена
        domain = normalizeDomain(domain);

        // Получение cookies из WebView
        String cookies = CookieManager.getInstance().getCookie(domain);
        if (cookies != null && !cookies.isEmpty()) {
            // Разбор cookies
            String[] cookieArray = cookies.split(";");
            for (String cookie : cookieArray) {
                int equalsIndex = cookie.indexOf('=');
                if (equalsIndex > 0) {
                    String cookieName = cookie.substring(0, equalsIndex).trim();
                    if (cookieName.equals(name)) {
                        return cookie.substring(equalsIndex + 1).trim();
                    }
                }
            }
        }

        // Получение cookie из нашего хранилища
        lock.readLock().lock();
        try {
            CookiePack cookiePack = cookiePacks.get(domain);
            if (cookiePack != null) {
                return cookiePack.getValue(name);
            }
        } finally {
            lock.readLock().unlock();
        }

        return null;
    }

    /**
     * Установка значения cookie
     * @param domain домен
     * @param name имя cookie
     * @param value значение cookie
     */
    public static void setValue(String domain, String name, String value) {
        if (domain == null || domain.isEmpty() || name == null || name.isEmpty()) {
            return;
        }

        // Нормализация домена
        domain = normalizeDomain(domain);

        // Установка cookie в WebView
        CookieManager.getInstance().setCookie(domain, name + "=" + value);
        CookieManager.getInstance().flush();

        // Установка cookie в нашем хранилище
        lock.writeLock().lock();
        try {
            CookiePack cookiePack = cookiePacks.get(domain);
            if (cookiePack == null) {
                cookiePack = new CookiePack();
                cookiePacks.put(domain, cookiePack);
            }
            cookiePack.setValue(name, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Проверка наличия cookie
     * @param domain домен
     * @param name имя cookie
     * @return true, если cookie существует
     */
    public static boolean exists(String domain, String name) {
        return getValue(domain, name) != null;
    }

    /**
     * Нормализация домена
     * @param domain домен
     * @return нормализованный домен
     */
    private static String normalizeDomain(String domain) {
        // Удаление протокола
        if (domain.startsWith("http://")) {
            domain = domain.substring(7);
        } else if (domain.startsWith("https://")) {
            domain = domain.substring(8);
        }

        // Удаление пути
        int slashIndex = domain.indexOf('/');
        if (slashIndex > 0) {
            domain = domain.substring(0, slashIndex);
        }

        // Удаление порта
        int colonIndex = domain.indexOf(':');
        if (colonIndex > 0) {
            domain = domain.substring(0, colonIndex);
        }

        return domain;
    }

    /**
     * Получение всех cookies для домена
     * @param domain домен
     * @return карта cookies (имя -> значение)
     */
    public static Map<String, String> getAllCookies(String domain) {
        Map<String, String> result = new HashMap<>();

        if (domain == null || domain.isEmpty()) {
            return result;
        }

        // Нормализация домена
        domain = normalizeDomain(domain);

        // Получение cookies из WebView
        String cookies = CookieManager.getInstance().getCookie(domain);
        if (cookies != null && !cookies.isEmpty()) {
            // Разбор cookies
            String[] cookieArray = cookies.split(";");
            for (String cookie : cookieArray) {
                int equalsIndex = cookie.indexOf('=');
                if (equalsIndex > 0) {
                    String name = cookie.substring(0, equalsIndex).trim();
                    String value = cookie.substring(equalsIndex + 1).trim();
                    result.put(name, value);
                }
            }
        }

        // Получение cookies из нашего хранилища
        lock.readLock().lock();
        try {
            CookiePack cookiePack = cookiePacks.get(domain);
            if (cookiePack != null) {
                Map<String, String> packCookies = cookiePack.getAllCookies();
                result.putAll(packCookies);
            }
        } finally {
            lock.readLock().unlock();
        }

        return result;
    }

    /**
     * Получение всех доменов, для которых есть cookies
     * @return список доменов
     */
    public static String[] getAllDomains() {
        lock.readLock().lock();
        try {
            return cookiePacks.keySet().toArray(new String[0]);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Сохранение cookies в WebView
     */
    public static void saveToWebView() {
        lock.readLock().lock();
        try {
            for (Map.Entry<String, CookiePack> entry : cookiePacks.entrySet()) {
                String domain = entry.getKey();
                CookiePack cookiePack = entry.getValue();
                Map<String, String> cookies = cookiePack.getAllCookies();
                
                for (Map.Entry<String, String> cookie : cookies.entrySet()) {
                    CookieManager.getInstance().setCookie(domain, cookie.getKey() + "=" + cookie.getValue());
                }
            }
            CookieManager.getInstance().flush();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Загрузка cookies из WebView
     */
    public static void loadFromWebView() {
        // Получение всех доменов из WebView
        // К сожалению, в Android нет прямого способа получить список всех доменов,
        // для которых есть cookies, поэтому мы можем только загрузить cookies для известных доменов
        lock.writeLock().lock();
        try {
            for (String domain : cookiePacks.keySet()) {
                String cookies = CookieManager.getInstance().getCookie(domain);
                if (cookies != null && !cookies.isEmpty()) {
                    CookiePack cookiePack = new CookiePack();
                    
                    // Разбор cookies
                    String[] cookieArray = cookies.split(";");
                    for (String cookie : cookieArray) {
                        cookiePack.add(cookie.trim());
                    }
                    
                    cookiePacks.put(domain, cookiePack);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}