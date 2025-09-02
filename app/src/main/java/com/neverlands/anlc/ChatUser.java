package com.neverlands.anlc;

/**
 * Класс для представления пользователя чата, аналог ChatUser.cs
 */
public class ChatUser {
    /**
     * Ник пользователя
     */
    private String nick;
    
    /**
     * Время последнего сообщения
     */
    private long lastMessageTime;
    
    /**
     * Флаг, указывающий, является ли пользователь другом
     */
    private boolean isFriend;

    /**
     * Конструктор по умолчанию
     */
    public ChatUser() {
        this.nick = "";
        this.lastMessageTime = System.currentTimeMillis();
        this.isFriend = false;
    }

    /**
     * Конструктор с параметрами
     * @param nick ник пользователя
     * @param isFriend флаг, указывающий, является ли пользователь другом
     */
    public ChatUser(String nick, boolean isFriend) {
        this.nick = nick;
        this.lastMessageTime = System.currentTimeMillis();
        this.isFriend = isFriend;
    }

    /**
     * Получение ника пользователя
     * @return ник пользователя
     */
    public String getNick() {
        return nick;
    }

    /**
     * Установка ника пользователя
     * @param nick ник пользователя
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * Получение времени последнего сообщения
     * @return время последнего сообщения в миллисекундах
     */
    public long getLastMessageTime() {
        return lastMessageTime;
    }

    /**
     * Установка времени последнего сообщения
     * @param lastMessageTime время последнего сообщения в миллисекундах
     */
    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    /**
     * Обновление времени последнего сообщения текущим временем
     */
    public void updateLastMessageTime() {
        this.lastMessageTime = System.currentTimeMillis();
    }

    /**
     * Проверка, является ли пользователь другом
     * @return true, если пользователь является другом
     */
    public boolean isFriend() {
        return isFriend;
    }

    /**
     * Установка флага друга
     * @param friend флаг, указывающий, является ли пользователь другом
     */
    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ChatUser chatUser = (ChatUser) obj;
        return nick != null ? nick.equals(chatUser.nick) : chatUser.nick == null;
    }

    @Override
    public int hashCode() {
        return nick != null ? nick.hashCode() : 0;
    }

    @Override
    public String toString() {
        return nick + (isFriend ? " (друг)" : "");
    }
}