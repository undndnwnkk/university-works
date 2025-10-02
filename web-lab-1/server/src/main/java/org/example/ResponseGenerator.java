package org.example;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseGenerator implements Command{
    private final int currentId;
    private final Request request;
    private final long elapsedMs;
    private final Gson gson;

    private boolean isValid;


    public ResponseGenerator(int id, Request request, long elapsedMs, Gson gson) {
        this.request = request;
        this.elapsedMs = elapsedMs;
        this.currentId = id;
        this.gson = gson;
    }

    @Override
    public void execute() {
        HitChecker hitChecker = new HitChecker(request);
        hitChecker.execute();
        isValid = hitChecker.getHitted();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timeToResponse =  formatter.format(now);

        String response = generateResponse(currentId, timeToResponse, request, isValid, elapsedMs, gson);

        System.out.print("Content-type: application/json\r\n");
        System.out.print("\r\n");
        System.out.print(response);
    }

    private String generateResponse(int id, String time, Request request, boolean isHitted, long executionTime, Gson gson) {
        ResponseEntity response = new ResponseEntity(
                id,
                time,
                request.getX().intValue(),
                request.getY().floatValue(),
                request.getR().floatValue(),
                isHitted,
                executionTime
        );

        return gson.toJson(response);
    }
}
