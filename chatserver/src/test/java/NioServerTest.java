import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import service.ClientObject;
import service.Configuration;
import service.NioServerProcess;
import service.Starter;
import utils.ConsoleLogger;
import utils.FileLogger;
import utils.Logger;

import java.io.File;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.Mockito.*;

public class NioServerTest {

    @BeforeAll
    static void initSuite() {
        System.out.println("Running NioServerTests");
    }

    @AfterAll
    static void completeSuite() {
        System.out.println("NioServerTests completed");
    }

    @Test
    void configuration_notNull_test() {
        Configuration expected = Configuration.getInstance();

        Assertions.assertNotNull(expected);
    }

    @Test
    void configuration_setFileSettings_test(@TempDir Path tempDir) {
        String file = String.valueOf(tempDir.resolve("testSettings.json"));
        Configuration config = Configuration.getInstance();

        Assertions.assertTrue(config.setFileSettings(file));
    }

    @Test
    void configuration_getFileSettings_test(@TempDir Path tempDir) {
        String filePath = String.valueOf(tempDir.resolve("testSettings.json"));
        File expected = new File(filePath);
        Configuration config = Configuration.getInstance();
        config.setFileSettings(filePath);
        File result = config.getFileSettings();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void configuration_Error1_test() {
        Configuration config = Configuration.getInstance();

        Assertions.assertThrows(NullPointerException.class, config::getFileSettings);
    }

    @Test
    void configuration_Error2_test() {
        Configuration config = Configuration.getInstance();

        Assertions.assertThrows(NullPointerException.class, () -> config.setConfiguration("test", "test"));

    }

    @Test
    void configuration_Error3_test() {
        Configuration config = Configuration.getInstance();

        Assertions.assertThrows(NullPointerException.class, () -> config.setFileSettings(null));
    }

    @Test
    void configuration_setConfiguration_test(@TempDir Path tempDir) {
        String file = String.valueOf(tempDir.resolve("testSettings.json"));
        Configuration config = Configuration.getInstance();
        config.setFileSettings(file);

        Assertions.assertTrue(config.setConfiguration("test", "test"));
    }

    @Test
    void starter_new_test() {
        try (MockedConstruction<Starter> mocked = Mockito.mockConstruction(Starter.class)) {
            Starter starter = new Starter(Configuration.getInstance());
            Mockito.when(starter.getConsoleLogger())
                    .thenReturn(ConsoleLogger.getInstance());

            Assertions.assertEquals(starter.getConsoleLogger(), ConsoleLogger.getInstance());
        }
    }

    @Test
    void starter_getPort_test(@TempDir Path tempDir) {
        String fileSettings = String.valueOf(tempDir.resolve("testSettings.json"));
        String fileFileLog = String.valueOf(tempDir.resolve("testFile.log"));
        Configuration config = Configuration.getInstance();
        config.setFileSettings(fileSettings);
        config.setConfiguration(
                "8090",
                fileFileLog
        );
        Starter starter = new Starter(config);

        int expected = 8090;
        int result = starter.getPort();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void starter_create_test(@TempDir Path tempDir) {
        String fileSettings = String.valueOf(tempDir.resolve("testSettings.json"));
        String fileFileLog = String.valueOf(tempDir.resolve("testFile.log"));
        Configuration config = Configuration.getInstance();
        config.setFileSettings(fileSettings);
        config.setConfiguration(
                "8090",
                fileFileLog
        );
        Starter starter = new Starter(config);

        Assertions.assertDoesNotThrow(starter::create);
    }

    @Test
    void starter_getServerSocketChannel_test(@TempDir Path tempDir) {
        String fileSettings = String.valueOf(tempDir.resolve("testSettings.json"));
        String fileFileLog = String.valueOf(tempDir.resolve("testFile.log"));
        Configuration config = Configuration.getInstance();
        config.setFileSettings(fileSettings);
        config.setConfiguration(
                "8090",
                fileFileLog
        );
        Starter starter = new Starter(config);

        Assertions.assertDoesNotThrow(starter::getServerSocketChannel);
    }

    @Test
    void starter_getSelector_test(@TempDir Path tempDir) {
        String fileSettings = String.valueOf(tempDir.resolve("testSettings.json"));
        String fileFileLog = String.valueOf(tempDir.resolve("testFile.log"));
        Configuration config = Configuration.getInstance();
        config.setFileSettings(fileSettings);
        config.setConfiguration(
                "8091",
                fileFileLog
        );
        Starter starter = new Starter(config);

        Assertions.assertDoesNotThrow(starter::getSelector);
    }

    @Test
    void clientObject_new_test() {
        try (MockedConstruction<ClientObject> mocked = Mockito.mockConstruction(ClientObject.class)) {
            SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
            ClientObject clientObject = new ClientObject(socketChannel, "test");
            Mockito.when(clientObject.getName())
                    .thenReturn("TestName");

            String result = clientObject.getName();

            Assertions.assertEquals(clientObject.getName(), result);
        }
    }

    @Test
    void clientObject_setName_getName_test() {
        String expected = "TestName";
        SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
        ClientObject clientObject = new ClientObject(socketChannel, "testAddress");
        clientObject.setName(expected);
        String result = clientObject.getName();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void clientObject_getSocketChannelAddress_test() {
        String expected = "TestAddress";
        SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
        ClientObject clientObject = new ClientObject(socketChannel, expected);
        String result = clientObject.getSocketChannelAddress();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void clientObject_getSocketChannel_test() {
        SocketChannel expected = Mockito.mock(SocketChannel.class);
        ClientObject clientObject = new ClientObject(expected, "testAddress");
        SocketChannel result = clientObject.getSocketChannel();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void clientObject_Errors_test() {
        ClientObject clientObject = new ClientObject(null, null);

        Assertions.assertThrows(NullPointerException.class, clientObject::getSocketChannel);
        Assertions.assertThrows(NullPointerException.class, clientObject::getSocketChannelAddress);
    }

    @Test
    void nioServerProcess_new_test() {
        try (MockedConstruction<NioServerProcess> mocked = Mockito.mockConstruction(NioServerProcess.class)) {
            Starter starter = Mockito.mock(Starter.class);
            NioServerProcess nioServerProcess = new NioServerProcess(starter);
            Mockito.when(nioServerProcess.getConsoleLogger())
                    .thenReturn(ConsoleLogger.getInstance());

            Assertions.assertEquals(nioServerProcess.getConsoleLogger(), ConsoleLogger.getInstance());
        }
    }

    @Test
    void nioServerProcess_start_test() {
        NioServerProcess nioServerProcess = Mockito.mock(NioServerProcess.class);
        nioServerProcess.start();

        Mockito.verify(nioServerProcess, Mockito.atLeastOnce()).start();
    }

    @Test
    void nioServerProcess_resolveClient_test() {
        SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
        NioServerProcess nioServerProcess = Mockito.mock(NioServerProcess.class);
        List<ClientObject> clientObjects = new CopyOnWriteArrayList<>();

        nioServerProcess.resolveClient(clientObjects, socketChannel);

        Mockito.verify(nioServerProcess, atLeastOnce()).resolveClient(clientObjects, socketChannel);
    }

    @Test
    void consoleLogger_notNull_test() {
        Logger expected = ConsoleLogger.getInstance();

        Assertions.assertNotNull(expected);
    }

    @Test
    void consoleLogger_log_test() {
        Logger logger = Mockito.mock(ConsoleLogger.class);
        logger.log(Mockito.anyString());

        Mockito.verify(logger, Mockito.atLeastOnce()).log(anyString());
    }

    @Test
    void fileLogger_notNull_test(@TempDir Path tempDir) {
        String fileSettings = String.valueOf(tempDir.resolve("testSettings.json"));
        String fileFileLog = String.valueOf(tempDir.resolve("testFile.log"));
        Configuration config = Configuration.getInstance();
        config.setFileSettings(fileSettings);
        config.setConfiguration(
                "8090",
                fileFileLog
        );
        Logger expected = FileLogger.getInstance();

        Assertions.assertNotNull(expected);
    }

    @Test
    void fileLogger_setFileLog_test(@TempDir Path tempDir) {
        String fileSettings = String.valueOf(tempDir.resolve("testSettings.json"));
        String fileFileLog = String.valueOf(tempDir.resolve("testFile.log"));

        File expected = new File(fileFileLog);

        Configuration config = Configuration.getInstance();
        config.setFileSettings(fileSettings);
        config.setConfiguration(
                "8090",
                fileFileLog
        );
        FileLogger logger = FileLogger.getInstance();
        File result = logger.setFileLog();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void fileLogger_log_test() {
        Logger logger = Mockito.mock(FileLogger.class);
        logger.log(Mockito.anyString());

        Mockito.verify(logger, Mockito.atLeastOnce()).log(anyString());
    }
}