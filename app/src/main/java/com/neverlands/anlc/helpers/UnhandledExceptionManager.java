package com.neverlands.anlc.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.neverlands.anlc.ANLCApplication;
import com.neverlands.anlc.AppVars;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Менеджер необработанных исключений, аналог UnhandledExceptionManager.cs
 */
public class UnhandledExceptionManager implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "UnhandledExceptionMgr";
    private static UnhandledExceptionManager instance;
    private final Thread.UncaughtExceptionHandler defaultHandler;
    private final Context context;

    /**
     * Конструктор менеджера необработанных исключений
     */
    private UnhandledExceptionManager() {
        this.context = ANLCApplication.getAppContext();
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * Добавление обработчика необработанных исключений
     */
    public static void addHandler() {
        if (instance == null) {
            instance = new UnhandledExceptionManager();
            Thread.setDefaultUncaughtExceptionHandler(instance);
        }
    }

    /**
     * Удаление обработчика необработанных исключений
     */
    public static void removeHandler() {
        if (instance != null) {
            Thread.setDefaultUncaughtExceptionHandler(instance.defaultHandler);
            instance = null;
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            // Запись информации об ошибке в лог
            Log.e(TAG, "Необработанное исключение: " + ex.getMessage(), ex);
            
            // Сохранение информации об ошибке в файл
            saveExceptionToFile(ex);
            
            // Отправка информации об ошибке на сервер (если необходимо)
            // sendExceptionToServer(ex);
            
            // Показ сообщения об ошибке пользователю
            showErrorMessage(ex);
            
            // Вызов стандартного обработчика исключений
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, ex);
            } else {
                System.exit(1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка в обработчике исключений", e);
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, ex);
            } else {
                System.exit(1);
            }
        }
    }

    /**
     * Сохранение информации об исключении в файл
     * @param ex исключение
     */
    private void saveExceptionToFile(Throwable ex) {
        try {
            // Создание директории для логов, если она не существует
            File logDir = new File(context.getFilesDir(), "logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // Создание имени файла с текущей датой и временем
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
            String fileName = "crash_" + dateFormat.format(new Date()) + ".log";
            File logFile = new File(logDir, fileName);

            // Запись информации об ошибке в файл
            try (FileOutputStream fos = new FileOutputStream(logFile);
                 PrintWriter writer = new PrintWriter(fos)) {
                
                // Запись информации о приложении
                writer.println("Приложение: " + AppVars.AppVersion.getProductFullVersion());
                writer.println("Дата и время: " + dateFormat.format(new Date()));
                writer.println("Устройство: " + Build.MANUFACTURER + " " + Build.MODEL);
                writer.println("Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
                writer.println("Пользователь: " + (AppVars.Profile != null ? AppVars.Profile.UserNick : "неизвестно"));
                writer.println();
                
                // Запись стека вызовов
                writer.println("Стек вызовов:");
                StringWriter stackTrace = new StringWriter();
                ex.printStackTrace(new PrintWriter(stackTrace));
                writer.println(stackTrace.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при сохранении информации об исключении", e);
        }
    }

    /**
     * Показ сообщения об ошибке пользователю
     * @param ex исключение
     */
    private void showErrorMessage(Throwable ex) {
        try {
            // Создание Intent для активности с сообщением об ошибке
            Intent intent = new Intent(context, ErrorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("error_message", ex.getMessage());
            intent.putExtra("error_stack", getStackTraceString(ex));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при показе сообщения об ошибке", e);
        }
    }

    /**
     * Получение строки стека вызовов
     * @param ex исключение
     * @return строка стека вызовов
     */
    private String getStackTraceString(Throwable ex) {
        StringWriter stackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(stackTrace));
        return stackTrace.toString();
    }
}