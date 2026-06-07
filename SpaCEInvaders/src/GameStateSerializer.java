// Json serializer code by ChatGPT.
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class GameStateSerializer
{
    private static final Gson gson =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    public String createGameStateJson(Integer score, Integer lives, String enter_game_state_event, Entity[] entities, ArrayList<Bullet> bullets)
    {
        JsonObject root = new JsonObject();

        root.addProperty("score", score);
        root.addProperty("lives", lives);
        root.addProperty("enter_game_state_event", enter_game_state_event);

        JsonArray drawn_entities = new JsonArray();
        for (Entity entity : entities)
        {
            if (!entity.dead)
            {
                System.out.println("entity (x, y, sprite, dead) = (" + entity.x + ", " + entity.y + ", " + entity.sprite + ", " + entity.dead + ")");
                JsonObject entity_to_add = new JsonObject();
                entity_to_add.addProperty("x", entity.x);
                entity_to_add.addProperty("y", entity.y);
                entity_to_add.addProperty("sprite", entity.sprite);
                if (entity.died_this_frame) {entity_to_add.addProperty("animation", entity.death_animation);}
                else                        {entity_to_add.addProperty("animation", "");}
                drawn_entities.add(entity_to_add);
            }
            
        }
        for (Entity entity : bullets)
        {
            if (!entity.dead)
            {
                JsonObject entity_to_add = new JsonObject();
                entity_to_add.addProperty("x", entity.x);
                entity_to_add.addProperty("y", entity.y);
                entity_to_add.addProperty("sprite", entity.sprite);
                if (entity.died_this_frame) {entity_to_add.addProperty("animation", entity.death_animation);}
                else                        {entity_to_add.addProperty("animation", "");}
                drawn_entities.add(entity_to_add);
            }
            
        }

        // JsonObject enemy2 = new JsonObject();
        // enemy2.addProperty("x", 456);
        // enemy2.addProperty("y", 456);
        // enemy2.addProperty("sprite", 456);
        // enemy2.addProperty("animation", "");
        // enemies.add(enemy2);

        root.add("enemies", drawn_entities);

        return gson.toJson(root);
    }

public String createGameStateJsonNoEntities(Integer score, Integer lives, String enter_game_state_event)
    {
        JsonObject root = new JsonObject();

        root.addProperty("score", score);
        root.addProperty("lives", lives);
        root.addProperty("enter_game_state_event", enter_game_state_event);

        JsonArray drawn_entities = new JsonArray();
        
        root.add("enemies", drawn_entities);

        return gson.toJson(root);
    }
}