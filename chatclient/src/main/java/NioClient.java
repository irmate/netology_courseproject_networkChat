import services.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClient {

    public static void main(String[] args) {
        final String fileSettingsPath = new File(".", "/src/main/resources/settings.json").getAbsolutePath();
        final String fileLogPath = new File(".", "/src/main/resources/file.log").getAbsolutePath();

        try {
            Configuration config = Configuration.getInstance();
            config.setFileSettings(fileSettingsPath);
            config.setFileLog(fileLogPath);

            SocketChannelCreator scc = new SocketChannelCreator();
            scc.setHostName("localhost");
            scc.setPort(config.getPort());

            final SocketChannel socketChannel = scc.create();

            Thread thread = new Thread(new MessageReceiveTask(socketChannel, ByteBuffer.allocate(2 << 10)));
            thread.setDaemon(true);
            thread.start();
            if (new ChatStarter(socketChannel).start() || new MessageSender(socketChannel).start()) {
                if (!thread.isAlive()) {
                    socketChannel.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}