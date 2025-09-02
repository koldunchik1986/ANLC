package com.neverlands.anlc;

import java.util.Date;

/**
 * Класс для представления контакта босса, аналог BossContact.cs
 */
public class BossContact {
    /**
     * Ник контакта
     */
    private String nick;
    
    /**
     * Уровень контакта
     */
    private int level;
    
    /**
     * Класс контакта
     */
    private String characterClass;
    
    /**
     * Клан контакта
     */
    private String clan;
    
    /**
     * Альянс контакта
     */
    private String alliance;
    
    /**
     * Замок контакта
     */
    private String castle;
    
    /**
     * Статус контакта
     */
    private String status;
    
    /**
     * Местоположение контакта
     */
    private String location;
    
    /**
     * Время последнего обновления контакта
     */
    private Date lastUpdate;
    
    /**
     * Флаг, указывающий, является ли контакт другом
     */
    private boolean isFriend;
    
    /**
     * Флаг, указывающий, является ли контакт врагом
     */
    private boolean isEnemy;
    
    /**
     * Флаг, указывающий, является ли контакт нейтральным
     */
    private boolean isNeutral;
    
    /**
     * Флаг, указывающий, является ли контакт боссом
     */
    private boolean isBoss;

    /**
     * Конструктор по умолчанию
     */
    public BossContact() {
        this.nick = "";
        this.level = 0;
        this.characterClass = "";
        this.clan = "";
        this.alliance = "";
        this.castle = "";
        this.status = "";
        this.location = "";
        this.lastUpdate = new Date();
        this.isFriend = false;
        this.isEnemy = false;
        this.isNeutral = true;
        this.isBoss = false;
    }

    /**
     * Конструктор с параметрами
     * @param nick ник контакта
     */
    public BossContact(String nick) {
        this();
        this.nick = nick;
    }

    /**
     * Получение ника контакта
     * @return ник контакта
     */
    public String getNick() {
        return nick;
    }

    /**
     * Установка ника контакта
     * @param nick ник контакта
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * Получение уровня контакта
     * @return уровень контакта
     */
    public int getLevel() {
        return level;
    }

    /**
     * Установка уровня контакта
     * @param level уровень контакта
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Получение класса контакта
     * @return класс контакта
     */
    public String getCharacterClass() {
        return characterClass;
    }

    /**
     * Установка класса контакта
     * @param characterClass класс контакта
     */
    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass;
    }

    /**
     * Получение клана контакта
     * @return клан контакта
     */
    public String getClan() {
        return clan;
    }

    /**
     * Установка клана контакта
     * @param clan клан контакта
     */
    public void setClan(String clan) {
        this.clan = clan;
    }

    /**
     * Получение альянса контакта
     * @return альянс контакта
     */
    public String getAlliance() {
        return alliance;
    }

    /**
     * Установка альянса контакта
     * @param alliance альянс контакта
     */
    public void setAlliance(String alliance) {
        this.alliance = alliance;
    }

    /**
     * Получение замка контакта
     * @return замок контакта
     */
    public String getCastle() {
        return castle;
    }

    /**
     * Установка замка контакта
     * @param castle замок контакта
     */
    public void setCastle(String castle) {
        this.castle = castle;
    }

    /**
     * Получение статуса контакта
     * @return статус контакта
     */
    public String getStatus() {
        return status;
    }

    /**
     * Установка статуса контакта
     * @param status статус контакта
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Получение местоположения контакта
     * @return местоположение контакта
     */
    public String getLocation() {
        return location;
    }

    /**
     * Установка местоположения контакта
     * @param location местоположение контакта
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Получение времени последнего обновления контакта
     * @return время последнего обновления контакта
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Установка времени последнего обновления контакта
     * @param lastUpdate время последнего обновления контакта
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Обновление времени последнего обновления контакта текущим временем
     */
    public void updateLastUpdate() {
        this.lastUpdate = new Date();
    }

    /**
     * Проверка, является ли контакт другом
     * @return true, если контакт является другом
     */
    public boolean isFriend() {
        return isFriend;
    }

    /**
     * Установка флага друга
     * @param friend флаг, указывающий, является ли контакт другом
     */
    public void setFriend(boolean friend) {
        isFriend = friend;
        if (friend) {
            isEnemy = false;
            isNeutral = false;
        }
    }

    /**
     * Проверка, является ли контакт врагом
     * @return true, если контакт является врагом
     */
    public boolean isEnemy() {
        return isEnemy;
    }

    /**
     * Установка флага врага
     * @param enemy флаг, указывающий, является ли контакт врагом
     */
    public void setEnemy(boolean enemy) {
        isEnemy = enemy;
        if (enemy) {
            isFriend = false;
            isNeutral = false;
        }
    }

    /**
     * Проверка, является ли контакт нейтральным
     * @return true, если контакт является нейтральным
     */
    public boolean isNeutral() {
        return isNeutral;
    }

    /**
     * Установка флага нейтральности
     * @param neutral флаг, указывающий, является ли контакт нейтральным
     */
    public void setNeutral(boolean neutral) {
        isNeutral = neutral;
        if (neutral) {
            isFriend = false;
            isEnemy = false;
        }
    }

    /**
     * Проверка, является ли контакт боссом
     * @return true, если контакт является боссом
     */
    public boolean isBoss() {
        return isBoss;
    }

    /**
     * Установка флага босса
     * @param boss флаг, указывающий, является ли контакт боссом
     */
    public void setBoss(boolean boss) {
        isBoss = boss;
    }

    /**
     * Получение полной информации о контакте
     * @return полная информация о контакте
     */
    public String getFullInfo() {
        StringBuilder info = new StringBuilder();
        
        if (level > 0) {
            info.append("Уровень: ").append(level).append("\n");
        }
        
        if (characterClass != null && !characterClass.isEmpty()) {
            info.append("Класс: ").append(characterClass).append("\n");
        }
        
        if (clan != null && !clan.isEmpty()) {
            info.append("Клан: ").append(clan).append("\n");
        }
        
        if (alliance != null && !alliance.isEmpty()) {
            info.append("Альянс: ").append(alliance).append("\n");
        }
        
        if (castle != null && !castle.isEmpty()) {
            info.append("Замок: ").append(castle).append("\n");
        }
        
        if (status != null && !status.isEmpty()) {
            info.append("Статус: ").append(status).append("\n");
        }
        
        if (location != null && !location.isEmpty()) {
            info.append("Местоположение: ").append(location).append("\n");
        }
        
        String relation = isFriend ? "Друг" : (isEnemy ? "Враг" : "Нейтральный");
        info.append("Отношение: ").append(relation).append("\n");
        
        if (isBoss) {
            info.append("Босс: Да\n");
        }
        
        return info.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BossContact contact = (BossContact) obj;
        
        return nick != null ? nick.equals(contact.nick) : contact.nick == null;
    }

    @Override
    public int hashCode() {
        return nick != null ? nick.hashCode() : 0;
    }

    @Override
    public String toString() {
        return nick;
    }
}