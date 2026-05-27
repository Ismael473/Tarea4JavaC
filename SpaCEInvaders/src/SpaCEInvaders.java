import java.util.ArrayList;

// Class for managing the gameloop
public class SpaCEInvaders
{
    // Aliens
    // An array of Alien objects aliens
    // An int current_moving_alien -> [0, 54] (There are 55 Aliens at the start of a game)
    // A Boolean representing the direction, [false, true] -> [right, left]
    // A Boolean representing the descending movement state, [false, true] -> [not descending, descending]
    Alien[] aliens = new Alien[55]; // (There are 55 Aliens at the start of a game)
    Entity[] entities = new Entity[56]; // entities holds the fixed entities. i. e. all except bullets. UFO?
    int right_descend_limit = 198;
    int left_descend_limit = 0;

    int current_moving_alien = 0;
    boolean direction = false;
    boolean descending = true;  // It starts at true so as not to trigger a descent first frame.
    int destroyed_aliens_counter = 0;
    int alien_freeze = 0;   // Timer to stop Alien movement. This is used when an alien is destroyed.

    // Player
    // Singleton singleton receives the inputs
    // Bullet player_bullet
    Singleton singleton = new Singleton();
    Player player = new Player(80, 0, 0);


    // Bullet 
    // A list of all Bullet objects bullets to draw.
    ArrayList<Bullet> bullets = new ArrayList<>();
    Bullet player_bullet = player.bullet;
    // Bullet player_bullet = null;
    // Class attributes are automatically initialized to null if they are unspecified like the line above.

    // Graphics
    // current_destroyed_entities holds the entities that have been set as dead in the current frame.
    ArrayList<Entity> current_destroyed_entities = new ArrayList<>();

    SpaCEInvaders()
    {
        // Entity Array
        for (int i = 0; i < aliens.length; i++)
        {
            int create_alien_x = 18 + (i%11)*16;
            int create_alien_y = 72 + (i/11)*16;
            int sprite = i/22*2;
            Alien create_alien = new Alien(create_alien_x, create_alien_y, sprite);
            aliens[i] = create_alien;
            entities[i] = create_alien;
            System.out.println("alien (x, y, sprite, dead) = (" + aliens[i].x + ", " + aliens[i].y + ", " + aliens[i].sprite + ", " + aliens[i].dead + ")");
            // System.out.println("entity (x, y, sprite, dead) = (" + entities[i].x + ", " + aliens[i].y + ", " + aliens[i].sprite + ", " + aliens[i].dead + ")");

        }
        // System.arraycopy(aliens, 0, entities, 0, aliens.length); // Adds all aliens to the entities array.
        entities[55] = player;

    }

    public void GameLoop()
    {
        System.out.println("Enter GameLoop");
        AdvanceFrame();
        ShowGame();

        // try
        // {
        //     Thread.sleep(16);
        // }
        // catch (InterruptedException e)
        // {
        //     e.printStackTrace();
        // }
    }









