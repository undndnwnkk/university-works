package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResponseGenerator implements Command{
    private final int currentId;
    private final Request request;
    private final long elapsedMs;
    private final Gson gson;
    private final Logger logger =  Logger.getLogger(ResponseGenerator.class.getName());

    private boolean isValid;


    public ResponseGenerator(int id, Request request, long elapsedMs, Gson gson) {
        this.request = request;
        this.elapsedMs = elapsedMs;
        this.currentId = id;
        this.gson = gson;
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

    @Override
    public void execute() {
        HitChecker hitChecker = new HitChecker(request);
        hitChecker.execute();
        isValid = hitChecker.getHitted();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timeToResponse =  formatter.format(now);

        String response = generateResponse(currentId, timeToResponse, request, isValid, elapsedMs, gson);
        logger.log(Level.INFO, "Final JSON response: " + response);

        byte[] body = response.getBytes(StandardCharsets.UTF_8);

        OutputStream out = System.out;

        String hdr =
                "Status: 200 OK\r\n" +
                        "Content-Type: application/json; charset=utf-8\r\n" +
                        "Content-Length: " + body.length + "\r\n" +
                        "\r\n";

        try {
            out.write(hdr.getBytes(StandardCharsets.US_ASCII));
            out.write(body);
            out.flush();
        } catch ( IOException ex) {
            Logger.getLogger(ResponseGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String toString() {
        return "ResponseGenerator{" +
                "currentId=" + currentId +
                ", request=" + request +
                ", elapsedMs=" + elapsedMs +
                ", gson=" + gson +
                ", isValid=" + isValid +
                '}';
    }
}
