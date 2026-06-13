package spaceinvaders.ce1106.game;

public class AliensConcreteFactory extends AliensAbstractFactory
{
    String death_animation = "alien_death_animation";

    @Override
    public Crab CreateCrab() {return new Crab(1, death_animation);}
    @Override
    public Octopus CreateOctopus() {return new Octopus(3, death_animation);}
    @Override
    public Squid CreateSquid() {return new Squid(5, death_animation);}

    @Override
    // Contrary to other Alien entities, UFO spawns dead and its move_distance is 1 instead of 2.
    public UFO CreateUFO()
    {
        UFO new_ufo = new UFO(29, death_animation);
        new_ufo.move_distance = 1;
        new_ufo.dead = true;
        return new_ufo;
    }
}
