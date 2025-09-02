package com.neverlands.anlc.model;

import android.util.Log;

import com.neverlands.anlc.ANLCApplication;
import com.neverlands.anlc.helpers.DataManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Класс для управления избранными локациями.
 */
public class Favorites {
    private static final String TAG = "Favorites";
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Список избранных локаций.
     */
    private static final List<Bookmark> bookmarks = new ArrayList<>();
    
    /**
     * Загрузка избранных локаций.
     */
    public static void loadFavorites() {
        lock.writeLock().lock();
        try {
            bookmarks.clear();
            // Загрузка избранных локаций из файла
            File file = new File(ANLCApplication.getAppContext().getFilesDir(), "data/favorites.json");
            if (file.exists()) {
                String json = DataManager.readFile(file);
                Gson gson = new Gson();
                Type type = new TypeToken<List<Bookmark>>(){}.getType();
                List<Bookmark> loadedBookmarks = gson.fromJson(json, type);
                if (loadedBookmarks != null) {
                    bookmarks.addAll(loadedBookmarks);
                }
                Log.i(TAG, "Favorites loaded: " + bookmarks.size() + " bookmarks");
            } else {
                // Если файл не существует, создаем пустой файл
                saveFavorites();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Сохранение избранных локаций.
     */
    public static void saveFavorites() {
        lock.readLock().lock();
        try {
            // Сохранение избранных локаций в файл
            File dir = new File(ANLCApplication.getAppContext().getFilesDir(), "data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "favorites.json");
            Gson gson = new Gson();
            String json = gson.toJson(bookmarks);
            DataManager.writeFile(file, json);
            Log.i(TAG, "Favorites saved: " + bookmarks.size() + " bookmarks");
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Добавление закладки в избранное.
     * @param bookmark Закладка для добавления
     */
    public static void addBookmark(Bookmark bookmark) {
        try {
            lock.writeLock().lock();
            
            // Проверяем, есть ли уже такая закладка
            for (int i = 0; i < bookmarks.size(); i++) {
                if (bookmarks.get(i).getRegNum().equals(bookmark.getRegNum())) {
                    // Если закладка уже существует, обновляем ее
                    bookmarks.set(i, bookmark);
                    Log.d(TAG, "Bookmark updated: " + bookmark.getName());
                    saveFavorites();
                    return;
                }
            }
            
            // Если закладки нет, добавляем новую
            bookmarks.add(bookmark);
            Log.d(TAG, "Bookmark added: " + bookmark.getName());
            saveFavorites();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Удаление закладки из избранного.
     * @param regNum Регистрационный номер локации
     * @return true, если закладка была удалена, иначе false
     */
    public static boolean removeBookmark(String regNum) {
        try {
            lock.writeLock().lock();
            
            for (int i = 0; i < bookmarks.size(); i++) {
                if (bookmarks.get(i).getRegNum().equals(regNum)) {
                    bookmarks.remove(i);
                    Log.d(TAG, "Bookmark removed: " + regNum);
                    saveFavorites();
                    return true;
                }
            }
            
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Получение списка всех закладок.
     * @return Список закладок
     */
    public static List<Bookmark> getBookmarks() {
        try {
            lock.readLock().lock();
            
            return new ArrayList<>(bookmarks);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Получение закладки по регистрационному номеру.
     * @param regNum Регистрационный номер локации
     * @return Закладка или null, если закладка не найдена
     */
    public static Bookmark getBookmark(String regNum) {
        try {
            lock.readLock().lock();
            
            for (Bookmark bookmark : bookmarks) {
                if (bookmark.getRegNum().equals(regNum)) {
                    return bookmark;
                }
            }
            
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Проверка, есть ли локация в избранном.
     * @param regNum Регистрационный номер локации
     * @return true, если локация в избранном, иначе false
     */
    public static boolean isBookmarked(String regNum) {
        try {
            lock.readLock().lock();
            
            for (Bookmark bookmark : bookmarks) {
                if (bookmark.getRegNum().equals(regNum)) {
                    return true;
                }
            }
            
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Получение количества закладок.
     * @return Количество закладок
     */
    public static int getBookmarkCount() {
        try {
            lock.readLock().lock();
            
            return bookmarks.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Очистка всех закладок.
     */
    public static void clearBookmarks() {
        try {
            lock.writeLock().lock();
            
            bookmarks.clear();
            Log.d(TAG, "All bookmarks cleared");
            saveFavorites();
        } finally {
            lock.writeLock().unlock();
        }
    }
}