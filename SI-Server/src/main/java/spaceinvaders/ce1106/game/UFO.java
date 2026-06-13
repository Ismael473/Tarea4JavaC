package spaceinvaders.ce1106.game;

public class UFO extends Alien
{
    UFO(int x, int y, int sprite)
    {
        super(x, y, sprite);    // Maybe this is necessary for polymorphism to work? Possible error.

    }
    UFO(int sprite, String death_aniamtion)
    {
        super(sprite, death_aniamtion);    // Maybe this is necessary for polymorphism to work? Possible error.
//        this.move_distance = 10;
    }

    @Override
    public void Move(Boolean direction)
    {
        // System.out.println("Alien Move(): direction = " + direction);
        if (direction){this.x -= move_distance;} // Move left.
        else          {this.x += move_distance;} // Move right.
    }
    // public void Descend()
    // {
    //     this.y += descent_distance;
    // }
}