import service.Configuration;
import service.NioServerProcess;
import service.Starter;

import java.io.File;

public class NioServer {

    public static void main(String[] args) {
        final String fileSettingsPath = new File(".", "/src/main/resources/settings.json").getAbsolutePath();
        final String fileLogPath = new File(".", "/src/main/resources/file.log").getAbsolutePath();

        Configuration config = Configuration.getInstance();
        if (
                config.setFileSettings(fileSettingsPath) &&
                        config.setConfiguration(
                                "8080",
                                fileLogPath
                        )
        ) {
            new NioServerProcess(new Starter(config)).start();
        }
    }
}