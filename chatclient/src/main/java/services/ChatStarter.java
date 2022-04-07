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

public class ChatStarter {
    private final SocketChannel socketChannel;
    private Scanner scanner;
    private Logger logger;

    public ChatStarter(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        logger = FileLogger.getInstance();
        scanner = new Scanner(System.in);
    }

    public void validateSocketChannelConnecting() throws ConnectException {
        if (!socketChannel.isConnected()) {
            throw new ConnectException("SocketChannel - отсутствует соединение с сервером");
        }
    }

    public boolean start() {
        try {
            validateSocketChannelConnecting();
            logger.log(
                    LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                            " " +
                            socketChannel.getRemoteAddress() +
                            " соединение c сервером установлено успешно"
            );
            System.out.println(
                    """
                            Приветствуем Вас в чате!
                            Пожалуйста укажите имя для начала общения.
                            Для выхода из чата напишите exit"""
            );
            String name = scanner.nextLine().trim() + "\r\n";
            if (("exit" + "\r\n").equals(name)) {
                logger.log(
                        LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                " " + socketChannel.getRemoteAddress() +
                                " соединение с сервером завершено успешно"
                );
                return true;
            }
            socketChannel.write(ByteBuffer.wrap(name.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}