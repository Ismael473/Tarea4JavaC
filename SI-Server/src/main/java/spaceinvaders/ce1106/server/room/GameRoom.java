package spaceinvaders.ce1106.server.room;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaceinvaders.ce1106.game.SpaCEInvaders;

public class GameRoom {
    private static final Logger LOG = LoggerFactory.getLogger(GameRoom.class);
    private static final long FRAME_DELAY_MS = 33;

    private final UUID roomId;
    private final SpaCEInvaders game;
    private final List<ClientObserver> observers;
    private volatile boolean running;
    private Thread gameLoopThread;
    private Runnable onFinishCallback;

    public GameRoom(UUID roomId) {
        this.roomId = roomId;
        this.game = new SpaCEInvaders();
        this.observers = new CopyOnWriteArrayList<>();
        this.running = false;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public SpaCEInvaders getGame() {
        return game;
    }

    public void setOnFinishCallback(Runnable callback) {
        this.onFinishCallback = callback;
    }

    public void attach(ClientObserver observer) {
        observers.add(observer);
        LOG.info("Observer attached to room {} (total: {})", roomId, observers.size());
    }

    public void detach(ClientObserver observer) {
        observers.remove(observer);
        LOG.info("Observer detached from room {} (total: {})", roomId, observers.size());
    }

    public List<ClientObserver> getObservers() {
        return observers;
    }

    public int getObserverCount() {
        return observers.size();
    }

    public boolean hasPlayer() {
        for (ClientObserver obs : observers) {
            if (obs.isPlayer()) return true;
        }
        return false;
    }

    public void startGameLoop() {
        if (running) return;
        running = true;
        gameLoopThread = new Thread(this::gameLoop, "GameRoom-" + roomId);
        gameLoopThread.setDaemon(true);
        gameLoopThread.start();
        LOG.info("Game loop started for room {}", roomId);
    }

    public void stopGameLoop() {
        running = false;
        if (gameLoopThread != null) {
            gameLoopThread.interrupt();
        }
        LOG.info("Game loop stopped for room {}", roomId);
    }

    private void gameLoop() {
        while (running) {
            try {
                String json = game.Update();
                notifyObservers(json);
                if (game.isFinished()) {
                    LOG.info("Game finished for room {}, stopping loop", roomId);
                    break;
                }
                Thread.sleep(FRAME_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                LOG.error("Error in game loop for room {}: {}", roomId, e.getMessage(), e);
            }
        }
        running = false;
        if (onFinishCallback != null) {
            onFinishCallback.run();
        }
    }

    private void notifyObservers(String json) {
        for (ClientObserver observer : observers) {
            try {
                observer.onGameState(json);
            } catch (Exception e) {
                LOG.error("Failed to send game state to observer in room {}: {}", roomId, e.getMessage());
                observers.remove(observer);
            }
        }
    }
}
