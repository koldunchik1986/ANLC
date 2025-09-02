package com.neverlands.anlc.helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Утилитарный класс для работы с файлами.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * Чтение содержимого файла.
     * @param file Файл для чтения
     * @return Содержимое файла в виде строки
     * @throws IOException В случае ошибки чтения
     */
    public static String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading file: " + file.getAbsolutePath(), e);
            throw e;
        }
        
        return content.toString();
    }

    /**
     * Запись строки в файл.
     * @param file Файл для записи
     * @param content Содержимое для записи
     * @throws IOException В случае ошибки записи
     */
    public static void writeFile(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(content);
        } catch (IOException e) {
            Log.e(TAG, "Error writing file: " + file.getAbsolutePath(), e);
            throw e;
        }
    }

    /**
     * Добавление строки в конец файла.
     * @param file Файл для добавления
     * @param content Содержимое для добавления
     * @throws IOException В случае ошибки записи
     */
    public static void appendToFile(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            writer.write(content);
        } catch (IOException e) {
            Log.e(TAG, "Error appending to file: " + file.getAbsolutePath(), e);
            throw e;
        }
    }

    /**
     * Проверка существования файла.
     * @param filePath Путь к файлу
     * @return true, если файл существует, иначе false
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * Создание директории, если она не существует.
     * @param dirPath Путь к директории
     * @return true, если директория создана или уже существует, иначе false
     */
    public static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return dir.isDirectory();
    }

    /**
     * Удаление файла.
     * @param filePath Путь к файлу
     * @return true, если файл успешно удален, иначе false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.delete();
    }

    /**
     * Получение размера файла.
     * @param filePath Путь к файлу
     * @return Размер файла в байтах или -1, если файл не существует
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        return -1;
    }

    /**
     * Копирование файла.
     * @param sourcePath Путь к исходному файлу
     * @param destPath Путь к целевому файлу
     * @return true, если файл успешно скопирован, иначе false
     */
    public static boolean copyFile(String sourcePath, String destPath) {
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            return false;
        }
        
        try (FileInputStream in = new FileInputStream(sourceFile);
             FileOutputStream out = new FileOutputStream(destFile)) {
            
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying file from " + sourcePath + " to " + destPath, e);
            return false;
        }
    }

    /**
     * Перемещение файла.
     * @param sourcePath Путь к исходному файлу
     * @param destPath Путь к целевому файлу
     * @return true, если файл успешно перемещен, иначе false
     */
    public static boolean moveFile(String sourcePath, String destPath) {
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        
        if (!sourceFile.exists() || !sourceFile.isFile()) {
            return false;
        }
        
        // Пробуем простое переименование
        if (sourceFile.renameTo(destFile)) {
            return true;
        }
        
        // Если переименование не удалось, копируем и удаляем исходный файл
        if (copyFile(sourcePath, destPath)) {
            return sourceFile.delete();
        }
        
        return false;
    }
}