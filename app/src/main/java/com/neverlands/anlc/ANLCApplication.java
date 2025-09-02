// ANLCApplication.java
package com.neverlands.anlc;

import android.app.Application;
import com.neverlands.anlc.helpers.DataManager;

public class ANLCApplication extends Application {
    private static ANLCApplication instance;
    /**
     * Инициализация приложения с профилем пользователя
     */
    public static void initWithConfig(com.neverlands.anlc.myprofile.UserConfig userConfig) {
        // TODO: Реализовать логику инициализации с профилем
        // Например, сохранить профиль в AppVars
        AppVars.Profile = userConfig;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    instance = this;
    DataManager.init(this); // ✅ Инициализация
    }

    public static android.content.Context getAppContext() {
    return instance;
    }
}