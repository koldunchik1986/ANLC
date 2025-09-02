package com.neverlands.anlc;

import android.content.Context;

import org.json.JSONObject;

/**
 * AppVars - Глобальные переменные приложения
 * Этот класс содержит все глобальные переменные, используемые в приложении
 */
public class AppVars {
    // Контекст приложения
    private static Context applicationContext;

    // Глобальные переменные для совместимости
    public static com.neverlands.anlc.myprofile.UserConfig Profile;
    public static Object MainForm;
    public static Object LocalProxy;
    public static Object ClearExplorerCacheFormMain;
    public static AppVersion AppVersion;
    
    // Менеджер аутентификации
    private static AuthManager authManager;
    
    // Менеджер простоя
    private static IdleManager idleManager;
    
    // Переменные состояния игры
    private static JSONObject characterData;
    private static JSONObject inventoryData;
    private static JSONObject locationData;
    
    // Информация о сессии
    private static String sessionId;
    private static String userId;
    private static boolean isLoggedIn;
    
    // Информация о персонаже
    private static String characterName;
    private static int characterLevel;
    private static String characterRace;
    private static String characterClass;
    private static int characterHealth;
    private static int characterMaxHealth;
    private static int characterMana;
    private static int characterMaxMana;
    private static int characterExperience;
    private static int characterGold;
    
    // Информация о локации
    private static String currentLocation;
    private static int currentX;
    private static int currentY;
    
    /**
     * Инициализировать переменные приложения
     * @param context Контекст приложения
     */
    public static void initialize(Context context) {
        applicationContext = context.getApplicationContext();
        authManager = AuthManager.getInstance(applicationContext);
        idleManager = IdleManager.getInstance();
        
        // Инициализировать значениями по умолчанию
        resetGameState();
    }
    
    /**
     * Сбросить переменные состояния игры
     */
    public static void resetGameState() {
        characterData = null;
        inventoryData = null;
        locationData = null;
        
        sessionId = "";
        userId = "";
        isLoggedIn = false;
        
        characterName = "";
        characterLevel = 0;
        characterRace = "";
        characterClass = "";
        characterHealth = 0;
        characterMaxHealth = 0;
        characterMana = 0;
        characterMaxMana = 0;
        characterExperience = 0;
        characterGold = 0;
        
        currentLocation = "";
        currentX = 0;
        currentY = 0;
    }
    
    /**
     * Обновить данные персонажа из JSON
     * @param jsonData JSON данные с сервера
     */
    public static void updateCharacterData(JSONObject jsonData) {
        if (jsonData == null) {
            return;
        }
        
        characterData = jsonData;
        
        try {
            // Извлечь основную информацию о персонаже
            characterName = jsonData.optString("name", "");
            characterLevel = jsonData.optInt("level", 0);
            characterRace = jsonData.optString("race", "");
            characterClass = jsonData.optString("class", "");
            
            // Извлечь здоровье и ману
            characterHealth = jsonData.optInt("hp", 0);
            characterMaxHealth = jsonData.optInt("maxhp", 0);
            characterMana = jsonData.optInt("ma", 0);
            characterMaxMana = jsonData.optInt("maxma", 0);
            
            // Извлечь опыт и золото
            characterExperience = jsonData.optInt("exp", 0);
            characterGold = jsonData.optInt("gold", 0);
        } catch (Exception e) {
            // Записать ошибку, но не вызывать сбой
            e.printStackTrace();
        }
    }
    
    /**
     * Обновить данные инвентаря из JSON
     * @param jsonData JSON данные с сервера
     */
    public static void updateInventoryData(JSONObject jsonData) {
        if (jsonData == null) {
            return;
        }
        
        inventoryData = jsonData;
    }
    
    /**
     * Обновить данные локации из JSON
     * @param jsonData JSON данные с сервера
     */
    public static void updateLocationData(JSONObject jsonData) {
        if (jsonData == null) {
            return;
        }
        
        locationData = jsonData;
        
        try {
            // Извлечь информацию о локации
            currentLocation = jsonData.optString("name", "");
            currentX = jsonData.optInt("x", 0);
            currentY = jsonData.optInt("y", 0);
        } catch (Exception e) {
            // Записать ошибку, но не вызывать сбой
            e.printStackTrace();
        }
    }
    
    /**
     * Обновить информацию о сессии
     * @param newSessionId ID сессии
     * @param newUserId ID пользователя
     * @param newIsLoggedIn Состояние входа
     */
    public static void updateSessionInfo(String newSessionId, String newUserId, boolean newIsLoggedIn) {
        sessionId = newSessionId;
        userId = newUserId;
        isLoggedIn = newIsLoggedIn;
    }
    
    // Геттеры для всех переменных
    
    public static Context getApplicationContext() {
        return applicationContext;
    }
    
    public static AuthManager getAuthManager() {
        return authManager;
    }
    
    public static IdleManager getIdleManager() {
        return idleManager;
    }
    
    public static JSONObject getCharacterData() {
        return characterData;
    }
    
    public static JSONObject getInventoryData() {
        return inventoryData;
    }
    
    public static JSONObject getLocationData() {
        return locationData;
    }
    
    public static String getSessionId() {
        return sessionId;
    }
    
    public static String getUserId() {
        return userId;
    }
    
    public static boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    public static String getCharacterName() {
        return characterName;
    }
    
    public static int getCharacterLevel() {
        return characterLevel;
    }
    
    public static String getCharacterRace() {
        return characterRace;
    }
    
    public static String getCharacterClass() {
        return characterClass;
    }
    
    public static int getCharacterHealth() {
        return characterHealth;
    }
    
    public static int getCharacterMaxHealth() {
        return characterMaxHealth;
    }
    
    public static int getCharacterMana() {
        return characterMana;
    }
    
    public static int getCharacterMaxMana() {
        return characterMaxMana;
    }
    
    public static int getCharacterExperience() {
        return characterExperience;
    }
    
    public static int getCharacterGold() {
        return characterGold;
    }
    
    public static String getCurrentLocation() {
        return currentLocation;
    }
    
    public static int getCurrentX() {
        return currentX;
    }
    
    public static int getCurrentY() {
        return currentY;
    }
}