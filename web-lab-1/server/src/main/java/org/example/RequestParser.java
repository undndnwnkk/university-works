package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class RequestParser implements Command {
    private final InputStream in;
    private final Gson gson;
    private Request parsedRequest;
    public RequestParser(InputStream in, Gson gson) {
        this.in = in;
        this.gson = gson;
    }

    @Override
    public void execute() {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            int charRead;

            while ((charRead = br.read()) != -1) {
                sb.append((char)  charRead);
            }
        } catch(IOException e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(400, "Bad Request");
            exceptionHandler.execute();
        }


        String body = sb.toString();

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

        if (jsonObject.size() > 3) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(400, "Too much arguments");
            exceptionHandler.execute();
        }

        parsedRequest = gson.fromJson(jsonObject, Request.class);

        if(parsedRequest.getX() == null || parsedRequest.getY() == null || parsedRequest.getR() == null) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(400, "Not enough arguments");
            exceptionHandler.execute();
        }
    }

    public Request getParsedRequest() {
        return parsedRequest;
    }
}
