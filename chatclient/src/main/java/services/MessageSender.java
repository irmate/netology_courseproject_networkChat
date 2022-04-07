package services;

import utils.FileLogger;
import utils.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Scanner;

public class MessageSender {
    private final SocketChannel socketChannel;
    private Logger logger;

    public MessageSender(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        logger = FileLogger.getInstance();
    }

    public void validateSocketChannelConnecting() throws ConnectException {
        if (!socketChannel.isConnected()) {
            throw new ConnectException("SocketChannel - отсутствует соединение с сервером");
        }
    }

    public Boolean start() {
        try (Scanner scanner = new Scanner(System.in)) {
            validateSocketChannelConnecting();
            String msg;
            while (true) {
                msg = scanner.nextLine().trim() + "\r\n";
                if (("exit" + "\r\n").equals(msg)) {
                    logger.log(
                            LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                    " " + socketChannel.getRemoteAddress() +
                                    " соединение с сервером завершено успешно"
                    );
                    break;
                }
                socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                logger.log(
                        LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                " " +
                                "вы отправили сообщение" +
                                ": " +
                                msg.trim()
                );
                Thread.sleep(2000);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}