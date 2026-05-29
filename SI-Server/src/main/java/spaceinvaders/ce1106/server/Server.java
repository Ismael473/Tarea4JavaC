package spaceinvaders.ce1106.server;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceinvaders.ce1106.client.ClientSession;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    public static final String discoveryMessage = "DISCOVER_SPACEINVADERS";

    private static final Gson GSON = new Gson();
    private final Map<UUID, ClientSession> clients = new ConcurrentHashMap<>();

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
        } catch (
                IOException e
        ) {
            LOG.error("Something went wrong while enabling the server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        UUID clientId = null;

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
        );
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {
            String handshake = in.readLine();
            if (handshake == null) {
                return;
            }

            JsonObject jsonObject = JsonParser.parseString(handshake).getAsJsonObject();

            String type = jsonObject.get("type").getAsString();

            if (!type.equals("subscribe")) {
                JsonObject response = new JsonObject();
                response.addProperty("type", "error");
                response.addProperty("message", "Invalid handshake received");
            }

            clientId = UUID.fromString(jsonObject.get("clientId").getAsString());

            ClientSession client = new ClientSession(
                    clientSocket,
                    out,
                    clientId
            );

            clients.put(clientId, client);
            LOG.info("Client {} connected", clientId);

            JsonObject response = new JsonObject();
            response.addProperty("status", "ok");

            out.println(GSON.toJson(response));

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
