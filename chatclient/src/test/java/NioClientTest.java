import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import services.*;
import utils.FileLogger;
import utils.Logger;

import java.io.*;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

public class NioClientTest {

    @BeforeAll
    static void initSuite() {
        System.out.println("Running NioClientTests");
    }

    @AfterAll
    static void completeSuite() {
        System.out.println("NioClientTests completed");
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
    void configuration_setFileLog_test(@TempDir Path tempDir) {
        String file = String.valueOf(tempDir.resolve("testFile.log"));
        Configuration config = Configuration.getInstance();

        Assertions.assertTrue(config.setFileLog(file));
    }


    @Test
    void configuration_getFileLog_test(@TempDir Path tempDir) {
        String file = String.valueOf(tempDir.resolve("testFile.log"));
        File expected = new File(file);
        Configuration config = Configuration.getInstance();
        config.setFileLog(file);
        File resultOfGet = config.getFileLog();

        Assertions.assertEquals(expected, resultOfGet);
    }

    @Test
    void configuration_getPort_test() {
        Configuration config = Configuration.getInstance();
        JSONParser parser = new JSONParser();
        int expected = 8888;

        try {
            JSONObject jsonObject = Mockito.mock(JSONObject.class);
            when(jsonObject.get("port"))
                    .thenReturn("8888");
            FileReader fileReader = Mockito.mock(FileReader.class);
            when(parser.parse(fileReader))
                    .thenReturn(jsonObject);
            int result = config.getPort();

            Assertions.assertEquals(expected, result);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    void configuration_getPort_Error_test() {
        Configuration config = Configuration.getInstance();

        Assertions.assertThrows(NullPointerException.class, config::getPort);
    }

    @Test
    void configuration_Error1_test() {
        Configuration config = Configuration.getInstance();

        Assertions.assertThrows(NullPointerException.class, () -> config.setFileSettings(null));
    }

    @Test
    void configuration_Error2_test() {
        Configuration config = Configuration.getInstance();

        Assertions.assertThrows(NullPointerException.class, () -> config.setFileLog(null));
    }

    @Test
    void configuration_Error3_test() {
        Configuration config = Configuration.getInstance();

        Assertions.assertThrows(NullPointerException.class, config::getFileLog);
    }

    @Test
    void socketChannelCreator_setHostName_test() {
        SocketChannelCreator scc = new SocketChannelCreator();

        String expected = "localhost";
        scc.setHostName(expected);
        String result = scc.getHostname();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void socketChannelCreator_setPort_test() {
        SocketChannelCreator scc = new SocketChannelCreator();

        int expected = 8888;
        scc.setPort(expected);
        int result = scc.getPort();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void socketChannelCreator_Errors_test() {
        SocketChannelCreator scc = new SocketChannelCreator();

        Assertions.assertThrows(NullPointerException.class, () -> scc.setHostName(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> scc.setPort(0));
    }

    @Test
    void socketChannelCreator_create_IOException_test() {
        SocketChannelCreator scc = new SocketChannelCreator();
        scc.setHostName("localhost");
        scc.setPort(8888);

        Assertions.assertThrows(IOException.class, scc::create);
    }

    @Test
    void socketChannelCreator_create_IllegalArgumentException_test() {
        SocketChannelCreator scc = new SocketChannelCreator();

        Assertions.assertThrows(IllegalArgumentException.class, scc::create);
    }

    @Test
    void messageReceiverTask_new_test() {
        try (MockedConstruction<MessageReceiveTask> mocked = Mockito.mockConstruction(MessageReceiveTask.class)) {
            SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
            ByteBuffer byteBuffer = Mockito.mock(ByteBuffer.class);
            MessageReceiveTask messageReceiveTask = new MessageReceiveTask(socketChannel, byteBuffer);
            Mockito.when(messageReceiveTask.getLogger())
                    .thenReturn(FileLogger.getInstance());

            Assertions.assertEquals(messageReceiveTask.getLogger(), FileLogger.getInstance());
        }
    }

    @Test
    void messageReceiverTask_run_test() {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        MessageReceiveTask messageReceiveTask = Mockito.mock(MessageReceiveTask.class);
        threadPool.submit(messageReceiveTask);
        threadPool.shutdown();
        Mockito.verify(messageReceiveTask, atLeastOnce()).run();
    }

    @Test
    void messageReceiverTask_NotYetConnectedException_test() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
        MessageReceiveTask messageReceiveTask = new MessageReceiveTask(socketChannel, inputBuffer);

        Assertions.assertThrows(NotYetConnectedException.class, messageReceiveTask::run);
    }

    @Test
    void messageReceiverTask_NullPointerException_test() {
        SocketChannel socketChannel = null;
        ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
        MessageReceiveTask messageReceiveTask = new MessageReceiveTask(socketChannel, inputBuffer);

        Assertions.assertThrows(NullPointerException.class, messageReceiveTask::run);
    }

    @Test
    void messageReceiverTask_IOException_test() {
        Throwable exception = Assertions.assertThrows(IOException.class, () -> {
            throw new IOException("что-то пошло не так");
        });

        Assertions.assertEquals("что-то пошло не так", exception.getMessage());
    }

    @Test
    void chatStarter_new_and_start_test() {
        try (MockedConstruction<ChatStarter> mocked = Mockito.mockConstruction(ChatStarter.class)) {
            SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
            ChatStarter chatStarter = new ChatStarter(socketChannel);
            Mockito.when(chatStarter.start())
                    .thenReturn(false);

            Assertions.assertFalse(chatStarter.start());
        }
    }

    @Test
    void chatStarter_Error_test() {
        SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
        ChatStarter chatStarter = new ChatStarter(socketChannel);

        Assertions.assertThrows(ConnectException.class, chatStarter::validateSocketChannelConnecting);
    }

    @Test
    void messageSender_new_and_start_test() {
        try (MockedConstruction<MessageSender> mocked = Mockito.mockConstruction(MessageSender.class)) {
            SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
            MessageSender messageSender = new MessageSender(socketChannel);
            Mockito.when(messageSender.start())
                    .thenReturn(true);

            Assertions.assertTrue(messageSender.start());
        }
    }

    @Test
    void messageSender_Error_test() {
        SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
        MessageSender messageSender = new MessageSender(socketChannel);

        Assertions.assertThrows(ConnectException.class, messageSender::validateSocketChannelConnecting);
    }

    @Test
    void fileLogger_notNull_test() {
        Logger logger = FileLogger.getInstance();

        Assertions.assertNotNull(logger);
    }

    @Test
    void fileLogger_log_test() {
        Logger logger = Mockito.mock(FileLogger.class);
        logger.log(anyString());

        Mockito.verify(logger, atLeastOnce()).log(anyString());
    }
}