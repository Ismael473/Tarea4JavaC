
import java.util.ArrayList;

public class SpaCEInvaders
{
    Alien[] aliens = new Alien[55];
    Entity[] entities = new Entity[132];
    int right_descend_limit = 198;
    int left_descend_limit = 0;
    int player_height = 0;
    AliensConcreteFactory aliens_concrete_factory = new AliensConcreteFactory();

    int current_moving_alien = 0;
    boolean direction = false;
    boolean descending = true;
    int destroyed_aliens_counter = 0;
    int alien_freeze = 0;

    public Singleton singleton = new Singleton();
    Integer player_starting_position_x = 80;
    Integer player_starting_position_y = 247;
    Player player = new Player(player_starting_position_x, player_starting_position_y, 0);

    ArrayList<Bullet> bullets = new ArrayList<>();
    Bullet player_bullet = player.bullet;

    ArrayList<Entity> current_destroyed_entities = new ArrayList<>();
    GameStateSerializer game_state_serializer = new GameStateSerializer(); 

    GameStatesEnum current_game_state = GameStatesEnum.MainMenu;
    int current_game_state_frame_count = 0;
    Integer score = 0;
    Integer lives = 3;
    String enter_game_state_event = "";
    private boolean finished = false;
    
    Integer bunker_count = 4;
    Integer bunkers_start_x = 35;
    Integer bunkers_spacing_x = (right_descend_limit - bunkers_start_x)/bunker_count;
    Integer bunkers_starting_y = 190;
    Bunker[] bunkers = new Bunker[bunker_count]; 

