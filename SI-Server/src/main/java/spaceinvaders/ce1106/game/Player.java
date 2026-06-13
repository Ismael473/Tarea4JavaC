package spaceinvaders.ce1106.game;

public class Player extends Entity
{
    // int width = 13;
    // int height = 8;

    // public boolean dead = false;    // Necessary for player?

    // int move_distance = 1;
    // int x;
    // int y;
    
    // int sprite;
    // // int sprite_offset = 0;
    
    Bullet bullet = new Bullet(); // Use a dead bullet as default.

    Player(int x, int y, int sprite)
    {
        this.x = x;
        this.y = y;

        this.sprite = sprite;
        // this.sprite_offset = sprite_offset;

        death_animation = "player_death_animation";

        width = 13;
        height = 8;

    }

    @Override
    public void Move(Boolean direction)
    {
        // System.out.println("Hi");
        if (direction){this.x -= move_distance;} // Move left.
        else          {this.x += move_distance;} // Move right.
    }

    // Returns a bullet only if the previous one was dead.
    public Bullet SpawnBullet()
    {
        // Bullet(int x, int y, int move_distance, int move_cooldown, int sprite, boolean move_up, int sprite_count, int height)
        if (this.bullet.dead)
        {
            this.bullet = new Bullet(x+width/2 - 1, y-5, 4, 0, 7, true, 1, 3, 4);
            return this.bullet;
        }
        return null;
    }



}