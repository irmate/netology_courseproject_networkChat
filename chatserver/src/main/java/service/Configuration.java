package service;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {
    public static Configuration config = null;
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

    public boolean setConfiguration(String port, String path) {
        if (fileSettings == null) {
            throw new NullPointerException();
        }
        JSONObject obj = new JSONObject();
        obj.put("port", port);
        obj.put("fileLogPath", path);
        try (FileWriter writer = new FileWriter(fileSettings, false)) {
            writer.write(obj.toJSONString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public File getFileSettings() {
        if (fileSettings == null) {
            throw new NullPointerException();
        }
        return fileSettings;
    }
}