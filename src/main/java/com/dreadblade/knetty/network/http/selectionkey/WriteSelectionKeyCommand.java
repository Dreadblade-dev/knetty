package com.dreadblade.knetty.network.selectionkey;

import com.dreadblade.knetty.config.Configuration;
import com.dreadblade.knetty.exception.StaticFileLoadException;
import com.dreadblade.knetty.network.http.request.HttpRequest;
import com.dreadblade.knetty.network.http.response.HttpResponse;
import com.dreadblade.knetty.network.http.response.HttpResponseBuilder;
import com.dreadblade.knetty.network.http.response.Status;
import com.dreadblade.knetty.util.StaticFilesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteSelectionKeyCommand implements SelectionKeyCommand {
    private static final Logger logger = LoggerFactory.getLogger(WriteSelectionKeyCommand.class);

    @Override
    public void execute(SelectionKey selectionKey) {
        SocketChannel client = (SocketChannel) selectionKey.channel();

        HttpRequest request = (HttpRequest) selectionKey.attachment();

        Configuration config = Configuration.getInstance();

        String body = "";
        HttpResponseBuilder builder = new HttpResponseBuilder();
        String filename = config.getStaticFilesLocation();

        logger.trace("Building response...");

        if (request != null) {
            filename += request.getPath();
            if ("/".equals(request.getPath())) {
                try {
                    Status status = Status.OK;
                    filename += status.getDefaultPageFilename();
                    body = StaticFilesLoader.loadStaticFile(filename);

                    builder.setStatus(status);
                } catch (StaticFileLoadException e) {
                    e.printStackTrace();
                }
            } else if (StaticFilesLoader.isFileExists(filename)) {
                try {
                    body = StaticFilesLoader.loadStaticFile(filename);

                    builder.setStatus(Status.OK);
                } catch (StaticFileLoadException e) {
                    logger.error(e.getMessage());
                }
            } else if (!StaticFilesLoader.isFileExists(filename)) {
                try {
                    Status status = Status.NOT_FOUND;
                    filename = config.getStaticFilesLocation() + status.getDefaultPageFilename();
                    body = StaticFilesLoader.loadStaticFile(filename);

                    builder.setStatus(status);
                } catch (StaticFileLoadException e) {
                    logger.error(e.getMessage());
                }
            }
        } else {
            filename += Status.BAD_REQUEST.getDefaultPageFilename();
            try {
                body = StaticFilesLoader.loadStaticFile(filename);
            } catch (StaticFileLoadException e) {
                logger.trace(e.getMessage());
            }
            builder.setStatus(Status.BAD_REQUEST);
        }

        HttpResponse response = builder
                .setVersion("HTTP/1.1")
                .addHeader("Content-Type: text/html")
                .addHeader("Content-length: " + body.length())
                .addBody(body).create();

        try {
            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
            client.write(buffer);
            logger.trace("Response was sent!");
            client.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