    private void AdvanceFrame()
    {
        // Win condition
        if (destroyed_aliens_counter >= aliens.length)
        {
            System.out.println("AdvanceFrame(): Win condition met! destroyed_aliens_counter = " + destroyed_aliens_counter);
        }

        // Alien movement
        // For choosing the moving Alien, alien_to_move = GetNextAlien(current_moving_alien)
        // Alien freeze frames after Alien death.        
        if (alien_freeze > 0)
        {
            System.out.println("alien_freeze = " + alien_freeze);
        }
        // Decend state start and stop.
        if (current_moving_alien == 0)
        {    
            // Stop descent if necessary.        
            if (descending) {descending = false;} // Stops descent when all active aliens have descended once.
            // Start descent logic.
            else if (DescentCondition(direction))
            {
                descending = true;
                direction = !direction;
            } 
        }

        Alien alien_to_move = GetNextAlien(current_moving_alien);
        if (alien_to_move != null) // If an alien was found.
        {
            alien_to_move.Move(direction);
            if (descending) {alien_to_move.Descend();}
            current_moving_alien ++;
        }
        else // This means GetNextAlien() returned null. i. e. The last alien in the formation was reached.
        {
            current_moving_alien = 0;   // Point towards the first alien again.
            // play a sound?
            AdvanceFrame();             // Restart the function.
        }
        //     A dead alien will still exist in the aliens array, but it is no longer updated nor shown.
        //         The rest of the aliens stay at their original memory position in the array
        //         An alien has a Boolean dead flag.
            
        // Alien shooting
        EmitShots();    // This modifies the bullets list.

        // Player movement and shooting
        if (singleton.left_is_pressed)  {player.Move(true);}
        if (singleton.right_is_pressed) {player.Move(false);}
        if (singleton.shoot_is_pressed)
        {
            Bullet possible_player_bullet = player.SpawnBullet();
            if (possible_player_bullet != null) {bullets.add(possible_player_bullet);}
        }

        // Bullet movement and collisions
        // for (Bullet bullet : bullets)    // This doesn't allow to remove while iterating.
        // Uses a backwards for loop for deleting bullets while iterating the list.
        for (int i = bullets.size() - 1; i >= 0; i--) 
        {
            Bullet bullet = bullets.get(i);
            if (UpdateCollisions(bullet))   // This may set bullet as dead and remove it from bullets.
            {
                alien_freeze = 16;
                destroyed_aliens_counter ++;
            }
            bullet.Move();
        }
    }

    private void ShowGame()
    {
        // // Show all the fixed entities.
        // for (Entity entity : entities)
        // {
        //     if (!entity.dead)
        //     {
        //         System.out.println("(x, y, sprite, dead) = (" + entity.x + ", " + entity.y + ", " + entity.sprite + ", " + entity.dead + ")");
        //     }
        // }
        
        // Show the alien at enities[current_moving_alien-1]
        int just_moved_alien = current_moving_alien -1;
        System.out.println("entities[" + just_moved_alien + "].(x, y, sprite, dead) = (" + entities[just_moved_alien].x + ", " + entities[just_moved_alien].y + ", " + entities[just_moved_alien].sprite + ", " + entities[just_moved_alien].dead + ")");
        
        // Show player. 
        // System.out.println("entities[55].(x, y, sprite, dead) = (" + entities[55].x + ", " + entities[55].y + ", " + entities[55].sprite + ", " + entities[55].dead + ")");
        
        // Show dynamic entities.
        for (Bullet bullet : bullets)
        {   
            if (!bullet.dead)
            {
                System.out.println("Bullet.(x, y) = (" + bullet.x + ", " + bullet.y + ")");
            }
        }
    }

    // If a bullet touches any other entity, set both as dead
    // Removes Bullet objects from bullets and adds both entities to current_destroyed_entities
    // If an Alien was hit, returns true.
    private boolean UpdateCollisions(Bullet bullet)
    {
        for (Entity entity : entities)
        {
            if (EntitiesAreColliding(entity, bullet))
            {
                entity.dead = true;
                bullet.dead = true;
                bullets.remove(bullet);
                current_destroyed_entities.add(entity);
                current_destroyed_entities.add(bullet);
                if (entity instanceof Alien) {return true;}
            }
        }
        return false;
    }

    // Detect if two Entity objects collide (intersection of rectangles).
    public boolean EntitiesAreColliding(Entity entity_1, Entity entity_2) 
    {
        int delta_y = entity_1.y - entity_2.y;
        int delta_x = entity_1.x - entity_2.x;
        return (delta_y >= -1*entity_1.height && delta_y <= entity_2.height) && (delta_x >= -1*entity_1.width && delta_x <= entity_2.width);
    }

