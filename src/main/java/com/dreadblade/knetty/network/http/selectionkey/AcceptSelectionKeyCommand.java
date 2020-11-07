package com.dreadblade.knetty.network.selectionkey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptSelectionKeyCommand implements SelectionKeyCommand {
    private static final Logger logger = LoggerFactory.getLogger(AcceptSelectionKeyCommand.class);
    private Selector selector;

    public AcceptSelectionKeyCommand(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void execute(SelectionKey selectionKey) {
        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
        try {
            logger.trace("Accepting connection...");
            SocketChannel client = server.accept();

            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            logger.trace("Connection accepted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
