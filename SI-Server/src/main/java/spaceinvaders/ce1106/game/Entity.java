package spaceinvaders.ce1106.game;

public class Entity
{
    Integer width = 12;
    Integer height = 8;

    public Boolean dead = true;
    public Boolean died_this_frame = false;
    public String death_animation = ""; 

    Integer move_distance = 2;
    Integer x;
    Integer y;
    
    Integer sprite;
    Integer sprite_offset = 0;
    Integer sprite_count = 4;
    
    Entity(){};

    public void Move(Boolean direction)
    {
        if (direction){this.x -= move_distance;}
        else          {this.x += move_distance;}
    }

    public void SetAsDead()
    {
        dead = true;
        died_this_frame = true;
    }
}
