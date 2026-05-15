import javax.swing.*;

public class Window{
    public static void main(String [] args){
        JFrame frame = new JFrame("Comandos del juego");
        JButton button = new JButton("Hacer Solicitud");
        JTextField text = new JTextField(20);

        frame.setLayout(new java.awt.FlowLayout());
        frame.add(text);
        frame.add(button);
        
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}