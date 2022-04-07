import services.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClient {

    public static void main(String[] args) {
        try {
            Configuration config = Configuration.getInstance();
            config.setFileSettings("/home/irmate/Документы/Develop/java лекции/Курсач 2/chatserver/src/main/resources/settings.json");
            config.setFileLog("/home/irmate/Документы/Develop/java лекции/Курсач 2/chatclient/src/main/resources/file.log");

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