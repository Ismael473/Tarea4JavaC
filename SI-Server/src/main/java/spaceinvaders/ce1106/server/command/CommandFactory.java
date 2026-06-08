package spaceinvaders.ce1106.server.command;

public class CommandFactory {
    public Command create(String type) {
        return switch (type) {
            case "subscribe" -> new SubscribeCommand();
            case "unsubscribe" -> new UnsubscribeCommand();
            case "create_room" -> new CreateRoomCommand();
            case "join_room" -> new JoinRoomCommand();
            case "input" -> new InputCommand();
            case "leave_room" -> new LeaveRoomCommand();
            case "list_rooms" -> new ListRoomsCommand();
            default -> new UnknownCommand();
        };
    }
}
