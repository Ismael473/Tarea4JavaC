package spaceinvaders.ce1106.server.command;

import java.util.UUID;

import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaceinvaders.ce1106.client.ClientSession;
import spaceinvaders.ce1106.server.context.ConnectionContext;
import spaceinvaders.ce1106.server.context.ServerContext;
import spaceinvaders.ce1106.server.room.ClientObserver;
import spaceinvaders.ce1106.server.room.GameRoom;
import spaceinvaders.ce1106.server.room.RoomManager;

public class CreateRoomCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(CreateRoomCommand.class);

    @Override
    public void execute(JsonObject payload, ConnectionContext connection, ServerContext ctx) {
        UUID clientId = UUID.fromString(payload.get("clientId").getAsString());

        ClientSession session = ctx.getClients().get(clientId);
        if (session == null) {
            JsonObject error = new JsonObject();
            error.addProperty("type", "error");
            error.addProperty("message", "Client not subscribed");
            connection.getOut().println(ctx.getGson().toJson(error));
            return;
        }

        RoomManager roomManager = ctx.getRoomManager();
        GameRoom room = roomManager.createRoom();
        room.setOnFinishCallback(() -> roomManager.removeRoom(room.getRoomId()));

        ClientObserver playerObserver = new ClientObserver(session, true);
        room.attach(playerObserver);
        room.startGameLoop();

        roomManager.mapClientToRoom(clientId, room.getRoomId());

        JsonObject response = new JsonObject();
        response.addProperty("status", "ok");
        response.addProperty("roomId", room.getRoomId().toString());
        connection.getOut().println(ctx.getGson().toJson(response));

        LOG.info("Client {} created and joined room {} as player", clientId, room.getRoomId());
    }
}
