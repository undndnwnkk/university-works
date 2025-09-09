package org.example;

import com.fastcgi.FCGIInterface;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server {
    private static final List<RequestEntity> history = new ArrayList<>();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        while(acceptRequest() >= 0) {
            handleRequest();
        }
    }

    private static int acceptRequest() {
        try {
            return new FCGIInterface().FCGIaccept();
        } catch (Exception e) {
            System.err.println("Не нашлась библиотека с FCGI, " + e.getMessage());
            throw e;
        }
    }

    private static void handleRequest() {
        long t0 = System.nanoTime();

        try {
            String method = System.getProperty("REQUEST_METHOD", "");
            String contentType = System.getProperty("CONTENT_TYPE", "");
            String contentLength = System.getProperty("CONTENT_LENGTH", "");

            if(!"POST".equalsIgnoreCase(method)) {
                System.err.println("Неверный тип запроса, поддерживаем только POST");
                return;
            }

            if(!contentType.equalsIgnoreCase("application/x-www-form-urlencoded")) {
                System.err.println("Неверный Content-Type: " + contentType);
                return;
            }

            int contentLengthInteger = Integer.parseInt(contentLength.trim());
            String body = readBody(System.in, contentLengthInteger);

            Map<String, String> params = parseFromUrlencoder(body);


        }
    }
}
