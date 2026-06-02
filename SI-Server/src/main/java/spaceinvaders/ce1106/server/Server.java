package spaceinvaders.ce1106.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceinvaders.ce1106.server.command.Command;
import spaceinvaders.ce1106.server.command.CommandFactory;
import spaceinvaders.ce1106.server.context.ConnectionContext;
import spaceinvaders.ce1106.server.context.ServerContext;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    public static final String discoveryMessage = "DISCOVER_SPACEINVADERS";

    private final ServerContext serverContext = new ServerContext();
    private final CommandFactory commandFactory = new CommandFactory();

    public void start() throws IOException {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOG.info("SpaCE Invaders Server is listening on port {}", port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOG.info("Accepted connection from {}", clientSocket.getInetAddress());
                new Thread(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }).start();
            }
        } catch (IOException e) {
            LOG.error("Something went wrong while enabling the server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
        );
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String line = in.readLine();
            if (line == null) {
                return;
            }

            LOG.debug("Request received: {}", line);

            JsonObject jsonObject = JsonParser.parseString(line).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();

            Command command = commandFactory.create(type);
            ConnectionContext connection = new ConnectionContext(clientSocket, out);
            command.execute(jsonObject, connection, serverContext);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public void startDiscovery() {
        int discoveryPort = 5001;
        try (DatagramSocket socket = new DatagramSocket(discoveryPort)) {
            LOG.info("Discovery Server is listening on port {}", discoveryPort);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());

                LOG.info("Discovered message from {}: {}", packet.getAddress(), message);

                if (!message.equals(discoveryMessage)) {
                    continue;
                }

                String response = "SPACEINVADERS_SERVER";
                byte[] responseBytes = response.getBytes();

                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());

                socket.send(responsePacket);
                LOG.info("Discovery response sent to {}", packet.getAddress());
            }

        } catch (IOException e) {
            LOG.error("Something went wrong while enabling the discovery server: " + e.getMessage());
        }
    }
}
