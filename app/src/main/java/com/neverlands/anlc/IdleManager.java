package com.neverlands.anlc;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * IdleManager - Управляет состоянием простоя приложения и выполняет периодические действия
 * Это аналог функциональности управления простоем из PC-версии
 */
public class IdleManager {
    private static final String TAG = "IdleManager";
    
    // Тайм-аут простоя по умолчанию в миллисекундах (5 минут)
    private static final long DEFAULT_IDLE_TIMEOUT = 5 * 60 * 1000;
    
    // Интервал поддержания соединения по умолчанию в миллисекундах (2 минуты)
    private static final long DEFAULT_KEEPALIVE_INTERVAL = 2 * 60 * 1000;
    
    // Экземпляр синглтона
    private static IdleManager instance;
    
    // Таймеры для планирования задач
    private Timer keepAliveTimer;
    private Timer idleTimer;
    
    // Тайм-аут простоя в миллисекундах
    private long idleTimeout = DEFAULT_IDLE_TIMEOUT;
    
    // Интервал поддержания соединения в миллисекундах
    private long keepAliveInterval = DEFAULT_KEEPALIVE_INTERVAL;
    
    // Состояние простоя
    private boolean isIdle = false;
    
    // Временная метка последней активности
    private long lastActivityTime;
    
    // Экземпляр NeverApi для связи с сервером
    private NeverApi neverApi;
    
    // Обработчик для выполнения задач в основном потоке
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    // Слушатель изменения состояния простоя
    private IdleStateChangeListener idleStateChangeListener;
    
    /**
     * Интерфейс для уведомлений об изменении состояния простоя
     */
    public interface IdleStateChangeListener {
        void onIdleStateChanged(boolean isIdle);
    }
    
    /**
     * Приватный конструктор для обеспечения шаблона синглтона
     */
    private IdleManager() {
        lastActivityTime = System.currentTimeMillis();
    }
    
    /**
     * Получить экземпляр синглтона
     * @return Экземпляр IdleManager
     */
    public static synchronized IdleManager getInstance() {
        if (instance == null) {
            instance = new IdleManager();
        }
        return instance;
    }
    
    /**
     * Инициализировать с экземпляром NeverApi
     * @param api Экземпляр NeverApi для связи с сервером
     */
    public void initialize(NeverApi api) {
        this.neverApi = api;
        resetTimers();
    }
    
    /**
     * Установить слушатель изменения состояния простоя
     * @param listener Слушатель для уведомления об изменениях состояния простоя
     */
    public void setIdleStateChangeListener(IdleStateChangeListener listener) {
        this.idleStateChangeListener = listener;
    }
    
    /**
     * Установить тайм-аут простоя
     * @param timeoutMillis Тайм-аут в миллисекундах
     */
    public void setIdleTimeout(long timeoutMillis) {
        this.idleTimeout = timeoutMillis;
        resetTimers();
    }
    
    /**
     * Установить интервал поддержания соединения
     * @param intervalMillis Интервал в миллисекундах
     */
    public void setKeepAliveInterval(long intervalMillis) {
        this.keepAliveInterval = intervalMillis;
        resetTimers();
    }
    
    /**
     * Сбросить и перезапустить все таймеры
     */
    private void resetTimers() {
        // Отменить существующие таймеры
        stopTimers();
        
        // Создать новые таймеры
        keepAliveTimer = new Timer("KeepAliveTimer");
        idleTimer = new Timer("IdleTimer");
        
        // Запланировать задачу поддержания соединения
        keepAliveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                performKeepAlive();
            }
        }, keepAliveInterval, keepAliveInterval);
        
        // Запланировать задачу проверки простоя
        idleTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkIdleState();
            }
        }, 1000, 1000); // Проверять каждую секунду
        
        Log.d(TAG, "Таймеры сброшены с тайм-аутом простоя: " + idleTimeout + 
              "мс, интервалом поддержания соединения: " + keepAliveInterval + "мс");
    }
    
    /**
     * Остановить все таймеры
     */
    private void stopTimers() {
        if (keepAliveTimer != null) {
            keepAliveTimer.cancel();
            keepAliveTimer = null;
        }
        
        if (idleTimer != null) {
            idleTimer.cancel();
            idleTimer = null;
        }
    }
    
    /**
     * Выполнить действие поддержания соединения для сохранения соединения с сервером
     */
    private void performKeepAlive() {
        if (neverApi != null && neverApi.isLoggedIn()) {
            // Выполнить в основном потоке, так как сетевые операции не должны быть в потоке таймера
            mainHandler.post(() -> {
                try {
                    // Простой запрос поддержания соединения, например, получить информацию о персонаже
                    neverApi.getCharacterInfo();
                    Log.d(TAG, "Запрос поддержания соединения успешно отправлен");
                } catch (Exception e) {
                    Log.e(TAG, "Ошибка при отправке запроса поддержания соединения", e);
                }
            });
        }
    }
    
    /**
     * Проверить, находится ли приложение в состоянии простоя
     */
    private void checkIdleState() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastActivity = currentTime - lastActivityTime;
        
        boolean newIdleState = timeSinceLastActivity > idleTimeout;
        
        // Если состояние простоя изменилось
        if (newIdleState != isIdle) {
            isIdle = newIdleState;
            Log.d(TAG, "Состояние простоя изменилось на: " + isIdle);
            
            // Уведомить слушателя в основном потоке
            if (idleStateChangeListener != null) {
                mainHandler.post(() -> idleStateChangeListener.onIdleStateChanged(isIdle));
            }
        }
    }
    
    /**
     * Записать активность пользователя для предотвращения состояния простоя
     */
    public void recordUserActivity() {
        lastActivityTime = System.currentTimeMillis();
        
        // Если мы были в состоянии простоя, теперь мы не в нем
        if (isIdle) {
            isIdle = false;
            Log.d(TAG, "Обнаружена активность пользователя, больше не в состоянии простоя");
            
            // Уведомить слушателя в основном потоке
            if (idleStateChangeListener != null) {
                mainHandler.post(() -> idleStateChangeListener.onIdleStateChanged(false));
            }
        }
    }
    
    /**
     * Зарегистрировать активность для отслеживания взаимодействий пользователя
     * @param activity Активность для регистрации
     */
    public void registerActivity(Activity activity) {
        // Записывать активность при каждом возобновлении активности
        activity.getWindow().getDecorView().setOnTouchListener((v, event) -> {
            recordUserActivity();
            return false; // Не потреблять событие
        });
        
        Log.d(TAG, "Активность зарегистрирована для отслеживания простоя: " + 
              activity.getClass().getSimpleName());
    }
    
    /**
     * Проверить, находится ли приложение в настоящее время в состоянии простоя
     * @return true если в состоянии простоя, false в противном случае
     */
    public boolean isIdle() {
        return isIdle;
    }
    
    /**
     * Получить время с момента последней активности в миллисекундах
     * @return Время в миллисекундах
     */
    public long getTimeSinceLastActivity() {
        return System.currentTimeMillis() - lastActivityTime;
    }
    
    /**
     * Завершить работу менеджера простоя и освободить ресурсы
     */
    public void shutdown() {
        stopTimers();
        Log.d(TAG, "IdleManager завершил работу");
    }
}