package spaceinvaders.ce1106.server.context;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class ConnectionContext {
    private final Socket socket;
    private final PrintWriter out;
    private UUID clientId;

    public ConnectionContext(Socket socket, PrintWriter out) {
        this.socket = socket;
        this.out = out;
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }
}
