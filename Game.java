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
  private static Color SIDEBAR_BACK = new Color(40, 44, 53);

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
    JPanel sidebar = initSidebar();
    this.add(sidebar, BorderLayout.WEST);
    JPanel main = new JPanel(new CardLayout());
    main.setPreferredSize(new Dimension(500, 500));
    JPanel menu = initMenu();
    main.add(menu, "menu");
    // GridLayout() takes row count then column count
    // That results in this weird ordering of (height, width),
    // but it's totally right - just a bit weird
    JPanel board = initBoard();
    main.add(board, "board");
    this.add(main, BorderLayout.CENTER);
  }

  private static JButton createIconButton(String iconURL, String actionCommand, ActionListener actionListener) {
    JButton button = new JButton();
    // One may hide buttons without screwing up the layout using the following:
    // button.setVisible(false);
    button.setOpaque(false);
    button.setBorderPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setIcon(createImageIcon(iconURL));
    button.setActionCommand(actionCommand);
    button.addActionListener(actionListener);
    return button;
  }

  private JPanel initSidebar() {
    JPanel sidebar = new JPanel(new GridLayout(5, 0));
    sidebar.setBackground(SIDEBAR_BACK);
    sidebar.setPreferredSize(new Dimension(100, 500));
    sidebar.add(createIconButton("icons/sidebar/home.png", "home", this));
    sidebar.add(createIconButton("icons/sidebar/undo.png", "undo", this));
    JLabel turn = new JLabel();
    turn.setHorizontalAlignment(JLabel.CENTER);
    turn.setIcon(createImageIcon("icons/turns/0.png"));
    turn.setOpaque(false);
    sidebar.add(turn);
    sidebar.add(createIconButton("icons/sidebar/instructions.png", "instructions", this));
    sidebar.add(createIconButton("icons/sidebar/settings.png", "settings", this));
    return sidebar;
  }

  private JPanel initMenu() {
    JPanel menu = new JPanel();
    menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
    JLabel title = new JLabel("Configuration");
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    title.setFont(new Font("Raleway", Font.PLAIN, 45));
    menu.add(title);
    // In fact, we could probably generalize these radio buttons
    // with a fancy class or something like that
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
    return menu;
  }

  private static JPanel initBoard() {
    // We may later use removeAll() if the user changes the configuration
    JPanel board = new JPanel(new GridLayout(height, width));
    board.setBackground(Color.GREEN);
    return board;
  }

  public void actionPerformed(ActionEvent e) {

  }

  private static ImageIcon createImageIcon(String path) {
    java.net.URL url = Game.class.getResource(path);

    if (url != null) {
      return new ImageIcon(url);
    } else {
      System.out.println("Cannot locate resource at \"" + path + "\".");
      return null;
    }
  }
}
