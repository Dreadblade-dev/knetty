package com.dreadblade.knetty.network.http.selectionkey;

import com.dreadblade.knetty.config.Configuration;
import com.dreadblade.knetty.exception.StaticFileLoadException;
import com.dreadblade.knetty.network.http.request.HttpRequest;
import com.dreadblade.knetty.network.http.response.ContentType;
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
    private final Configuration config;

    public WriteSelectionKeyCommand() {
        this.config = Configuration.getInstance();
    }

    @Override
    public void execute(SelectionKey selectionKey) {
        SocketChannel client = (SocketChannel) selectionKey.channel();

        HttpRequest request = (HttpRequest) selectionKey.attachment();

        logger.trace("Building response...");

        HttpResponse response = handleRequest(request);

        logger.trace("Response was built!");
        try {
            logger.trace("Sending response...");
            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
            client.write(buffer);
            logger.trace("Response was sent!");
            client.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        if (request != null) {
            String filename = config.getStaticFilesLocation() + request.getPath();
            String body = null;
            HttpResponseBuilder builder = new HttpResponseBuilder();
            if ("/".equals(request.getPath())) {
                try {
                    Status status = Status.OK;
                    filename += status.getDefaultPageFilename();
                    body = StaticFilesLoader.loadStaticFile(filename);

                    builder.setStatus(status);
                } catch (StaticFileLoadException e) {
                    logger.error(e.getMessage());
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
            if (body != null) {
                return builder.setVersion("HTTP/1.1")
                        .addHeader("Content-type: " + ContentType.getContentType(filename))
                        .addHeader("Content-length: " + (body.length() + 2))
                        .addBody(body).create();
            } else {
                return handleInternalServerError();
            }
        } else {
            return handleBadRequest();
        }
    }

    private HttpResponse handleBadRequest() {
        Status status = Status.BAD_REQUEST;
        String filename = config.getStaticFilesLocation() + status.getDefaultPageFilename();
        String body = null;
        try {
            body = StaticFilesLoader.loadStaticFile(filename);
        } catch (StaticFileLoadException e) {
            logger.trace(e.getMessage());
        }
        if (body != null) {
            return new HttpResponseBuilder()
                    .setStatus(status)
                    .setVersion("HTTP/1.1")
                    .addHeader("Content-type: " + ContentType.getContentType(filename))
                    .addHeader("Content-length: " + (body.length() + 1))
                    .addBody(body).create();
        } else {
            return handleInternalServerError();
        }
    }

    private HttpResponse handleInternalServerError() {
        Status status = Status.INTERNAL_SERVER_ERROR;
        String filename = config.getStaticFilesLocation() + status.getDefaultPageFilename();
        String body;
        try {
            body = StaticFilesLoader.loadStaticFile(filename);
        } catch (StaticFileLoadException e) {
            body = "Internal server error 500";
            logger.trace(e.getMessage());
        }
        return new HttpResponseBuilder()
                    .setStatus(status)
                    .setVersion("HTTP/1.1")
                    .addHeader("Content-type: " + ContentType.getContentType(filename))
                    .addHeader("Content-length: " + (body.length() + 1))
                    .addBody(body).create();
    }
}
