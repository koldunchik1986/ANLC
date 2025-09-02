package com.neverlands.anlc;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NeverApi - Основной класс для коммуникации с игровым сервером
 * Это прямой порт класса NeverApi.cs из PC-версии
 */
public class NeverApi {
    private static final String TAG = "NeverApi";
    
    // Базовые URL для игры
    private static final String BASE_URL = "https://www.neverlands.ru/";
    private static final String GAME_URL = BASE_URL + "game.php";
    private static final String MAIN_URL = BASE_URL + "main.php";
    private static final String CHAT_URL = BASE_URL + "ch.php";
    
    // Переменные сессии
    private String sessionId = "";
    private String userId = "";
    private boolean isLoggedIn = false;
    
    // HTTP клиент для выполнения запросов
    private final HttpClient httpClient;
    
    /**
     * Конструктор инициализирует HTTP клиент
     */
    public NeverApi() {
        httpClient = HttpClient.getInstance();
    }
    
    /**
     * Вход в игровой сервер
     * @param username Имя пользователя/логин
     * @param password Пароль пользователя
     * @return true если вход успешен, false в противном случае
     */
    public boolean login(String username, String password) {
        try {
            // Очистить существующие куки
            httpClient.clearCookies();
            
            // Шаг 1: Получить страницу входа для получения начальных куков
            String loginPage = httpClient.get(BASE_URL);
            
            // Шаг 2: Подготовить данные формы входа
            Map<String, String> formData = new HashMap<>();
            formData.put("action", "login");
            formData.put("login", username);
            formData.put("pass", password);
            formData.put("save", "1"); // Запомнить вход
            
            // Шаг 3: Отправить форму входа
            String response = httpClient.post(BASE_URL, formData);
            
            // Шаг 4: Проверить, был ли вход успешным
            if (response.contains("Неверный логин или пароль") || 
                response.contains("Incorrect login or password")) {
                Log.e(TAG, "Вход не удался: Неверное имя пользователя или пароль");
                return false;
            }
            
            // Шаг 5: Извлечь ID пользователя и ID сессии из куков или ответа
            extractSessionInfo();
            
            // Шаг 6: Проверить вход, обратившись к странице игры
            String gamePageResponse = httpClient.get(GAME_URL);
            
            // Если мы можем получить доступ к странице игры, мы вошли в систему
            isLoggedIn = !gamePageResponse.contains("Вы не авторизованы") && 
                         !gamePageResponse.contains("You are not authorized");
            
            Log.d(TAG, "Вход " + (isLoggedIn ? "успешен" : "не удался") + 
                  " для пользователя: " + username);
            
            return isLoggedIn;
            
        } catch (IOException e) {
            Log.e(TAG, "Ошибка входа", e);
            return false;
        }
    }
    
    /**
     * Извлечь ID сессии и ID пользователя из куков или ответа
     */
    private void extractSessionInfo() {
        // Получить ID сессии из кука
        String phpSessionId = httpClient.getCookie("PHPSESSID");
        if (phpSessionId != null) {
            sessionId = phpSessionId;
            Log.d(TAG, "ID сессии извлечен: " + sessionId);
        }
        
        // Получить ID пользователя - это может быть в другом куке или в содержимом страницы
        // Для этого примера мы предполагаем, что он находится в куке с именем "user_id"
        String userIdCookie = httpClient.getCookie("user_id");
        if (userIdCookie != null) {
            userId = userIdCookie;
            Log.d(TAG, "ID пользователя извлечен: " + userId);
        }
    }
    
