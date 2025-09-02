package com.neverlands.anlc;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * AuthManager - Управляет аутентификацией и профилями пользователей
 * Этот класс интегрируется с NeverApi для фактической аутентификации
 */
public class AuthManager {
    private static final String TAG = "AuthManager";
    
    // Ключи для SharedPreferences
    private static final String PREFS_NAME = "ABClientPrefs";
    private static final String KEY_CURRENT_PROFILE = "currentProfile";
    private static final String KEY_PROFILE_PREFIX = "profile_";
    private static final String KEY_USERNAME = "_username";
    private static final String KEY_PASSWORD = "_password";
    private static final String KEY_SAVE_PASSWORD = "_savePassword";
    
    // Экземпляр синглтона
    private static AuthManager instance;
    
    // Контекст приложения
    private final Context context;
    
    // Экземпляр NeverApi для аутентификации
    private final NeverApi neverApi;
    
    // Информация о текущем профиле
    private String currentProfileName;
    private String currentUsername;
    private String currentPassword;
    private boolean currentSavePassword;
    
    // Состояние аутентификации
    private boolean isAuthenticated = false;
    
    /**
     * Приватный конструктор для обеспечения шаблона синглтона
     * @param context Контекст приложения
     */
    private AuthManager(Context context) {
        this.context = context.getApplicationContext();
        this.neverApi = new NeverApi();
    }
    
