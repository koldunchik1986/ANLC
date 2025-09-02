package com.neverlands.anlc.model;

import java.util.Date;

/**
 * Класс, представляющий клетку ABC на карте.
 */
public class AbcCell {
    private String regNum;
    private String label;
    private int cost;
    private Date verified;
    private boolean visited;

    /**
     * Получение регистрационного номера клетки.
     * @return Регистрационный номер клетки
     */
    public String getRegNum() {
        return regNum;
    }

    /**
     * Установка регистрационного номера клетки.
     * @param regNum Регистрационный номер клетки
     */
    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    /**
     * Получение названия клетки.
     * @return Название клетки
     */
    public String getLabel() {
        return label;
    }

    /**
     * Установка названия клетки.
     * @param label Название клетки
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Получение стоимости прохода через клетку.
     * @return Стоимость прохода
     */
    public int getCost() {
        return cost;
    }

    /**
     * Установка стоимости прохода через клетку.
     * @param cost Стоимость прохода
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Получение времени последней проверки клетки.
     * @return Время последней проверки
     */
    public Date getVerified() {
        return verified;
    }

    /**
     * Установка времени последней проверки клетки.
     * @param verified Время последней проверки
     */
    public void setVerified(Date verified) {
        this.verified = verified;
    }

    /**
     * Проверка, была ли клетка посещена.
     * @return true, если клетка была посещена, иначе false
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * Установка флага посещения клетки.
     * @param visited Флаг посещения
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}