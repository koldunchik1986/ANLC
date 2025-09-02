package com.neverlands.anlc.abforms;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.neverlands.anlc.AppVars;
import com.neverlands.anlc.R;
import com.neverlands.anlc.helpers.ExplorerHelper;

/**
 * Активность для очистки кэша браузера, аналог ClearExplorerCacheForm.cs
 */
public class ClearExplorerCacheActivity extends Activity implements com.neverlands.anlc.ClearCacheCallback {
    private TextView tvStatus;
    private ProgressBar progressBar;
    private Button btnClose;
    private Handler handler;
    private int progressValue;
    private boolean isClearing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_explorer_cache);

        // Инициализация компонентов UI
        tvStatus = findViewById(R.id.tvClearCacheStatus);
        progressBar = findViewById(R.id.progressBarClearCache);
        btnClose = findViewById(R.id.btnCloseClearCache);

        // Инициализация обработчика для обновления UI
        handler = new Handler(Looper.getMainLooper());

        // Установка обработчика кнопки закрытия
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Сохранение ссылки на активность в глобальных переменных
        AppVars.ClearExplorerCacheFormMain = this;

        // Запуск процесса очистки кэша
        startClearingCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Удаление ссылки на активность из глобальных переменных
        if (AppVars.ClearExplorerCacheFormMain == this) {
            AppVars.ClearExplorerCacheFormMain = null;
        }
    }

    /**
     * Запуск процесса очистки кэша
     */
    private void startClearingCache() {
        isClearing = true;
        progressValue = 0;
        progressBar.setProgress(0);
        tvStatus.setText(R.string.clear_cache_starting);
        btnClose.setEnabled(false);

        // Запуск анимации прогресса
        startProgressAnimation();

        // Запуск очистки кэша в отдельном потоке
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Очистка кэша браузера
                    ExplorerHelper.clearCache();
                    
                    // Обновление UI после завершения очистки
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            isClearing = false;
                            progressBar.setProgress(100);
                            tvStatus.setText(R.string.clear_cache_completed);
                            btnClose.setEnabled(true);
                        }
                    });
                } catch (Exception e) {
                    // Обработка ошибок
                    final String errorMessage = e.getMessage();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            isClearing = false;
                            tvStatus.setText(getString(R.string.clear_cache_error, errorMessage));
                            btnClose.setEnabled(true);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Запуск анимации прогресса
     */
    private void startProgressAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isClearing) {
                    // Увеличение значения прогресса
                    progressValue += 5;
                    if (progressValue > 95) {
                        progressValue = 95;
                    }
                    
                    // Обновление прогресс-бара
                    progressBar.setProgress(progressValue);
                    
                    // Обновление текста статуса
                    if (progressValue < 30) {
                        tvStatus.setText(R.string.clear_cache_cookies);
                    } else if (progressValue < 60) {
                        tvStatus.setText(R.string.clear_cache_files);
                    } else {
                        tvStatus.setText(R.string.clear_cache_finalizing);
                    }
                    
                    // Повторный запуск анимации
                    handler.postDelayed(this, 200);
                }
            }
        }, 200);
    }

    /**
     * Обновление статуса очистки кэша
     * @param status текст статуса
     */
    public void updateStatus(final String status) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                tvStatus.setText(status);
            }
        });
    }
}