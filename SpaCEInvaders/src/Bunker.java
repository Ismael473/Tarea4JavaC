
public class Bunker
{
    BunkerBlock[] bunker_blocks = new BunkerBlock[19];
    public Bunker(Integer x, Integer y)
    {
        Integer[][] bunker_shape = 
        {
            {22,20, 0,21,22},
            {22,22,22,22,22},
            {22,22,22,22,22},
            {23,22,22,22,24}
        };

        Integer bunker_block_index = 0;
        for (Integer i = 0; i < bunker_shape.length; i++)
        {
            Integer j = 0;
            while (j < bunker_shape[i].length)
            {
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
