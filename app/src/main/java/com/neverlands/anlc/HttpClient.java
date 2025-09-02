package com.neverlands.anlc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP клиент с управлением куками для Android-версии ABClient
 * Этот класс обрабатывает все HTTP-запросы и поддерживает состояние куков между запросами
 */
public class HttpClient {
    private static final String TAG = "HttpClient";
    private static HttpClient instance;
    private final CookieManager cookieManager;
    
    // Заголовки по умолчанию для всех запросов
    private final Map<String, String> defaultHeaders;

    private HttpClient() {
        // Инициализация менеджера куков с разрешающей политикой
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
        
        // Настройка заголовков по умолчанию, которые будут отправляться с каждым запросом
        defaultHeaders = new HashMap<>();
        defaultHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        defaultHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        defaultHeaders.put("Accept-Language", "en-US,en;q=0.5");
        defaultHeaders.put("Connection", "keep-alive");
    }

    /**
     * Получить экземпляр синглтона HttpClient
     * @return экземпляр HttpClient
     */
    public static synchronized HttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }
        return instance;
    }

    /**
     * Выполнить GET-запрос по указанному URL
     * @param urlString URL для отправки запроса
     * @return Тело ответа в виде строки
     * @throws IOException Если соединение не удалось
     */
    public String get(String urlString) throws IOException {
        return get(urlString, null);
    }

    /**
     * Выполнить GET-запрос с пользовательскими заголовками
     * @param urlString URL для отправки запроса
     * @param headers Дополнительные заголовки для включения
     * @return Тело ответа в виде строки
     * @throws IOException Если соединение не удалось
     */
    public String get(String urlString, Map<String, String> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod("GET");
            
            // Добавить заголовки по умолчанию
            for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
            
            // Добавить пользовательские заголовки, если они предоставлены
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            
            // Получить ответ
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "GET код ответа: " + responseCode + " для URL: " + urlString);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection);
            } else {
                Log.e(TAG, "GET запрос не удался с кодом ответа: " + responseCode);
                return readErrorResponse(connection);
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Выполнить POST-запрос с данными формы
     * @param urlString URL для отправки запроса
     * @param formData Данные формы в виде пар ключ-значение
     * @return Тело ответа в виде строки
     * @throws IOException Если соединение не удалось
     */
    public String post(String urlString, Map<String, String> formData) throws IOException {
        return post(urlString, formData, null);
    }

    /**
     * Выполнить POST-запрос с данными формы и пользовательскими заголовками
     * @param urlString URL для отправки запроса
     * @param formData Данные формы в виде пар ключ-значение
     * @param headers Дополнительные заголовки для включения
     * @return Тело ответа в виде строки
     * @throws IOException Если соединение не удалось
     */
    public String post(String urlString, Map<String, String> formData, Map<String, String> headers) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            
            // Добавить заголовки по умолчанию
            for (Map.Entry<String, String> header : defaultHeaders.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
            
            // Добавить заголовок типа содержимого для данных формы
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            // Добавить пользовательские заголовки, если они предоставлены
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            
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
            
            // Записать данные формы в соединение
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = formDataBuilder.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Получить ответ
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "POST код ответа: " + responseCode + " для URL: " + urlString);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection);
            } else {
                Log.e(TAG, "POST запрос не удался с кодом ответа: " + responseCode);
                return readErrorResponse(connection);
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Прочитать тело ответа из соединения
     * @param connection Активное HTTP-соединение
     * @return Тело ответа в виде строки
     * @throws IOException Если чтение не удалось
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    /**
     * Прочитать тело ответа об ошибке из соединения
     * @param connection Активное HTTP-соединение
     * @return Тело ответа об ошибке в виде строки
     * @throws IOException Если чтение не удалось
     */
    private String readErrorResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    /**
     * Получить все куки, хранящиеся в менеджере куков
     * @return Список всех куков
     */
    public List<HttpCookie> getCookies() {
        return cookieManager.getCookieStore().getCookies();
    }

    /**
     * Получить определенный кук по имени
     * @param name Имя кука для поиска
     * @return Значение кука или null, если не найдено
     */
    public String getCookie(String name) {
        List<HttpCookie> cookies = getCookies();
        for (HttpCookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Очистить все сохраненные куки
     */
    public void clearCookies() {
        cookieManager.getCookieStore().removeAll();
    }
}