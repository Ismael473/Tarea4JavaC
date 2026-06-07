public class Bullet extends Entity
{
    /*
    Notes
    // move_distance = 4 // 4 for the player's
    // move_cooldown = 2 // inactive frames after moving // 0 for the player's
    // // speed = move_distance / (move_cooldown + 1) [pixels/f]
    // // alien bullet speed  = 4/3 pixels/f = 1,33 pixels/f
    // // player bullet speed = 4/1 pixels/f = 4 pixels/f
    // sprite = ???
    // sprite_offset = 0
    // x = ???
    // y = ???
    */


    // int width = 1; // All are 1 pixel wide.
    // int height = 7; // Alien bullets are 7 pixels tall; and the player's, 4.

    // public boolean dead = false;

    // int move_distance = 4; // 3 for the player's
    int move_cooldown = 2; // inactive frames after moving // 0 for the player's
    int current_move_cooldown = 0; // time in frames until the bullet can move again.
    boolean move_up = false;    // direction of movement.

    // int x;
    // int y;
    
    // int sprite;
    // int sprite_offset = 0;
    // int sprite_count = 4;  // The number of sprites the bullet has. 
    // e. g.: an alien bullet has 4 sprites, so sprite_count = 4.
    // sprite_count must not be 0.

    // Dead bullet constructor. dead starts as true.
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
        // this.sprite_offset = sprite_offset;

        this.width = width;
        this.height = height;

        death_animation = "bullet_death_animation";

        dead = false;

    }

    // "@Override" -> This is not the same Move(boolean direction). This one takes no argument. 
    // If the cooldown is over, moves the bullet and updates sprite_offset.
    public void Move()
    {
        // If the cooldown is over.
        if (current_move_cooldown <= 0)
        {
            // Move the bullet
            if (move_up){this.y -= move_distance;} // Move up.
            else        {this.y += move_distance;} // Move down.
            current_move_cooldown = move_cooldown;
            // updates sprite_offset
            sprite_offset = (sprite_offset+1)%sprite_count;
        }
        // Decrease current cooldown.
        else {current_move_cooldown --;}
    }
}