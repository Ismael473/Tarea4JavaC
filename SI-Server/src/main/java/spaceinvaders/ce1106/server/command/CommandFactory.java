package spaceinvaders.ce1106.server.command;

public class CommandFactory {
    public Command create(String type) {
        return switch (type) {
            case "subscribe" -> new SubscribeCommand();
            case "unsubscribe" -> new UnsubscribeCommand();
            default -> new UnknownCommand();
        };
    }
}
