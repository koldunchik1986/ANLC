package com.neverlands.anlc.abproxy;

import android.util.Log;

import com.neverlands.anlc.AppVars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для обработки HTTP-сессии в прокси-сервере, аналог Session.cs
 */
public class Session implements Runnable {
    private static final String TAG = "ProxySession";
    private static final int BUFFER_SIZE = 8192;
    private static final int CONNECT_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 30000;

    private final Socket clientSocket;

    /**
     * Конструктор сессии
     * @param clientSocket сокет клиента
     */
    public Session(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при обработке запроса", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Ошибка при закрытии сокета клиента", e);
            }
        }
    }

    /**
     * Обработка HTTP-запроса
     */
    private void processRequest() {
        try {
            // Получение входного и выходного потоков
            InputStream clientIn = clientSocket.getInputStream();
            OutputStream clientOut = clientSocket.getOutputStream();

            // Чтение заголовков запроса
            HttpRequestHeaders requestHeaders = readRequestHeaders(clientIn);
            if (requestHeaders == null) {
                return;
            }

            // Обработка метода CONNECT (для HTTPS)
            if (requestHeaders.getMethod().equalsIgnoreCase("CONNECT")) {
                handleConnectMethod(requestHeaders, clientIn, clientOut);
                return;
            }

            // Получение URL запроса
            URL url = new URL(requestHeaders.getUrl());
            
            // Проверка, нужно ли использовать внешний прокси
            boolean useExternalProxy = AppVars.Profile != null && AppVars.Profile.isDoHttpLog();

            // Создание соединения с сервером
            HttpURLConnection connection;
            if (useExternalProxy && AppVars.LocalProxy != null) {
            // connection = (HttpURLConnection) url.openConnection(AppVars.LocalProxy);
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            // Настройка соединения
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestMethod(requestHeaders.getMethod());
            connection.setInstanceFollowRedirects(false);

            // Установка заголовков запроса
            for (HttpHeaderItem header : requestHeaders.getHeaders()) {
                if (!header.getName().equalsIgnoreCase("Proxy-Connection")) {
                    connection.setRequestProperty(header.getName(), header.getValue());
                }
            }

            // Добавление cookies, если необходимо
            String cookies = CookiesManager.obtain(url.getHost());
            if (cookies != null && !cookies.isEmpty()) {
                connection.setRequestProperty("Cookie", cookies);
            }

            // Отправка тела запроса, если это POST или PUT
            if (requestHeaders.getMethod().equalsIgnoreCase("POST") || requestHeaders.getMethod().equalsIgnoreCase("PUT")) {
                connection.setDoOutput(true);
                
                // Чтение и отправка тела запроса
                int contentLength = requestHeaders.getContentLength();
                if (contentLength > 0) {
                    byte[] buffer = new byte[contentLength];
                    int bytesRead = 0;
                    int totalBytesRead = 0;
                    
                    while (totalBytesRead < contentLength && (bytesRead = clientIn.read(buffer, totalBytesRead, contentLength - totalBytesRead)) != -1) {
                        totalBytesRead += bytesRead;
                    }
                    
                    OutputStream out = connection.getOutputStream();
                    out.write(buffer, 0, totalBytesRead);
                    out.flush();
                }
            }

            // Получение ответа от сервера
            int responseCode = connection.getResponseCode();
            
            // Формирование заголовков ответа
            StringBuilder responseHeaders = new StringBuilder();
            responseHeaders.append("HTTP/1.1 ").append(responseCode).append(" ").append(connection.getResponseMessage()).append("\r\n");
            
            // Добавление заголовков ответа
            for (String headerName : connection.getHeaderFields().keySet()) {
                if (headerName != null) {
                    for (String headerValue : connection.getHeaderFields().get(headerName)) {
                        responseHeaders.append(headerName).append(": ").append(headerValue).append("\r\n");
                        
                        // Сохранение cookies
                        if (headerName.equalsIgnoreCase("Set-Cookie")) {
                            CookiesManager.add(url.getHost(), headerValue);
                        }
                    }
                }
            }
            responseHeaders.append("\r\n");
            
            // Отправка заголовков ответа клиенту
            clientOut.write(responseHeaders.toString().getBytes());
            
            // Получение и отправка тела ответа
            InputStream responseStream;
            try {
                responseStream = connection.getInputStream();
            } catch (IOException e) {
                responseStream = connection.getErrorStream();
            }
            
            if (responseStream != null) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                
                while ((bytesRead = responseStream.read(buffer)) != -1) {
                    clientOut.write(buffer, 0, bytesRead);
                }
                
                responseStream.close();
            }
            
            // Закрытие соединения
            connection.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при обработке запроса", e);
        }
    }

    /**
     * Чтение заголовков HTTP-запроса
     * @param inputStream входной поток
     * @return заголовки запроса или null в случае ошибки
     */
    private HttpRequestHeaders readRequestHeaders(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String requestLine = reader.readLine();
            
            if (requestLine == null || requestLine.isEmpty()) {
                return null;
            }
            
            // Разбор строки запроса
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length != 3) {
                return null;
            }
            
            String method = requestParts[0];
            String url = requestParts[1];
            
            // Если URL не содержит протокол, добавляем его
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            
            // Создание объекта заголовков запроса
            HttpRequestHeaders headers = new HttpRequestHeaders(method, url);
            
            // Чтение заголовков
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                int colonIndex = line.indexOf(':');
                if (colonIndex > 0) {
                    String name = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    headers.addHeader(name, value);
                }
            }
            
            return headers;
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при чтении заголовков запроса", e);
            return null;
        }
    }

    /**
     * Обработка метода CONNECT (для HTTPS)
     * @param requestHeaders заголовки запроса
     * @param clientIn входной поток клиента
     * @param clientOut выходной поток клиента
     */
    private void handleConnectMethod(HttpRequestHeaders requestHeaders, InputStream clientIn, OutputStream clientOut) {
        try {
            // Получение хоста и порта из URL
            String[] hostPort = requestHeaders.getUrl().split(":");
            String host = hostPort[0];
            int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 443;
            
            // Создание соединения с сервером
            Socket serverSocket = new Socket();
            serverSocket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT);
            
            // Отправка ответа клиенту о успешном соединении
            String response = "HTTP/1.1 200 Connection Established\r\n\r\n";
            clientOut.write(response.getBytes());
            clientOut.flush();
            
            // Создание потоков для передачи данных между клиентом и сервером
            InputStream serverIn = serverSocket.getInputStream();
            OutputStream serverOut = serverSocket.getOutputStream();
            
            // Запуск потоков для передачи данных
            Thread clientToServer = new Thread(new StreamForwarder(clientIn, serverOut));
            Thread serverToClient = new Thread(new StreamForwarder(serverIn, clientOut));
            
            clientToServer.start();
            serverToClient.start();
            
            // Ожидание завершения потоков
            clientToServer.join();
            serverToClient.join();
            
            // Закрытие соединения с сервером
            serverSocket.close();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при обработке метода CONNECT", e);
        }
    }

    /**
     * Класс для передачи данных между потоками
     */
    private static class StreamForwarder implements Runnable {
        private final InputStream in;
        private final OutputStream out;
        
        public StreamForwarder(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }
        
        @Override
        public void run() {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    out.flush();
                }
            } catch (IOException e) {
                // Игнорируем ошибки при закрытии соединения
            }
        }
    }
}