package org.example;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestChecker implements Command{
    private final String method;
    private final String contentType;
    private final String length;
    private final String uri;
    private final Logger logger = Logger.getLogger(Server.class.getName());

    private boolean isValid = true;

    public RequestChecker(String method, String type, String length, String uri) {
        this.method = method;
        this.contentType = type;
        this.length = length;
        this.uri = uri;
    }

    @Override
    public void execute() {
        logger.log(Level.INFO, "Starting request checker");
        logger.log(Level.INFO, "Method: " + method);
        logger.log(Level.INFO, "Content-Type: " + contentType);
        logger.log(Level.INFO, "Length: " + length);
        logger.log(Level.INFO, "URI: " + uri);

        if (!"/calculate".equals(uri) && !"/calculate/".equals(uri)) {
            new ExceptionHandler(404, "Not found").execute();
            isValid = false;
            return;
        }

        if (method == null || !method.equalsIgnoreCase("POST")) {
            new ExceptionHandler(405, "Only POST required").execute();
            logger.log(Level.INFO, "Not post");
            isValid = false;
            return;
        }

        String ct = contentType;
        if (ct == null || ct.isEmpty()) ct = System.getProperty("CONTENT_TYPE");
        if (ct == null || ct.isEmpty()) ct = System.getProperty("HTTP_CONTENT_TYPE");
        if (ct == null || ct.isEmpty()) ct = System.getenv("CONTENT_TYPE");
        if (ct == null || ct.isEmpty()) ct = System.getenv("HTTP_CONTENT_TYPE");

        String mime = null;
        if (ct != null) {
            int p = ct.indexOf(';');
            mime = (p >= 0 ? ct.substring(0, p) : ct).trim().toLowerCase(Locale.ROOT);
        }

        boolean hasBody = false;
        String cl = length;
        if (cl == null) cl = System.getProperty("CONTENT_LENGTH");
        if (cl == null) cl = System.getenv("CONTENT_LENGTH");
        if (cl != null) {
            try { hasBody = Integer.parseInt(cl.trim()) > 0; } catch (Exception ignored) {}
        }

        logger.log(Level.INFO, "Our content-type: " + mime);
        boolean okType = "application/json".equals(mime);

        if (!okType) {
            new ExceptionHandler(415, "Incorrect must be: application/json").execute();
            logger.log(Level.INFO, "Not application/json");
            isValid = false;
            return;
        }

        if (!hasBody) {
            new ExceptionHandler(411, "Data is null").execute();
            logger.log(Level.INFO, "Empty data");
            isValid = false;
            return;
        }

        if (isValid) {
            logger.log(Level.INFO, "Correct request");
        }
    }

    public boolean isValid() {
        logger.log(Level.INFO, "isValid: " + isValid);
        return isValid;
    }
}