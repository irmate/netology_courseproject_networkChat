package services;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SocketChannelCreator {
    private String hostname;
    private int port;

    public void setHostName(String hostname) {
        if (hostname == null) {
            throw new NullPointerException("отсутствует значение");
        }
        this.hostname = hostname;
    }

    public void setPort(int port) {
        if (port == 0) {
            throw new IllegalArgumentException("введено значение 0");
        }
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public SocketChannel create() throws IOException {
        InetSocketAddress socketAddress = new InetSocketAddress(hostname, port);
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(socketAddress);
        return socketChannel;
    }
}