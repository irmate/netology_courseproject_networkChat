package utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger implements Logger {
    public static FileLogger logger = null;
    private final File fileLog;

    private FileLogger() {
        fileLog = setFileLog();
    }

    public static FileLogger getInstance() {
        if (logger == null) logger = new FileLogger();
        return logger;
    }

    public File setFileLog() {
        File file = null;
        JSONParser parser = new JSONParser();
        Configuration config = Configuration.getInstance();
        try {
            Object obj = parser.parse(new FileReader(config.getFileSettings()));
            JSONObject jsonObject = (JSONObject) obj;
            file = new File((String) jsonObject.get("fileLogPath"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void log(String msg) {
        try (FileWriter wr = new FileWriter(fileLog, true)) {
            wr.write(msg + "\n");
            wr.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}