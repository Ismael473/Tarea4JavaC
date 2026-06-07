package spaceinvaders.ce1106.server.room;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomManager {
    private static final Logger LOG = LoggerFactory.getLogger(RoomManager.class);

    private final ConcurrentHashMap<UUID, GameRoom> rooms;
    private final ConcurrentHashMap<UUID, UUID> clientToRoom;

    public RoomManager() {
        this.rooms = new ConcurrentHashMap<>();
        this.clientToRoom = new ConcurrentHashMap<>();
    }

    public GameRoom createRoom() {
        UUID roomId = UUID.randomUUID();
        GameRoom room = new GameRoom(roomId);
        rooms.put(roomId, room);
        LOG.info("Room created: {}", roomId);
        return room;
    }

    public void removeRoom(UUID roomId) {
        GameRoom room = rooms.remove(roomId);
        if (room != null) {
            room.stopGameLoop();
            clientToRoom.entrySet().removeIf(e -> e.getValue().equals(roomId));
            LOG.info("Room removed: {}", roomId);
        }
    }

    public GameRoom getRoom(UUID roomId) {
        return rooms.get(roomId);
    }

    public void mapClientToRoom(UUID clientId, UUID roomId) {
        clientToRoom.put(clientId, roomId);
    }

    public void unmapClient(UUID clientId) {
        clientToRoom.remove(clientId);
    }

    public UUID getClientRoom(UUID clientId) {
        return clientToRoom.get(clientId);
    }

    public void removeClientFromRoom(UUID clientId) {
        UUID roomId = clientToRoom.remove(clientId);
        if (roomId == null) return;

        GameRoom room = rooms.get(roomId);
        if (room == null) return;

        for (ClientObserver obs : room.getObservers()) {
            if (obs.getSession().getUuid().equals(clientId)) {
                room.detach(obs);
                break;
            }
        }

        if (room.getObserverCount() == 0) {
            removeRoom(roomId);
        }
    }

    public List<UUID> listActiveRoomIds() {
        return new ArrayList<>(rooms.keySet());
    }
}
