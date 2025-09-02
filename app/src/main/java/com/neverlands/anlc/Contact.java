package com.neverlands.anlc;

import java.util.Date;

/**
 * Класс для представления контакта, аналог Contact.cs
 */
public class Contact {
    /**
     * Ник контакта
     */
    private String nick;
    
    /**
     * Группа контакта
     */
    private String group;
    
    /**
     * Комментарий к контакту
     */
    private String comment;
    
    /**
     * Время последнего обновления контакта
     */
    private Date lastUpdate;
    
    /**
     * Флаг, указывающий, отмечен ли контакт
     */
    private boolean isChecked;
    
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
     * Конструктор по умолчанию
     */
    public Contact() {
        this.nick = "";
        this.group = "";
        this.comment = "";
        this.lastUpdate = new Date();
        this.isChecked = false;
        this.level = 0;
        this.characterClass = "";
        this.clan = "";
        this.alliance = "";
        this.castle = "";
        this.status = "";
        this.location = "";
    }

    /**
     * Конструктор с параметрами
     * @param nick ник контакта
     * @param group группа контакта
     */
    public Contact(String nick, String group) {
        this();
        this.nick = nick;
        this.group = group;
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
     * Получение группы контакта
     * @return группа контакта
     */
    public String getGroup() {
        return group;
    }

    /**
     * Установка группы контакта
     * @param group группа контакта
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Получение комментария к контакту
     * @return комментарий к контакту
     */
    public String getComment() {
        return comment;
    }

    /**
     * Установка комментария к контакту
     * @param comment комментарий к контакту
     */
    public void setComment(String comment) {
        this.comment = comment;
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
     * Проверка, отмечен ли контакт
     * @return true, если контакт отмечен
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * Установка флага отметки контакта
     * @param checked флаг отметки контакта
     */
    public void setChecked(boolean checked) {
        isChecked = checked;
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
        
        if (comment != null && !comment.isEmpty()) {
            info.append("Комментарий: ").append(comment).append("\n");
        }
        
        return info.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Contact contact = (Contact) obj;
        
        if (nick != null ? !nick.equals(contact.nick) : contact.nick != null) return false;
        return group != null ? group.equals(contact.group) : contact.group == null;
    }

    @Override
    public int hashCode() {
        int result = nick != null ? nick.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return nick;
    }
}