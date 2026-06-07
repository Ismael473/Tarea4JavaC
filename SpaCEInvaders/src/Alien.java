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
    
    Bullet bullet = new Bullet(-1, -1, -1, -1, -1, true, -1, -1); // Use a dead bullet as default.

    double normal_distribution_standard_deviation = 0.5;

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

    @Override
    public void Move(Boolean direction)
    {
        System.out.println("Alien Move(): direction = " + direction);
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
            this.bullet = new Bullet(this.x+width/2, this.y + this.height + 5, 4, 3, 1, false, 4, 7);
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