public enum GameStatesEnum
{
    LoadNextRound,  // This is when the bunkers regenerate after a black panel momentarily hides the game.
    SpawnAliens,    // Spawns one alien per frame until all 55 are present.
    GameLoop,       // The player is controllable.
    DeathAnimation, // Simmilar to GameLoop. Stops the aliens and the player from moving, but not the bullets.
    GameOver,       // A different ending for the DeathAnimation. Leads to Main Menu.  
    
    WinAnimation,    // Brief freeze after defeating all aliens and before moving to LoadNextRound.
    
    MainMenu        // Asks to press a button to start. The player is sent here after losing.
    // LoadMainMenu    // Similar to LoadNextRound.

}

// Only GameLoop and MainMenu allow inputs from the player.
