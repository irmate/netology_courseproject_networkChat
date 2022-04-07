package services;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Configuration {
    public static Configuration config = null;
    private File fileLog;
    private File fileSettings;

    private Configuration() {

    }

    public static Configuration getInstance() {
        if (config == null) config = new Configuration();
        return config;
    }

    public boolean setFileSettings(String path) {
        fileSettings = new File(path);
        return true;
    }

    public boolean setFileLog(String path) {
        fileLog = new File(path);
        return true;
    }

    public File getFileLog() {
        if (fileLog == null) {
            throw new NullPointerException();
        }
        return fileLog;
    }

    public int getPort() {
        if (fileSettings == null) {
            throw new NullPointerException();
        }
        String port = null;
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(fileSettings));
            JSONObject jsonObject = (JSONObject) obj;
            port = (String) jsonObject.get("port");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(port);
    }
}