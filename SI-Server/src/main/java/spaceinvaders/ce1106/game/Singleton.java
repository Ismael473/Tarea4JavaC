package spaceinvaders.ce1106.game;

// import java.util.ArrayList;

public class Singleton
{
    Integer last_id;
    // public ArrayList<SceneStructure> structures;
    // public ArrayList<Entity> entities;

    public boolean left_is_pressed;
    // public boolean up_is_pressed;
    public boolean right_is_pressed;
    // public boolean down_is_pressed;
    public boolean shoot_is_pressed;

    public boolean left_was_pressed = false;
    // public boolean up_was_pressed = false;
    public boolean right_was_pressed = false;
    // public boolean down_was_pressed = false;
    public boolean shoot_was_pressed = false;

    public Integer score = 0;

    // public Singleton(Integer last_id, ArrayList<SceneStructure> structures, ArrayList<Entity> entities)
    // {
    //     this.last_id = last_id;
    //     this.structures = structures;
    //     this.entities = entities;
        
    //     this.left_is_pressed = false;
    //     this.up_is_pressed = false;
    //     this.right_is_pressed = false;
    //     this.down_is_pressed = false;
    //     this.jump_is_pressed = false;
    // }
    public Singleton()
    {
        this.left_is_pressed = false;
        // this.up_is_pressed = false;
        this.right_is_pressed = false;
        // this.down_is_pressed = false;
        this.shoot_is_pressed = false;
    }
    

    public Integer GenerateID()
    {
        
        return ++this.last_id;  // Returns the current value and then increments it.
    }    

}