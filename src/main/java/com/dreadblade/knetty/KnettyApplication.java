package com.dreadblade.knetty;

public class KnettyApplication {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.bootstrap();
    }
}
