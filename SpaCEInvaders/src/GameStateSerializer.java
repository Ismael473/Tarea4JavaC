
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class GameStateSerializer
{
    private static final Gson gson = new GsonBuilder().create();

    public String createGameStateJson(Integer score, Integer lives, String enter_game_state_event, Entity[] entities, ArrayList<Bullet> bullets)
    {
        JsonObject root = new JsonObject();

        root.addProperty("score", score);
        root.addProperty("lives", lives);
        root.addProperty("enter_game_state_event", enter_game_state_event);

        JsonArray drawn_entities = new JsonArray();
        for (Entity entity : entities)
        {
            if (!entity.dead || entity.died_this_frame)
            {
                JsonObject entity_to_add = new JsonObject();
                entity_to_add.addProperty("x", entity.x);
                entity_to_add.addProperty("y", entity.y);
                entity_to_add.addProperty("sprite", entity.sprite + entity.sprite_offset);
                if (entity.died_this_frame) {entity_to_add.addProperty("animation", entity.death_animation);}
                else                        {entity_to_add.addProperty("animation", "");}
                drawn_entities.add(entity_to_add);
            }
            
        }
        for (Entity entity : bullets)
        {
            if (!entity.dead || entity.died_this_frame)
            {
                JsonObject entity_to_add = new JsonObject();
                entity_to_add.addProperty("x", entity.x);
                entity_to_add.addProperty("y", entity.y);
                entity_to_add.addProperty("sprite", entity.sprite + entity.sprite_offset);
                if (entity.died_this_frame) {entity_to_add.addProperty("animation", entity.death_animation);}
                else                        {entity_to_add.addProperty("animation", "");}
                drawn_entities.add(entity_to_add);
            }
            
        }

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
