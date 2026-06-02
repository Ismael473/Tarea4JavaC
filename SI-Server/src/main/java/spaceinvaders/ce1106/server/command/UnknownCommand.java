package spaceinvaders.ce1106.server.command;

import java.io.PrintWriter;

import com.google.gson.JsonObject;
import spaceinvaders.ce1106.server.context.ConnectionContext;
import spaceinvaders.ce1106.server.context.ServerContext;

public class UnknownCommand implements Command {
    @Override
    public void execute(JsonObject payload, ConnectionContext connection, ServerContext ctx) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "error");
        response.addProperty("message", "Invalid handshake received");

        connection.getOut().println(ctx.getGson().toJson(response));
    }
}
