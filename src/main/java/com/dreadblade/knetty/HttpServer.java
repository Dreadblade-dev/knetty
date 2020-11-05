package com.dreadblade.knetty;

import com.dreadblade.knetty.config.Configuration;
import com.dreadblade.knetty.exception.StaticFileNotFoundException;
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
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpServer {
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
            logger.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    private void handleClient(Future<AsynchronousSocketChannel> future)
            throws InterruptedException, ExecutionException, TimeoutException, IOException {
        logger.info("Incoming connection");

        AsynchronousSocketChannel clientChannel = future.get(30, TimeUnit.SECONDS);

        while (clientChannel != null && clientChannel.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            StringBuilder builder = new StringBuilder();
            boolean keepReading = true;

            while (keepReading) {
                clientChannel.read(buffer).get();

                int position = buffer.position();
                keepReading = position == BUFFER_SIZE;

                byte[] array = keepReading
                        ? buffer.array()
                        : Arrays.copyOfRange(buffer.array(), 0, position);

                builder.append(new String(array));
                buffer.clear();
            }

            HttpRequest request = null;
            try {
                request = ParseUtils.parseRequest(builder.toString());
            } catch (InvalidHttpRequestException e) {
                e.printStackTrace();
            }
            logger.debug(request.toString());

            sendResponse(clientChannel, request);
        }
    }

    private void sendResponse(AsynchronousSocketChannel clientChannel, HttpRequest request) {
        HttpResponse response = null;
        String body;
        String staticFilesLocation = config.getStaticFilesLocation();
        String filepath = staticFilesLocation;
        if (request != null) {
            if (request.getPath().equals(config.getRootPath())) {
                filepath += "index.html";
            } else {
                filepath += request.getPath();
            }

            try {
                body = StaticFilesLoader.loadStaticFile(filepath);
                response = createResponse(Status.OK, body);
            } catch (StaticFileNotFoundException e) {
                logger.info("Not found 404: " + e.getMessage());
                try {
                    body = StaticFilesLoader.loadStaticFile(staticFilesLocation + "not_found_404.html");
                    response = createResponse(Status.NOT_FOUND, body);
                } catch (StaticFileNotFoundException exc) {
                    logger.error(exc.getMessage());
                }
            }

        }
        logger.debug(response.toString());

        ByteBuffer resp = ByteBuffer.wrap(response.getBytes());


        if (clientChannel.isOpen()) {
            try {
                clientChannel.write(resp);
                clientChannel.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
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
}
