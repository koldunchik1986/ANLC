package com.neverlands.anlc;

/**
 * Класс для представления записи в магазине
 */
public class ShopEntry {
    /**
     * Название предмета
     */
    private String name;
    
    /**
     * Цена предмета
     */
    private int price;
    
    /**
     * Скрипт для покупки предмета
     */
    private String script;

    /**
     * Конструктор по умолчанию
     */
    public ShopEntry() {
        this.name = "";
        this.price = 0;
        this.script = "";
    }

    /**
     * Конструктор с параметрами
     * @param name название предмета
     * @param price цена предмета
     * @param script скрипт для покупки предмета
     */
    public ShopEntry(String name, int price, String script) {
        this.name = name;
        this.price = price;
        this.script = script;
    }

    /**
     * Получение названия предмета
     * @return название предмета
     */
    public String getName() {
        return name;
    }

    /**
     * Установка названия предмета
     * @param name название предмета
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получение цены предмета
     * @return цена предмета
     */
    public int getPrice() {
        return price;
    }

    /**
     * Установка цены предмета
     * @param price цена предмета
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Получение скрипта для покупки предмета
     * @return скрипт для покупки предмета
     */
    public String getScript() {
        return script;
    }

    /**
     * Установка скрипта для покупки предмета
     * @param script скрипт для покупки предмета
     */
    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ShopEntry shopEntry = (ShopEntry) obj;
        
        if (price != shopEntry.price) return false;
        if (name != null ? !name.equals(shopEntry.name) : shopEntry.name != null) return false;
        return script != null ? script.equals(shopEntry.script) : shopEntry.script == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + price;
        result = 31 * result + (script != null ? script.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + " (" + price + " NV)";
    }
}