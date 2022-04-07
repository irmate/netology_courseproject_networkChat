package service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.ConsoleLogger;
import utils.FileLogger;
import utils.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Starter {
    private final Configuration config;
    private ServerSocketChannel ssc;
    private Selector selector;
    private Logger consoleLogger;
    private Logger fileLogger;

    public Starter(Configuration config){
        consoleLogger = ConsoleLogger.getInstance();
        fileLogger = FileLogger.getInstance();
        this.config = config;
        create();
    }

    public Logger getConsoleLogger() {
        return consoleLogger;
    }

    public int getPort() {
        String port = null;
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(config.getFileSettings()));
            JSONObject jsonObject = (JSONObject) obj;
            port = (String) jsonObject.get("port");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(port);
    }

    public void create() {
        try {
            ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(getPort()));
            ssc.configureBlocking(false);
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            consoleLogger.log(
                    LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                            " Сервер запущен и готов принимать подключения"
            );
            fileLogger.log(
                    LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM)) +
                            " Сервер запущен и готов принимать подключения"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSocketChannel getServerSocketChannel() {
        if(ssc == null){
            throw new NullPointerException("ServerSocketChannel не открыт");
        }
        return ssc;
    }

    public Selector getSelector() {
        if(selector == null){
            throw new NullPointerException("Selector не определён");
        }
        return selector;
    }
}