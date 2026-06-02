package spaceinvaders.ce1106.server.command;

import java.io.PrintWriter;

import com.google.gson.JsonObject;
import spaceinvaders.ce1106.server.context.ConnectionContext;
import spaceinvaders.ce1106.server.context.ServerContext;

public interface Command {
    void execute(JsonObject payload, ConnectionContext connection, ServerContext ctx);
}
