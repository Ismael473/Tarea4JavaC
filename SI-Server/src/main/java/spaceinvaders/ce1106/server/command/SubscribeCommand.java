package spaceinvaders.ce1106.server.command;

import java.io.PrintWriter;
import java.util.UUID;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceinvaders.ce1106.client.ClientSession;
import spaceinvaders.ce1106.server.context.ConnectionContext;
import spaceinvaders.ce1106.server.context.ServerContext;

public class SubscribeCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(SubscribeCommand.class);

    @Override
    public void execute(JsonObject payload, ConnectionContext connection, ServerContext ctx) {
        UUID clientId = UUID.fromString(payload.get("clientId").getAsString());

        ClientSession client = new ClientSession(
                connection.getSocket(),
                connection.getOut(),
                clientId
        );

        ctx.getClients().put(clientId, client);
        LOG.info("Client {} connected", clientId);

        JsonObject response = new JsonObject();
        response.addProperty("status", "ok");

        connection.getOut().println(ctx.getGson().toJson(response));
    }
}
