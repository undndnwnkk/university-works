package org.example;

public class RequestChecker implements Command{
    private final String method;
    private final String contentType;
    private final String length;

    private boolean isValid;

    public RequestChecker(String method, String type, String length) {
        this.method = method;
        this.contentType = type;
        this.length = length;
    }

    @Override
    public void execute() {
        if (method == null || !method.equalsIgnoreCase("POST")) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(405, "Only POST required");
            exceptionHandler.execute();
            isValid = false;
        }

        if (contentType == null || !contentType.toLowerCase().startsWith("application/json")) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(415, "Incorrect must be: application/json");
            exceptionHandler.execute();
            isValid = false;
        }

        if (length == null) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(411, "Data is null");
            exceptionHandler.execute();
            isValid = false;
        }
    }

    public boolean isValid() {
        return isValid;
    }
}
