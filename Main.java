import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private JButton ENCODEButton;
    private JButton DECODEButton;
    private JPanel MainMenu;

    public Main(){
        setTitle("Image Steganographer");
//        setLocation(560, 240);
        setContentPane(MainMenu);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600);
        setBackground(Color.BLACK);
        setResizable(false);
        setVisible(true);

        ENCODEButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(EncodePage::new);
        });
        DECODEButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(DecodePage::new);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}