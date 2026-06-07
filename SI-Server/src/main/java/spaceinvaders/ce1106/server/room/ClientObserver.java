package spaceinvaders.ce1106.server.room;

import spaceinvaders.ce1106.client.ClientSession;

public class ClientObserver implements GameObserver {
    private final ClientSession session;
    private final boolean isPlayer;

    public ClientObserver(ClientSession session, boolean isPlayer) {
        this.session = session;
        this.isPlayer = isPlayer;
    }

    public ClientSession getSession() {
        return session;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    @Override
    public void onGameState(String json) {
        session.getOutput().println(json);
    }
}
