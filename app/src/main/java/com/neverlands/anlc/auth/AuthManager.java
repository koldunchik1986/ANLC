package com.neverlands.anlc.auth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.neverlands.anlc.myprofile.UserConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.FormBody;

public class AuthManager {
    private static java.util.Map<String, ProfileCookieJar> profileCookieJars = new java.util.HashMap<>();
    private static java.util.Map<String, OkHttpClient> profileClients = new java.util.HashMap<>();
    private static java.util.Map<String, Thread> heartbeatThreads = new java.util.HashMap<>();

    private static final String TAG = "AuthManager";
    private static final String LOGIN_URL = "http://neverlands.ru/game.php";
    private static final String MAIN_URL = "http://neverlands.ru/main.php";
    private static final String BASE_URL = "http://neverlands.ru/";

    public static interface AuthCallback {
        void onSuccess(List<Cookie> cookies);
        void onFailure(String error);
    }

    public static void authorizeAsync(UserConfig profile, Context context, AuthCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // 1. Очистить cookies, если нужно
                String loginKey = profile.getLogin();
                ProfileCookieJar cookieJar = new ProfileCookieJar(profile);
                profileCookieJars.put(loginKey, cookieJar);
                cookieJar.clear();

                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(cookieJar)
                        .build();
                profileClients.put(loginKey, client);

                // 2. GET watermark
                Request getRequest = new Request.Builder()
                        .url(BASE_URL)
                        .header("User-Agent", getUserAgent())
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                        .build();
                Response getResponse = client.newCall(getRequest).execute();
                logToFile(context, "GET watermark: " + getResponse.code());
                getResponse.close();

                // 3. POST login
                String login = encode(profile.getLogin());
                String password = encode(profile.getPassword());
                FormBody postBody = new FormBody.Builder(Charset.forName("windows-1251"))
                        .addEncoded("user", login)
                        .addEncoded("pass", password)
                        .build();
                Request postRequest = new Request.Builder()
                        .url(LOGIN_URL)
                        .post(postBody)
                        .header("User-Agent", getUserAgent())
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=windows-1251")
                        .header("Referer", BASE_URL)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                        .build();
                Response postResponse = client.newCall(postRequest).execute();
                logToFile(context, "POST login: " + postResponse.code());
                // Явно обновляем куки из ответа POST
                List<Cookie> postCookies = Cookie.parseAll(HttpUrl.get(LOGIN_URL), postResponse.headers());
                cookieJar.saveFromResponse(HttpUrl.get(LOGIN_URL), postCookies);
                postResponse.close();

                // 4. Сохраняем cookies после POST
                List<Cookie> cookies = cookieJar.getCookies();
                String cookieHeader = "";
                if (cookies != null && !cookies.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Cookie c : cookies) {
                        sb.append(c.name()).append("=").append(c.value()).append("; ");
                    }
                    cookieHeader = sb.toString();
                }

                // 5. GET main.php с этими cookies
                Request mainRequest = new Request.Builder()
                        .url(MAIN_URL)
                        .header("User-Agent", getUserAgent())
                        .header("Referer", BASE_URL)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Cookie", cookieHeader)
                        .build();
                Response mainResponse = client.newCall(mainRequest).execute();
                String mainHtml = decodeResponse(mainResponse);
                logToFile(context, "GET main.php: " + mainResponse.code());
                logToFile(context, "main.php sent cookies: " + cookieHeader);
                logToFile(context, "main.php response: " + mainHtml);
                mainResponse.close();

                // 6. Проверить результат
                if (mainHtml.contains("id=\"auth_form\"") || mainHtml.contains("captcha")) {
                    postToMain(callback, false, "Ошибка авторизации: требуется повторный вход или капча");
                    return;
                }

                // 7. Успех
                startHeartbeat(loginKey, client, cookieJar, context);
                postToMain(callback, true, cookies);
            } catch (Exception e) {
                logToFile(context, "Auth error: " + e.getMessage());
                postToMain(callback, false, "Ошибка авторизации: " + e.getMessage());
            } finally {
                executor.shutdown(); // Важно: освобождаем ресурсы
            }
        });
    }

    // Декодирование ответа с поддержкой gzip и windows-1251
    private static String decodeResponse(Response response) throws IOException {
        String encoding = response.header("Content-Encoding", "");
        String charset = "windows-1251";
        String contentType = response.header("Content-Type", "");
        if (contentType != null && contentType.contains("charset=")) {
            String[] parts = contentType.split("charset=");
            if (parts.length > 1) charset = parts[1].trim();
        }
        byte[] raw = response.body().bytes();
        byte[] decompressed = raw;
        if (encoding.contains("gzip")) {
            java.util.zip.GZIPInputStream gzipStream = new java.util.zip.GZIPInputStream(new java.io.ByteArrayInputStream(raw));
            decompressed = gzipStream.readAllBytes();
        }
        return new String(decompressed, charset);
    }

    // Heartbeat: периодически запрашивать main.php
    public static void startHeartbeat(String loginKey, OkHttpClient client, ProfileCookieJar cookieJar, Context context) {
        stopHeartbeat(loginKey);
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Request mainRequest = new Request.Builder()
                            .url(MAIN_URL)
                            .header("User-Agent", getUserAgent())
                            .header("Referer", BASE_URL)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                            .header("Accept-Encoding", "gzip, deflate")
                            .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                            .build();
                    Response mainResponse = client.newCall(mainRequest).execute();
                    String mainHtml = decodeResponse(mainResponse);
                    logToFile(context, "HEARTBEAT main.php: " + mainResponse.code() + "\n" + mainHtml);
                    mainResponse.close();
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                logToFile(context, "HEARTBEAT error: " + e.getMessage());
            }
        });
        heartbeatThreads.put(loginKey, thread);
        thread.start();
    }

    public static void stopHeartbeat(String loginKey) {
        Thread thread = heartbeatThreads.get(loginKey);
        if (thread != null) {
            thread.interrupt();
            heartbeatThreads.remove(loginKey);
        }
    }

    private static void postToMain(AuthCallback callback, boolean success, Object result) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (success) {
                callback.onSuccess((List<Cookie>) result);
            } else {
                callback.onFailure((String) result);
            }
        });
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, "windows-1251");
        } catch (Exception e) {
            return value;
        }
    }

    private static String getUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    }

    private static void logToFile(Context context, String text) {
        try {
            File logFile = new File(context.getFilesDir(), "log.txt");
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(text + "\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "Log error", e);
        }
    }
}

// CookieJar для профиля
class ProfileCookieJar implements CookieJar {
    private final UserConfig profile;
    private List<Cookie> cookies = new java.util.ArrayList<>();

    public ProfileCookieJar(UserConfig profile) {
        this.profile = profile;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        this.cookies = new java.util.ArrayList<>(cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return cookies;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void clear() {
        cookies.clear();
    }
}