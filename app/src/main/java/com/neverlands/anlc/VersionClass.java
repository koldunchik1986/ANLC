package com.neverlands.anlc;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Класс для работы с версией приложения, аналог VersionClass.cs
 */
public class VersionClass {
    private final String productName;
    private final String productVersion;
    private final Set<String> nickNames;
    private String userNick;

    /**
     * Конструктор класса версии
     * @param productName название продукта
     * @param productVersion версия продукта
     */
    public VersionClass(String productName, String productVersion) {
        this.productName = productName;
        this.productVersion = productVersion;
        this.nickNames = new HashSet<>();
    }

    /**
     * Получение полного названия продукта с версией
     * @return полное название продукта с версией
     */
    public String getProductFullVersion() {
        return String.format("%s v%s", productName, productVersion);
    }
        public String getFullVersion() {
            return productName + " " + productVersion;
        }

    /**
     * Получение короткой версии продукта
     * @return короткая версия продукта
     */
    public String getProductShortVersion() {
        return String.format("%s v%s", productName, productVersion);
    }

    /**
     * Получение короткой версии продукта с ником пользователя
     * @return короткая версия продукта с ником пользователя
     */
    public String getNickProductShortVersion() {
        if (userNick != null && !userNick.isEmpty()) {
            return String.format("%s v%s [%s]", productName, productVersion, userNick);
        } else {
            return getProductShortVersion();
        }
    }

    /**
     * Получение информации о системе
     * @return строка с информацией о системе
     */
    public String getSystemInfo() {
        return String.format("Android %s (API %d)", Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
    }

    /**
     * Получение информации о дате и времени сборки
     * @return строка с датой и временем сборки
     */
    public String getBuildInfo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return dateFormat.format(new Date(getBuildDate()));
    }

    /**
     * Получение даты сборки приложения
     * @return дата сборки в миллисекундах
     */
    private long getBuildDate() {
        try {
            PackageManager pm = ANLCApplication.getAppContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ANLCApplication.getAppContext().getPackageName(), 0);
            return pi.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            return System.currentTimeMillis();
        }
    }

    /**
     * Добавление ника пользователя
     * @param nick ник пользователя
     */
    public void addNick(String nick) {
        if (nick != null && !nick.isEmpty()) {
            nickNames.add(nick);
            userNick = nick;
        }
    }

    /**
     * Получение списка ников пользователей
     * @return массив ников пользователей
     */
    public String[] getNickNames() {
        return nickNames.toArray(new String[0]);
    }
}