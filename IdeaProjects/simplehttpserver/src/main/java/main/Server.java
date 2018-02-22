package main;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        Path current = Paths.get(".").normalize().toAbsolutePath();
        logger.debug("Server started at port {} and showing directory at {}", PORT, current);

        // starting a new thread with a new client session
        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ClientSession(socket)).start();
        }
    }
}
