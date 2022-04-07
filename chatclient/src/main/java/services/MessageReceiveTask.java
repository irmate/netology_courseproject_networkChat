package services;

import utils.FileLogger;
import utils.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class MessageReceiveTask implements Runnable {
    private final SocketChannel socketChannel;
    private final ByteBuffer inputBuffer;
    private Logger logger;

    public MessageReceiveTask(SocketChannel socketChannel, ByteBuffer inputBuffer) {
        this.socketChannel = socketChannel;
        this.inputBuffer = inputBuffer;
        logger = FileLogger.getInstance();
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public void run() {
        String msg;
        try {
            while (true) {
                int bytesCount = socketChannel.read(inputBuffer);
                msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8).trim();
                System.out.println(msg);
                inputBuffer.clear();
                logger.log(
                        LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                " " +
                                msg
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}