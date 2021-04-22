package gui;

public class Log {
    final String message, time;

    public Log(String time, String message) {
        this.time = time;
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
