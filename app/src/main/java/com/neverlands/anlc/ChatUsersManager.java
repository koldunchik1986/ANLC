package com.neverlands.anlc;

import android.util.Log;
import android.util.Xml;

import com.neverlands.anlc.helpers.DataManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Менеджер пользователей чата, аналог ChatUsersManager.cs
 */
public class ChatUsersManager {
    private static final String TAG = "ChatUsersManager";
    private static final String CHAT_USERS_FILE = "chatusers.xml";
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Map<String, ChatUser> chatUsers = new HashMap<>();

    /**
     * Загрузка списка пользователей чата из файла
     */
    public static void load() {
        lock.writeLock().lock();
        try {
            chatUsers.clear();
            
            File file = new File(DataManager.getFilePath(CHAT_USERS_FILE));
            if (!file.exists()) {
                Log.i(TAG, "Файл пользователей чата не найден");
                return;
            }

            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(reader);
                
                int eventType = parser.getEventType();
                ChatUser currentUser = null;
                String currentTag = null;
                
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            currentTag = parser.getName();
                            if ("user".equals(currentTag)) {
                                currentUser = new ChatUser();
                            }
                            break;
                            
                        case XmlPullParser.TEXT:
                            if (currentUser != null && currentTag != null) {
                                String text = parser.getText();
                                switch (currentTag) {
                                    case "nick":
                                        currentUser.setNick(text);
                                        break;
                                    case "time":
                                        try {
                                            currentUser.setLastMessageTime(Long.parseLong(text));
                                        } catch (NumberFormatException e) {
                                            currentUser.setLastMessageTime(System.currentTimeMillis());
                                        }
                                        break;
                                    case "friend":
                                        currentUser.setFriend("true".equalsIgnoreCase(text));
                                        break;
                                }
                            }
                            break;
                            
                        case XmlPullParser.END_TAG:
                            if ("user".equals(parser.getName()) && currentUser != null) {
                                chatUsers.put(currentUser.getNick().toUpperCase(), currentUser);
                                currentUser = null;
                            }
                            currentTag = null;
                            break;
                    }
                    eventType = parser.next();
                }
                
                Log.i(TAG, "Загружено " + chatUsers.size() + " пользователей чата");
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Ошибка при загрузке пользователей чата", e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Сохранение списка пользователей чата в файл
     */
    public static void save() {
        lock.readLock().lock();
        try {
            File file = new File(DataManager.getFilePath(CHAT_USERS_FILE));
            
            // Создание родительской директории, если она не существует
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file);
                 OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(writer);
                serializer.startDocument("UTF-8", true);
                serializer.startTag("", "chatusers");
                
                for (ChatUser user : chatUsers.values()) {
                    serializer.startTag("", "user");
                    
                    serializer.startTag("", "nick");
                    serializer.text(user.getNick());
                    serializer.endTag("", "nick");
                    
                    serializer.startTag("", "time");
                    serializer.text(String.valueOf(user.getLastMessageTime()));
                    serializer.endTag("", "time");
                    
                    serializer.startTag("", "friend");
                    serializer.text(user.isFriend() ? "true" : "false");
                    serializer.endTag("", "friend");
                    
                    serializer.endTag("", "user");
                }
                
                serializer.endTag("", "chatusers");
                serializer.endDocument();
                
                Log.i(TAG, "Сохранено " + chatUsers.size() + " пользователей чата");
            } catch (IOException e) {
                Log.e(TAG, "Ошибка при сохранении пользователей чата", e);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Добавление или обновление пользователя чата
     * @param nick ник пользователя
     * @param isFriend флаг, указывающий, является ли пользователь другом
     */
    public static void addOrUpdateUser(String nick, boolean isFriend) {
        if (nick == null || nick.isEmpty()) {
            return;
        }
        
        lock.writeLock().lock();
        try {
            String upperNick = nick.toUpperCase();
            ChatUser user = chatUsers.get(upperNick);
            
            if (user == null) {
                user = new ChatUser(nick, isFriend);
                chatUsers.put(upperNick, user);
            } else {
                user.setNick(nick); // Обновляем ник с правильным регистром
                user.setFriend(isFriend);
                user.updateLastMessageTime();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Получение пользователя чата по нику
     * @param nick ник пользователя
     * @return пользователь чата или null, если пользователь не найден
     */
    public static ChatUser getUser(String nick) {
        if (nick == null || nick.isEmpty()) {
            return null;
        }
        
        lock.readLock().lock();
        try {
            return chatUsers.get(nick.toUpperCase());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Удаление пользователя чата по нику
     * @param nick ник пользователя
     */
    public static void removeUser(String nick) {
        if (nick == null || nick.isEmpty()) {
            return;
        }
        
        lock.writeLock().lock();
        try {
            chatUsers.remove(nick.toUpperCase());
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Очистка списка пользователей чата
     */
    public static void clear() {
        lock.writeLock().lock();
        try {
            chatUsers.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Получение списка всех пользователей чата
     * @return список всех пользователей чата
     */
    public static List<ChatUser> getAllUsers() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(chatUsers.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получение списка пользователей чата, отсортированного по времени последнего сообщения
     * @return отсортированный список пользователей чата
     */
    public static List<ChatUser> getUsersSortedByTime() {
        lock.readLock().lock();
        try {
            List<ChatUser> users = new ArrayList<>(chatUsers.values());
            Collections.sort(users, new Comparator<ChatUser>() {
                @Override
                public int compare(ChatUser u1, ChatUser u2) {
                    return Long.compare(u2.getLastMessageTime(), u1.getLastMessageTime());
                }
            });
            return users;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получение списка пользователей чата, отсортированного по нику
     * @return отсортированный список пользователей чата
     */
    public static List<ChatUser> getUsersSortedByNick() {
        lock.readLock().lock();
        try {
            List<ChatUser> users = new ArrayList<>(chatUsers.values());
            Collections.sort(users, new Comparator<ChatUser>() {
                @Override
                public int compare(ChatUser u1, ChatUser u2) {
                    return u1.getNick().compareToIgnoreCase(u2.getNick());
                }
            });
            return users;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получение количества пользователей чата
     * @return количество пользователей чата
     */
    public static int getUserCount() {
        lock.readLock().lock();
        try {
            return chatUsers.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Проверка, существует ли пользователь чата с указанным ником
     * @param nick ник пользователя
     * @return true, если пользователь существует
     */
    public static boolean userExists(String nick) {
        if (nick == null || nick.isEmpty()) {
            return false;
        }
        
        lock.readLock().lock();
        try {
            return chatUsers.containsKey(nick.toUpperCase());
        } finally {
            lock.readLock().unlock();
        }
    }
}