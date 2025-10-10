package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestParser implements Command {
    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);
    private final InputStream in;
    private final Gson gson;
    private Request parsedRequest;
    public RequestParser(InputStream in, Gson gson) {
        this.in = in;
        this.gson = gson;
    }

    @Override
    public void execute() {
        int len;
        try {
            String cls = System.getProperty("CONTENT_LENGTH");
            if (cls == null) { new ExceptionHandler(411, "Length Required").execute(); return; }
            len = Integer.parseInt(cls.trim());
            if (len <= 0) { new ExceptionHandler(411, "Empty body").execute(); return; }
        } catch (Exception e) {
            new ExceptionHandler(400, "Bad Request").execute(); return;
        }

        byte[] buf = new byte[len];
        int off = 0;
        try {
            while (off < len) {
                int r = in.read(buf, off, len - off);
                if (r <= 0) break;
                off += r;
            }
        } catch (IOException e) {
            new ExceptionHandler(400, "Bad Request").execute(); return;
        }
        String body = new String(buf, 0, off, java.nio.charset.StandardCharsets.UTF_8);

        JsonObject jsonObject;
        try {
            jsonObject = com.google.gson.JsonParser.parseString(body).getAsJsonObject();
        } catch (Exception ex) {
            new ExceptionHandler(400, "Invalid JSON").execute(); return;
        }

        if (jsonObject.size() > 3) {
            new ExceptionHandler(400, "Too much arguments").execute(); return;
        }

        parsedRequest = gson.fromJson(jsonObject, Request.class);
        if (parsedRequest == null ||
                parsedRequest.getX() == null ||
                parsedRequest.getY() == null ||
                parsedRequest.getR() == null) {
            new ExceptionHandler(400, "Not enough arguments").execute();
            parsedRequest = null;
            return;
        }
    }


    public Request getParsedRequest() {
        return parsedRequest;
    }
}
