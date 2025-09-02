package com.neverlands.anlc;

import android.util.Log;
import android.util.Xml;
import android.widget.TreeView;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Менеджер контактов, аналог ContactsManager.cs
 */
public class ContactsManager {
    private static final String TAG = "ContactsManager";
    private static final String CONTACTS_FILE = "contacts.xml";
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Map<String, List<Contact>> contactGroups = new HashMap<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    /**
     * Загрузка списка контактов из файла
     */
    public static void load() {
        lock.writeLock().lock();
        try {
            contactGroups.clear();
            
            File file = new File(DataManager.getFilePath(CONTACTS_FILE));
            if (!file.exists()) {
                Log.i(TAG, "Файл контактов не найден");
                return;
            }

            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(reader);
                
                int eventType = parser.getEventType();
                Contact currentContact = null;
                String currentTag = null;
                
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            currentTag = parser.getName();
                            if ("contact".equals(currentTag)) {
                                currentContact = new Contact();
                            }
                            break;
                            
                        case XmlPullParser.TEXT:
                            if (currentContact != null && currentTag != null) {
                                String text = parser.getText();
                                switch (currentTag) {
                                    case "nick":
                                        currentContact.setNick(text);
                                        break;
                                    case "group":
                                        currentContact.setGroup(text);
                                        break;
                                    case "comment":
                                        currentContact.setComment(text);
                                        break;
                                    case "lastupdate":
                                        try {
                                            currentContact.setLastUpdate(dateFormat.parse(text));
                                        } catch (ParseException e) {
                                            currentContact.setLastUpdate(new Date());
                                        }
                                        break;
                                    case "checked":
                                        currentContact.setChecked("true".equalsIgnoreCase(text));
                                        break;
                                    case "level":
                                        try {
                                            currentContact.setLevel(Integer.parseInt(text));
                                        } catch (NumberFormatException e) {
                                            currentContact.setLevel(0);
                                        }
                                        break;
                                    case "class":
                                        currentContact.setCharacterClass(text);
                                        break;
                                    case "clan":
                                        currentContact.setClan(text);
                                        break;
                                    case "alliance":
                                        currentContact.setAlliance(text);
                                        break;
                                    case "castle":
                                        currentContact.setCastle(text);
                                        break;
                                    case "status":
                                        currentContact.setStatus(text);
                                        break;
                                    case "location":
                                        currentContact.setLocation(text);
                                        break;
                                }
                            }
                            break;
                            
                        case XmlPullParser.END_TAG:
                            if ("contact".equals(parser.getName()) && currentContact != null) {
                                addContact(currentContact);
                                currentContact = null;
                            }
                            currentTag = null;
                            break;
                    }
                    eventType = parser.next();
                }
                
