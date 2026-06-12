
public class Player extends Entity
{
    Bullet bullet = new Bullet();

    Player(int x, int y, int sprite)
    {
        this.x = x;
        this.y = y;

        this.sprite = sprite;

        death_animation = "player_death_animation";

        width = 13;
        height = 8;
    }

    @Override
    public void Move(Boolean direction)
    {
        if (direction){this.x -= move_distance;}
        else          {this.x += move_distance;}
    }

    public Bullet SpawnBullet()
    {
        if (this.bullet.dead)
        {
            this.bullet = new Bullet(x+width/2 - 1, y-5, 4, 0, 7, true, 1, 3, 4);
            return this.bullet;
        }
        return null;
    }
}
