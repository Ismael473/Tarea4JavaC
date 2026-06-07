public class AliensConcreteFactory extends AliensAbstractFactory
{
    String death_animation = "alien_death_animation";

    @Override
    public Crab CreateCrab() {return new Crab(1, death_animation);}
    @Override
    public Octopus CreateOctopus() {return new Octopus(3, death_animation);}
    @Override
    public Squid CreateSquid() {return new Squid(5, death_animation);}
}
