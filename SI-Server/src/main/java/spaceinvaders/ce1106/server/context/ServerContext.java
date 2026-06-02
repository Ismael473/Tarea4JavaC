package spaceinvaders.ce1106.server.context;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import spaceinvaders.ce1106.client.ClientSession;

public class ServerContext {
    private final Map<UUID, ClientSession> clients = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public Map<UUID, ClientSession> getClients() {
        return clients;
    }

    public Gson getGson() {
        return gson;
    }
}
