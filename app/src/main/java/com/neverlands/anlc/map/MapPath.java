package com.neverlands.anlc.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий путь на карте.
 */
public class MapPath {
    private List<String> path;
    private int currentIndex;
    
    /**
     * Конструктор по умолчанию.
     */
    public MapPath() {
        path = new ArrayList<>();
        currentIndex = 0;
    }
    
    /**
     * Конструктор с начальным путем.
     * @param initialPath Начальный путь
     */
    public MapPath(List<String> initialPath) {
        this.path = new ArrayList<>(initialPath);
        currentIndex = 0;
    }
    
    /**
     * Добавление локации в путь.
     * @param location Регистрационный номер локации
     */
    public void addLocation(String location) {
        path.add(location);
    }
    
    /**
     * Получение следующей локации в пути.
     * @return Следующая локация или null, если путь закончился
     */
    public String getNextLocation() {
        if (currentIndex < path.size()) {
            return path.get(currentIndex++);
        }
        return null;
    }
    
    /**
     * Проверка, есть ли еще локации в пути.
     * @return true, если есть еще локации, иначе false
     */
    public boolean hasMoreLocations() {
        return currentIndex < path.size();
    }
    
    /**
     * Получение текущего индекса в пути.
     * @return Текущий индекс
     */
    public int getCurrentIndex() {
        return currentIndex;
    }
    
    /**
     * Установка текущего индекса в пути.
     * @param index Новый индекс
     */
    public void setCurrentIndex(int index) {
        if (index >= 0 && index <= path.size()) {
            currentIndex = index;
        }
    }
    
    /**
     * Сброс индекса пути на начало.
     */
    public void reset() {
        currentIndex = 0;
    }
    
    /**
     * Получение всего пути.
     * @return Список регистрационных номеров локаций
     */
    public List<String> getPath() {
        return new ArrayList<>(path);
    }
    
    /**
     * Получение оставшегося пути.
     * @return Список оставшихся регистрационных номеров локаций
     */
    public List<String> getRemainingPath() {
        return new ArrayList<>(path.subList(currentIndex, path.size()));
    }
    
    /**
     * Получение длины всего пути.
     * @return Длина пути
     */
    public int getPathLength() {
        return path.size();
    }
    
    /**
     * Получение оставшейся длины пути.
     * @return Оставшаяся длина пути
     */
    public int getRemainingPathLength() {
        return path.size() - currentIndex;
    }
    
    /**
     * Очистка пути.
     */
    public void clear() {
        path.clear();
        currentIndex = 0;
    }
}