public class Crab extends Alien
{  
    Crab(int x, int y, int sprite)
    {
        super(x, y, sprite);    // Maybe this is necessary for polymorphism to work? Possible error.
        
    }
    Crab(int sprite, String death_aniamtion)
    {
        super(sprite, death_aniamtion);    // Maybe this is necessary for polymorphism to work? Possible error.
        
    }

    // @Override
    // public void Move(Boolean direction)
    // {
    //     System.out.println("Alien Move(): direction = " + direction);
    //     if (direction){this.x -= move_distance;} // Move left.
    //     else          {this.x += move_distance;} // Move right.
    //     sprite_offset = 1 - sprite_offset;
    // }
    // public void Descend()
    // {
    //     this.y += descent_distance;
    // }
}