import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * A virtual Reversi, with some options
 *
 * @author  Ryan Nguyen
 * @version 2019-01-07
 */
public class Game extends JPanel implements ActionListener {
  private static int width = 8;
  private static int height = 8;
  private static int playerCount = 2;
  private static Color SIDEBAR_BACK = new Color(18, 18, 18);

  public static void main(String[] args) {
    JPanel content = new Game();
    JFrame window = new JFrame();
    window.setContentPane(content);
    // We want to design around a 6:5 viewport
    window.setSize(600, 500);
    window.setResizable(false);
    // Centres the window on the screen
    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }

  public Game() {
    this.setLayout(new BorderLayout());
    JPanel sidebar = new JPanel(new GridLayout(5, 0));
    sidebar.setBackground(SIDEBAR_BACK);
    sidebar.setPreferredSize(new Dimension(100, 500));
    sidebar.add(new JButton("home"));
    // This is the code necessary to make a JButton transparent
    /*
    JButton undo = new JButton("undo");
    undo.setOpaque(false);
    undo.setBorderPainted(false);
    */
    sidebar.add(new JButton("undo"));
    sidebar.add(new JPanel());
    sidebar.add(new JButton("instructions"));
    sidebar.add(new JButton("settings"));
    this.add(sidebar, BorderLayout.WEST);
    JPanel container = new JPanel(new CardLayout());
    container.setPreferredSize(new Dimension(500, 500));
    JPanel menu = new JPanel();
    menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
    JLabel title = new JLabel("Configuration");
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    title.setFont(new Font("Raleway", Font.PLAIN, 45));
    menu.add(title);
    {
    JPanel colours = new JPanel();
    ButtonGroup coloursGroup = new ButtonGroup();
    colours.setAlignmentX(Component.CENTER_ALIGNMENT);
    JRadioButton black = new JRadioButton("black");
    black.setSelected(true);
    coloursGroup.add(black);
    colours.add(black);
    JRadioButton white = new JRadioButton("white");
    coloursGroup.add(white);
    colours.add(white);
    menu.add(colours);
    }
    {
    JPanel order = new JPanel();
    ButtonGroup orderGroup = new ButtonGroup();
    order.setAlignmentX(Component.CENTER_ALIGNMENT);
    JRadioButton first = new JRadioButton("first");
    first.setSelected(true);
    orderGroup.add(first);
    order.add(first);
    JRadioButton second = new JRadioButton("second");
    orderGroup.add(second);
    order.add(second);
    menu.add(order);
    }
    JButton play = new JButton("Play");
    play.setAlignmentX(Component.CENTER_ALIGNMENT);
    menu.add(play);
    container.add(menu, "menu");
    // GridLayout() takes row count then column count
    // That results in this weird ordering of (height, width),
    // but it's totally right - just a bit weird
    JPanel board = new JPanel(new GridLayout(height, width));
    board.setBackground(Color.GREEN);
    container.add(board, "board");
    this.add(container, BorderLayout.CENTER);
  }

  public void actionPerformed(ActionEvent e) {

  }
}