    /**
     * Проверить, вошел ли пользователь в систему
     * @return true если вошел, false в противном случае
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    /**
     * Получить информацию о персонаже
     * @return JSON строка с данными персонажа или null, если не удалось
     */
    public String getCharacterInfo() {
        if (!isLoggedIn) {
            Log.e(TAG, "Невозможно получить информацию о персонаже: Не выполнен вход");
            return null;
        }
        
        try {
            // Запросить информацию о персонаже из игры
            String response = httpClient.get(GAME_URL + "?act=who");
            
            // Извлечь данные персонажа с помощью регулярного выражения
            // Этот шаблон может потребовать корректировки в зависимости от фактического формата ответа
            Pattern pattern = Pattern.compile("var\\s+chardata\\s*=\\s*(\\{.*?\\});", 
                                             Pattern.DOTALL);
            Matcher matcher = pattern.matcher(response);
            
            if (matcher.find()) {
                String jsonStr = matcher.group(1);
                // Проверить, что это правильный JSON
                try {
                    new JSONObject(jsonStr);
                    return jsonStr;
                } catch (JSONException e) {
                    Log.e(TAG, "Не удалось разобрать данные персонажа как JSON", e);
                    return null;
                }
            } else {
                Log.e(TAG, "Данные персонажа не найдены в ответе");
                return null;
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при получении информации о персонаже", e);
            return null;
        }
    }
    
    /**
     * Получить предметы инвентаря
     * @return JSON строка с данными инвентаря или null, если не удалось
     */
    public String getInventory() {
        if (!isLoggedIn) {
            Log.e(TAG, "Невозможно получить инвентарь: Не выполнен вход");
            return null;
        }
        
        try {
            // Запросить инвентарь из игры
            String response = httpClient.get(GAME_URL + "?act=inv&s=1");
            
            // Извлечь данные инвентаря с помощью регулярного выражения
            Pattern pattern = Pattern.compile("var\\s+invdata\\s*=\\s*(\\{.*?\\});", 
                                             Pattern.DOTALL);
            Matcher matcher = pattern.matcher(response);
            
            if (matcher.find()) {
                String jsonStr = matcher.group(1);
                // Проверить, что это правильный JSON
                try {
                    new JSONObject(jsonStr);
                    return jsonStr;
                } catch (JSONException e) {
                    Log.e(TAG, "Не удалось разобрать данные инвентаря как JSON", e);
                    return null;
                }
            } else {
                Log.e(TAG, "Данные инвентаря не найдены в ответе");
                return null;
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при получении инвентаря", e);
            return null;
        }
    }
    
    /**
     * Отправить сообщение в чат
     * @param message Текст сообщения
     * @param channel Канал чата (например, "general", "trade", "clan")
     * @return true если сообщение успешно отправлено, false в противном случае
     */
    public boolean sendChatMessage(String message, String channel) {
        if (!isLoggedIn) {
            Log.e(TAG, "Невозможно отправить сообщение в чат: Не выполнен вход");
            return false;
        }
        
        try {
            // Кодировать сообщение для URL
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            String encodedChannel = URLEncoder.encode(channel, StandardCharsets.UTF_8.toString());
            
            // Подготовить данные формы
            Map<String, String> formData = new HashMap<>();
            formData.put("act", "say");
            formData.put("to", encodedChannel);
            formData.put("msg", encodedMessage);
            
            // Отправить сообщение
            String response = httpClient.post(CHAT_URL, formData);
            
            // Проверить, было ли сообщение отправлено успешно
            // Точный шаблон ответа зависит от игрового сервера
            return !response.contains("error") && !response.contains("ошибка");
            
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при отправке сообщения в чат", e);
            return false;
        }
    }
    
    /**
     * Переместить персонажа в игровом мире
     * @param direction Направление движения (n, s, e, w, ne, nw, se, sw)
     * @return true если перемещение успешно, false в противном случае
     */
    public boolean move(String direction) {
        if (!isLoggedIn) {
            Log.e(TAG, "Невозможно переместиться: Не выполнен вход");
            return false;
        }
        
        try {
            // Подготовить данные формы
            Map<String, String> formData = new HashMap<>();
            formData.put("act", "go");
            formData.put("dir", direction);
            
            // Отправить команду перемещения
            String response = httpClient.post(GAME_URL, formData);
            
            // Проверить, было ли перемещение успешным
            // Точный шаблон ответа зависит от игрового сервера
            return !response.contains("error") && !response.contains("ошибка");
            
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при перемещении персонажа", e);
            return false;
        }
    }
    
    /**
     * Получить информацию о текущей локации
     * @return JSON строка с данными локации или null, если не удалось
     */
    public String getLocation() {
        if (!isLoggedIn) {
            Log.e(TAG, "Невозможно получить локацию: Не выполнен вход");
            return null;
        }
        
        try {
            // Запросить информацию о локации из игры
            String response = httpClient.get(GAME_URL + "?act=map");
            
            // Извлечь данные локации с помощью регулярного выражения
            Pattern pattern = Pattern.compile("var\\s+locdata\\s*=\\s*(\\{.*?\\});", 
                                             Pattern.DOTALL);
            Matcher matcher = pattern.matcher(response);
            
            if (matcher.find()) {
                String jsonStr = matcher.group(1);
                // Проверить, что это правильный JSON
                try {
                    new JSONObject(jsonStr);
                    return jsonStr;
                } catch (JSONException e) {
                    Log.e(TAG, "Не удалось разобрать данные локации как JSON", e);
                    return null;
                }
            } else {
                Log.e(TAG, "Данные локации не найдены в ответе");
                return null;
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при получении локации", e);
            return null;
        }
    }
    
    /**
     * Выйти из игры
     * @return true если выход успешен, false в противном случае
     */
    public boolean logout() {
        if (!isLoggedIn) {
            // Уже вышли
            return true;
        }
        
        try {
            // Отправить запрос на выход
            String response = httpClient.get(BASE_URL + "?action=logout");
            
            // Очистить куки и информацию о сессии
            httpClient.clearCookies();
            sessionId = "";
            userId = "";
            isLoggedIn = false;
            
            return true;
            
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при выходе", e);
            return false;
        }
    }
    
    /**
     * Получить ID сессии
     * @return Текущий ID сессии
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Получить ID пользователя
     * @return Текущий ID пользователя
     */
    public String getUserId() {
        return userId;
    }
}