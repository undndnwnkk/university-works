package org.example;

import com.fastcgi.FCGIInterface;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Server {
    private static final List<ResponseEntity> history = new ArrayList<>();
    Gson gson = new Gson();
    int id = 1;

    public static void main(String[] args) throws UnsupportedEncodingException {
        new Server().run();
    }

    private void run() throws UnsupportedEncodingException {
        FCGIInterface fcgi = new FCGIInterface();
        while(fcgi.FCGIaccept() >= 0) {
            handleRequest();
            id++;
        }
    }

    private void handleRequest() throws UnsupportedEncodingException {
        long startTime = System.nanoTime();

        try {
            String method = System.getProperty("REQUEST_METHOD");
            if (method == null || !method.equalsIgnoreCase("POST")) {
                sendErrorResponse(405, "Разрешен только POST запрос");
                return;
            }

            String contentType = System.getProperty("CONTENT_TYPE");
            if (contentType == null || !contentType.toLowerCase().startsWith("application/x-www-form-urlencoded")) {
                sendErrorResponse(415, "Неподдерживаемый Content-Type, должен быть: application/x-www-form-urlencoded.");
                return;
            }

            String contentLengthStr = System.getProperty("CONTENT_LENGTH");
            if (contentLengthStr == null) {
                sendErrorResponse(411, "Нулевая длина данных");
                return;
            }

            int contentLengthInteger = Integer.parseInt(contentLengthStr.trim());
            String body = readBody(System.in, contentLengthInteger);
            Map<String, String> data = parseFromRequest(body);
            List<String> numbersData = getNumbersFromData(data);
            boolean isValidData = checkValid(numbersData);

            if(isValidData) {
                boolean isHitted = checkHit(numbersData);
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;
                LocalTime currentTime = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String timeToResponse =  formatter.format(currentTime);
                String response = generateResponse(id, timeToResponse, numbersData, isHitted, executionTime);
                System.out.print("Content-type: application/json\r\n");
                System.out.print("\r\n");
                System.out.print(response);

            } else {
                String errorJson = "{\"error\":\"Invalid data\"}";
                System.out.print("Content-type: application/json\r\n");
                System.out.print("Status: 400 Bad Request\r\n");
                System.out.print("\r\n");
                System.out.print(errorJson);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readBody(InputStream in, int contentLength) {
        byte[] buffer = new byte[contentLength];
        try {
            in.read(buffer);
        } catch (Exception e) {
            System.err.println("Ошибка при чтении тела запроса: " + e.getMessage());
            return null;
        }

        return new String(buffer, StandardCharsets.UTF_8);
    }    

    private Map<String, String> parseFromRequest(String body) throws UnsupportedEncodingException {
        Map<String, String> dataFromRequest = new HashMap<>();
        List<String> pairs = Arrays.asList(body.split("&"));

        if(pairs.size() == 3) {
            for (String pair: pairs) {
                int indexOfEquality = pair.indexOf("=");
                if(indexOfEquality == -1) {
                    throw new RuntimeException("Invalid response");
                }

                String key = URLDecoder.decode(pair.substring(0, indexOfEquality).trim(), "UTF-8");
                String value = URLDecoder.decode(pair.substring(indexOfEquality + 1).trim(), "UTF-8");

                if(value.isEmpty()) {
                    value = null;
                }

                dataFromRequest.put(key, value);
            }

            return dataFromRequest;
        } else {
            throw new RuntimeException("Invalid request");
        }
    }

    private List<String> getNumbersFromData(Map<String, String> data) {
        List<String> numbers = new ArrayList<>();
        numbers.add(data.get("x"));
        numbers.add(data.get("y"));
        numbers.add(data.get("r"));

        return numbers;
    }

    private boolean checkValid(List<String> numbers) {
        int x = Integer.parseInt(numbers.get(0));
        float y = Float.parseFloat(numbers.get(1));
        float r = Float.parseFloat(numbers.get(2));

        if (x < -3 || x > 5) {
            return false;
        }

        if (y < -5 || y > 3) {
            return false;
        }

        if (r < 1 || r > 3) {
            return false;
        }

        return true;
    }

    private boolean checkHit(List<String> numbers) {
        int x = Integer.parseInt(numbers.get(0));
        float y = Float.parseFloat(numbers.get(1));
        float r = Float.parseFloat(numbers.get(2));

        if (x * x + y * y <= r * r) {
            return true;
        } else {
            return false;
        }
    }

    private String generateResponse(int id, String time, List<String> numbers, boolean isHitted, long executionTime) {
        ResponseEntity response = new ResponseEntity(
                id,
                time,
                Integer.parseInt(numbers.get(0)),
                Float.parseFloat(numbers.get(1)),
                Float.parseFloat(numbers.get(2)),
                isHitted,
                executionTime
        );

        try {
            return gson.toJson(response);
        } catch (Exception e) {
            throw new RuntimeException("Invalid json generating");
        }
    }

    private void sendErrorResponse(int statusCode, String message) {
        String statusText;
        switch (statusCode) {
            case 400: statusText = "Bad Request"; break;
            case 405: statusText = "Method Not Allowed"; break;
            case 411: statusText = "Length Required"; break;
            case 415: statusText = "Unsupported Media Type"; break;
            case 500:
            default: statusText = "Internal Server Error"; break;
        }

        String errorJson = String.format("{\"error\":\"%s\"}", message.replace("\"", "\\\""));
        System.out.print("Content-type: application/json\r\n");
        System.out.printf("Status: %d %s\r\n", statusCode, statusText);
        System.out.print("\r\n");
        System.out.print(errorJson);
    }

}
