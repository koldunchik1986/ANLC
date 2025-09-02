package com.neverlands.anlc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * MainActivity - Главный экран игры
 * Эта активность отображается после успешного входа
 */
public class MainActivity extends AppCompatActivity implements IdleManager.IdleStateChangeListener {
    private static final String TAG = "MainActivity";
    
    // Компоненты UI
    private TextView tvCharacterName;
    private TextView tvCharacterLevel;
    private TextView tvCharacterHealth;
    private TextView tvCharacterMana;
    private TextView tvLocation;
    private Button btnRefresh;
    private Button btnInventory;
    private Button btnMap;
    private Button btnChat;
    
    // Игровые компоненты
    private NeverApi neverApi;
    private IdleManager idleManager;
    private Handler refreshHandler;
    private Runnable refreshRunnable;
    
    // Интервал обновления в миллисекундах
    private static final long REFRESH_INTERVAL = 30000; // 30 секунд
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Инициализировать компоненты UI
        tvCharacterName = findViewById(R.id.tvCharacterName);
        tvCharacterLevel = findViewById(R.id.tvCharacterLevel);
        tvCharacterHealth = findViewById(R.id.tvCharacterHealth);
        tvCharacterMana = findViewById(R.id.tvCharacterMana);
        tvLocation = findViewById(R.id.tvLocation);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnInventory = findViewById(R.id.btnInventory);
        btnMap = findViewById(R.id.btnMap);
        btnChat = findViewById(R.id.btnChat);
        
        // Настроить слушатели кликов
        btnRefresh.setOnClickListener(v -> refreshGameData());
        btnInventory.setOnClickListener(v -> openInventory());
        btnMap.setOnClickListener(v -> openMap());
        btnChat.setOnClickListener(v -> openChat());
        
        // Получить игровые компоненты
        neverApi = AppVars.getAuthManager().getNeverApi();
        idleManager = AppVars.getIdleManager();
        
        // Настроить менеджер простоя
        idleManager.setIdleStateChangeListener(this);
        idleManager.registerActivity(this);
        
        // Настроить обработчик обновления
        refreshHandler = new Handler(Looper.getMainLooper());
        refreshRunnable = this::refreshGameData;
        
        // Начальное обновление данных
        refreshGameData();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Запустить периодическое обновление
        startRefreshTimer();
        
        // Записать активность пользователя
        idleManager.recordUserActivity();
        
        // Проверить, все еще выполнен вход
        if (!neverApi.isLoggedIn()) {
            showLoginExpiredDialog();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Остановить периодическое обновление
        stopRefreshTimer();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            logout();
            return true;
        } else if (id == R.id.action_settings) {
            openSettings();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Запустить таймер обновления
     */
    private void startRefreshTimer() {
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }
    
    /**
     * Остановить таймер обновления
     */
    private void stopRefreshTimer() {
        refreshHandler.removeCallbacks(refreshRunnable);
    }
    
    /**
     * Обновить игровые данные с сервера
     */
    private void refreshGameData() {
        // Показать индикатор загрузки
        setRefreshingState(true);
        
        // Использовать фоновый поток для сетевых операций
        new Thread(() -> {
            try {
                // Получить информацию о персонаже
                String characterInfoJson = neverApi.getCharacterInfo();
                if (characterInfoJson != null) {
                    JSONObject characterData = new JSONObject(characterInfoJson);
                    AppVars.updateCharacterData(characterData);
                }
                
                // Получить информацию о локации
                String locationJson = neverApi.getLocation();
                if (locationJson != null) {
                    JSONObject locationData = new JSONObject(locationJson);
                    AppVars.updateLocationData(locationData);
                }
                
                // Обновить UI в основном потоке
                runOnUiThread(() -> {
                    updateUI();
                    setRefreshingState(false);
                    
                    // Перезапустить таймер обновления
                    stopRefreshTimer();
                    startRefreshTimer();
                });
                
            } catch (JSONException e) {
                Log.e(TAG, "Ошибка при разборе данных JSON", e);
                
                // Показать ошибку в основном потоке
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, 
                                  "Ошибка обновления данных: " + e.getMessage(), 
                                  Toast.LENGTH_SHORT).show();
                    setRefreshingState(false);
                });
            }
        }).start();
    }
    
    /**
     * Обновить UI текущими игровыми данными
     */
    private void updateUI() {
        // Обновить информацию о персонаже
        tvCharacterName.setText(AppVars.getCharacterName());
        tvCharacterLevel.setText(getString(R.string.level_format, AppVars.getCharacterLevel()));
        tvCharacterHealth.setText(getString(R.string.health_format, 
                                          AppVars.getCharacterHealth(), 
                                          AppVars.getCharacterMaxHealth()));
        tvCharacterMana.setText(getString(R.string.mana_format, 
                                        AppVars.getCharacterMana(), 
                                        AppVars.getCharacterMaxMana()));
        
        // Обновить информацию о локации
        tvLocation.setText(AppVars.getCurrentLocation());
    }
    
    /**
     * Установить состояние UI обновления
     * @param isRefreshing Обновляются ли данные в настоящее время
     */
    private void setRefreshingState(boolean isRefreshing) {
        btnRefresh.setEnabled(!isRefreshing);
        btnRefresh.setText(isRefreshing ? R.string.refreshing : R.string.refresh);
    }
    
    /**
     * Открыть экран инвентаря
     */
    private void openInventory() {
        // Записать активность пользователя
        idleManager.recordUserActivity();
        
        // TODO: Реализовать экран инвентаря
        Toast.makeText(this, "Инвентарь еще не реализован", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Открыть экран карты
     */
    private void openMap() {
        // Записать активность пользователя
        idleManager.recordUserActivity();
        
        // TODO: Реализовать экран карты
        Toast.makeText(this, "Карта еще не реализована", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Открыть экран чата
     */
    private void openChat() {
        // Записать активность пользователя
        idleManager.recordUserActivity();
        
        // TODO: Реализовать экран чата
        Toast.makeText(this, "Чат еще не реализован", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Открыть экран настроек
     */
    private void openSettings() {
        // Записать активность пользователя
        idleManager.recordUserActivity();
        
        // TODO: Реализовать экран настроек
        Toast.makeText(this, "Настройки еще не реализованы", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Выйти из игры
     */
    private void logout() {
        // Показать диалог подтверждения
        new AlertDialog.Builder(this)
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                // Выполнить выход
                new Thread(() -> {
                    boolean success = AppVars.getAuthManager().logout();
                    
                    // Вернуться к экрану входа
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(MainActivity.this, 
                                          R.string.logout_success, 
                                          Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, 
                                          R.string.logout_failed, 
                                          Toast.LENGTH_SHORT).show();
                        }
                        
                        // Вернуться к экрану приветствия
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                }).start();
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }
    
    /**
     * Показать диалог истечения срока действия входа
     */
    private void showLoginExpiredDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.session_expired_title)
            .setMessage(R.string.session_expired_message)
            .setPositiveButton(R.string.ok, (dialog, which) -> {
                // Вернуться к экрану приветствия
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            })
            .setCancelable(false)
            .show();
    }
    
    @Override
    public void onIdleStateChanged(boolean isIdle) {
        if (isIdle) {
            // Приложение теперь в состоянии простоя
            Log.d(TAG, "Приложение теперь в состоянии простоя");
            
            // Показать уведомление о простое
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, 
                              R.string.idle_notification, 
                              Toast.LENGTH_SHORT).show();
            });
        } else {
            // Приложение больше не в состоянии простоя
            Log.d(TAG, "Приложение больше не в состоянии простоя");
        }
    }
}