// Durations in frames for each of the game states.
// After this frame count is reached, the game state's advance frame function should decide a new value for
// current_game_state and reset current_game_state_frame_count back to 0.

// Enum code from ChatGPT.
public enum GameStatesDuration
{
    LoadNextRoundFrameCount(60),   // Example values
    SpawnAliensFrameCount(55),
    DeathAnimationFrameCount(30),
    WinAnimationFrameCount(90);

    public final int frames;

    GameStatesDuration(int frames)
    {
        this.frames = frames;
    }
}

// Original enum.
// public enum GameStatesDuration
// {
//     LoadNextRoundFrameCount,   // This is when the bunkers regenerate after a black panel momentarily hides the game.
//     SpawnAliensFrameCount,     // Spawns one alien per frame until all 55 are present.
//     DeathAnimationFrameCount,  // Simmilar to GameLoop. Stops the aliens and the player from moving, but not the bullets.
    
//     WinAnimationFrameCount    // Brief freeze after defeating all aliens and before moving to LoadNextRound.
    
//     // LoadMainMenuFrameCount    // Similar to LoadNextRound.

// }