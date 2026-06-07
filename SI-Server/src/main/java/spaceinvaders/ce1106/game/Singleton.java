package spaceinvaders.ce1106.game;

public class Singleton
{
    Integer last_id;

    public boolean left_is_pressed;
    public boolean right_is_pressed;
    public boolean shoot_is_pressed;

    public boolean left_was_pressed = false;
    public boolean right_was_pressed = false;
    public boolean shoot_was_pressed = false;

    public Integer score = 0;

    public Singleton()
    {
        this.left_is_pressed = false;
        this.right_is_pressed = false;
        this.shoot_is_pressed = false;
    }
    

    public Integer GenerateID()
    {
        
        return ++this.last_id;
    }    

}
