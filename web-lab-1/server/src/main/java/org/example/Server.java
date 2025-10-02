package org.example;

import com.fastcgi.FCGIInterface;
import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;

public class Server {
    Gson gson = new Gson();
    static int id = 1;

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
            RequestChecker requestChecker = new RequestChecker(System.getProperty("REQUEST_METHOD"), System.getProperty("CONTENT_TYPE"), System.getProperty("CONTENT_LENGTH"));
            requestChecker.execute();
            boolean canStart = requestChecker.isValid();

            if (canStart) {
                RequestParser requestParser = new RequestParser(System.in, gson);
                requestParser.execute();
                Request request = requestParser.getParsedRequest();

                Validation validation = new Validation(request);
                validation.execute();
                boolean isValidData = validation.getValid();

                if(isValidData) {
                    long endTime = System.nanoTime();
                    long elapsedMs = endTime - startTime;
                    ResponseGenerator responseGenerator = new ResponseGenerator(id, request, elapsedMs, gson);
                    responseGenerator.execute();

                } else {
                    ExceptionHandler exceptionHandler = new ExceptionHandler(400, "Invalid data");
                    exceptionHandler.execute();
                }
            } else {
                return;
            }

        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(500, "Internal Server Error");
            exceptionHandler.execute();
        }
    }
}
