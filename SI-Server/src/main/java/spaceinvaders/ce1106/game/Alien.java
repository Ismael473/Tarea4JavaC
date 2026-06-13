package spaceinvaders.ce1106.game;

import java.util.Random;

public class Alien extends Entity
{
    // int width = 12; // Squids, 8; Crabs, 11; Octopuses, 12
    // int height = 8; // All are 8 pixels tall

    // public boolean dead = false;
    
    // int move_distance = 2;
    int descent_distance = 8;
    // int x;
    // int y;
    
    // int sprite;
    // int sprite_offset = 0;
    
    Bullet bullet = new Bullet(); // Use a dead bullet as default.
    Random random = new Random();   // Used for random bullet sprites.

    // Low  values make the Aliens shoot near the player.
    // High values make the Aliens shoot further away from the player.
    double normal_distribution_standard_deviation = 25.0;

    Alien(int x, int y, int sprite)
    {
        super();    // Maybe this is necessary for polymorphism to work? Possible error.
        this.x = x;
        this.y = y;

        this.sprite = sprite;
        // this.sprite_offset = sprite_offset;

        death_animation = "alien_death_animation";
        dead = true;
    }
    Alien(int sprite, String death_animation)
    {
        super();    // Maybe this is necessary for polymorphism to work? Possible error.

        this.sprite = sprite;
        // this.sprite_offset = sprite_offset;

        this.death_animation = death_animation;
        dead = true;
    }

    @Override
    public void Move(Boolean direction)
    {
        // System.out.println("Alien Move(): direction = " + direction);
        if (direction){this.x -= move_distance;} // Move left.
        else          {this.x += move_distance;} // Move right.
        sprite_offset = 1 - sprite_offset;
    }
    public void Descend()
    {
        // System.out.println("Hi");
        this.y += descent_distance;
    }

    // Returns a bullet or null if the Alien didn't shoot.
    // Create a new bullet at the Alien's position at random and if this.bullet is not dead.
    // The chance of shooting goes up if the player is close horizontally.
    public Bullet ShootChance(int player_x)
    {
        // Shoot condition
        if (this.bullet.dead && Math.random() < NormalDistribution(this.x, normal_distribution_standard_deviation, player_x))
        {
            Integer random_bullet_sprite = random.nextInt(3)*4 + 8;
            this.bullet = new Bullet(this.x+width/2, this.y + this.height + 5, 4, 3, random_bullet_sprite, false, 4, 3, 7);
            return this.bullet;
        }
        // If the shooting condition wasn't met.
        return null;
    }
    private double NormalDistribution(double mean, double standard_deviation, double x)
    {
        return Math.exp(-1*Math.pow(x-mean, 2)/(2*Math.pow(standard_deviation, 2)))/(standard_deviation*Math.sqrt(2*Math.PI));
    }
}