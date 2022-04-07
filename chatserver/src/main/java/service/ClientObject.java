package service;

import java.nio.channels.SocketChannel;

public class ClientObject {
    private final String socketChannelAddress;
    private String name;
    private final SocketChannel socketChannel;


    public ClientObject(SocketChannel socketChannel, String socketChannelAddress) {
        this.socketChannel = socketChannel;
        this.socketChannelAddress = socketChannelAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocketChannelAddress() {
        if (socketChannelAddress == null){
            throw new NullPointerException();
        }
        return socketChannelAddress;
    }

    public String getName() {
        return name;
    }

    public SocketChannel getSocketChannel() {
        if (socketChannel == null){
            throw new NullPointerException();
        }
        return socketChannel;
    }
}