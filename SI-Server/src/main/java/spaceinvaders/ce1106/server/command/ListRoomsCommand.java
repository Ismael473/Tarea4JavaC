package spaceinvaders.ce1106.server.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import spaceinvaders.ce1106.server.context.ConnectionContext;
import spaceinvaders.ce1106.server.context.ServerContext;
import spaceinvaders.ce1106.server.room.GameRoom;
import spaceinvaders.ce1106.server.room.RoomManager;

import java.util.List;
import java.util.UUID;

public class ListRoomsCommand implements Command {

    @Override
    public void execute(JsonObject payload, ConnectionContext connection, ServerContext ctx) {
        RoomManager roomManager = ctx.getRoomManager();
        List<UUID> roomIds = roomManager.listActiveRoomIds();

        JsonArray roomsArray = new JsonArray();

        for (UUID roomId : roomIds) {
            GameRoom room = roomManager.getRoom(roomId);
            if (room == null) continue;

            int playerCount = 0;
            int spectatorCount = 0;

            for (var obs : room.getObservers()) {
                if (obs.isPlayer()) {
                    playerCount++;
                } else {
                    spectatorCount++;
                }
            }

            JsonObject roomObj = new JsonObject();
            roomObj.addProperty("roomId", roomId.toString());
            roomObj.addProperty("playerCount", playerCount);
            roomObj.addProperty("spectatorCount", spectatorCount);
            roomsArray.add(roomObj);
        }

        JsonObject response = new JsonObject();
        response.addProperty("type", "room_list");
        response.add("rooms", roomsArray);

        connection.getOut().println(ctx.getGson().toJson(response));
    }
}
