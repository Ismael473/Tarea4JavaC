package spaceinvaders.ce1106.game;

import javax.swing.JFrame;


public class Main {
    public static void main(String[] args) {
        System.out.println("Hello SpaCE");
        // Player player;
        // Test t = new Test();
        // t.hello();
        
        /////// RECEIVE KEYBOARD LOGIC ///////
        JFrame window = new JFrame("Keyboard Test");
        Keyboard listener = new Keyboard();
        
        window.addKeyListener(listener);
        window.setSize(400, 400);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /////// RECEIVE KEYBOARD LOGIC ///////



        SpaCEInvaders game = new SpaCEInvaders();
        Singleton singleton = game.singleton; 
        
        for (int i = 0; i < 600; i++)
        {
            /////// RECEIVE KEYBOARD LOGIC ///////
            singleton.left_was_pressed      = singleton.left_is_pressed; 
            singleton.right_was_pressed     = singleton.right_is_pressed; 
            singleton.shoot_was_pressed     = singleton.shoot_is_pressed; 
            
            singleton.left_is_pressed       = Keyboard.a;
            singleton.right_is_pressed      = Keyboard.d;
            singleton.shoot_is_pressed      = Keyboard.w;
            /////// RECEIVE KEYBOARD LOGIC ///////


            game.Update();
            
            Integer sleep_time;
            if (game.current_game_state != GameStatesEnum.GameLoop) {sleep_time = 33;}
            else {sleep_time = 333;}

            if (game.current_game_state == GameStatesEnum.DeathAnimation) {sleep_time = 166;}


            try
            {
                // Thread.sleep(16);
                Thread.sleep(sleep_time);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("Bye SpaCE");
    }
}