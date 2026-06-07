package spaceinvaders.ce1106.server.command;

import java.util.UUID;

import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaceinvaders.ce1106.server.context.ConnectionContext;
import spaceinvaders.ce1106.server.context.ServerContext;
import spaceinvaders.ce1106.server.room.RoomManager;

public class LeaveRoomCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(LeaveRoomCommand.class);

    @Override
    public void execute(JsonObject payload, ConnectionContext connection, ServerContext ctx) {
        UUID clientId = UUID.fromString(payload.get("clientId").getAsString());

        RoomManager roomManager = ctx.getRoomManager();
        roomManager.removeClientFromRoom(clientId);

        JsonObject response = new JsonObject();
        response.addProperty("status", "ok");
        connection.getOut().println(ctx.getGson().toJson(response));

        LOG.info("Client {} left room", clientId);
    }
}
