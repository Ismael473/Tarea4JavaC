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
    Entity[] entities = new Entity[132]; // entities holds the fixed entities. i. e. all except bullets. UFO?
    // Entity count: 55 Aliens, 1 player, 3 Bunkers of 19 bunker blocks.
    int right_descend_limit = 198;
    int left_descend_limit = 0;
    int player_height = 0;  // This is a y coordinate value that the bottom of the player sprite touches.
                            // This is also the win condition for aliens to win. ( If the bottom of their sprite touches this value).
    AliensConcreteFactory aliens_concrete_factory = new AliensConcreteFactory();


    int current_moving_alien = 0;
    boolean direction = false;
    boolean descending = true;  // It starts at true so as not to trigger a descent first frame.
    int destroyed_aliens_counter = 0;
    int alien_freeze = 0;   // Timer to stop Alien movement. This is used when an alien is destroyed.

    // Player
    // Singleton singleton receives the inputs
    // Bullet player_bullet
    Singleton singleton = new Singleton();
    Integer player_starting_position_x = 80;
    Integer player_starting_position_y = 247;
    Player player = new Player(player_starting_position_x, player_starting_position_y, 0);


    // Bullet 
    // A list of all Bullet objects bullets to draw.
    ArrayList<Bullet> bullets = new ArrayList<>();
    Bullet player_bullet = player.bullet;
    // Bullet player_bullet = null;
    // Class attributes are automatically initialized to null if they are unspecified like the line above.

    // Graphics
    // current_destroyed_entities holds the entities that have been set as dead in the current frame.
    ArrayList<Entity> current_destroyed_entities = new ArrayList<>();
    GameStateSerializer game_state_serializer = new GameStateSerializer(); 

    // Game state
    GameStatesEnum current_game_state = GameStatesEnum.MainMenu;
    int current_game_state_frame_count = 0; // Number of frames since the last game state change.
    Integer score = 0;
    Integer lives = 3;
    // enter_game_state_event is modified in the EnterState...() functions.
    // enter_game_state_event is set to a different string for the first frame a new game state is entered.
    // enter_game_state_event is set back to "" after the first frame has passed.
    String enter_game_state_event = ""; 
    
    // Bunkers
    Integer bunker_count = 4;
    Integer bunkers_start_x = 14;
    Integer bunkers_spacing_x = (right_descend_limit - bunkers_start_x)/bunker_count;
    Integer bunkers_starting_y = 20;
    Bunker[] bunkers = new Bunker[bunker_count]; // (There are 55 Aliens at the start of a game) 

    // SpaCEInvaders()
    // {
    //     // Entity Array
    //     for (int i = 0; i < aliens.length; i++)
    //     {
    //         int create_alien_x = 18 + (i%11)*16;
    //         int create_alien_y = 72 + ((54-i)/11)*16;
    //         int sprite = i/22*2;
    //         Alien create_alien = new Alien(create_alien_x, create_alien_y, sprite);
    //         aliens[i] = create_alien;
    //         entities[i] = create_alien;
    //         System.out.println("alien (x, y, sprite, dead) = (" + aliens[i].x + ", " + aliens[i].y + ", " + aliens[i].sprite + ", " + aliens[i].dead + ")");
    //         // System.out.println("entity (x, y, sprite, dead) = (" + entities[i].x + ", " + aliens[i].y + ", " + aliens[i].sprite + ", " + aliens[i].dead + ")");

    //     }
    //     // System.arraycopy(aliens, 0, entities, 0, aliens.length); // Adds all aliens to the entities array.
    //     entities[55] = player;

    //     // Create a bunker_count amount of Bunker objects and add each of their BunkerBlock objects to entities.
    //     for (int i = 0; i < bunker_count; i++)
    //     {
    //         Bunker bunker_to_add = new Bunker(bunkers_start_x + i*bunkers_spacing_x, bunkers_starting_y);
    //         for (int j = 0; j < bunker_to_add.bunker_blocks.length; j++)
    //         {
    //             entities[56 + bunker_to_add.bunker_blocks.length*i + j] = bunker_to_add.bunker_blocks[j];
    //         }
    //         bunkers[i] = bunker_to_add;
    //     }
    // }
    SpaCEInvaders()
    {
        // Entity Array
        for (int i = 0; i < aliens.length; i++)
        {
            int alien_x = 18 + (i%11)*16;
            int alien_y = 72 + ((54-i)/11)*16;
            
            Integer index_to_alien_type = i/22*2;
            Alien create_alien;

            switch (index_to_alien_type)
            {
                case 0:
                    create_alien = aliens_concrete_factory.CreateOctopus();
                    break;
                case 1:
                    create_alien = aliens_concrete_factory.CreateCrab();
                    break;
                case 2:
                    create_alien = aliens_concrete_factory.CreateSquid();
                    break;
            
                default:
                    System.out.println("Reached default case in Alien creation of SpaCEInvaders constructor!");
                    create_alien = aliens_concrete_factory.CreateCrab();
                    break;
            }

            create_alien.x = alien_x;
            create_alien.y = alien_y;
            aliens[i] = create_alien;
            entities[i] = create_alien;
            System.out.println("alien (x, y, sprite, dead) = (" + aliens[i].x + ", " + aliens[i].y + ", " + aliens[i].sprite + ", " + aliens[i].dead + ")");
            // System.out.println("entity (x, y, sprite, dead) = (" + entities[i].x + ", " + aliens[i].y + ", " + aliens[i].sprite + ", " + aliens[i].dead + ")");

        }
        // System.arraycopy(aliens, 0, entities, 0, aliens.length); // Adds all aliens to the entities array.
        entities[55] = player;

        // Create a bunker_count amount of Bunker objects and add each of their BunkerBlock objects to entities.
        for (int i = 0; i < bunker_count; i++)
        {
            Bunker bunker_to_add = new Bunker(bunkers_start_x + i*bunkers_spacing_x, bunkers_starting_y);
            for (int j = 0; j < bunker_to_add.bunker_blocks.length; j++)
            {
                entities[56 + bunker_to_add.bunker_blocks.length*i + j] = bunker_to_add.bunker_blocks[j];
            }
            bunkers[i] = bunker_to_add;
        }
    }

    // Chooses an advance frame behavior for the game based on current_game_state.
    // current_game_state is updated by the advance frame functions.
    public String Update()
    {
        System.out.println("Enter Update");

        current_game_state_frame_count ++;
        enter_game_state_event = "";    // Reset the newly entered game state event.
        // Json respond. It is completed by GameStateSerializer.
        // The function from GameStateSerializer depends on the game state.
        //      There are two responses: with and without including entities.
        String Json_to_send = "";       

        // Executes a frame. Chooses behavior based on current_game_state.
        System.out.print("Frame count = " + current_game_state_frame_count + "; ");
        switch (current_game_state)
        {
            case GameStatesEnum.LoadNextRound:
                System.out.println("current_game_state is LoadNextRound");
                AdvanceFrameLoadNextRound();
                Json_to_send = game_state_serializer.createGameStateJsonNoEntities(score, lives, enter_game_state_event);   
                break;
            case GameStatesEnum.SpawnAliens:
                System.out.println("current_game_state is SpawnAliens");
                AdvanceFrameSpawnAliens();
                Json_to_send = game_state_serializer.createGameStateJson(score, lives, enter_game_state_event, entities, bullets);
                break;
            case GameStatesEnum.GameLoop:
                System.out.println("current_game_state is GameLoop");
                AdvanceFrame();
                Json_to_send = game_state_serializer.createGameStateJson(score, lives, enter_game_state_event, entities, bullets);
                break;
            case GameStatesEnum.DeathAnimation:
                System.out.println("current_game_state is DeathAnimation");
                AdvanceFrameDeathAnimation();
                Json_to_send = game_state_serializer.createGameStateJson(score, lives, enter_game_state_event, entities, bullets);
                break;
            case GameStatesEnum.GameOver:
                System.out.println("current_game_state is GameOver");
                AdvanceFrameGameOver();
                Json_to_send = game_state_serializer.createGameStateJsonNoEntities(score, lives, enter_game_state_event);   
                break;
            case GameStatesEnum.WinAnimation:
                System.out.println("current_game_state is WinAnimation");
                AdvanceFrameWinAnimation();
                Json_to_send = game_state_serializer.createGameStateJson(score, lives, enter_game_state_event, entities, bullets);
                break;
            case GameStatesEnum.MainMenu:
                System.out.println("current_game_state is MainMenu");
                AdvanceFrameMainMenu();
                Json_to_send = game_state_serializer.createGameStateJsonNoEntities(score, lives, enter_game_state_event);   
                break;
        
            default:
                System.out.println("ERROR: invalid game state in Update().");
                break;
        }


        // ShowGame();
        // System.out.println(Json_to_send);
        DebugShowGame();
        
        ResetControllerState();
        return Json_to_send;


    }

    // public String GenerateJSON()
    // {
        
    // }

    private void ResetControllerState()
    {
        singleton.right_is_pressed = false;
        singleton.left_is_pressed = false;
        singleton.shoot_is_pressed = false;
    }

    public void MoveRight() {singleton.right_is_pressed = true;}
    public void MoveLeft() {singleton.left_is_pressed = true;}

    private void DebugShowGame()
    {
        System.out.println("(left, right, shoot, player_dead?, player_bullet_dead?) = " + singleton.left_is_pressed + ", " + singleton.right_is_pressed + ", " + singleton.shoot_is_pressed + ", " + player.dead + ", " + player_bullet.dead);
        System.out.println("bullets.size() = " + bullets.size());
        System.out.println("player (x, y, sprite, dead) = (" + player.x + ", " + player.y + ", " + player.sprite + ", " + player.dead + ")");
        System.out.println();

        // for (Bullet bullet : bullets)
        // {
        //     System.out.println("bullet (x, y, sprite, dead) = (" + bullet.x + ", " + bullet.y + ", " + bullet.sprite + ", " + bullet.dead + ")");
        // }
    }

    private void ClearRecentDeaths()
    {
        for (Entity entity : entities)
        {
            entity.died_this_frame = false;
        }
        for (Bullet bullet : bullets)
        {
            bullet.died_this_frame = false;
        }
    }



    // Advance frame behavior for the playable game.
    private void AdvanceFrame()
    {
        ClearRecentDeaths();
        // Win condition
        if (destroyed_aliens_counter >= aliens.length)
        {
            System.out.println("AdvanceFrame(): Win condition met! destroyed_aliens_counter = " + destroyed_aliens_counter);
            EnterStateWinAnimation();
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
        if (singleton.left_is_pressed && player.x >= left_descend_limit)  {player.Move(true);}
        if (singleton.right_is_pressed && player.x <= right_descend_limit) {player.Move(false);}
        if (singleton.shoot_is_pressed)
        {
            Bullet possible_player_bullet = player.SpawnBullet();
            if (possible_player_bullet != null)
            {
                bullets.add(possible_player_bullet);
                player_bullet = player.bullet;
            }
        }

        // Bullet movement and collisions
        // for (Bullet bullet : bullets)    // This doesn't allow to remove while iterating.
        // Uses a backwards for loop for deleting bullets while iterating the list.
        for (int i = bullets.size() - 1; i >= 0; i--)   // TODO: Check if this should be i > 0 or i >= 0
        {
            Bullet bullet = bullets.get(i);
            System.out.println("Bullet movement and collisions of bullet at (x, y) = (" + bullet.x + ", " + bullet.y + ")");
            // Destroy bullets out of bounds.
            if (EntityIsOutOfBounds(bullet))
            {
                bullet.SetAsDead();
                bullets.remove(i);
                continue;
            }
            
            if (UpdateCollisions(bullet))   // This may set bullet as dead and remove it from bullets.
            {
                alien_freeze = 16;
                destroyed_aliens_counter ++;
            }
            bullet.Move();

        }

        // Player death logic.
        if (player.dead)
        {
            EnterStateDeathAnimation();
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
    private Boolean UpdateCollisions(Bullet bullet)
    {
        for (Entity entity : entities)
        {
            if (EntitiesAreColliding(entity, bullet))
            {
                entity.SetAsDead();
                bullet.SetAsDead();
                System.out.print("entities collided. bullets.size() = " + bullets.size());
                bullets.remove(bullet);
                System.out.println(" -> bullets.size() = " + bullets.size());
                current_destroyed_entities.add(entity);
                current_destroyed_entities.add(bullet);
                if (entity instanceof Alien) {return true;}
            }
        }
        return false;
    }

    private Boolean EntityIsOutOfBounds(Entity entity)
        {return entity.x < 0 || entity.x + entity.width > right_descend_limit || entity.y < 0 || entity.y + entity.height > 255;}

    // // Detect if two Entity objects collide (intersection of rectangles).
    // public Boolean EntitiesAreColliding(Entity entity_1, Entity entity_2) 
    // {
    //     int delta_y = entity_1.y - entity_2.y;
    //     int delta_x = entity_1.x - entity_2.x;
    //     // return (delta_y >= -1*entity_1.height && delta_y <= entity_2.height) && (delta_x >= -1*entity_1.width && delta_x <= entity_2.width);
    //     return (-1*delta_x < entity_1.width && delta_x < entity_2.width && delta_y < entity_1.height && -1*delta_y < entity_2.height);
    // }
    // Detect if two Entity objects collide (intersection of rectangles).
    public Boolean EntitiesAreColliding(Entity entity_1, Entity entity_2) 
    {
        // System.out.println("entity_1: (x, y, width, height)" + entity_1.x + ", " + entity_1.y + ", " + entity_1.width + ", " + entity_1.height);
        // System.out.println("entity_2: (x, y, width, height)" + entity_2.x + ", " + entity_2.y + ", " + entity_2.width + ", " + entity_2.height);

        Integer entity_1_opposite_x = entity_1.x + entity_1.width;
        Integer entity_1_opposite_y = entity_1.y + entity_1.height;
        Integer entity_2_opposite_x = entity_2.x + entity_2.width;
        Integer entity_2_opposite_y = entity_2.y + entity_2.height;

        Boolean x_intersect =   entity_2.x <= entity_1.x && entity_1.x <= entity_2_opposite_x ||
                                entity_2.x <= entity_1_opposite_x && entity_1_opposite_x <= entity_2_opposite_x ||
                                entity_1.x <= entity_2.x && entity_2.x <= entity_1_opposite_x ||
                                entity_1.x <= entity_2_opposite_x && entity_2_opposite_x <= entity_1_opposite_x;
        Boolean y_intersect =   entity_2.y <= entity_1.y && entity_1.y <= entity_2_opposite_y ||
                                entity_2.y <= entity_1_opposite_y && entity_1_opposite_y <= entity_2_opposite_y ||
                                entity_1.y <= entity_2.y && entity_2.y <= entity_1_opposite_y ||
                                entity_1.y <= entity_2_opposite_y && entity_2_opposite_y <= entity_1_opposite_y;
        return x_intersect && y_intersect;
    }

    // Have all bottom aliens decide whether to shoot and add the new shot Bullet objects to bullets.
    private void EmitShots()
    {
        ArrayList<Alien> bottom_aliens = GetBottomAliens(); // All the aliens at the bottom of their columns.
        for (Alien bottom_alien : bottom_aliens)
        {
            // possibly_emitted_bullet is not null if the Alien decided to shoot.
            Bullet possibly_emitted_bullet = bottom_alien.ShootChance(player.x); 
            if (possibly_emitted_bullet != null)
            {
                bullets.add(possibly_emitted_bullet);
                System.out.println("Alien shot at (x, y) =" + possibly_emitted_bullet.x + ", " + possibly_emitted_bullet.y);
            }
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
        if (direction)  // True means left.
        {
            Alien leftmost_alien = GetLeftmostAlien(0);
            System.out.println("DescentCondition(): leftmost_alien.(x,y) = (" + leftmost_alien.x + ", " + leftmost_alien.y);
            if (leftmost_alien.x <= left_descend_limit) {return true;}
        }
            else
        {
            Alien rightmost_alien = GetRightmostAlien(0);
            System.out.println("DescentCondition(): rightmost_alien.(x,y) = (" + rightmost_alien.x + ", " + rightmost_alien.y);
            if (rightmost_alien.x >= right_descend_limit) {return true;}
        }
        return false;
    }



    private void EnterStateLoadNextRound()
    {
        enter_game_state_event = "load_next_round";
        current_game_state = GameStatesEnum.LoadNextRound;
        current_game_state_frame_count = 0;
    }

    // LoadNextRound logic.
    // Waits some frames between destroying the last alien during GameLoop and start spawing the next round during SpawnAliens.
    // The graphic interface should draw a black rectangle moving from left to right of about a third of the screen wide
    // for the transition.
    private void AdvanceFrameLoadNextRound()
    {
        // Revive some of the bunker blocks each frame.
        // Revives blocks evenly during the frames until GameStatesDuration.LoadNextRoundFrameCount.frames
        //  (i. e. until this game state finishes).
        Integer bunker_blocks_count = bunker_count*bunkers[0].bunker_blocks.length;
        Integer current_frame_bottom_limit = bunker_blocks_count*current_game_state_frame_count/GameStatesDuration.LoadNextRoundFrameCount.frames;
        Integer current_frame_top_limit =    bunker_blocks_count*(current_game_state_frame_count+1)/GameStatesDuration.LoadNextRoundFrameCount.frames - 1;
        
        Integer bunker_blocks_offset = entities.length - bunker_blocks_count;
        for (int i = current_frame_bottom_limit; i <= current_frame_top_limit; i++)
        {
            // if bunker_blocks_offset + i is a valid index in entities, revive the BunkerBlock at i.
            if (bunker_blocks_offset + i < entities.length){entities[bunker_blocks_offset + i].dead = false;}
        }
        if (current_game_state_frame_count >= GameStatesDuration.LoadNextRoundFrameCount.frames)
        {
            EnterStateSpawnAliens();
        }
    }

    private void EnterStateSpawnAliens()
    {
        enter_game_state_event = "spawn_aliens";
        current_game_state = GameStatesEnum.SpawnAliens;
        current_game_state_frame_count = 0;
    }

    
    // SpawnAliens logic.
    // Repositions and revives (set dead as false again) all 55 aliens.
    // current_game_state_frame_count must start at 0.
    private void AdvanceFrameSpawnAliens()
    {
        if (current_game_state_frame_count >= GameStatesDuration.SpawnAliensFrameCount.frames)
        {
            destroyed_aliens_counter = 0;
            EnterStateGameLoop();
        }
        int x = 18 + (current_game_state_frame_count%11)*16;
        int y = 72 + ((54-current_game_state_frame_count)/11)*16;
        // int sprite = current_game_state_frame_count/22*2;
        aliens[current_game_state_frame_count].x = x;
        aliens[current_game_state_frame_count].y = y;
        // aliens[current_game_state_frame_count].sprite = sprite;
        aliens[current_game_state_frame_count].dead = false;
        aliens[current_game_state_frame_count].sprite_offset = 0;
        System.out.println("alien (x, y, sprite, dead) = (" + aliens[current_game_state_frame_count].x + ", " + aliens[current_game_state_frame_count].y + ", " + aliens[current_game_state_frame_count].sprite + ", " + aliens[current_game_state_frame_count].dead + ")");
    }   

    private void EnterStateGameLoop()
    {
        enter_game_state_event = "game_loop";
        current_game_state = GameStatesEnum.GameLoop;
        current_game_state_frame_count = 0;
        ReviveAndResetPlayer();
    }

    private void EnterStateDeathAnimation()
    {
        enter_game_state_event = "death_animation";
        current_game_state = GameStatesEnum.DeathAnimation;
        current_game_state_frame_count = 0;
    }

    private void AdvanceFrameDeathAnimation()
    {
        // 0 <= current_game_state_frame_count < 56     (i. e. 56 frames).
            // Let the death animation play (i. e. do nothing).
        // current_game_state_frame_count == 56         (i. e. 1 frame).
            // Subtract 1 life.
        // 57 <= current_game_state_frame_count < 187   (i. e. 130 frames).
            // Do nothing.
        // current_game_state_frame_count == 187         (i. e. 1 frame).
            // teleport player to x = 0.
            // revive player.
        if (current_game_state_frame_count == 56)   // Subtract 1 life go to GameOver if there are 0 left.
        {
            lives --;
            if (lives <= 0)
            {
                EnterStateGameOver();
            }
        }
        if (current_game_state_frame_count == 187)  // Revives the player.
        {
            player.x = 0;
            player.dead = false;
        }
        if (current_game_state_frame_count > 187)   // The player regains control one frame after reviving.
        {
            EnterStateGameLoop();
        }
    }

    private void EnterStateGameOver()
    {
        enter_game_state_event = "game_over";
        current_game_state = GameStatesEnum.GameOver;
        current_game_state_frame_count = 0;
    }

    private void AdvanceFrameGameOver()
    {
        // 0 <= current_game_state_frame_count < 180     (i. e. 180 frames).
            // plays the game over animation.
        // 180 <= current_game_state_frame_count < 260     (i. e. 80 frames).
            // Starts transition to the main menu.
        if (current_game_state_frame_count == 180)  // Revives the player.
        {
            System.out.println("GameOver: Play black panel transition.");
        }
        if (current_game_state_frame_count > 260)   // The player regains control one frame after reviving.
        {
            EnterStateMainMenu();
        }        
    }

    private void EnterStateMainMenu()
    {
        enter_game_state_event = "main_menu";
        current_game_state = GameStatesEnum.GameOver;
        current_game_state_frame_count = 0;
    }

    private void AdvanceFrameMainMenu()
    {
        // Press any button to start the game.
        if (singleton.right_is_pressed || singleton.left_is_pressed || singleton.shoot_is_pressed)
            {EnterStateLoadNextRound();}
    }

    private void EnterStateWinAnimation()
    {
        enter_game_state_event = "win_animation";
        current_game_state = GameStatesEnum.WinAnimation;
        current_game_state_frame_count = 0;
    }

    private void AdvanceFrameWinAnimation()
    {
        // 0 <= current_game_state_frame_count < 45     (i. e. 45 frames).
            // Simply freezes the game for a short duration.
        if (current_game_state_frame_count > 45)   // The player regains control one frame after reviving.
        {
            EnterStateLoadNextRound();
        }     
    }

    private void ReviveAndResetPlayer()
    {
        System.out.println("ReviveAndResetPlayer()");
        player.dead = false;
        player.x = 80;
        player.y = 247;
    }



}