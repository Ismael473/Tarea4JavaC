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
    
    Bullet bullet = new Bullet(-1, -1, -1, -1, -1, true, -1, -1); // Use a dead bullet as default.


    Player(int x, int y, int sprite)
    {
        this.x = x;
        this.y = y;

        this.sprite = sprite;
        // this.sprite_offset = sprite_offset;
    }

    @Override
    public void Move(boolean direction)
    {
        // System.out.println("Hi");
        if (direction){this.x -= move_distance;} // Move left.
        else          {this.x += move_distance;} // Move right.
    }

    // Returns a bullet only if the previous one was dead.
    public Bullet SpawnBullet()
    {
        // Bullet(int x, int y, int move_distance, int move_cooldown, int sprite, boolean move_up, int sprite_count, int height)
        if (this.bullet.dead){return new Bullet(x+width/2, y+5, 4, 0, 0, true, 1, 4);}
        return null;
    }



}