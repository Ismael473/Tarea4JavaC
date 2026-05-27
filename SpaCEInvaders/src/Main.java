public class Main {
    public static void main(String[] args) {
        System.out.println("Hello SpaCE");
        // Player player;
        // Test t = new Test();
        // t.hello();

        SpaCEInvaders game = new SpaCEInvaders();
        for (int i = 0; i < 620; i++)
        {
            game.GameLoop();
        }
        System.out.println("Bye SpaCE");
    }
}