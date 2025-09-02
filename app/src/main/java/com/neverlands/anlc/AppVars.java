// AppVars.java
package com.neverlands.anlc;

import com.neverlands.anlc.myprofile.UserConfig;
import com.neverlands.anlc.abforms.MainActivity;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Глобальные переменные приложения, аналог AppVars.cs
 */
public class AppVars {
    public static boolean WaitOpen;
    public static boolean AutoOpenNevid;
    public static boolean DoSelfNevid;
    public static boolean DoPerenap;
    public static boolean DoFury;
    public static boolean DoShowWalkers;
    public static String MyCoordOld;
    public static String MyLocOld;
    public static java.util.Date LastInitForm;
    public static java.util.Date LastAdv;
    public static java.util.Date LastTorgAdv;
    public static AutoboiState Autoboi;
    public static ClearCacheCallback ClearExplorerCacheFormMain;
    public static boolean AutoFishCheckUd;
    public static boolean AutoFishWearUd;
    public static boolean AutoFishCheckUm;
    public static String AutoFishHand1;
    public static String AutoFishHand1D;
    public static String AutoFishHand2;
    public static String AutoFishHand2D;
    public static String AutoFishMassa;
    public static int AutoFishNV;
    public static boolean AutoFishDrink;
    public static boolean AutoSkinCheckUm;
    public static boolean AutoSkinCheckRes;
    public static boolean SkinUm;
    public static boolean AutoSkinCheckKnife;
    public static boolean AutoSkinArmedKnife;
    public static java.net.Proxy LocalProxy;
    /**
     * Версия приложения и все, что с ней связано.
     */
    public static final VersionClass AppVersion = new VersionClass("ANLC", "1.0.0");

    /**
     * Русская кодовая страница.
     */
    public static final Charset Codepage = java.nio.charset.StandardCharsets.UTF_8;

    /**
     * Русская культура.
     */
    public static final Locale Culture = new Locale("ru", "RU");

    /**
     * Английская культура.
     */
    public static final Locale EnUsCulture = new Locale("en", "US");

    /**
     * Ссылка на основную форму.
     */
    public static MainActivity MainForm;

    /**
     * Профиль пользователя.
     */
    public static UserConfig Profile;

    /**
     * Сообщение об ошибке аккаунта.
     */
    public static String AccountError = "";

    /**
     * Флаг запроса подтверждения выхода.
     */
    public static boolean DoPromptExit = true;

    /**
     * Флаг автоматического питья.
     */
    public static boolean AutoDrink = false;

    /**
     * Флаг автоматического обновления.
     */
    public static boolean AutoRefresh = false;

    // ... остальные поля (можно добавить по мере необходимости)

    /**
     * Блокировка статистики.
     */
    public static final ReentrantReadWriteLock lockStat = new ReentrantReadWriteLock();

    /**
     * Блокировка адреса и статуса.
     */
    public static final ReentrantReadWriteLock lockAddressStatus = new ReentrantReadWriteLock();

    /**
     * Блокировка объектов.
     */
    public static final ReentrantReadWriteLock lockOb = new ReentrantReadWriteLock();

    /**
     * Блокировка балуна.
     */
    public static final ReentrantReadWriteLock lockBaloon = new ReentrantReadWriteLock();
}