    /**
     * Получить экземпляр синглтона
     * @param context Контекст приложения
     * @return Экземпляр AuthManager
     */
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }
    
    /**
     * Загрузить последний использованный профиль
     * @return true если профиль был загружен, false в противном случае
     */
    public boolean loadLastProfile() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String profileName = prefs.getString(KEY_CURRENT_PROFILE, null);
        
        if (profileName != null) {
            return loadProfile(profileName);
        }
        
        return false;
    }
    
    /**
     * Загрузить определенный профиль
     * @param profileName Имя профиля для загрузки
     * @return true если профиль был загружен, false в противном случае
     */
    public boolean loadProfile(String profileName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String prefixKey = KEY_PROFILE_PREFIX + profileName;
        
        String username = prefs.getString(prefixKey + KEY_USERNAME, null);
        String password = prefs.getString(prefixKey + KEY_PASSWORD, null);
        boolean savePassword = prefs.getBoolean(prefixKey + KEY_SAVE_PASSWORD, false);
        
        if (username != null) {
            currentProfileName = profileName;
            currentUsername = username;
            currentPassword = password; // Может быть null, если не сохранен
            currentSavePassword = savePassword;
            
            // Сохранить как текущий профиль
            prefs.edit().putString(KEY_CURRENT_PROFILE, profileName).apply();
            
            Log.d(TAG, "Загружен профиль: " + profileName + " с именем пользователя: " + username);
            return true;
        }
        
        Log.e(TAG, "Не удалось загрузить профиль: " + profileName);
        return false;
    }
    
    /**
     * Сохранить текущий профиль
     * @return true если профиль был сохранен, false в противном случае
     */
    public boolean saveCurrentProfile() {
        if (currentProfileName == null || currentUsername == null) {
            Log.e(TAG, "Невозможно сохранить профиль: Нет текущего профиля");
            return false;
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        String prefixKey = KEY_PROFILE_PREFIX + currentProfileName;
        editor.putString(prefixKey + KEY_USERNAME, currentUsername);
        
        if (currentSavePassword && currentPassword != null) {
            editor.putString(prefixKey + KEY_PASSWORD, currentPassword);
        } else {
            editor.remove(prefixKey + KEY_PASSWORD);
        }
        
        editor.putBoolean(prefixKey + KEY_SAVE_PASSWORD, currentSavePassword);
        editor.putString(KEY_CURRENT_PROFILE, currentProfileName);
        
        boolean result = editor.commit();
        Log.d(TAG, "Профиль сохранен: " + currentProfileName + ", результат: " + result);
        
        return result;
    }
    
    /**
     * Создать новый профиль
     * @param profileName Имя профиля
     * @param username Имя пользователя
     * @param password Пароль
     * @param savePassword Сохранять ли пароль
     * @return true если профиль был создан, false в противном случае
     */
    public boolean createProfile(String profileName, String username, String password, boolean savePassword) {
        if (profileName == null || username == null) {
            Log.e(TAG, "Невозможно создать профиль: Отсутствует необходимая информация");
            return false;
        }
        
        currentProfileName = profileName;
        currentUsername = username;
        currentPassword = password;
        currentSavePassword = savePassword;
        
        return saveCurrentProfile();
    }
    
    /**
     * Удалить профиль
     * @param profileName Имя профиля для удаления
     * @return true если профиль был удален, false в противном случае
     */
    public boolean deleteProfile(String profileName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        String prefixKey = KEY_PROFILE_PREFIX + profileName;
        editor.remove(prefixKey + KEY_USERNAME);
        editor.remove(prefixKey + KEY_PASSWORD);
        editor.remove(prefixKey + KEY_SAVE_PASSWORD);
        
        // Если это был текущий профиль, очистить его
        if (profileName.equals(prefs.getString(KEY_CURRENT_PROFILE, null))) {
            editor.remove(KEY_CURRENT_PROFILE);
        }
        
        boolean result = editor.commit();
        
        // Если мы удалили текущий профиль, очистить данные текущего профиля
        if (profileName.equals(currentProfileName)) {
            currentProfileName = null;
            currentUsername = null;
            currentPassword = null;
            currentSavePassword = false;
            isAuthenticated = false;
        }
        
        Log.d(TAG, "Профиль удален: " + profileName + ", результат: " + result);
        return result;
    }
    
    /**
     * Получить все имена профилей
     * @return Массив имен профилей
     */
    public String[] getProfileNames() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allPrefs = prefs.getAll();
        
        Set<String> profileNames = new HashSet<>();
        
        for (String key : allPrefs.keySet()) {
            if (key.startsWith(KEY_PROFILE_PREFIX) && key.endsWith(KEY_USERNAME)) {
                String profileName = key.substring(KEY_PROFILE_PREFIX.length(), 
                                                 key.length() - KEY_USERNAME.length());
                profileNames.add(profileName);
            }
        }
        
        return profileNames.toArray(new String[0]);
    }
    
    /**
     * Войти с текущим профилем
     * @return true если вход успешен, false в противном случае
     */
    public boolean login() {
        if (currentUsername == null || currentPassword == null) {
            Log.e(TAG, "Невозможно войти: Отсутствует имя пользователя или пароль");
            return false;
        }
        
        return login(currentUsername, currentPassword);
    }
    
    /**
     * Войти с определенными учетными данными
     * @param username Имя пользователя
     * @param password Пароль
     * @return true если вход успешен, false в противном случае
     */
    public boolean login(String username, String password) {
        try {
            // Использовать NeverApi для выполнения фактического входа
            boolean success = neverApi.login(username, password);
            
            if (success) {
                isAuthenticated = true;
                Log.d(TAG, "Вход успешен для пользователя: " + username);
                
                // Если это новый вход (не из текущего профиля), обновить текущий профиль
                if (!username.equals(currentUsername)) {
                    currentUsername = username;
                    currentPassword = password;
                    // Не изменять currentProfileName или currentSavePassword
                }
            } else {
                isAuthenticated = false;
                Log.e(TAG, "Вход не удался для пользователя: " + username);
            }
            
            return success;
        } catch (Exception e) {
            isAuthenticated = false;
            Log.e(TAG, "Ошибка входа", e);
            return false;
        }
    }
    
    /**
     * Выйти текущего пользователя
     * @return true если выход успешен, false в противном случае
     */
    public boolean logout() {
        try {
            boolean success = neverApi.logout();
            isAuthenticated = false;
            Log.d(TAG, "Выход " + (success ? "успешен" : "не удался"));
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка выхода", e);
            isAuthenticated = false;
            return false;
        }
    }
    
    /**
     * Проверить, аутентифицирован ли пользователь
     * @return true если аутентифицирован, false в противном случае
     */
    public boolean isAuthenticated() {
        return isAuthenticated && neverApi.isLoggedIn();
    }
    
    /**
     * Получить экземпляр NeverApi
     * @return Экземпляр NeverApi
     */
    public NeverApi getNeverApi() {
        return neverApi;
    }
    
    /**
     * Получить имя текущего профиля
     * @return Имя текущего профиля
     */
    public String getCurrentProfileName() {
        return currentProfileName;
    }
    
    /**
     * Получить текущее имя пользователя
     * @return Текущее имя пользователя
     */
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Проверить, сохранен ли пароль для текущего профиля
     * @return true если пароль сохранен, false в противном случае
     */
    public boolean isPasswordSaved() {
        return currentSavePassword;
    }
    
    /**
     * Установить, сохранять ли пароль для текущего профиля
     * @param savePassword Сохранять ли пароль
     */
    public void setSavePassword(boolean savePassword) {
        currentSavePassword = savePassword;
        saveCurrentProfile();
    }
}