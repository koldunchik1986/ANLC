package com.neverlands.anlc.model;

/**
 * Класс, представляющий локацию на карте.
 */
public class MapLocation {
    private String regNum;
    private int x;
    private int y;

    /**
     * Получение регистрационного номера локации.
     * @return Регистрационный номер локации
     */
    public String getRegNum() {
        return regNum;
    }

    /**
     * Установка регистрационного номера локации.
     * @param regNum Регистрационный номер локации
     */
    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    /**
     * Получение координаты X локации.
     * @return Координата X
     */
    public int getX() {
        return x;
    }

    /**
     * Установка координаты X локации.
     * @param x Координата X
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Получение координаты Y локации.
     * @return Координата Y
     */
    public int getY() {
        return y;
    }

    /**
     * Установка координаты Y локации.
     * @param y Координата Y
     */
    public void setY(int y) {
        this.y = y;
    }
}