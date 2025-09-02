package com.neverlands.anlc.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.neverlands.anlc.ANLCApplication;

/**
 * Класс для настройки эмуляции браузера, аналог FeatureBrowserEmulation.cs
 */
public class FeatureBrowserEmulation {
    private static final String TAG = "BrowserEmulation";

    /**
     * Изменение режима эмуляции браузера
     */
    public static void changeMode() {
        try {
            // На Android мы не можем изменять реестр Windows, но можем настроить WebView
            // для эмуляции десктопного браузера через User-Agent и другие настройки
            setWebViewDesktopMode(ANLCApplication.getAppContext());
            Log.i(TAG, "Режим эмуляции браузера установлен");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при изменении режима эмуляции браузера", e);
        }
    }

    /**
     * Настройка WebView для эмуляции десктопного браузера
     * @param context контекст приложения
     */
    @SuppressLint("SetJavaScriptEnabled")
    private static void setWebViewDesktopMode(Context context) {
        try {
            // Создаем временный WebView для установки глобальных настроек
            WebView webView = new WebView(context);
            WebSettings settings = webView.getSettings();

            // Устанавливаем User-Agent для эмуляции десктопного браузера
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
            settings.setUserAgentString(userAgent);

            // Включаем JavaScript
            settings.setJavaScriptEnabled(true);

            // Включаем DOM storage
            settings.setDomStorageEnabled(true);

            // Включаем поддержку смешанного контента (http и https)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

            // Включаем поддержку зума
            settings.setSupportZoom(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);

            // Включаем поддержку мультитач
            settings.setUseWideViewPort(true);

            // Настройка кэширования
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
            settings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
            settings.setDomStorageEnabled(true);

            // Освобождаем ресурсы
            webView.destroy();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при настройке WebView для эмуляции десктопного браузера", e);
        }
    }

    /**
     * Применение настроек эмуляции браузера к конкретному WebView
     * @param webView WebView для настройки
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void applyToWebView(WebView webView) {
        if (webView != null) {
            try {
                WebSettings settings = webView.getSettings();

                // Устанавливаем User-Agent для эмуляции десктопного браузера
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
                settings.setUserAgentString(userAgent);

                // Включаем JavaScript
                settings.setJavaScriptEnabled(true);

                // Включаем DOM storage
                settings.setDomStorageEnabled(true);

                // Включаем поддержку смешанного контента (http и https)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }

                // Включаем поддержку зума
                settings.setSupportZoom(true);
                settings.setBuiltInZoomControls(true);
                settings.setDisplayZoomControls(false);

                // Включаем поддержку мультитач
                settings.setUseWideViewPort(true);

                // Настройка кэширования
                settings.setCacheMode(WebSettings.LOAD_DEFAULT);
                settings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
                settings.setDomStorageEnabled(true);

                Log.i(TAG, "Настройки эмуляции браузера применены к WebView");
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при применении настроек эмуляции браузера к WebView", e);
            }
        }
    }
}