                Log.i(TAG, "Загружено " + getContactCount() + " контактов в " + contactGroups.size() + " группах");
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Ошибка при загрузке контактов", e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Сохранение списка контактов в файл
     */
    public static void save() {
        lock.readLock().lock();
        try {
            File file = new File(DataManager.getFilePath(CONTACTS_FILE));
            
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
                serializer.startTag("", "contacts");
                
                for (List<Contact> contacts : contactGroups.values()) {
                    for (Contact contact : contacts) {
                        serializer.startTag("", "contact");
                        
                        serializer.startTag("", "nick");
                        serializer.text(contact.getNick());
                        serializer.endTag("", "nick");
                        
                        serializer.startTag("", "group");
                        serializer.text(contact.getGroup());
                        serializer.endTag("", "group");
                        
                        serializer.startTag("", "comment");
                        serializer.text(contact.getComment() != null ? contact.getComment() : "");
                        serializer.endTag("", "comment");
                        
                        serializer.startTag("", "lastupdate");
                        serializer.text(dateFormat.format(contact.getLastUpdate()));
                        serializer.endTag("", "lastupdate");
                        
                        serializer.startTag("", "checked");
                        serializer.text(contact.isChecked() ? "true" : "false");
                        serializer.endTag("", "checked");
                        
                        serializer.startTag("", "level");
                        serializer.text(String.valueOf(contact.getLevel()));
                        serializer.endTag("", "level");
                        
                        serializer.startTag("", "class");
                        serializer.text(contact.getCharacterClass() != null ? contact.getCharacterClass() : "");
                        serializer.endTag("", "class");
                        
                        serializer.startTag("", "clan");
                        serializer.text(contact.getClan() != null ? contact.getClan() : "");
                        serializer.endTag("", "clan");
                        
                        serializer.startTag("", "alliance");
                        serializer.text(contact.getAlliance() != null ? contact.getAlliance() : "");
                        serializer.endTag("", "alliance");
                        
                        serializer.startTag("", "castle");
                        serializer.text(contact.getCastle() != null ? contact.getCastle() : "");
                        serializer.endTag("", "castle");
                        
                        serializer.startTag("", "status");
                        serializer.text(contact.getStatus() != null ? contact.getStatus() : "");
                        serializer.endTag("", "status");
                        
                        serializer.startTag("", "location");
                        serializer.text(contact.getLocation() != null ? contact.getLocation() : "");
                        serializer.endTag("", "location");
                        
                        serializer.endTag("", "contact");
                    }
                }
                
                serializer.endTag("", "contacts");
                serializer.endDocument();
                
                Log.i(TAG, "Сохранено " + getContactCount() + " контактов в " + contactGroups.size() + " группах");
            } catch (IOException e) {
                Log.e(TAG, "Ошибка при сохранении контактов", e);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Добавление контакта
     * @param contact контакт для добавления
     */
    public static void addContact(Contact contact) {
        if (contact == null || contact.getNick() == null || contact.getNick().isEmpty() || 
            contact.getGroup() == null || contact.getGroup().isEmpty()) {
            return;
        }
        
        lock.writeLock().lock();
        try {
            List<Contact> contacts = contactGroups.get(contact.getGroup());
            if (contacts == null) {
                contacts = new ArrayList<>();
                contactGroups.put(contact.getGroup(), contacts);
            }
            
            // Проверка на дубликаты
            for (int i = 0; i < contacts.size(); i++) {
                if (contacts.get(i).getNick().equalsIgnoreCase(contact.getNick())) {
                    contacts.set(i, contact);
                    return;
                }
            }
            
            contacts.add(contact);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удаление контакта
     * @param contact контакт для удаления
     */
    public static void removeContact(Contact contact) {
        if (contact == null || contact.getNick() == null || contact.getNick().isEmpty() || 
            contact.getGroup() == null || contact.getGroup().isEmpty()) {
            return;
        }
        
        lock.writeLock().lock();
        try {
            List<Contact> contacts = contactGroups.get(contact.getGroup());
            if (contacts != null) {
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).getNick().equalsIgnoreCase(contact.getNick())) {
                        contacts.remove(i);
                        break;
                    }
                }
                
                if (contacts.isEmpty()) {
                    contactGroups.remove(contact.getGroup());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Удаление группы контактов
     * @param groupName название группы
     */
    public static void removeGroup(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return;
        }
        
        lock.writeLock().lock();
        try {
            contactGroups.remove(groupName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Получение контакта по нику и группе
     * @param nick ник контакта
     * @param group группа контакта
     * @return контакт или null, если контакт не найден
     */
    public static Contact getContact(String nick, String group) {
        if (nick == null || nick.isEmpty() || group == null || group.isEmpty()) {
            return null;
        }
        
        lock.readLock().lock();
        try {
            List<Contact> contacts = contactGroups.get(group);
            if (contacts != null) {
                for (Contact contact : contacts) {
                    if (contact.getNick().equalsIgnoreCase(nick)) {
                        return contact;
                    }
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получение контакта по нику (поиск во всех группах)
     * @param nick ник контакта
     * @return контакт или null, если контакт не найден
     */
    public static Contact findContactByNick(String nick) {
        if (nick == null || nick.isEmpty()) {
            return null;
        }
        
        lock.readLock().lock();
        try {
            for (List<Contact> contacts : contactGroups.values()) {
                for (Contact contact : contacts) {
                    if (contact.getNick().equalsIgnoreCase(nick)) {
                        return contact;
                    }
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получение списка всех групп контактов
     * @return список названий групп
     */
    public static List<String> getGroups() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(contactGroups.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получение списка контактов в группе
     * @param group название группы
     * @return список контактов в группе
     */
    public static List<Contact> getContactsInGroup(String group) {
        if (group == null || group.isEmpty()) {
            return new ArrayList<>();
        }
        
        lock.readLock().lock();
        try {
            List<Contact> contacts = contactGroups.get(group);
            return contacts != null ? new ArrayList<>(contacts) : new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получение списка всех контактов
     * @return список всех контактов
     */
    public static List<Contact> getAllContacts() {
        lock.readLock().lock();
        try {
            List<Contact> allContacts = new ArrayList<>();
            for (List<Contact> contacts : contactGroups.values()) {
                allContacts.addAll(contacts);
            }
            return allContacts;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Получение количества контактов
     * @return общее количество контактов
     */
    public static int getContactCount() {
        lock.readLock().lock();
        try {
            int count = 0;
            for (List<Contact> contacts : contactGroups.values()) {
                count += contacts.size();
            }
            return count;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Очистка списка контактов
     */
    public static void clear() {
        lock.writeLock().lock();
        try {
            contactGroups.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Обработка изменения состояния отметки контакта
     * @param treeView дерево контактов
     * @param node узел дерева
     */
    public static void afterCheck(TreeView treeView, Object node) {
        // В Android TreeView работает иначе, чем в Windows Forms
        // Этот метод будет реализован в адаптере для Android
    }
}