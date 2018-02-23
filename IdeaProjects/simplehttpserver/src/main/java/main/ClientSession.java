package main;

import http.HttpRequest;
import http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientSession implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientSession.class);
    private Socket socket;

    public ClientSession(Socket socket) {
        this.socket = socket;
        logger.debug("\n\n\nConnected a client {}", socket);
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // parse request int request object
            HttpRequest request = new HttpRequest(br);
            // generate response to the request
            HttpResponse response = new HttpResponse(request);
            // write response into the socket stream
            try (BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream())) {
                response.write(bos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
