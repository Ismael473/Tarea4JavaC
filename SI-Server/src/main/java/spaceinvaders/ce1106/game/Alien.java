package spaceinvaders.ce1106.game;

import java.util.Random;

public class Alien extends Entity
{
    int descent_distance = 8;
    
    Bullet bullet = new Bullet();
    Random random = new Random();
    
    double normal_distribution_standard_deviation = 0.5;

    Alien(int x, int y, int sprite)
    {
        super();
        this.x = x;
        this.y = y;

        this.sprite = sprite;

        death_animation = "alien_death_animation";
        dead = true;
    }
    Alien(int sprite, String death_animation)
    {
        super();

        this.sprite = sprite;

        this.death_animation = death_animation;
        dead = true;
    }

    @Override
    public void Move(Boolean direction)
    {
        if (direction){this.x -= move_distance;}
        else          {this.x += move_distance;}
        sprite_offset = 1 - sprite_offset;
    }
    public void Descend()
    {
        this.y += descent_distance;
    }

    public Bullet ShootChance(int player_x)
    {
        if (this.bullet.dead && Math.random() < NormalDistribution(this.x, normal_distribution_standard_deviation, player_x))
        {
            Integer random_bullet_sprite = random.nextInt(3)*4 + 8;
            this.bullet = new Bullet(this.x+width/2, this.y + this.height + 5, 4, 3, random_bullet_sprite, false, 4, 3, 7);
            return this.bullet;
        }
        return null;
    }
    private double NormalDistribution(double mean, double standard_deviation, double x)
    {
        return Math.exp(-1*Math.pow(x-mean, 2)/(2*Math.pow(standard_deviation, 2)))/(standard_deviation*Math.sqrt(2*Math.PI));
    }
}
