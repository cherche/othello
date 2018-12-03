import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Game extends JPanel implements ActionListener {
  private int width;
  private int height;
  private Othello othello;

  public static void main(String[] args) {
    JPanel content = new Game(4, 4);
    JFrame window = new JFrame();
    window.setContentPane(content);
    window.setSize(300, 300);
    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }

  public Game(int width, int height) {
    this.width = width;
    this.height = height;
    this.othello = new Othello(width, height);
    JPanel game = new JPanel();
    // Grid layout goes by number of rows then number of columns
    // So this looks a bit confusing, but it's right
    game.setLayout(new GridLayout(height, width));

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        JButton button = new JButton();
        button.setActionCommand(String.valueOf(x + y * width));
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setOpaque(true);

        if (true) {
          button.setBackground(Color.GREEN);
        } else if (true) {

        } else {

        }
        game.add(button);
      }
    }

    this.add(game);
  }

  public void actionPerformed(ActionEvent e) {
    int id = Integer.parseInt(e.getActionCommand());
    int x = id % height;
    int y = id / height;
    ArrayList<int[]> captures = othello.place(new int[] {x, y}, 2);
  }
}
