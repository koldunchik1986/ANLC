package com.neverlands.anlc.abproxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с заголовками HTTP-запросов, аналог HttpRequestHeaders.cs
 */
public class HttpRequestHeaders {
    private final String method;
    private final String url;
    private final List<HttpHeaderItem> headers;

    /**
     * Конструктор заголовков HTTP-запроса
     * @param method метод запроса (GET, POST, и т.д.)
     * @param url URL запроса
     */
    public HttpRequestHeaders(String method, String url) {
        this.method = method;
        this.url = url;
        this.headers = new ArrayList<>();
    }

    /**
     * Получение метода запроса
     * @return метод запроса
     */
    public String getMethod() {
        return method;
    }

    /**
     * Получение URL запроса
     * @return URL запроса
     */
    public String getUrl() {
        return url;
    }

    /**
     * Получение списка заголовков
     * @return список заголовков
     */
    public List<HttpHeaderItem> getHeaders() {
        return headers;
    }

    /**
     * Добавление заголовка
     * @param name имя заголовка
     * @param value значение заголовка
     */
    public void addHeader(String name, String value) {
        headers.add(new HttpHeaderItem(name, value));
    }

    /**
     * Получение значения заголовка по имени
     * @param name имя заголовка
     * @return значение заголовка или null, если заголовок не найден
     */
    public String getHeaderValue(String name) {
        for (HttpHeaderItem header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    /**
     * Получение длины содержимого запроса
     * @return длина содержимого запроса или 0, если заголовок Content-Length не найден
     */
    public int getContentLength() {
        String contentLength = getHeaderValue("Content-Length");
        if (contentLength != null) {
            try {
                return Integer.parseInt(contentLength);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Получение типа содержимого запроса
     * @return тип содержимого запроса или null, если заголовок Content-Type не найден
     */
    public String getContentType() {
        return getHeaderValue("Content-Type");
    }

    /**
     * Получение хоста запроса
     * @return хост запроса или null, если заголовок Host не найден
     */
    public String getHost() {
        return getHeaderValue("Host");
    }

    /**
     * Получение строки User-Agent
     * @return строка User-Agent или null, если заголовок User-Agent не найден
     */
    public String getUserAgent() {
        return getHeaderValue("User-Agent");
    }

    /**
     * Получение строки Referer
     * @return строка Referer или null, если заголовок Referer не найден
     */
    public String getReferer() {
        return getHeaderValue("Referer");
    }

    /**
     * Получение строки Cookie
     * @return строка Cookie или null, если заголовок Cookie не найден
     */
    public String getCookie() {
        return getHeaderValue("Cookie");
    }

    /**
     * Проверка, является ли запрос AJAX-запросом
     * @return true, если запрос является AJAX-запросом
     */
    public boolean isAjaxRequest() {
        String xRequestedWith = getHeaderValue("X-Requested-With");
        return xRequestedWith != null && xRequestedWith.equalsIgnoreCase("XMLHttpRequest");
    }

    /**
     * Проверка, является ли запрос запросом на изображение
     * @return true, если запрос является запросом на изображение
     */
    public boolean isImageRequest() {
        String accept = getHeaderValue("Accept");
        return accept != null && accept.contains("image/");
    }

    /**
     * Проверка, является ли запрос запросом на JavaScript
     * @return true, если запрос является запросом на JavaScript
     */
    public boolean isJavaScriptRequest() {
        String accept = getHeaderValue("Accept");
        return accept != null && accept.contains("application/javascript");
    }

    /**
     * Проверка, является ли запрос запросом на CSS
     * @return true, если запрос является запросом на CSS
     */
    public boolean isCssRequest() {
        String accept = getHeaderValue("Accept");
        return accept != null && accept.contains("text/css");
    }

    /**
     * Проверка, является ли запрос запросом на HTML
     * @return true, если запрос является запросом на HTML
     */
    public boolean isHtmlRequest() {
        String accept = getHeaderValue("Accept");
        return accept != null && accept.contains("text/html");
    }

    /**
     * Проверка, является ли запрос запросом на JSON
     * @return true, если запрос является запросом на JSON
     */
    public boolean isJsonRequest() {
        String accept = getHeaderValue("Accept");
        return accept != null && accept.contains("application/json");
    }

    /**
     * Проверка, является ли запрос запросом на XML
     * @return true, если запрос является запросом на XML
     */
    public boolean isXmlRequest() {
        String accept = getHeaderValue("Accept");
        return accept != null && accept.contains("application/xml");
    }

    /**
     * Проверка, является ли запрос запросом на текст
     * @return true, если запрос является запросом на текст
     */
    public boolean isTextRequest() {
        String accept = getHeaderValue("Accept");
        return accept != null && accept.contains("text/plain");
    }

    /**
     * Получение строки запроса
     * @return строка запроса
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(url).append(" HTTP/1.1\r\n");
        
        for (HttpHeaderItem header : headers) {
            sb.append(header.getName()).append(": ").append(header.getValue()).append("\r\n");
        }
        
        sb.append("\r\n");
        return sb.toString();
    }
}