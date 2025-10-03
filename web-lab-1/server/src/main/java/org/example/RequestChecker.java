package org.example;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestChecker implements Command{
    private final String method;
    private final String contentType;
    private final String length;
    private final Logger logger = Logger.getLogger(Server.class.getName());

    private boolean isValid = true;

    public RequestChecker(String method, String type, String length) {
        this.method = method;
        this.contentType = type;
        this.length = length;
    }

    @Override
    public void execute() {
        logger.log(Level.INFO, "Starting request checker");
        if (method == null || !method.equalsIgnoreCase("POST")) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(405, "Only POST required");
            exceptionHandler.execute();
            logger.log(Level.INFO, "Not post");
            isValid = false;
        }

        if (contentType == null || !contentType.toLowerCase().startsWith("application/json")) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(415, "Incorrect must be: application/json");
            exceptionHandler.execute();
            logger.log(Level.INFO, "Not application/json");
            isValid = false;
        }

        if (length == null) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(411, "Data is null");
            exceptionHandler.execute();
            logger.log(Level.INFO, "Empty data");
            isValid = false;
        }
        logger.log(Level.INFO, "Correct request");
    }

    public boolean isValid() {
        logger.log(Level.INFO, "isValid: " + isValid);
        return isValid;
    }
}
