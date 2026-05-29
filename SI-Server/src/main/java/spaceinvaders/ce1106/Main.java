package spaceinvaders.ce1106;

import spaceinvaders.ce1106.server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server();

        new Thread(server::startDiscovery).start();

        server.start();
    }
}
