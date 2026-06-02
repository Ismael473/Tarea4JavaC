package spaceinvaders.ce1106.server.context;

import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionContext {
    private final Socket socket;
    private final PrintWriter out;

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
}
