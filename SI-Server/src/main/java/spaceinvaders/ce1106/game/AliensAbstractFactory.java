package spaceinvaders.ce1106.game;

public abstract class AliensAbstractFactory
{
    public abstract Octopus CreateOctopus();
    public abstract Crab CreateCrab();
    public abstract Squid CreateSquid();

    public abstract UFO CreateUFO();

}