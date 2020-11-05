package com.dreadblade.knetty;

import com.dreadblade.knetty.network.http.Header;
import com.dreadblade.knetty.network.http.request.HttpRequest;
import com.dreadblade.knetty.network.http.request.Method;
import com.dreadblade.knetty.network.http.response.HttpResponse;
import com.dreadblade.knetty.network.http.response.HttpResponseBuilder;
import com.dreadblade.knetty.network.http.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            server.bind(new InetSocketAddress("127.0.0.1", 8080));

            while (true) {
                Future<AsynchronousSocketChannel> future = server.accept();
                handleClient(future);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
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

            String[] requestData = builder.toString().split("\r\\n");
            String[] requestFirstLine = requestData[0].split("\\s");
            Method method = Method.getMethod(requestFirstLine[0]);
            String path = requestFirstLine[1];
            String version = requestFirstLine[2];
            List<Header> headers = new ArrayList<>();
            for (int i = 1; i < requestData.length; i++) {
                Header header = new Header();
                String[] splitRequest = requestData[i].split(": ");
                header.setName(splitRequest[0]);
                header.setValue(splitRequest[1]);
                headers.add(header);
            }
            HttpRequest request = new HttpRequest(method, path, version, headers);
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