    // Have all bottom aliens decide whether to shoot and add the new shot Bullet objects to bullets.
    private void EmitShots()
    {
        ArrayList<Alien> bottom_aliens = GetBottomAliens(); // All the aliens at the bottom of their columns.
        for (Alien bottom_alien : bottom_aliens)
        {
            // possibly_emitted_bullet is not null if the Alien decided to shoot.
            Bullet possibly_emitted_bullet = bottom_alien.ShootChance(player.x); 
            if (possibly_emitted_bullet != null) {bullets.add(possibly_emitted_bullet);}
        }
    }
    // SpaCEInvaders has a method EmitShots()
	// 		This method calls ShootChance() in every bottom alien.
	// 			Aliens that are the lowest in their columns.
	// 		This method saves all the new Bullet objects in the bullets list.
	// 	When a bullet must be destroyed, it is set as dead and removed from bullets.
	// 		This means that whenever the original alien shoots again the Bullet is orphaned and thus garbage collected.
	// 			This can even happen in the next round.
	


    // Return the alien at aliens[alien_counter] or the next available one.
    // Returns null if there are no aliens at aliens[alien_counter] or ahead.
    // In this case the end of the aliens' formation was reached.
    // GetNextAlien() doesn't receive the aliens array because the Class of this method has aliens as an attribute.
    Alien GetNextAlien(int alien_counter)
    {
        if (alien_counter > 54){return null;}
        if (!this.aliens[alien_counter].dead){return this.aliens[alien_counter];}
        return GetNextAlien(alien_counter + 1);
    }


    // Returns the offset of the first not dead Alien in aliens
    int FirstAlien()
    {
        for (int i = 0; i < aliens.length; i++)
        {
            if (!aliens[i].dead){return i;}    
        }
        System.out.println("Error in FirstAlien(): no aliens found.");
        return -1;
    }

    // Returns the Alien with the highest x value.
    // Looks in the order of the aliens array.
    // Requires at least one Alien to exist.
    // offset must start at 0.
    Alien GetRightmostAlien(int offset)
    {
        if (!aliens[10-offset].dead) {return aliens[10-offset];}
        if (!aliens[21-offset].dead) {return aliens[21-offset];}
        if (!aliens[32-offset].dead) {return aliens[32-offset];}
        if (!aliens[43-offset].dead) {return aliens[43-offset];}
        if (!aliens[54-offset].dead) {return aliens[54-offset];}
        // If none of the rightmost column of aliens was not dead, try with the aliens to the left of those.
        return GetRightmostAlien(offset+1);
    }
    // Similar to GetRightmostAlien()
    Alien GetLeftmostAlien(int offset)
    {
        if (!aliens[0+offset].dead) {return aliens[0+offset];}
        if (!aliens[11+offset].dead) {return aliens[11+offset];}
        if (!aliens[22+offset].dead) {return aliens[22+offset];}
        if (!aliens[33+offset].dead) {return aliens[33+offset];}
        if (!aliens[44+offset].dead) {return aliens[44+offset];}
        // If none of the leftmost column of aliens was not dead, try with the aliens to the right of those.
        return GetLeftmostAlien(offset+1);
    }
	
    ArrayList<Alien> GetBottomAliens()
    {
        ArrayList<Alien> bottom_aliens = new ArrayList<>();
        for (int i = 0; i < 11; i++)
        {
            Alien alien_to_add = GetBottomAlienOfColumn(i);
            if (alien_to_add != null) {bottom_aliens.add(alien_to_add);}
        }
        return bottom_aliens;
    }
    Alien GetBottomAlienOfColumn(int offset)
    {
        if (!aliens[0+offset].dead) {return aliens[0+offset];}
        if (!aliens[11+offset].dead) {return aliens[11+offset];}
        if (!aliens[22+offset].dead) {return aliens[22+offset];}
        if (!aliens[33+offset].dead) {return aliens[33+offset];}
        if (!aliens[44+offset].dead) {return aliens[44+offset];}
        return null;
    }

    boolean DescentCondition(boolean direction)
    {
        if (direction)
        {
            Alien rightmost_alien = GetRightmostAlien(0);
            if (rightmost_alien.x >= right_descend_limit) {return true;}
        }
        else
        {
            Alien leftmost_alien = GetLeftmostAlien(0);
            if (leftmost_alien.x <= left_descend_limit) {return true;}
        }
        return false;
    }


}