package com.dreadblade.knetty;

import com.dreadblade.knetty.config.Configuration;
import com.dreadblade.knetty.network.http.request.HttpRequest;
import com.dreadblade.knetty.network.http.response.HttpResponse;
import com.dreadblade.knetty.network.http.response.HttpResponseBuilder;
import com.dreadblade.knetty.network.http.response.Status;
import com.dreadblade.knetty.util.ParseUtils;
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
    private final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final static int BUFFER_SIZE = 256;
    private AsynchronousServerSocketChannel server;

    public void bootstrap() {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress("127.0.0.1", Configuration.getInstance().getPort()));

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

            HttpRequest request = ParseUtils.parseRequest(builder.toString());
            logger.debug(request.toString());

            String body = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<title>Sample page</title>" +
                    "</head>" +
                    "<body>" +
                    "<h1>Hello, knetty!</h1>" +
                    "</body>" +
                    "</html>";

            HttpResponse response = new HttpResponseBuilder()
                    .setVersion("HTTP/1.1")
                    .setStatus(Status.OK)
                    .addHeader("Content-Type: text/html")
                    .addHeader("Content-length: " + body.length())
                    .addBody(body)
                    .create();

            logger.debug(response.toString());
            ByteBuffer resp = ByteBuffer.wrap(response.getBytes());
            clientChannel.write(resp);

            clientChannel.close();
        }
    }
}
