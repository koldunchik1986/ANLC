package com.neverlands.anlc.myprofile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.neverlands.anlc.ANLCApplication;
import com.neverlands.anlc.AppConsts;
import com.neverlands.anlc.R;
import com.neverlands.anlc.forms.AskPasswordActivity;
import com.neverlands.anlc.forms.AutoLogonActivity;
import com.neverlands.anlc.forms.ProfileActivity;
import com.neverlands.anlc.forms.ProfilesActivity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс для выбора конфигурации пользователя, аналог ConfigSelector.cs
 */
public class ConfigSelector {
    public static void loadProfiles() {
        // TODO: реализовать загрузку профилей
    }
        /**
         * Получить список имён профилей
         */
        public static List<String> getProfileNames() {
            List<String> profileNames = new ArrayList<>();
            File profilesDir = new File(ANLCApplication.getAppContext().getFilesDir(), "profiles");
            if (!profilesDir.exists()) {
                return profileNames;
            }
            File[] fileList = profilesDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(AppConsts.PROFILE_EXTENSION);
                }
            });
            if (fileList != null) {
                for (File file : fileList) {
                    String fileName = file.getName();
                    // Убираем расширение
                    if (fileName.endsWith(AppConsts.PROFILE_EXTENSION)) {
                        String profileName = fileName.substring(0, fileName.length() - AppConsts.PROFILE_EXTENSION.length());
                        profileNames.add(profileName);
                    }
                }
            }
            Collections.sort(profileNames);
            return profileNames;
        }
    /**
     * Получить профиль по имени
     */
    public static UserConfig getProfile(String profileName) {
    File profilesDir = new File(ANLCApplication.getAppContext().getFilesDir(), "profiles");
    File file = new File(profilesDir, profileName + AppConsts.PROFILE_EXTENSION);
    if (!file.exists()) return null;
    return UserConfig.load(file.getAbsolutePath());
    }

    /**
     * Проверить, существует ли профиль
     */
    public static boolean profileExists(String profileName) {
        File profilesDir = new File(ANLCApplication.getAppContext().getFilesDir(), "profiles");
        File file = new File(profilesDir, profileName + AppConsts.PROFILE_EXTENSION);
        return file.exists();
    }

    /**
     * Добавить новый профиль
     */
    public static void addProfile(UserConfig config) {
        if (config != null) {
            config.save();
        }
    }

    /**
     * Сохранить профиль
     */
    public static void saveProfile(UserConfig config) {
        if (config != null) {
            config.save();
        }
    }
    private static final String TAG = "ConfigSelector";
    private static final String CONFIGS_LOAD_ERROR_TITLE = "Ошибка работы с профайлами";

    /**
     * Обработка выбора профиля пользователя
     * @param activity активность, из которой вызывается метод
     * @return выбранный профиль пользователя или null
     */
    public static UserConfig process(Activity activity) {
        try {
            // Получаем список профайлов в директории приложения
            File filesDir = ANLCApplication.getAppContext().getFilesDir();
            File profilesDir = new File(filesDir, "profiles");
            
            if (!profilesDir.exists()) {
                profilesDir.mkdirs();
            }
            
            File[] fileList = profilesDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(AppConsts.PROFILE_EXTENSION);
                }
            });
            
            if (fileList == null || fileList.length == 0) {
                return createNewConfig(activity);
            }

            List<UserConfig> listProfiles = new ArrayList<>();
            for (File fileInfo : fileList) {
                UserConfig currentConfig = UserConfig.load(fileInfo.getAbsolutePath());
                if (currentConfig == null) {
                    continue;
                }
                listProfiles.add(currentConfig);
            }

            if (listProfiles.isEmpty()) {
                return createNewConfig(activity);
            }

            Collections.sort(listProfiles);

            // Проверка на автовход
            UserConfig firstProfile = listProfiles.get(0);
            if (firstProfile.isAutoLogin() &&
                firstProfile.getUserNick() != null && !firstProfile.getUserNick().isEmpty() &&
                firstProfile.getUserPassword() != null && !firstProfile.getUserPassword().isEmpty()) {
                Intent intent = new Intent(activity, AutoLogonActivity.class);
                intent.putExtra("nick", firstProfile.getUserNick());
                intent.putExtra("password", firstProfile.getUserPassword());
                activity.startActivityForResult(intent, AppConsts.REQUEST_AUTO_LOGON);
                // Результат будет обработан в onActivityResult вызывающей активности
                // Здесь возвращаем null, так как реальный результат будет получен позже
                return null;
            }

            UserConfig selectedUserConfig = selectExistingConfig(activity, listProfiles.toArray(new UserConfig[0]));
            return selectedUserConfig == null ? null : tryDecrypt(activity, selectedUserConfig);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при выборе профиля", e);
            configsLoadError(activity, e.getMessage());
            return null;
        }
    }

    /**
     * Создание нового профиля пользователя
     * @param activity активность, из которой вызывается метод
     * @return созданный профиль пользователя или null
     */
    public static UserConfig createNewConfig(Activity activity) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        activity.startActivityForResult(intent, AppConsts.REQUEST_CREATE_PROFILE);
        
        // Результат будет обработан в onActivityResult вызывающей активности
        // Здесь возвращаем null, так как реальный результат будет получен позже
        return null;
    }

    /**
     * Попытка расшифровать профиль пользователя
     * @param activity активность, из которой вызывается метод
     * @param userConfig профиль пользователя
     * @return расшифрованный профиль пользователя или null
     */
    public static UserConfig tryDecrypt(Activity activity, UserConfig userConfig) {
        if (userConfig == null) {
            throw new IllegalArgumentException("userConfig");
        }

        if (userConfig.getUserPassword() == null || userConfig.getUserPassword().isEmpty()) {
            Intent intent = new Intent(activity, AskPasswordActivity.class);
            intent.putExtra("configHash", userConfig.getConfigHash());
            activity.startActivityForResult(intent, AppConsts.REQUEST_ASK_PASSWORD);
            
            // Результат будет обработан в onActivityResult вызывающей активности
            // Здесь возвращаем null, так как реальный результат будет получен позже
            return null;
        }

        return userConfig;
    }

    /**
     * Редактирование существующего профиля пользователя
     * @param activity активность, из которой вызывается метод
     * @param userConfig профиль пользователя для редактирования
     * @return отредактированный профиль пользователя или null
     */
    public static UserConfig editExistingConfig(Activity activity, UserConfig userConfig) {
        if (userConfig == null) {
            throw new IllegalArgumentException("userConfig");
        }

        userConfig = tryDecrypt(activity, userConfig);
        if (userConfig == null) {
            return null;
        }

        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra("userConfig", userConfig);
        activity.startActivityForResult(intent, AppConsts.REQUEST_EDIT_PROFILE);
        
        // Результат будет обработан в onActivityResult вызывающей активности
        // Здесь возвращаем null, так как реальный результат будет получен позже
        return null;
    }

    /**
     * Отображение ошибки загрузки профилей
     * @param activity активность, из которой вызывается метод
     * @param message сообщение об ошибке
     */
    private static void configsLoadError(Activity activity, String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("message");
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(CONFIGS_LOAD_ERROR_TITLE)
               .setMessage(message)
               .setPositiveButton(android.R.string.ok, null)
               .setIcon(android.R.drawable.ic_dialog_alert)
               .show();
    }

    /**
     * Выбор существующего профиля пользователя
     * @param activity активность, из которой вызывается метод
     * @param arrayConfig массив профилей пользователя
     * @return выбранный профиль пользователя или null
     */
    private static UserConfig selectExistingConfig(Activity activity, UserConfig[] arrayConfig) {
        Intent intent = new Intent(activity, ProfilesActivity.class);
        intent.putExtra("profiles", arrayConfig);
        activity.startActivityForResult(intent, AppConsts.REQUEST_SELECT_PROFILE);
        
        // Результат будет обработан в onActivityResult вызывающей активности
        // Здесь возвращаем null, так как реальный результат будет получен позже
        return null;
    }
}