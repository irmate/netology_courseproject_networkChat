package service;

import utils.ConsoleLogger;
import utils.FileLogger;
import utils.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class NioServerProcess {
    private final ServerSocketChannel ssc;
    private final Selector selector;
    private final Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();
    private final List<ClientObject> clientObjects = new CopyOnWriteArrayList<>();
    private Logger consoleLogger;
    private Logger fileLogger;

    public NioServerProcess(Starter starter) {
        ssc = starter.getServerSocketChannel();
        selector = starter.getSelector();
        consoleLogger = ConsoleLogger.getInstance();
        fileLogger = FileLogger.getInstance();
    }

    public ClientObject resolveClient(List<ClientObject> clientObjects, SocketChannel sc) {
        ClientObject clientObject = null;
        for (ClientObject co : clientObjects) {
            if (co.getSocketChannel().equals(sc)) {
                clientObject = co;
                break;
            }
        }
        return clientObject;
    }

    public Logger getConsoleLogger(){
        return consoleLogger;
    }

    public void start() {
        try {
            while (true) {
                for (ClientObject co : clientObjects) {
                    if (!co.getSocketChannel().isConnected()) {
                        consoleLogger.log(
                                LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                        " Сервер Client Connection завершён, Sockethetchannel клиента: " +
                                        co.getSocketChannelAddress()
                        );
                        fileLogger.log(
                                LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                        " Сервер Client Connection завершён, Sockethetchannel клиента: " +
                                        co.getSocketChannelAddress()
                        );
                        clientObjects.remove(co);
                        break;
                    }
                }
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        SocketChannel sc = ssc.accept();
                        consoleLogger.log(
                                LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                        " Сервер Client Connection успешен, Sockethetchannel клиента: " +
                                        sc.toString()
                        );
                        fileLogger.log(
                                LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                        " Сервер Client Connection успешен, Sockethetchannel клиента: " +
                                        sc.toString()
                        );
                        sc.configureBlocking(false);
                        final ByteBuffer inputBuffer = ByteBuffer.allocate(1000);
                        sockets.put(sc, inputBuffer);
                        sc.register(selector, SelectionKey.OP_READ);
                        clientObjects.add(new ClientObject(sc, sc.toString()));
                    }
                    if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = sockets.get(sc);
                        int bytesRead = sc.read(buffer);
                        if (bytesRead == -1) {
                            sc.close();
                        }
                        if (bytesRead > 0 && buffer.get(buffer.position() - 1) == '\n') {
                            sc.register(selector, SelectionKey.OP_WRITE);

                        }
                    } else if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ClientObject clientObject = resolveClient(clientObjects, sc);
                        if (clientObject.getName() == null) {
                            ByteBuffer buffer = sockets.get(sc);
                            buffer.flip();
                            String clientMessage = new String(buffer.array(), buffer.position(), buffer.limit());
                            String clientName = clientMessage.replace("\r\n", "");
                            consoleLogger.log(
                                    LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                            " Пользователь " +
                                            clientObject.getSocketChannelAddress() +
                                            " установил имя для чата - " +
                                            clientName
                            );
                            fileLogger.log(
                                    LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                            " Пользователь " +
                                            clientObject.getSocketChannelAddress() +
                                            " установил имя для чата - " +
                                            clientName
                            );
                            buffer.clear();
                            clientObject.setName(clientName);
                            sc.register(selector, SelectionKey.OP_READ);
                        } else {
                            ByteBuffer buffer = sockets.get(sc);
                            buffer.flip();
                            String clientMessage = new String(buffer.array(), buffer.position(), buffer.limit());
                            String response = String.format("%s: %s", clientObject.getName(), clientMessage);
                            fileLogger.log(
                                    LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                                            " " +
                                            response.trim()
                            );
                            buffer.clear();
                            ByteBuffer[] bfArray = new ByteBuffer[clientObjects.size()];
                            for (int i = 0; i < clientObjects.size(); i++) {
                                if (sc.equals(clientObjects.get(i).getSocketChannel())) {
                                    clientObjects.get(i).getSocketChannel().register(selector, SelectionKey.OP_READ);
                                    continue;
                                }
                                bfArray[i] = sockets.get(clientObjects.get(i).getSocketChannel());
                                bfArray[i].clear();
                                bfArray[i].put(ByteBuffer.wrap(response.getBytes()));
                                bfArray[i].flip();
                                clientObjects.get(i).getSocketChannel().write(bfArray[i]);
                                if (!bfArray[i].hasRemaining()) {
                                    bfArray[i].compact();
                                    clientObjects.get(i).getSocketChannel().register(selector, SelectionKey.OP_READ);
                                }
                            }
                        }
                    }
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}