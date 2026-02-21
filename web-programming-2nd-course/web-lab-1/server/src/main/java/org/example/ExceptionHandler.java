package org.example;

public class ExceptionHandler implements Command {
    private final int statusCode;
    private final String message;

    public ExceptionHandler(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public void execute() {
        String statusText = switch (statusCode) {
            case 400 -> "Bad Request";
            case 405 -> "Method Not Allowed";
            case 411 -> "Length Required";
            case 415 -> "Unsupported Media Type";
            case 500 -> "Not enough data";
            default -> "Internal Server Error";
        };

        String errorJson = String.format("{\"error\":\"%s\"}", message.replace("\"", "\\\""));
        System.out.print("Content-type: application/json\r\n");
        System.out.printf("Status: %d %s\r\n", statusCode, statusText);
        System.out.print("\r\n");
        System.out.print(errorJson);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
