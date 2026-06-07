package spaceinvaders.ce1106.server.command;

import java.util.UUID;

import com.google.gson.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaceinvaders.ce1106.game.SpaCEInvaders;
import spaceinvaders.ce1106.server.context.ConnectionContext;
import spaceinvaders.ce1106.server.context.ServerContext;
import spaceinvaders.ce1106.server.room.GameRoom;
import spaceinvaders.ce1106.server.room.RoomManager;

public class InputCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(InputCommand.class);

    @Override
    public void execute(JsonObject payload, ConnectionContext connection, ServerContext ctx) {
        UUID clientId = UUID.fromString(payload.get("clientId").getAsString());

        RoomManager roomManager = ctx.getRoomManager();
        UUID roomId = roomManager.getClientRoom(clientId);
        if (roomId == null) {
            JsonObject error = new JsonObject();
            error.addProperty("type", "error");
            error.addProperty("message", "Client not in any room");
            connection.getOut().println(ctx.getGson().toJson(error));
            return;
        }

        GameRoom room = roomManager.getRoom(roomId);
        if (room == null) {
            JsonObject error = new JsonObject();
            error.addProperty("type", "error");
            error.addProperty("message", "Room not found");
            connection.getOut().println(ctx.getGson().toJson(error));
            return;
        }

        boolean left = payload.get("left").getAsBoolean();
        boolean right = payload.get("right").getAsBoolean();
        boolean shoot = payload.get("shoot").getAsBoolean();

        SpaCEInvaders game = room.getGame();
        game.singleton.left_is_pressed = left;
        game.singleton.right_is_pressed = right;
        game.singleton.shoot_is_pressed = shoot;

        // LOG.debug("Input from {}: left={}, right={}, shoot={}", clientId, left, right, shoot);
    }
}
