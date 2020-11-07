package com.dreadblade.knetty.network.http;

import com.dreadblade.knetty.config.Configuration;
import com.dreadblade.knetty.exception.InvalidSelectionKeyCommandException;
import com.dreadblade.knetty.network.Server;
import com.dreadblade.knetty.network.http.request.HttpRequestHandler;
import com.dreadblade.knetty.network.selectionkey.SelectionKeyCommand;
import com.dreadblade.knetty.network.selectionkey.SelectionKeyCommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class HttpServer implements Server, HttpRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private SelectionKeyCommandFactory selectionKeyCommandFactory;
    private Configuration config;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private InetSocketAddress address;

    public HttpServer(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public void init() {
        logger.info("Knetty server initialization...");
        config = Configuration.getInstance();
        try {
            selector = Selector.open();

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(address);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            selectionKeyCommandFactory = new SelectionKeyCommandFactory(selector);
        } catch (Exception e) {
            logger.error(e.getClass().getName());
            logger.info("Knetty server was not initialized, shutdown...");
            System.exit(-1);
        }
    }

    @Override
    public void start() {
        logger.info("Knetty server started!");
        while (true) {
            handle();
        }
    }

    public void handle() {
        int readyCount = 0;
        try {
            readyCount = selector.select();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (readyCount == 0) {
            return;
        }

        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator iterator = readyKeys.iterator();

        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();

            if (!key.isValid()) {
                continue;
            }

            try {
                SelectionKeyCommand command = selectionKeyCommandFactory.getSelectionKeyCommand(key);
                command.execute(key);
            } catch (InvalidSelectionKeyCommandException e) {
                logger.error(e.getClass().getName());
            }
        }
    }

    @Override
    public void stop() {
        try {
            selector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            logger.error(e.getClass().getName());
        }
    }
}