    public SpaCEInvaders()
    {
        for (int i = 0; i < aliens.length; i++)
        {
            int alien_x = 18 + (i%11)*16;
            int alien_y = 72 + ((54-i)/11)*16;
            
            Integer index_to_alien_type = i/22;
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
        }
        entities[55] = player;

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

    public String Update()
    {
        current_game_state_frame_count ++;
        enter_game_state_event = "";
        String Json_to_send = "";       

        switch (current_game_state)
        {
            case GameStatesEnum.LoadNextRound:
                AdvanceFrameLoadNextRound();
                Json_to_send = game_state_serializer.createGameStateJsonNoEntities(score, lives, enter_game_state_event);   
                break;
            case GameStatesEnum.SpawnAliens:
                AdvanceFrameSpawnAliens();
                Json_to_send = game_state_serializer.createGameStateJson(score, lives, enter_game_state_event, entities, bullets);
                break;
            case GameStatesEnum.GameLoop:
                AdvanceFrame();
                Json_to_send = game_state_serializer.createGameStateJson(score, lives, enter_game_state_event, entities, bullets);
                break;
            case GameStatesEnum.DeathAnimation:
                AdvanceFrameDeathAnimation();
                Json_to_send = game_state_serializer.createGameStateJson(score, lives, enter_game_state_event, entities, bullets);
                break;
            case GameStatesEnum.GameOver:
                AdvanceFrameGameOver();
                Json_to_send = game_state_serializer.createGameStateJsonNoEntities(score, lives, enter_game_state_event);   
                break;
            case GameStatesEnum.WinAnimation:
                AdvanceFrameWinAnimation();
                Json_to_send = game_state_serializer.createGameStateJson(score, lives, enter_game_state_event, entities, bullets);
                break;
            case GameStatesEnum.MainMenu:
                AdvanceFrameMainMenu();
                Json_to_send = game_state_serializer.createGameStateJsonNoEntities(score, lives, enter_game_state_event);   
                break;
        
            default:
                break;
        }

        ResetControllerState();
        return Json_to_send;
    }

    private void ResetControllerState()
    {
        singleton.right_is_pressed = false;
        singleton.left_is_pressed = false;
        singleton.shoot_is_pressed = false;
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

    private void AdvanceFrame()
    {
        ClearRecentDeaths();
        if (destroyed_aliens_counter >= aliens.length)
        {
            EnterStateWinAnimation();
        }

        if (current_moving_alien == 0)
        {    
            if (descending) {descending = false;}
            else if (DescentCondition(direction))
            {
                descending = true;
                direction = !direction;
            } 
        }

        if (alien_freeze == 0)
        {
            Alien alien_to_move = GetNextAlien(current_moving_alien);
            if (alien_to_move != null)
            {
                int moved_index = current_moving_alien;
                while (moved_index <= 54 && aliens[moved_index].dead) moved_index++;
                alien_to_move.Move(direction);
                if (descending) {alien_to_move.Descend();}
                current_moving_alien = moved_index + 1;
            }
            else
            {
                current_moving_alien = 0;
            }
        }
        else
        {
            alien_freeze--;
        }
            
        EmitShots();

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

        for (int i = bullets.size() - 1; i >= 0; i--) 
        {
            Bullet bullet = bullets.get(i);
            if (EntityIsOutOfBounds(bullet))
            {
                bullet.SetAsDead();
                bullets.remove(i);
                continue;
            }
            
            if (UpdateCollisions(bullet))
            {
                alien_freeze = 16;
                destroyed_aliens_counter ++;
            }
            bullet.Move();
        }

        if (player.dead)
        {
            EnterStateDeathAnimation();
        }
    }

    private Boolean UpdateCollisions(Bullet bullet)
    {
        for (Entity entity : entities)
        {
            if (entity.dead) continue;
            if (EntitiesAreColliding(entity, bullet))
            {
                entity.SetAsDead();
                bullet.SetAsDead();
                bullets.remove(bullet);
                current_destroyed_entities.add(entity);
                current_destroyed_entities.add(bullet);
                if (entity instanceof Alien) {return true;}
            }
        }
        return false;
    }

    private Boolean EntityIsOutOfBounds(Entity entity)
        {return entity.x < 0 || entity.x + entity.width > right_descend_limit || entity.y < 0 || entity.y + entity.height > 255;}

    public Boolean EntitiesAreColliding(Entity entity_1, Entity entity_2) 
    {
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

    private void EmitShots()
    {
        ArrayList<Alien> bottom_aliens = GetBottomAliens();
        for (Alien bottom_alien : bottom_aliens)
        {
            Bullet possibly_emitted_bullet = bottom_alien.ShootChance(player.x); 
            if (possibly_emitted_bullet != null)
            {
                bullets.add(possibly_emitted_bullet);
            }
        }
    }

    Alien GetNextAlien(int alien_counter)
    {
        if (alien_counter > 54){return null;}
        if (!this.aliens[alien_counter].dead){return this.aliens[alien_counter];}
        return GetNextAlien(alien_counter + 1);
    }

    int FirstAlien()
    {
        for (int i = 0; i < aliens.length; i++)
        {
            if (!aliens[i].dead){return i;}    
        }
        return -1;
    }

    Alien GetRightmostAlien(int offset)
    {
        if (!aliens[10-offset].dead) {return aliens[10-offset];}
        if (!aliens[21-offset].dead) {return aliens[21-offset];}
        if (!aliens[32-offset].dead) {return aliens[32-offset];}
        if (!aliens[43-offset].dead) {return aliens[43-offset];}
        if (!aliens[54-offset].dead) {return aliens[54-offset];}
        return GetRightmostAlien(offset+1);
    }

    Alien GetLeftmostAlien(int offset)
    {
        if (!aliens[0+offset].dead) {return aliens[0+offset];}
        if (!aliens[11+offset].dead) {return aliens[11+offset];}
        if (!aliens[22+offset].dead) {return aliens[22+offset];}
        if (!aliens[33+offset].dead) {return aliens[33+offset];}
        if (!aliens[44+offset].dead) {return aliens[44+offset];}
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
            Alien leftmost_alien = GetLeftmostAlien(0);
            if (leftmost_alien.x <= left_descend_limit) {return true;}
        }
            else
        {
            Alien rightmost_alien = GetRightmostAlien(0);
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

    private void AdvanceFrameLoadNextRound()
    {
        Integer bunker_blocks_count = bunker_count*bunkers[0].bunker_blocks.length;
        Integer current_frame_bottom_limit = bunker_blocks_count*current_game_state_frame_count/GameStatesDuration.LoadNextRoundFrameCount.frames;
        Integer current_frame_top_limit =    bunker_blocks_count*(current_game_state_frame_count+1)/GameStatesDuration.LoadNextRoundFrameCount.frames - 1;
        
        Integer bunker_blocks_offset = entities.length - bunker_blocks_count;
        for (int i = current_frame_bottom_limit; i <= current_frame_top_limit; i++)
        {
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

    private void AdvanceFrameSpawnAliens()
    {
        int index = current_game_state_frame_count - 1;
        if (index >= GameStatesDuration.SpawnAliensFrameCount.frames)
        {
            destroyed_aliens_counter = 0;
            EnterStateGameLoop();
            return;
        }
        int x = 18 + (index%11)*16;
        int y = 72 + ((54-index)/11)*16;
        aliens[index].x = x;
        aliens[index].y = y;
        aliens[index].dead = false;
        aliens[index].sprite_offset = 0;
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
        if (current_game_state_frame_count == 56)
        {
            lives --;
            if (lives <= 0)
            {
                EnterStateGameOver();
            }
        }
        if (current_game_state_frame_count == 187)
        {
            player.x = 0;
            player.dead = false;
        }
        if (current_game_state_frame_count > 187)
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
        if (current_game_state_frame_count > 260)
        {
            finished = true;
        }        
    }

    public boolean isFinished()
    {
        return finished;
    }

    private void AdvanceFrameMainMenu()
    {
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
        if (current_game_state_frame_count > 45)
        {
            EnterStateLoadNextRound();
        }     
    }

    private void ReviveAndResetPlayer()
    {
        player.dead = false;
        player.x = player_starting_position_x;
        player.y = player_starting_position_y;
    }
}
