public class BunkerBlock extends Entity
{
    // int width = 4; 
    // int height = 4; 

    // public boolean dead = false;
    
    // int x;
    // int y;
    
    // int sprite;
    

    BunkerBlock(Integer x, Integer y, Integer sprite)
    {
        super();    // Maybe this is necessary for polymorphism to work? Possible error.
        this.x = x;
        this.y = y;

        this.sprite = sprite;

        width = 4;
        height = 4;
    }


}