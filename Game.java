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
  private static Color FORE = new Color(125, 130, 142);
  private static Color BACK = new Color(40, 44, 53);
  private static Color SIDEBAR_BACK = new Color(40, 44, 53);
  private static Color BOARD_BACK = new Color(117, 204, 71);
  private static Color BOARD_OUTLINE = Color.BLACK;
  private static JPanel main;
  private static CardLayout mainLayout;
  private static JComponent[] boardSidebarComponents;

  public static void main(String[] args) {
    JPanel content = new Game();
    JFrame window = new JFrame("Othello");
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
    mainLayout = new CardLayout();
    main = new JPanel(mainLayout);
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

  private static JButton createSidebarButton(String name, ActionListener actionListener) {
    JButton button = new JButton();
    // One may hide buttons without screwing up the layout using the following:
    // button.setVisible(false);
    button.setOpaque(false);
    button.setBorderPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    String iconURL = "icons/sidebar/" + name + ".png";
    button.setIcon(createImageIcon(iconURL));
    String actionCommand = name;
    button.setActionCommand(actionCommand);
    button.addActionListener(actionListener);
    return button;
  }

  private static void setSidebarMode(boolean isVisible) {
    for (int i = 0; i < boardSidebarComponents.length; i++) {
      boardSidebarComponents[i].setVisible(isVisible);
    }
  }

  private JPanel initSidebar() {
    JPanel sidebar = new JPanel(new GridLayout(5, 0));
    sidebar.setBackground(SIDEBAR_BACK);
    sidebar.setPreferredSize(new Dimension(100, 500));
    JButton home = createSidebarButton("home", this);
    sidebar.add(home);
    JButton undo = createSidebarButton("undo", this);
    sidebar.add(undo);
    JLabel turn = new JLabel();
    turn.setHorizontalAlignment(JLabel.CENTER);
    turn.setIcon(createImageIcon("icons/turns/0.png"));
    turn.setOpaque(false);
    // Basically, these are the ones that should only be visible
    // when the board is visible
    boardSidebarComponents = new JComponent[] {
      home,
      undo,
      turn
    };
    setSidebarMode(false);
    sidebar.add(turn);
    sidebar.add(createSidebarButton("instructions", this));
    sidebar.add(createSidebarButton("settings", this));
    return sidebar;
  }

  private JPanel initMenu() {
    JPanel menu = new JPanel();
    menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
    JLabel title = new JLabel("Othello");
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
    play.setActionCommand("board");
    play.addActionListener(this);
    play.setAlignmentX(Component.CENTER_ALIGNMENT);
    menu.add(play);
    return menu;
  }

  private static JPanel initBoard() {
    // We may later use removeAll() if the user changes the configuration
    JPanel board = new JPanel(new GridLayout(height, width));

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        JPanel tile = new JPanel();
        tile.setOpaque(false);
        tile.setBorder(BorderFactory.createLineBorder(BOARD_OUTLINE));
        board.add(tile);
      }
    }

    board.setBackground(BOARD_BACK);
    return board;
  }

  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();

    if ("board".equals(actionCommand)) {
      setSidebarMode(true);
      mainLayout.show(main, "board");
    } else if ("home".equals(actionCommand)) {
      /* Something like this to make sure they don't lose everything
      Object[] options = { "Continue", "Cancel" };
      JOptionPane.showOptionDialog(null, "Your progress will not be saved.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
      */
      setSidebarMode(false);
      mainLayout.show(main, "menu");
    }
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
