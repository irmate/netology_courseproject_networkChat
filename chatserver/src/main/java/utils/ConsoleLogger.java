package utils;

public class ConsoleLogger implements Logger {
    public static ConsoleLogger logger = null;

    private ConsoleLogger() {

    }

    public static ConsoleLogger getInstance() {
        if (logger == null) logger = new ConsoleLogger();
        return logger;
    }

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }
}