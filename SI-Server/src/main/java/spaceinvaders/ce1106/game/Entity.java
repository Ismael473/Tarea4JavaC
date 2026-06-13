package spaceinvaders.ce1106.game;

public class Entity
{
    // int team = 0;   // Team is used for colissions so that a bullet can only hit enemies.

    Integer width = 12; // Squids, 8; Crabs, 11; Octopuses, 12
    Integer height = 8; // All are 8 pixels tall

    public Boolean dead = true;
    public Boolean died_this_frame = false;
    public String death_animation = ""; 

    Integer move_distance = 2;
    Integer x;
    Integer y;
    
    Integer sprite;
    Integer sprite_offset = 0;
    Integer sprite_count = 4;  // The number of sprites an entity has. 
    

    Entity(){}; // This allows child classes to have their own constructors fully defined.
    // Entity(int x, int y, int sprite)
    // {
    //     System.out.println("Entity constructor called!");
    //     this.x = x;
    //     this.y = y;
    //     this.sprite = sprite;
    // }

    public void Move(Boolean direction)
    {
        System.out.println("Move from the entity class!");
        if (direction){this.x -= move_distance;} // Move left.
        else          {this.x += move_distance;} // Move right.
    }

    public void SetAsDead()
    {
        dead = true;
        died_this_frame = true;
    }
    

    
}
