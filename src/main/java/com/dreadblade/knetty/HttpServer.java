package com.dreadblade.knetty;

import com.dreadblade.knetty.config.Configuration;
import com.dreadblade.knetty.exception.StaticFileLoadException;
import com.dreadblade.knetty.network.http.request.HttpRequestHandler;
import com.dreadblade.knetty.network.http.request.HttpRequest;
import com.dreadblade.knetty.exception.InvalidHttpRequestException;
import com.dreadblade.knetty.network.http.response.HttpResponse;
import com.dreadblade.knetty.network.http.response.HttpResponseBuilder;
import com.dreadblade.knetty.network.http.response.Status;
import com.dreadblade.knetty.util.ParseUtils;
import com.dreadblade.knetty.util.StaticFilesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpServer implements HttpRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private static final int BUFFER_SIZE = 256;
    private static Configuration config;
    private AsynchronousServerSocketChannel server;

    public void bootstrap() {

        try {
            config = Configuration.getInstance();
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress("127.0.0.1", config.getPort()));

            logger.info("Server started!");

            while (true) {
                Future<AsynchronousSocketChannel> future = server.accept();
                handleClient(future);
            }
        } catch (Exception e) {
            logger.error(e.getClass().getName());
        }

        logger.info("Server stopped!");
    }

    private void handleClient(Future<AsynchronousSocketChannel> future)
            throws InterruptedException, ExecutionException, TimeoutException {
        logger.info("Incoming connection");

        AsynchronousSocketChannel clientChannel = future.get(30, TimeUnit.SECONDS);

        while (clientChannel != null && clientChannel.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            StringBuilder builder = new StringBuilder();
            boolean keepReading = true;

            while (keepReading) {
                int readResult = clientChannel.read(buffer).get();

                keepReading = readResult == BUFFER_SIZE;
                buffer.flip();
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                builder.append(charBuffer);

                buffer.clear();
            }

            logger.debug(builder.toString());
            HttpRequest request = null;
            try {
                request = ParseUtils.parseRequest(builder.toString());
                logger.debug(request.toString());
            } catch (InvalidHttpRequestException e) {
                logger.info(e.getMessage());
            }

            HttpResponse response = handle(request);
            sendResponse(clientChannel, response);
        }
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpResponse response = null;
        String body;
        String staticFilesLocation = config.getStaticFilesLocation();
        String filepath = staticFilesLocation;

        if (request != null) {
            if (request.getMethod() != null && request.getPath() != null && request.getVersion() != null) {
                if (request.getPath().equals(config.getRootPath())) {
                    filepath += "index.html";
                } else {
                    filepath += request.getPath();
                }

                try {
                    body = StaticFilesLoader.loadStaticFile(filepath);
                    response = createResponse(Status.OK, body);
                } catch (StaticFileLoadException e) {
                    logger.info("Not found 404: " + e.getMessage() + " on path + " + request.getPath());
                    try {
                        body = StaticFilesLoader.loadStaticFile(staticFilesLocation + "not_found_404.html");
                        response = createResponse(Status.NOT_FOUND, body);
                    } catch (StaticFileLoadException exc) {
                        logger.error(exc.getMessage());
                    }
                }
                logger.debug(response.toString());
            }
        } else {
            try {
                filepath += Status.BAD_REQUEST.getDefaultPageFilename();
                body = StaticFilesLoader.loadStaticFile(filepath);
                response = createResponse(Status.BAD_REQUEST, body);
            } catch (StaticFileLoadException e) {
                e.printStackTrace();
            }
        }


        return response;
    }

    public HttpResponse createResponse(Status status, String body) {
        return new HttpResponseBuilder()
                .setVersion("HTTP/1.1")
                .setStatus(status)
                .addHeader("Content-Type: text/html")
                .addHeader("Content-length: " + body.length())
                .addBody(body)
                .create();
    }

    private void sendResponse(AsynchronousSocketChannel clientChannel, HttpResponse response) {
        if (clientChannel.isOpen()) {
            try {
                ByteBuffer resp = ByteBuffer.wrap(response.getBytes());
                clientChannel.write(resp);
                clientChannel.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
