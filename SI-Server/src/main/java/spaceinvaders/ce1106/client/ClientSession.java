package spaceinvaders.ce1106.client;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class ClientSession {
    private final UUID uuid;
    private final Socket clientSocket;
    private final PrintWriter output;

    public ClientSession(Socket clientSocket, PrintWriter output, UUID uuid) {
        this.uuid = uuid;
        this.clientSocket = clientSocket;
        this.output = output;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public PrintWriter getOutput() {
        return output;
    }
}
