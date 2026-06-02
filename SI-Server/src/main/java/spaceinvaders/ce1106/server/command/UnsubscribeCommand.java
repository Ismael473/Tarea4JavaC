package spaceinvaders.ce1106.server.command;

import java.io.PrintWriter;
import java.util.UUID;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceinvaders.ce1106.server.context.ConnectionContext;
import spaceinvaders.ce1106.server.context.ServerContext;

public class UnsubscribeCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(UnsubscribeCommand.class);

    @Override
    public void execute(JsonObject payload, ConnectionContext connection, ServerContext ctx) {
        UUID clientId = UUID.fromString(payload.get("clientId").getAsString());

        ctx.getClients().remove(clientId);
        LOG.info("Client {} disconnected", clientId);

        JsonObject response = new JsonObject();
        response.addProperty("status", "ok");
        connection.getOut().println(ctx.getGson().toJson(response));
    }
}
