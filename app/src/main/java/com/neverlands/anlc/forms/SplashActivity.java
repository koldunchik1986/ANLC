package com.neverlands.anlc.forms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.neverlands.anlc.ANLCApplication;
import com.neverlands.anlc.AppVars;
import com.neverlands.anlc.R;
import com.neverlands.anlc.VersionClass;
import com.neverlands.anlc.abforms.MainActivity;
import com.neverlands.anlc.abproxy.CookiesManager;
import com.neverlands.anlc.helpers.DataManager;
import com.neverlands.anlc.map.Map;
import com.neverlands.anlc.model.Favorites;
import com.neverlands.anlc.myprofile.ConfigSelector;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Активность загрузочного экрана.
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_TIMEOUT = 2000; // 2 секунды

    private TextView versionTextView;
    private TextView statusTextView;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        versionTextView = findViewById(R.id.versionTextView);
        statusTextView = findViewById(R.id.statusTextView);

        // Отображаем версию приложения
        versionTextView.setText(AppVars.AppVersion.getFullVersion());

        // Создаем пул потоков для выполнения задач инициализации
        executorService = Executors.newSingleThreadExecutor();

        // Запускаем инициализацию приложения в фоновом потоке
        executorService.execute(this::initializeApp);
    }

    /**
     * Инициализация приложения.
     */
    private void initializeApp() {
        try {
            updateStatus("Инициализация приложения...");

            // Создаем необходимые директории
            createDirectories();

            // Инициализируем менеджер cookies
            updateStatus("Инициализация cookies...");
            CookiesManager.initialize();

            // Загружаем профили пользователей
            updateStatus("Загрузка профилей...");
            ConfigSelector.loadProfiles();

            // Загружаем карту
            updateStatus("Загрузка карты...");
            Map.loadMap();

            // Загружаем избранное
            updateStatus("Загрузка избранного...");
            Favorites.loadFavorites();

            // Задержка для отображения загрузочного экрана
            Thread.sleep(SPLASH_TIMEOUT);

            // Переходим к главному экрану
            updateStatus("Запуск приложения...");
            startMainActivity();
        } catch (Exception e) {
            Log.e(TAG, "Error during app initialization", e);
            updateStatus("Ошибка инициализации: " + e.getMessage());
        }
    }

    /**
     * Создание необходимых директорий.
     */
    private void createDirectories() {
        File dataDir = new File(ANLCApplication.getAppContext().getFilesDir(), "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File profilesDir = new File(ANLCApplication.getAppContext().getFilesDir(), "profiles");
        if (!profilesDir.exists()) {
            profilesDir.mkdirs();
        }

        File cacheDir = new File(ANLCApplication.getAppContext().getCacheDir(), "webcache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    /**
     * Обновление статуса инициализации.
     * @param status Текст статуса
     */
    private void updateStatus(String status) {
        new Handler(Looper.getMainLooper()).post(() -> statusTextView.setText(status));
    }

    /**
     * Запуск главной активности.
     */
    private void startMainActivity() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}