import service.Configuration;
import service.NioServerProcess;
import service.Starter;

public class NioServer {

    public static void main(String[] args) {
        Configuration config = Configuration.getInstance();
        if (
                config.setFileSettings("/home/irmate/Документы/Develop/java лекции/Курсач 2/chatserver/src/main/resources/settings.json") &&
                        config.setConfiguration(
                                "8080",
                                "/home/irmate/Документы/Develop/java лекции/Курсач 2/chatserver/src/main/resources/file.log"
                        )
        ) {
            new NioServerProcess(new Starter(config)).start();
        }
    }
}