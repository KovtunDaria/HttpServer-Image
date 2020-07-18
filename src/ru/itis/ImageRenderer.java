package ru.itis;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import java.util.Date;

public class ImageRenderer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        server.createContext("/", exchange -> {
            addHeaders(exchange.getResponseHeaders());
            byte[] body = createBody();
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream bodyStream = exchange.getResponseBody()) {
                bodyStream.write(body);
                bodyStream.flush();
            }
        });

        server.setExecutor(Executors.newFixedThreadPool(15));
        server.start();
        System.out.println("Server started on port: " + PORT);
    }


    private static void addHeaders(Headers headers) {
        headers.add("Content-Type", "image/png");
        headers.add("Server", "localhost");
        headers.add("Date", new Date().toString());
    }

    private static byte[] createBody() {
        Path image = Paths.get("pic.PNG");
        try (InputStream input = Files.newInputStream(image)) {
            List<Byte> result = new ArrayList<>();
            byte[] buffer = new byte[1024];
            while (input.read(buffer) > 0) {
                for (byte element : buffer) {
                    result.add(element);
                }
            }
            buffer = new byte[result.size()];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = result.get(i);
            }
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}