import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Game extends JPanel implements ActionListener {
  private int width;
  private int height;
  private int playerCount = 2;
  private JButton[][] board;
  private Othello othello;
  private final Color[] colors = new Color[] {
    Color.GREEN,
    Color.BLACK,
    Color.WHITE
  };

  public static void main(String[] args) {
    JPanel content = new Game(8, 8);
    JFrame window = new JFrame();
    window.setContentPane(content);
    window.setSize(300, 300);
    // Centres the window on the screen
    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }

  public Game(int width, int height) {
    this.width = width;
    this.height = height;
    this.board = new JButton[width][height];
    this.othello = new Othello(width, height, playerCount);
    JPanel game = new JPanel();
    // Grid layout goes by number of rows then number of columns
    // So this looks a bit confusing, but it's right
    game.setLayout(new GridLayout(height, width));
    // Wow, setPreferredSize() is magical
    int TILE_SIZE = 32;
    game.setPreferredSize(new Dimension(TILE_SIZE * width, TILE_SIZE * height));

    // Now, we make each tile and add it
    // The fact is, all of this really slows things down
    // I think it would be best to just have JPanel instances
    // and do some event delegation, if possible
    // That is to say, we assign a single mouse listener and
    // just figure out what was clicked in the handler
    // I don't even know if that's possible, but it's optimal
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        JButton button = new JButton();
        button.setActionCommand(x + "," + y);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setOpaque(true);
        int boardValue = othello.getBoardValue(new int[] {x, y});
        button.setBackground(colors[boardValue]);

        game.add(button);
        board[x][y] = button;
      }
    }

    this.add(game);
  }

  public void actionPerformed(ActionEvent e) {
    // Earlier, we encoded the tile ID with the action command
    // Now, we'll just decode it
    String id = e.getActionCommand();
    System.out.println(id);
    String[] split = id.split(",");
    int[] coords = {
      Integer.parseInt(split[0]),
      Integer.parseInt(split[1])
    };
    // Cache the attacker before making the move (since that would change the attacker)
    // Perhaps the attacker should be included in the State instace instead ...
    int attacker = othello.getTurn();
    State state = othello.place(coords);
    System.out.println(state.toString());

    // Now we just do some different stuff depending on whether the move was allowed
    // There still needs to be checking for when the game ends (all players skip),
    // but this is really very good so far. I'm happy
    if (state.isValidMove) {
      ArrayList<int[]> updates = state.updates;

      for (int i = 0; i < updates.size(); i++) {
        int[] pair = updates.get(i);
        int x = pair[0];
        int y = pair[1];
        board[x][y].setBackground(colors[attacker]);
      }
    } else if (state.isSkipped) {
      System.out.println("Player " + attacker + " skipped!");
    }
  }
}
