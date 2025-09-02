// DataManager.java
package com.neverlands.anlc.helpers;

import android.content.Context;
import android.util.Log;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class DataManager {
    /**
     * Получение абсолютного пути к файлу в директории приложения
     */
    public static String getFilePath(String fileName) {
        File dir = getAppDir();
        return new File(dir, fileName).getAbsolutePath();
    }
    private static final String TAG = "DataManager";
    private static Context context; // ✅ Сделано static

    /**
     * Инициализация DataManager
     */
    public static void init(Context appContext) {
        context = appContext.getApplicationContext();
    }

    /**
     * Получение директории приложения
     */
    public static File getAppDir() {
        if (context == null) {
            throw new IllegalStateException("DataManager не инициализирован");
        }
        return context.getFilesDir();
    }

    /**
     * Получение директории конфигов
     */
    public static File getConfigDir() {
        File dir = new File(getAppDir(), "config");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Чтение файла по объекту
     */
    public static String readFile(File file) {
        if (file == null || !file.exists()) return null;
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "Ошибка чтения файла", e);
            return null;
        }
        return content.toString();
    }

    /**
     * Запись файла
     */
    public static boolean writeFile(File file, String content) {
        try {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                 BufferedWriter writer = new BufferedWriter(osw)) {
                writer.write(content);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Ошибка записи файла", e);
            return false;
        }
    }
}