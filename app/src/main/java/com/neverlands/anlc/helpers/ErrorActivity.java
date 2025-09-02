package com.neverlands.anlc.helpers;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neverlands.anlc.AppVars;
import com.neverlands.anlc.R;

/**
 * Активность для отображения ошибок приложения
 */
public class ErrorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        // Получение информации об ошибке из Intent
        String errorMessage = getIntent().getStringExtra("error_message");
        String errorStack = getIntent().getStringExtra("error_stack");

        // Отображение информации об ошибке
        TextView tvErrorTitle = findViewById(R.id.tvErrorTitle);
        TextView tvErrorMessage = findViewById(R.id.tvErrorMessage);
        TextView tvErrorStack = findViewById(R.id.tvErrorStack);
        Button btnCopy = findViewById(R.id.btnCopyError);
        Button btnClose = findViewById(R.id.btnCloseError);

        tvErrorTitle.setText(getString(R.string.error_title));
        tvErrorMessage.setText(errorMessage != null ? errorMessage : getString(R.string.error_unknown));
        tvErrorStack.setText(errorStack != null ? errorStack : "");

        // Обработчик кнопки копирования ошибки
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyErrorToClipboard(errorMessage, errorStack);
            }
        });

        // Обработчик кнопки закрытия
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(1);
            }
        });
    }

    /**
     * Копирование информации об ошибке в буфер обмена
     * @param errorMessage сообщение об ошибке
     * @param errorStack стек вызовов
     */
    private void copyErrorToClipboard(String errorMessage, String errorStack) {
        try {
            // Формирование полного текста ошибки
            StringBuilder errorText = new StringBuilder();
            errorText.append("Приложение: ").append(AppVars.AppVersion.getProductFullVersion()).append("\n");
            errorText.append("Ошибка: ").append(errorMessage).append("\n\n");
            errorText.append("Стек вызовов:\n").append(errorStack);

            // Копирование в буфер обмена
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Информация об ошибке", errorText.toString());
            clipboard.setPrimaryClip(clip);

            // Уведомление пользователя
            Toast.makeText(this, R.string.error_copied, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_copy_failed, Toast.LENGTH_SHORT).show();
        }
    }
}