package com.dreadblade.knetty.network.selectionkey;

import com.dreadblade.knetty.exception.InvalidHttpRequestException;
import com.dreadblade.knetty.network.http.request.HttpRequest;
import com.dreadblade.knetty.util.ParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ReadSelectionKeyCommand implements SelectionKeyCommand {
    private static final Logger logger = LoggerFactory.getLogger(ReadSelectionKeyCommand.class);

    @Override
    public void execute(SelectionKey selectionKey) {
        SocketChannel client = (SocketChannel) selectionKey.channel();

        StringBuilder builder = new StringBuilder();

        int BUFFER_SIZE = 1024;
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        logger.trace("Reading request...");

        try {
            boolean keepReading = true;
            while (keepReading) {
                int readResult = client.read(buffer);

                keepReading = readResult == BUFFER_SIZE;
                buffer.flip();
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                builder.append(charBuffer);
                buffer.clear();
            }

            HttpRequest request = ParseUtils.parseRequest(builder.toString());
            selectionKey.attach(request);
        } catch (IOException e) {
            selectionKey.attach(null);
            logger.error(e.getClass().getName());
        } catch (InvalidHttpRequestException exc) {
            logger.trace(exc.getMessage());
            selectionKey.attach(null);
        } finally {
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
    }
}
