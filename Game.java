import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.event.*;
import java.util.*;

/**
 * A virtual Reversi, with some options
 *
 * @author  Ryan Nguyen
 * @version 2019-01-10
 */
public class Game extends JPanel implements ActionListener {
  private static int width = 8;
  private static int height = 8;
  private static int playerCount = 2;
  private static Color FORE = new Color(125, 130, 142);
  private static Color BACK = new Color(50, 54, 62);
  private static Color FORWARD_BACK = new Color(40, 44, 53);
  private static Color BOARD_BACK = new Color(0, 187, 84);
  private static Color BOARD_OUTLINE = BACK;
  private static JPanel main;
  private static CardLayout mainLayout;
  private static JTextField playerCountField;
  private static JButton play;
  private static JPanel board = new JPanel();
  private static JComponent[] boardSidebarComponents;

  public static void main(String[] args) {
    JPanel content = new Game();
    // We want to design around a 6:5 viewport
    content.setPreferredSize(new Dimension(600, 500));
    JFrame frame = new JFrame("Othello");
    frame.setContentPane(content);
    // Setting the window size directly actually includes the title bar
    // in the height, which means that a window size of (600, 500)
    // would leave maybe (600, 480) for our actual content
    frame.pack();
    frame.setResizable(false);
    // Centres the window on the screen
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  public Game() {
    this.setLayout(new BorderLayout());
    JPanel sidebar = initSidebar();
    this.add(sidebar, BorderLayout.WEST);
    mainLayout = new CardLayout();
    main = new JPanel(mainLayout);
    main.setBackground(BACK);
    main.setPreferredSize(new Dimension(500, 500));
    JPanel menu = initMenu();
    main.add(menu, "menu");
    // GridLayout() takes row count then column count
    // That results in this weird ordering of (height, width),
    // but it's totally right - just a bit weird
    JPanel boardContainer = initBoardContainer();
    main.add(boardContainer, "boardContainer");
    this.add(main, BorderLayout.CENTER);
  }

  private static JButton createIconButton(String iconURL, String actionCommand, ActionListener actionListener) {
    JButton button = new JButton();
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setIcon(createImageIcon(iconURL));
    button.setActionCommand(actionCommand);
    button.addActionListener(actionListener);
    button.setOpaque(true);
    button.setBorderPainted(false);
    return button;
  }

  private static JButton createSidebarButton(String name, String actionCommand, ActionListener actionListener) {
    String iconURL = "icons/sidebar/" + name + ".png";
    JButton button = createIconButton(iconURL, actionCommand, actionListener);
    button.setOpaque(false);
    return button;
  }

  private static void setSidebarMode(boolean isVisible) {
    for (int i = 0; i < boardSidebarComponents.length; i++) {
      boardSidebarComponents[i].setVisible(isVisible);
    }
  }

  private JPanel initSidebar() {
    JPanel sidebar = new JPanel(new GridLayout(5, 0));
    sidebar.setBackground(FORWARD_BACK);
    sidebar.setPreferredSize(new Dimension(100, 500));
    JButton home = createSidebarButton("home", "home", this);
    sidebar.add(home);
    JButton undo = createSidebarButton("undo", "undo", this);
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
    sidebar.add(createSidebarButton("instructions", "instructions", this));
    sidebar.add(createSidebarButton("settings", "settings", this));
    return sidebar;
  }

  private JPanel initMenu() {
    JPanel menu = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    JPanel container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    JLabel title = new JLabel("Othello");
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    title.setFont(new Font("Gill Sans", Font.PLAIN, 52));
    title.setForeground(FORE);
    title.setBorder(new EmptyBorder(0, 0, 10, 0));
    container.add(title);
    // I just moved this to a new method because it was
    // a huge pain to read right here. This is for readability.
    // Actually, all the init methods are for readability and maintainability.
    // They don't actually generalize anything, unlike the create methods.
    JPanel configField = initPlayerCountField();
    container.add(configField);
    play = createIconButton("icons/menu/play.png", "play", this);
    play.setBorder(new EmptyBorder(30, 0, 0, 0));
    play.setOpaque(false);
    play.setAlignmentX(Component.CENTER_ALIGNMENT);
    container.add(play);
    container.setOpaque(false);
    menu.add(container, c);
    menu.setOpaque(false);
    return menu;
  }

  private JPanel initPlayerCountField() {
    JPanel pair = new JPanel();
    JLabel label = new JLabel(createImageIcon("icons/menu/player-count.png"));
    label.setBorder(new EmptyBorder(0, 10, 0, 5));
    /*
    label.setFont(new Font("Open Sans", Font.PLAIN, 36));
    label.setForeground(FORE);
    */
    pair.add(label);
    JTextField field = new JTextField("2", 2);
    field.setFont(new Font("Open Sans", Font.PLAIN, 36));
    field.setForeground(FORE);
    field.setOpaque(false);
    field.setHorizontalAlignment(JTextField.CENTER);
    field.setBorder(BorderFactory.createEmptyBorder());
    field.addKeyListener(new KeyListener() {
      public void keyTyped(KeyEvent e) {}

      public void keyPressed(KeyEvent e) {}

      public void keyReleased(KeyEvent e) {
        int value = getPlayerCountFieldValue();

        if (value == -1) {
          play.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          play.setIcon(createImageIcon("icons/menu/play-disabled.png"));
        } else {
          play.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          play.setIcon(createImageIcon("icons/menu/play.png"));
        }
      }
    });
    playerCountField = field;
    pair.add(field);
    pair.setBackground(FORWARD_BACK);
    pair.setAlignmentX(Component.CENTER_ALIGNMENT);
    return pair;
  }
  private static JPanel initBoardContainer() {
    // GridBagLayout vertically and horizontally centres its children by default
    JPanel boardContainer = new JPanel(new GridBagLayout());
    JPanel board = createBoard();
    // Here, we'll do some calculations to figure out
    // how big to make the board
    board.setPreferredSize(new Dimension(480, 480));
    // This will do for now
    boardContainer.add(board);
    boardContainer.setOpaque(false);
    return boardContainer;
  }

  private static JPanel createBoard() {
    board.removeAll();
    board.setLayout(new GridLayout(height, width));

    // We must iterate in this order because visually,
    // the components are added to a GridLayout left-to-right, top-to-bottom
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        JPanel tile = new JPanel();
        tile.setOpaque(true);
        tile.setBorder(BorderFactory.createLineBorder(BOARD_OUTLINE));
        tile.setBackground(BOARD_BACK);
        board.add(tile);
      }
    }

    board.setBackground(BACK);
    return board;
  }

  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();

    if ("play".equals(actionCommand)) {
      int value = getPlayerCountFieldValue();

      if (value == -1) {
        play.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        play.setIcon(createImageIcon("icons/menu/play-disabled.png"));
        return;
      } else {
        play.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        play.setIcon(createImageIcon("icons/menu/play.png"));
        playerCount = value;
      }

      setSidebarMode(true);
      mainLayout.show(main, "boardContainer");
      // Technically speaking, it's pretty inefficient to rebuild the board
      // every time, but I'll leave efficiency for a later date
      createBoard();
    } else if ("home".equals(actionCommand)) {
      /* Something like this to make sure they don't lose everything
      Object[] options = { "Continue", "Cancel" };
      JOptionPane.showOptionDialog(null, "Your progress will not be saved.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
      */
      setSidebarMode(false);
      mainLayout.show(main, "menu");
    }
  }

  private static int getPlayerCountFieldValue() {
    // Returns -1 if the playerCount would be unacceptable
    try {
      int value = Integer.parseInt(playerCountField.getText());

      if (2 <= value && value <= 4) {
        return value;
      } else {
        return -1;
      }
    } catch (Exception e) {
      return -1;
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
