package com.neverlands.anlc;

import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер таймеров приложения, аналог AppTimerManager.cs
 */
public class AppTimerManager {
    private static final Map<String, AppTimer> timers = new HashMap<>();

    /**
     * Установка таймеров приложения
     * @param configTimers массив конфигураций таймеров
     */
    public static void setAppTimers(Object[] configTimers) {
        // Останавливаем все существующие таймеры
        for (AppTimer timer : timers.values()) {
            timer.stop();
        }
        timers.clear();

        // Создаем новые таймеры из конфигурации
        if (configTimers != null) {
            for (Object configTimer : configTimers) {
                if (configTimer instanceof String) {
                    String timerConfig = (String) configTimer;
                    String[] parts = timerConfig.split("\\|");
                    if (parts.length >= 3) {
                        String name = parts[0];
                        int interval;
                        boolean enabled;
                        
                        try {
                            interval = Integer.parseInt(parts[1]);
                            enabled = Boolean.parseBoolean(parts[2]);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        
                        createTimer(name, interval, enabled);
                    }
                }
            }
        }
    }

    /**
     * Создание таймера
     * @param name имя таймера
     * @param interval интервал срабатывания в миллисекундах
     * @param enabled начальное состояние активности
     */
    private static void createTimer(String name, int interval, boolean enabled) {
        AppTimer timer = new AppTimer(name, interval, () -> onTimerTick(name));
        timers.put(name, timer);
        
        if (enabled) {
            timer.start();
        }
    }

    /**
     * Обработчик срабатывания таймера
     * @param timerName имя сработавшего таймера
     */
    private static void onTimerTick(String timerName) {
        // Обработка различных типов таймеров
        switch (timerName) {
            case "TimerCrap":
                if (AppVars.MainForm != null) {
                        // AppVars.MainForm.timerCrap();
                }
                break;
            case "TimerClock":
                if (AppVars.MainForm != null) {
                        // AppVars.MainForm.timerClock();
                }
                break;
            case "TimerTray":
                if (AppVars.MainForm != null) {
                        // AppVars.MainForm.trayIconTick();
                }
                break;
            case "TimerCheckInfo":
                if (AppVars.MainForm != null) {
                        // AppVars.MainForm.checkInfo();
                }
                break;
            // Добавьте обработку других таймеров по мере необходимости
        }
    }

    /**
     * Получение таймера по имени
     * @param name имя таймера
     * @return таймер или null, если таймер не найден
     */
    public static AppTimer getTimer(String name) {
        return timers.get(name);
    }

    /**
     * Запуск таймера по имени
     * @param name имя таймера
     */
    public static void startTimer(String name) {
        AppTimer timer = timers.get(name);
        if (timer != null) {
            timer.start();
        }
    }

    /**
     * Остановка таймера по имени
     * @param name имя таймера
     */
    public static void stopTimer(String name) {
        AppTimer timer = timers.get(name);
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Проверка активности таймера по имени
     * @param name имя таймера
     * @return true, если таймер активен
     */
    public static boolean isTimerEnabled(String name) {
        AppTimer timer = timers.get(name);
        return timer != null && timer.isEnabled();
    }

    /**
     * Установка активности таймера по имени
     * @param name имя таймера
     * @param enabled новое состояние активности
     */
    public static void setTimerEnabled(String name, boolean enabled) {
        AppTimer timer = timers.get(name);
        if (timer != null) {
            timer.setEnabled(enabled);
        }
    }
}