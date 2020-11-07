package com.dreadblade.knetty;

import com.dreadblade.knetty.config.Configuration;
import com.dreadblade.knetty.network.http.HttpServer;

import java.net.InetSocketAddress;

public class KnettyApplication {
    public static void main(String[] args) {
        Configuration config = Configuration.getInstance();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", config.getPort());
        HttpServer server = new HttpServer(address);
        server.init();
        server.start();
    }
}
