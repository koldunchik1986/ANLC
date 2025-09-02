package com.neverlands.anlc;

/**
 * AppConsts - Константы приложения
 * Этот класс содержит все константы, используемые в приложении
 */
public class AppConsts {
    // Информация о приложении
    public static final String APP_NAME = "ABClient";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_BUILD = "1";
    
    // URL серверов
    public static final String BASE_URL = "https://www.neverlands.ru/";
    public static final String GAME_URL = BASE_URL + "game.php";
    public static final String MAIN_URL = BASE_URL + "main.php";
    public static final String CHAT_URL = BASE_URL + "ch.php";
    public static final String MAP_URL = BASE_URL + "map.php";
    
    // Конечные точки API
    public static final String LOGIN_ENDPOINT = BASE_URL;
    public static final String LOGOUT_ENDPOINT = BASE_URL + "?action=logout";
    public static final String CHARACTER_INFO_ENDPOINT = GAME_URL + "?act=who";
    public static final String INVENTORY_ENDPOINT = GAME_URL + "?act=inv&s=1";
    public static final String MAP_ENDPOINT = GAME_URL + "?act=map";
    
    // Параметры запроса
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_LOGIN = "login";
    public static final String PARAM_PASSWORD = "pass";
    public static final String PARAM_SAVE = "save";
    public static final String PARAM_ACT = "act";
    public static final String PARAM_DIR = "dir";
    public static final String PARAM_TO = "to";
    public static final String PARAM_MSG = "msg";
    
    // Значения действий
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_LOGOUT = "logout";
    public static final String ACTION_WHO = "who";
    public static final String ACTION_INV = "inv";
    public static final String ACTION_MAP = "map";
    public static final String ACTION_GO = "go";
    public static final String ACTION_SAY = "say";
    
    // Каналы чата
    public static final String CHANNEL_GENERAL = "general";
    public static final String CHANNEL_TRADE = "trade";
    public static final String CHANNEL_CLAN = "clan";
    
    // Значения направлений
    public static final String DIR_NORTH = "n";
    public static final String DIR_SOUTH = "s";
    public static final String DIR_EAST = "e";
    public static final String DIR_WEST = "w";
    public static final String DIR_NORTHEAST = "ne";
    public static final String DIR_NORTHWEST = "nw";
    public static final String DIR_SOUTHEAST = "se";
    public static final String DIR_SOUTHWEST = "sw";
    
    // SharedPreferences
    public static final String PREFS_NAME = "ABClientPrefs";
    public static final String KEY_CURRENT_PROFILE = "currentProfile";
    public static final String KEY_PROFILE_PREFIX = "profile_";
    public static final String KEY_USERNAME = "_username";
    public static final String KEY_PASSWORD = "_password";
    public static final String KEY_SAVE_PASSWORD = "_savePassword";
    
    // Тайм-ауты простоя (миллисекунды)
    public static final long IDLE_TIMEOUT = 5 * 60 * 1000; // 5 минут
    public static final long KEEPALIVE_INTERVAL = 2 * 60 * 1000; // 2 минуты
    
    // Сообщения об ошибках
    public static final String ERROR_LOGIN_FAILED = "Неверный логин или пароль";
    public static final String ERROR_LOGIN_FAILED_EN = "Incorrect login or password";
    public static final String ERROR_NOT_AUTHORIZED = "Вы не авторизованы";
    public static final String ERROR_NOT_AUTHORIZED_EN = "You are not authorized";
    
    // Дополнительные параметры Intent
    public static final String EXTRA_PROFILE_NAME = "profileName";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_SAVE_PASSWORD = "savePassword";
    
    // Коды запросов
    public static final int REQUEST_LOGIN = 1001;
    public static final int REQUEST_PROFILE = 1002;
    
    // Коды результатов
    public static final int RESULT_PROFILE_CREATED = 2001;
    public static final int RESULT_PROFILE_DELETED = 2002;
    
    // Флаги отладки
    public static final boolean DEBUG_HTTP = true;
    public static final boolean DEBUG_API = true;
    public static final boolean DEBUG_AUTH = true;
    
    // Приватный конструктор для предотвращения создания экземпляра
    private AppConsts() {
        // Этот класс не должен быть создан как экземпляр
    }
}