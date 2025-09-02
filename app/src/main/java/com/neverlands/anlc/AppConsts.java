package com.neverlands.anlc;

/**
 * Класс с константами приложения, аналог AppConsts.cs
 */
public class AppConsts {
    /**
     * Расширение файла профиля
     */
    public static final String PROFILE_EXTENSION = ".anlc";

    /**
     * Код запроса для создания профиля
     */
    public static final int REQUEST_CREATE_PROFILE = 1001;

    /**
     * Код запроса для редактирования профиля
     */
    public static final int REQUEST_EDIT_PROFILE = 1002;

    /**
     * Код запроса для выбора профиля
     */
    public static final int REQUEST_SELECT_PROFILE = 1003;

    /**
     * Код запроса для ввода пароля
     */
    public static final int REQUEST_ASK_PASSWORD = 1004;

    /**
     * Код запроса для автологона
     */
    public static final int REQUEST_AUTO_LOGON = 1005;

    /**
     * Базовый URL игры
     */
    public static final String GAME_BASE_URL = "http://www.neverlands.ru/";

    /**
     * URL главной страницы игры
     */
    public static final String GAME_MAIN_URL = GAME_BASE_URL + "main.php";

    /**
     * URL форума игры
     */
    public static final String GAME_FORUM_URL = "http://forum.neverlands.ru/";

    /**
     * URL для выхода из игры
     */
    public static final String GAME_EXIT_URL = GAME_BASE_URL + "exit.php";

    /**
     * Путь к директории профилей
     */
    public static final String PROFILES_DIRECTORY = "profiles";

    /**
     * Путь к директории логов
     */
    public static final String LOGS_DIRECTORY = "logs";

    /**
     * Путь к директории кэша
     */
    public static final String CACHE_DIRECTORY = "cache";

    /**
     * Путь к директории карт
     */
    public static final String MAPS_DIRECTORY = "maps";

    /**
     * Путь к директории загрузок
     */
    public static final String DOWNLOADS_DIRECTORY = "downloads";

    /**
     * Имя файла с контактами
     */
    public static final String CONTACTS_FILE = "contacts.xml";

    /**
     * Имя файла с пользователями чата
     */
    public static final String CHAT_USERS_FILE = "chatusers.xml";

    /**
     * Имя файла с закладками
     */
    public static final String FAVORITES_FILE = "favorites.xml";

    /**
     * Имя файла с телепортами
     */
    public static final String TELEPORTS_FILE = "teleports.xml";

    /**
     * Имя файла с картой
     */
    public static final String MAP_FILE = "map.xml";

    /**
     * Имя файла с предметами
     */
    public static final String THINGS_FILE = "things.xml";

    /**
     * Имя файла с клетками
     */
    public static final String CELLS_FILE = "cells.xml";

    /**
     * Имя файла с нейросетью
     */
    public static final String NEURO_FILE = "neuro.dat";

    /**
     * Максимальное количество попыток подключения
     */
    public static final int MAX_CONNECTION_ATTEMPTS = 3;

    /**
     * Таймаут подключения (в миллисекундах)
     */
    public static final int CONNECTION_TIMEOUT = 30000;

    /**
     * Таймаут чтения (в миллисекундах)
     */
    public static final int READ_TIMEOUT = 30000;

    /**
     * Интервал проверки соединения (в миллисекундах)
     */
    public static final int CONNECTION_CHECK_INTERVAL = 60000;

    /**
     * Интервал обновления часов (в миллисекундах)
     */
    public static final int CLOCK_UPDATE_INTERVAL = 1000;

    /**
     * Интервал обновления интерфейса (в миллисекундах)
     */
    public static final int UI_UPDATE_INTERVAL = 100;

    /**
     * Интервал проверки информации (в миллисекундах)
     */
    public static final int INFO_CHECK_INTERVAL = 5000;

    /**
     * Интервал обработки мусора (в миллисекундах)
     */
    public static final int CRAP_INTERVAL = 10000;

    /**
     * Интервал обновления трея (в миллисекундах)
     */
    public static final int TRAY_UPDATE_INTERVAL = 500;

    /**
     * Максимальное количество вкладок
     */
    public static final int MAX_TABS = 10;

    /**
     * Максимальное количество контактов
     */
    public static final int MAX_CONTACTS = 1000;

    /**
     * Максимальное количество пользователей чата
     */
    public static final int MAX_CHAT_USERS = 1000;

    /**
     * Максимальное количество закладок
     */
    public static final int MAX_FAVORITES = 100;

    /**
     * Максимальное количество телепортов
     */
    public static final int MAX_TELEPORTS = 100;

    /**
     * Максимальное количество предметов
     */
    public static final int MAX_THINGS = 10000;

    /**
     * Максимальное количество клеток
     */
    public static final int MAX_CELLS = 10000;

    /**
     * Максимальное количество записей в логе
     */
    public static final int MAX_LOG_ENTRIES = 1000;

    /**
     * Максимальное количество записей в чате
     */
    public static final int MAX_CHAT_ENTRIES = 1000;

    /**
     * Максимальное количество записей в истории команд
     */
    public static final int MAX_COMMAND_HISTORY = 100;

    /**
     * Максимальное количество записей в истории поиска
     */
    public static final int MAX_SEARCH_HISTORY = 100;

    /**
     * Максимальное количество записей в истории URL
     */
    public static final int MAX_URL_HISTORY = 100;

    /**
     * Максимальное количество записей в истории приватов
     */
    public static final int MAX_PRIVATE_HISTORY = 100;

    /**
     * Максимальное количество записей в истории боев
     */
    public static final int MAX_FIGHT_HISTORY = 100;

    /**
     * Максимальное количество записей в истории разделки
     */
    public static final int MAX_RAZDELKA_HISTORY = 100;

    /**
     * Максимальное количество записей в истории рыбалки
     */
    public static final int MAX_FISHING_HISTORY = 100;

    /**
     * Максимальное количество записей в истории торговли
     */
    public static final int MAX_TRADE_HISTORY = 100;

    /**
     * Максимальное количество записей в истории перемещений
     */
    public static final int MAX_MOVE_HISTORY = 100;

    /**
     * Максимальное количество записей в истории действий
     */
    public static final int MAX_ACTION_HISTORY = 100;

    /**
     * Максимальное количество записей в истории ошибок
     */
    public static final int MAX_ERROR_HISTORY = 100;

    /**
     * Максимальное количество записей в истории системных сообщений
     */
    public static final int MAX_SYSTEM_HISTORY = 100;

    /**
     * Максимальное количество записей в истории отладки
     */
    public static final int MAX_DEBUG_HISTORY = 100;

    /**
     * Максимальное количество записей в истории информации
     */
    public static final int MAX_INFO_HISTORY = 100;

    /**
     * Максимальное количество записей в истории предупреждений
     */
    public static final int MAX_WARNING_HISTORY = 100;

    /**
     * Максимальное количество записей в истории критических ошибок
     */
    public static final int MAX_CRITICAL_HISTORY = 100;
}