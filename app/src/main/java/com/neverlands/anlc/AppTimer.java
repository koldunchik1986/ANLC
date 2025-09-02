package com.neverlands.anlc;

import android.os.Handler;
import android.os.Looper;

/**
 * Класс для работы с таймерами, аналог AppTimer.cs
 */
public class AppTimer {
    private final Handler handler;
    private final Runnable runnable;
    private final int interval;
    private boolean isEnabled;
    private final String name;

    /**
     * Конструктор таймера
     * @param name имя таймера
     * @param interval интервал срабатывания в миллисекундах
     * @param callback функция обратного вызова
     */
    public AppTimer(String name, int interval, Runnable callback) {
        this.name = name;
        this.interval = interval;
        this.handler = new Handler(Looper.getMainLooper());
        this.isEnabled = false;
        
        this.runnable = new Runnable() {
            @Override
            public void run() {
                if (isEnabled) {
                    callback.run();
                    handler.postDelayed(this, interval);
                }
            }
        };
    }

    /**
     * Запуск таймера
     */
    public void start() {
        if (!isEnabled) {
            isEnabled = true;
            handler.postDelayed(runnable, interval);
        }
    }

    /**
     * Остановка таймера
     */
    public void stop() {
        if (isEnabled) {
            isEnabled = false;
            handler.removeCallbacks(runnable);
        }
    }

    /**
     * Получение имени таймера
     * @return имя таймера
     */
    public String getName() {
        return name;
    }

    /**
     * Получение интервала таймера
     * @return интервал таймера в миллисекундах
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Проверка активности таймера
     * @return true, если таймер активен
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Установка активности таймера
     * @param enabled новое состояние активности
     */
    public void setEnabled(boolean enabled) {
        if (enabled && !isEnabled) {
            start();
        } else if (!enabled && isEnabled) {
            stop();
        }
    }
}