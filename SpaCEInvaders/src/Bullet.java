
public class Bullet extends Entity
{
    int move_cooldown = 2;
    int current_move_cooldown = 0;
    boolean move_up = false;

    Bullet()
    {
        super();
    }

    Bullet(int x, int y, int move_distance, int move_cooldown, int sprite, boolean move_up, int sprite_count, int width, int height)
    {
        super();
        this.move_distance = move_distance;
        this.move_cooldown = move_cooldown;
        this.move_up = move_up;
        
        this.x = x;
        this.y = y;

        this.sprite = sprite;
        this.sprite_count = sprite_count;

        this.width = width;
        this.height = height;

        death_animation = "bullet_death_animation";

        dead = false;
    }

    public void Move()
    {
        if (current_move_cooldown <= 0)
        {
            if (move_up){this.y -= move_distance;}
            else        {this.y += move_distance;}
            current_move_cooldown = move_cooldown;
            sprite_offset = (sprite_offset+1)%sprite_count;
        }
        else {current_move_cooldown --;}
    }
}
