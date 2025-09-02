package com.neverlands.anlc.map;

import android.util.Log;

import com.neverlands.anlc.ANLCApplication;
import com.neverlands.anlc.helpers.DataManager;
import com.neverlands.anlc.model.AbcCell;
import com.neverlands.anlc.model.MapLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Класс для работы с картой.
 */
public class Map {
    private static final String TAG = "Map";
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Карта клеток ABC.
     */
    public static final java.util.Map<String, AbcCell> AbcCells = new HashMap<>();
    
    /**
     * Карта локаций.
     */
    public static final java.util.Map<String, MapLocation> Location = new HashMap<>();
    
    /**
     * Загрузка карты.
     */
    public static void loadMap() {
        try {
            lock.writeLock().lock();
            
            AbcCells.clear();
            Location.clear();
            
            // Загрузка клеток ABC
            loadAbcCells();
            
            // Загрузка локаций
            loadLocations();
            
            Log.i(TAG, "Map loaded: " + AbcCells.size() + " cells, " + Location.size() + " locations");
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Загрузка клеток ABC.
     */
    private static void loadAbcCells() {
        // Загрузка клеток ABC из файла
        File file = new File(ANLCApplication.getAppContext().getFilesDir(), "data/abcells.json");
        if (file.exists()) {
            String json = DataManager.readFile(file);
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, AbcCell>>(){}.getType();
            HashMap<String, AbcCell> cells = gson.fromJson(json, type);
            
            if (cells != null) {
                AbcCells.putAll(cells);
            }
            
            Log.i(TAG, "ABC cells loaded: " + AbcCells.size() + " cells");
        } else {
            // Если файл не существует, создаем пустой файл
            saveAbcMap();
        }
    }
    
    /**
     * Загрузка локаций.
     */
    private static void loadLocations() {
        // Загрузка локаций из файла
        File file = new File(ANLCApplication.getAppContext().getFilesDir(), "data/map.json");
        if (file.exists()) {
            String json = DataManager.readFile(file);
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, MapLocation>>(){}.getType();
            HashMap<String, MapLocation> locations = gson.fromJson(json, type);
            
            if (locations != null) {
                Location.putAll(locations);
            }
            
            Log.i(TAG, "Locations loaded: " + Location.size() + " locations");
        } else {
            // Если файл не существует, создаем пустой файл
            saveMap();
        }
    }
    
    /**
     * Сохранение карты клеток ABC.
     */
    public static void saveAbcMap() {
        lock.readLock().lock();
        try {
            // Сохранение клеток ABC в файл
            File dir = new File(ANLCApplication.getAppContext().getFilesDir(), "data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "abcells.json");
            Gson gson = new Gson();
            String json = gson.toJson(AbcCells);
            DataManager.writeFile(file, json);
            Log.i(TAG, "ABC cells saved: " + AbcCells.size() + " cells");
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Сохранение карты локаций.
     */
    public static void saveMap() {
        lock.readLock().lock();
        try {
            // Сохранение локаций в файл
            File dir = new File(ANLCApplication.getAppContext().getFilesDir(), "data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "map.json");
            Gson gson = new Gson();
            String json = gson.toJson(Location);
            DataManager.writeFile(file, json);
            Log.i(TAG, "Locations saved: " + Location.size() + " locations");
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Добавление клетки ABC.
     * @param regNum Регистрационный номер клетки
     * @param label Название клетки
     * @param cost Стоимость прохода
     * @return Добавленная клетка
     */
    public static AbcCell addAbcCell(String regNum, String label, int cost) {
        try {
            lock.writeLock().lock();
            
            AbcCell cell = AbcCells.get(regNum);
            
            if (cell == null) {
                cell = new AbcCell();
                cell.setRegNum(regNum);
                cell.setLabel(label);
                cell.setCost(cost);
                cell.setVerified(new Date());
                
                AbcCells.put(regNum, cell);
                
                Log.d(TAG, "ABC cell added: " + regNum + " - " + label);
            } else {
                cell.setLabel(label);
                cell.setCost(cost);
                cell.setVerified(new Date());
                
                Log.d(TAG, "ABC cell updated: " + regNum + " - " + label);
            }
            
            return cell;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Добавление локации.
     * @param regNum Регистрационный номер локации
     * @param x Координата X
     * @param y Координата Y
     * @return Добавленная локация
     */
    public static MapLocation addLocation(String regNum, int x, int y) {
        try {
            lock.writeLock().lock();
            
            MapLocation location = Location.get(regNum);
            
            if (location == null) {
                location = new MapLocation();
                location.setRegNum(regNum);
                location.setX(x);
                location.setY(y);
                
                Location.put(regNum, location);
                
                Log.d(TAG, "Location added: " + regNum + " - (" + x + ", " + y + ")");
            } else {
                location.setX(x);
                location.setY(y);
                
                Log.d(TAG, "Location updated: " + regNum + " - (" + x + ", " + y + ")");
            }
            
            return location;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Получение клетки ABC по регистрационному номеру.
     * @param regNum Регистрационный номер клетки
     * @return Клетка ABC или null, если клетка не найдена
     */
    public static AbcCell getAbcCell(String regNum) {
        try {
            lock.readLock().lock();
            
            return AbcCells.get(regNum);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Получение локации по регистрационному номеру.
     * @param regNum Регистрационный номер локации
     * @return Локация или null, если локация не найдена
     */
    public static MapLocation getLocation(String regNum) {
        try {
            lock.readLock().lock();
            
            return Location.get(regNum);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Сброс всех посещенных клеток.
     */
    public static void resetAllVisitedCells() {
        try {
            lock.writeLock().lock();
            
            for (AbcCell cell : AbcCells.values()) {
                cell.setVisited(false);
            }
            
            Log.i(TAG, "All visited cells reset");
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Получение количества клеток ABC.
     * @return Количество клеток ABC
     */
    public static int getAbcCellCount() {
        try {
            lock.readLock().lock();
            
            return AbcCells.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Получение количества локаций.
     * @return Количество локаций
     */
    public static int getLocationCount() {
        try {
            lock.readLock().lock();
            
            return Location.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}