
public enum GameStatesDuration
{
    LoadNextRoundFrameCount(60),
    SpawnAliensFrameCount(55),
    DeathAnimationFrameCount(30),
    WinAnimationFrameCount(90);

    public final int frames;

    GameStatesDuration(int frames)
    {
        this.frames = frames;
    }
}
