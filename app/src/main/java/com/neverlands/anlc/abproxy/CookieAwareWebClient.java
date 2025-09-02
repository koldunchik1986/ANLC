package com.neverlands.anlc;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CookieAwareWebClient - Альтернатива WebView для определенных операций
 * Это прямая замена WebClient из .NET, используемого в PC-версии
 */
public class CookieAwareWebClient {
    private static final String TAG = "CookieAwareWebClient";
    
    // Android CookieManager для WebView
    private final CookieManager cookieManager;
    
    // Заголовки по умолчанию для всех запросов
    private final Map<String, String> defaultHeaders;
    
    /**
     * Конструктор инициализирует менеджер куков и заголовки по умолчанию
     */
    public CookieAwareWebClient() {
        // Инициализация менеджера куков
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        
        // Настройка заголовков по умолчанию
        defaultHeaders = new HashMap<>();
        defaultHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        defaultHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        defaultHeaders.put("Accept-Language", "en-US,en;q=0.5");
        defaultHeaders.put("Connection", "keep-alive");
    }
    
    /**
     * Загрузить строковое содержимое с URL
     * @param urlString URL для загрузки
     * @return Строковое содержимое с URL
     * @throws IOException Если соединение не удалось
     */
    public String downloadString(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod("GET");
            
            // Добавить заголовки по умолчанию
            for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
            
            // Добавить куки из менеджера куков
            String cookies = cookieManager.getCookie(urlString);
            if (cookies != null) {
                connection.setRequestProperty("Cookie", cookies);
            }
            
            // Получить ответ
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "GET код ответа: " + responseCode + " для URL: " + urlString);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Прочитать ответ
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    // Сохранить куки из ответа
                    saveCookiesFromConnection(connection, urlString);
                    
                    return response.toString();
                }
            } else {
                Log.e(TAG, "GET запрос не удался с кодом ответа: " + responseCode);
                throw new IOException("HTTP код ошибки: " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * Загрузить строковое содержимое на URL с использованием POST
     * @param urlString URL для загрузки
     * @param data Строковые данные для загрузки
     * @return Ответ от сервера
     * @throws IOException Если соединение не удалось
     */
    public String uploadString(String urlString, String data) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            
            // Добавить заголовки по умолчанию
            for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
            
            // Добавить заголовок типа содержимого
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            // Добавить куки из менеджера куков
            String cookies = cookieManager.getCookie(urlString);
            if (cookies != null) {
                connection.setRequestProperty("Cookie", cookies);
            }
            
            // Записать данные в соединение
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Получить ответ
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "POST код ответа: " + responseCode + " для URL: " + urlString);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Прочитать ответ
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    // Сохранить куки из ответа
                    saveCookiesFromConnection(connection, urlString);
                    
                    return response.toString();
                }
            } else {
                Log.e(TAG, "POST запрос не удался с кодом ответа: " + responseCode);
                throw new IOException("HTTP код ошибки: " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * Загрузить значения с использованием POST (данные формы)
     * @param urlString URL для загрузки
     * @param formData Данные формы в виде пар ключ-значение
     * @return Ответ от сервера
     * @throws IOException Если соединение не удалось
     */
    public String uploadValues(String urlString, Map<String, String> formData) throws IOException {
        // Построить строку данных формы
        StringBuilder formDataBuilder = new StringBuilder();
        if (formData != null) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                if (formDataBuilder.length() > 0) {
                    formDataBuilder.append("&");
                }
                formDataBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        
        // Использовать метод uploadString с данными формы
        return uploadString(urlString, formDataBuilder.toString());
    }
    
    /**
     * Сохранить куки из соединения в менеджер куков
     * @param connection Активное HTTP-соединение
     * @param urlString URL соединения
     */
    private void saveCookiesFromConnection(HttpURLConnection connection, String urlString) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");
        
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cookieManager.setCookie(urlString, cookie);
            }
            Log.d(TAG, "Куки сохранены для URL: " + urlString);
        }
    }
    
    /**
     * Установить кук для определенного домена
     * @param domain Домен для кука
     * @param cookieName Имя кука
     * @param cookieValue Значение кука
     */
    public void setCookie(String domain, String cookieName, String cookieValue) {
        String cookieString = cookieName + "=" + cookieValue;
        cookieManager.setCookie(domain, cookieString);
        Log.d(TAG, "Кук установлен: " + cookieString + " для домена: " + domain);
    }
    
    /**
     * Получить значение кука по имени
     * @param domain Домен для кука
     * @param cookieName Имя кука
     * @return Значение кука или null, если не найдено
     */
    public String getCookie(String domain, String cookieName) {
        String cookies = cookieManager.getCookie(domain);
        if (cookies != null) {
            String[] cookiePairs = cookies.split(";");
            for (String cookiePair : cookiePairs) {
                String[] cookieNameValue = cookiePair.trim().split("=");
                if (cookieNameValue.length == 2 && cookieNameValue[0].equals(cookieName)) {
                    return cookieNameValue[1];
                }
            }
        }
        return null;
    }
    
    /**
     * Очистить все куки
     */
    public void clearCookies() {
        cookieManager.removeAllCookies(null);
        Log.d(TAG, "Все куки очищены");
    }
    
    /**
     * Установить пользовательский заголовок для всех запросов
     * @param name Имя заголовка
     * @param value Значение заголовка
     */
    public void setHeader(String name, String value) {
        defaultHeaders.put(name, value);
        Log.d(TAG, "Заголовок установлен: " + name + "=" + value);
    }
}