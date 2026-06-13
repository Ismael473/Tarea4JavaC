package spaceinvaders.ce1106.game;

// Creates and contains an array bunker_blocks with BunkerBolck objects.
// The x and y Integers in the cosntructor must be the top left coordinate of the bottom left BunkerBlock. 
public class Bunker
{
    BunkerBlock[] bunker_blocks = new BunkerBlock[19];
    public Bunker(Integer x, Integer y)
    {
        // The array starts from the bottom left corner.
        Integer[][] bunker_shape = 
        {
            {23,22,22,22,24},
            {22,22,22,22,22},
            {22,22,22,22,22},
            {22,20, 0,21,22}
        };

        Integer bunker_block_index = 0;
        for (Integer i = 0; i < bunker_shape.length; i++)
        {
            Integer j = 0;
            while (j < bunker_shape[i].length)
            {
                System.out.println("(i, j) = " + i + ", " + j);
                if (bunker_shape[i][j] != 0) 
                {
                    bunker_blocks[bunker_block_index] = new BunkerBlock(x + 4*j, y + 4*i, bunker_shape[i][j]);
                    bunker_block_index ++;
                }    
                j ++;
            }
        }
    }
}
