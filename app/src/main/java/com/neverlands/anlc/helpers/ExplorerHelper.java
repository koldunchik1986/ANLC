package com.neverlands.anlc.helpers;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;

import com.neverlands.anlc.ANLCApplication;
import com.neverlands.anlc.AppVars;
import com.neverlands.anlc.abforms.ClearExplorerCacheActivity;

import java.io.File;
import java.io.IOException;

/**
 * Помощник для работы с браузером, аналог ExplorerHelper.cs
 */
public class ExplorerHelper {
    private static final String TAG = "ExplorerHelper";

    /**
     * Очистка кэша браузера
     */
    public static void clearCache() {
        try {
            Context context = ANLCApplication.getAppContext();
            
            // Очистка кэша WebView
            new WebView(context).clearCache(true);
            
            // Очистка cookies
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
            
            // Очистка хранилища WebView
            WebStorage.getInstance().deleteAllData();
            
            // Очистка HTTP кэша
            HttpResponseCache cache = HttpResponseCache.getInstalled();
            if (cache != null) {
                cache.delete();
            }
            
            // Очистка кэш-директории приложения
            clearCacheDir(context.getCacheDir());
            
            // Обновление статуса в UI
            updateClearCacheStatus("Кэш браузера очищен");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при очистке кэша браузера", e);
            updateClearCacheStatus("Ошибка при очистке кэша: " + e.getMessage());
        }
    }

    /**
     * Очистка директории кэша
     * @param dir директория для очистки
     */
    private static void clearCacheDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        clearCacheDir(child);
                    } else {
                        child.delete();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при очистке директории кэша", e);
            }
        }
    }

    /**
     * Обновление статуса очистки кэша в UI
     * @param status текст статуса
     */
    private static void updateClearCacheStatus(final String status) {
        if (AppVars.ClearExplorerCacheFormMain != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                AppVars.ClearExplorerCacheFormMain.updateStatus(status);
            });
        }
    }

    /**
     * Настройка WebView для эмуляции десктопного браузера
     * @param webView WebView для настройки
     */
    public static void configureWebViewForDesktop(WebView webView) {
        if (webView != null) {
            // Настройка User-Agent для эмуляции десктопного браузера
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
            webView.getSettings().setUserAgentString(userAgent);
            
            // Включение JavaScript
            webView.getSettings().setJavaScriptEnabled(true);
            
            // Включение DOM storage
            webView.getSettings().setDomStorageEnabled(true);
            
            // Включение поддержки зума
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            
            // Включение поддержки мультитач
            webView.getSettings().setUseWideViewPort(true);
            
            // Включение поддержки файлов cookie
            CookieManager.getInstance().setAcceptCookie(true);
            
            // Настройка кэширования
            webView.getSettings().setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
            android.webkit.WebSettings settings = webView.getSettings();
            settings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
            settings.setDomStorageEnabled(true);
            
            // Включение поддержки смешанного контента (http и https)
            webView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    /**
     * Получение cookies для домена
     * @param domain домен
     * @return строка cookies
     */
    public static String getCookiesForDomain(String domain) {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(domain);
        return cookies != null ? cookies : "";
    }

    /**
     * Установка cookies для домена
     * @param domain домен
     * @param cookieString строка cookies
     */
    public static void setCookiesForDomain(String domain, String cookieString) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(domain, cookieString);
        cookieManager.flush();
    }

    /**
     * Очистка cookies для домена
     * @param domain домен
     */
    public static void clearCookiesForDomain(String domain) {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(domain);
        if (cookies != null) {
            String[] cookieList = cookies.split(";");
            for (String cookie : cookieList) {
                String[] cookieParts = cookie.split("=");
                if (cookieParts.length > 0) {
                    String cookieName = cookieParts[0].trim();
                    cookieManager.setCookie(domain, cookieName + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT");
                }
            }
            cookieManager.flush();
        }
    }
}