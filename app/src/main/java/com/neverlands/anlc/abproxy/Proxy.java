package com.neverlands.anlc.abproxy;

import android.net.Uri;
import android.util.Log;

import com.neverlands.anlc.ANLCApplication;
import com.neverlands.anlc.AppVars;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Класс для работы с локальным прокси-сервером, аналог Proxy.cs
 */
public class Proxy {
    /**
     * Заглушка для метода установки системного http-прокси
     */
    public static void setHttpProxySystemProperty(String host, int port) {
        // TODO: Реализовать логику установки http-прокси для системы/вебвью
        // Для Android WebView это не требуется, но для совместимости оставляем
    }
    private static final String TAG = "Proxy";
    private static final int MAX_CONNECTIONS = 50;
    private static final int START_PORT = 8052;
    private static final int MAX_PORT_ATTEMPTS = 10;

    private int listenPort;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean isRunning;

    /**
     * Конструктор прокси-сервера
     */
    public Proxy() {
        this.listenPort = START_PORT;
        this.threadPool = Executors.newFixedThreadPool(MAX_CONNECTIONS);
        this.isRunning = false;
    }

    /**
     * Запуск прокси-сервера
     * @return true, если сервер успешно запущен
     */
    public boolean start() {
        try {
            // Поиск свободного порта
            for (int attempt = 0; attempt < MAX_PORT_ATTEMPTS; attempt++) {
                try {
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), listenPort));
                    break;
                } catch (IOException e) {
                    listenPort++;
                    if (serverSocket != null) {
                        try {
                            serverSocket.close();
                        } catch (IOException ignored) {
                        }
                        serverSocket = null;
                    }
                }
            }

            // Проверка, удалось ли найти свободный порт
            if (serverSocket == null) {
                Log.e(TAG, "Не удалось найти свободный порт для прокси-сервера");
                return false;
            }

            // Настройка прокси для WebView
            setupProxy();

            // Запуск потока для обработки подключений
            isRunning = true;
            new Thread(this::acceptConnections).start();

            Log.i(TAG, "Прокси-сервер запущен на порту " + listenPort);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при запуске прокси-сервера", e);
            return false;
        }
    }

    /**
     * Остановка прокси-сервера
     */
    public void stop() {
        isRunning = false;
        threadPool.shutdown();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Ошибка при закрытии серверного сокета", e);
            }
            serverSocket = null;
        }

        Log.i(TAG, "Прокси-сервер остановлен");
    }

    /**
     * Настройка прокси для WebView
     */
    private void setupProxy() {
        // Создание локального прокси
    // Удаляем вызов android.net.Proxy.setHttpProxySystemProperty, так как он не существует в современных Android
    // Proxy.setHttpProxySystemProperty(InetAddress.getLoopbackAddress().getHostAddress(), listenPort);

        // Сохранение прокси в глобальных переменных
        AppVars.LocalProxy = new java.net.Proxy(
            java.net.Proxy.Type.HTTP,
            new java.net.InetSocketAddress("127.0.0.1", listenPort)
        );

        // Настройка системных свойств для прокси
        System.setProperty("http.proxyHost", InetAddress.getLoopbackAddress().getHostAddress());
        System.setProperty("http.proxyPort", String.valueOf(listenPort));
        System.setProperty("https.proxyHost", InetAddress.getLoopbackAddress().getHostAddress());
        System.setProperty("https.proxyPort", String.valueOf(listenPort));
    }

    /**
     * Обработка подключений к прокси-серверу
     */
    private void acceptConnections() {
        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new Session(clientSocket));
            } catch (SocketException e) {
                if (isRunning) {
                    Log.e(TAG, "Ошибка при принятии подключения", e);
                }
            } catch (IOException e) {
                Log.e(TAG, "Ошибка при принятии подключения", e);
            }
        }
    }

    /**
     * Получение порта прокси-сервера
     * @return порт прокси-сервера
     */
    public int getListenPort() {
        return listenPort;
    }

    /**
     * Проверка, запущен ли прокси-сервер
     * @return true, если прокси-сервер запущен
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Получение URL прокси-сервера
     * @return URL прокси-сервера
     */
    public String getProxyUrl() {
        return "http://" + InetAddress.getLoopbackAddress().getHostAddress() + ":" + listenPort;
    }

    /**
     * Получение URI прокси-сервера
     * @return URI прокси-сервера
     */
    public Uri getProxyUri() {
        return Uri.parse(getProxyUrl());
    }
}