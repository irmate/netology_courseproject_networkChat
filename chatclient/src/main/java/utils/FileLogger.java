package utils;

import services.Configuration;

import java.io.FileWriter;
import java.io.IOException;

public class FileLogger implements Logger {
    public static FileLogger logger = null;

    private FileLogger() {
    }

    public static FileLogger getInstance() {
        if (logger == null) logger = new FileLogger();
        return logger;
    }

    @Override
    public void log(String msg) {
        Configuration config = Configuration.getInstance();
        try (FileWriter wr = new FileWriter(config.getFileLog(), true)) {
            wr.write(msg + "\n");
            wr.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}