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
  private static Othello othello;
  private static Color FORE = new Color(125, 130, 142);
  private static Color BACK = new Color(50, 54, 62);
  private static Color FORWARD_BACK = new Color(40, 44, 53);
  private static Color BOARD_BACK = new Color(0, 187, 84);
  private static Color BOARD_OUTLINE = BOARD_BACK.darker();
  private static boolean isDone = false;
  private static JFrame frame;
  private static JPanel main;
  private static JLabel turn;
  private static CardLayout mainLayout;
  private static JTextField playerCountField;
  private static JButton play;
  private static ImageIcon[] playIcons = {
    createImageIcon("icons/menu/play.png"),
    createImageIcon("icons/menu/play-disabled.png")
  };
  private static ImageIcon[] indicatorIcons;
  private static ImageIcon[] tileIcons;
  private static JPanel board = new JPanel();
  // Okay, to be honest, this array makes it really hard to change
  // the height and width later. Oh well. Goodbye options.
  private static JLabel[][] tiles = new JLabel[width][height];
  private static JComponent[] boardSidebarComponents;
  // This is a lot more efficient than instantiating
  // a new MouseListener for every single JPanel
  // "But why didn't you just use JButtons instead"?
  // From experience, a ton of JButtons will take quite
  // some time to load in, so this makes loading faster
  // Semantically speaking, it's fine not to use JButtons
  // since the game tiles arne't buttons in the traditional sense
  public static MouseListener mouseListener = new MouseListener() {
    public void mousePressed(MouseEvent e) {
      // If the game is over, nothing should happen when you click
      if (isDone) {
        return;
      }

      // This is actually a bit like event delegation in JavaScript
      // In this case, we actually still need to assign a listener
      // to every element, but it's similar since we msut identify
      // the element that is actually triggering the listener, and
      // there is only one listener
      JComponent pressed = (JComponent) e.getComponent();
      String name = pressed.getName();
      int id = Integer.parseInt(name);
      /*
      int x = id / height;
      int y = id % height;
      System.out.println("(" + x + ", " + y + ")");
      */

      int[] pos = {id / height, id % height};

      if (othello.isValidMove(pos, othello.getTurn() + 1)) {
        State state = othello.makeMove(pos);
        isDone = state.isDone;

        ArrayList<int[]> updates = state.updates;
        frame.setTitle("Othello");

        for (int i = 0; i < updates.size(); i++) {
          int[] update = updates.get(i);
          setTile(update, state.currentTurn);
        }

        if (isDone) {
          frame.setTitle("Othello: It's all over");
          // We don't want to update the turn indicator if the game is over
          return;
        }

        turn.setIcon(indicatorIcons[state.nextTurn]);
      } else {
        frame.setTitle("Othello: That is not a valid move!");
      }
    }

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}
  };
  // Closing the main window should quit the program in its entirety
  public static WindowListener windowListener = new WindowListener() {
    public void windowClosing(WindowEvent e) {
      System.exit(0);
    }

    public void windowClosed(WindowEvent e) {
      System.exit(0);
    }

    public void windowActivated(WindowEvent e) {}

    public void windowDeactivated(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}

    public void windowIconified(WindowEvent e) {}

    public void windowOpened(WindowEvent e) {}
  };

  public static void main(String[] args) {
    initIndicatorIcons();
    initTileIcons();
    JPanel content = new Game();
    // We want to design around a 6:5 viewport
    content.setPreferredSize(new Dimension(600, 500));
    frame = new JFrame("Othello");
    frame.setContentPane(content);
    // Setting the window size directly actually includes the title bar
    // in the height, which means that a window size of (600, 500)
    // would leave maybe (600, 480) for our actual content
    frame.pack();
    frame.setResizable(false);
    // Centres the window on the screen
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.addWindowListener(windowListener);
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
    turn = new JLabel();
    turn.setHorizontalAlignment(JLabel.CENTER);
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

  private static void initIndicatorIcons() {
    int MAX_PLAYER_COUNT = 3;
    indicatorIcons = new ImageIcon[MAX_PLAYER_COUNT];

    for (int i = 0; i < MAX_PLAYER_COUNT; i++) {
      indicatorIcons[i] = createImageIcon("icons/turns/indicators/" + i + ".png");
    }
  }

  private static void initTileIcons() {
    int MAX_PLAYER_COUNT = 3;
    tileIcons = new ImageIcon[MAX_PLAYER_COUNT];

    for (int i = 0; i < MAX_PLAYER_COUNT; i++) {
      tileIcons[i] = createImageIcon("icons/turns/tiles/" + i + ".png");
    }
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
          play.setIcon(playIcons[1]);
        } else {
          play.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          play.setIcon(playIcons[0]);
        }
      }
    });
    playerCountField = field;
    pair.add(field);
    pair.setBackground(FORWARD_BACK);
    pair.setAlignmentX(Component.CENTER_ALIGNMENT);
    pair.setToolTipText("You may have 2 to 4 players.");
    label.setToolTipText("You may have 2 to 4 players.");
    field.setToolTipText("You may have 2 to 4 players.");
    return pair;
  }

  private static JPanel initBoardContainer() {
    // GridBagLayout vertically and horizontally centres its children by default
    JPanel boardContainer = new JPanel(new GridBagLayout());
    JPanel board = createBoard();
    board.setBackground(BACK);
    // Here, we might do some calculations to figure out
    // how big to make the board
    board.setPreferredSize(new Dimension(480, 480));
    // This will do for now
    boardContainer.add(board);
    boardContainer.setOpaque(false);
    return boardContainer;
  }

  private static JPanel createBoard() {
    // We wipe it in case the dimensions of the board change
    board.removeAll();
    board.setLayout(new GridLayout(height, width));

    // We must iterate in this order because visually,
    // the components are added to a GridLayout left-to-right, top-to-bottom
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        JLabel tile = new JLabel();
        tile.setOpaque(true);
        tile.setBorder(BorderFactory.createLineBorder(BOARD_OUTLINE));
        tile.setBackground(BOARD_BACK);
        // This stores the coordinates as a number in base height
        // It's honestly a bit cheaty to use a name to store
        // coordinates of this tile, but it's pretty efficient
        tile.setName(String.valueOf(x * height + y));
        tile.addMouseListener(mouseListener);
        board.add(tile);
        tiles[x][y] = tile;
      }
    }

    return board;
  }

  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();

    if ("home".equals(actionCommand)) {
      /* Something like this to make sure they don't lose everything
      Object[] options = { "Continue", "Cancel" };
      JOptionPane.showOptionDialog(null, "Your progress will not be saved.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
      */
      setSidebarMode(false);
      mainLayout.show(main, "menu");
    } else if ("play".equals(actionCommand)) {
      int value = getPlayerCountFieldValue();

      // We can't start the game if the playerCount isn't valid
      if (value == -1) {
        return;
      }

      playerCount = value;
      isDone = false;
      setSidebarMode(true);
      mainLayout.show(main, "boardContainer");
      // Technically speaking, it's pretty inefficient to rebuild the board
      // every time, but I'll leave efficiency for a later date
      createBoard();
      othello = new Othello(width, height, playerCount);
      // We should basically define an initial board depending
      // on the number of players manually
      // I don't know of a mathematical way to generate symmetrical
      // positions for all possible board sizes and player counts
      int[][] initialBoard;

      // We should have different initial boards for each playerCount
      if (playerCount == 2) {
        initialBoard = new int[][] {
          {3, 3, 1},
          {4, 4, 1},
          {3, 4, 0},
          {4, 3, 0}
        };
      } else if (playerCount == 3) {
        initialBoard = new int[][] {
          {3, 3, 1},
          {4, 4, 1},
          {3, 4, 2},
          {4, 3, 0}
        };
      } else {
        initialBoard = new int[][] {
          {3, 3, 1},
          {4, 4, 1},
          {3, 4, 0},
          {4, 3, 0}
        };
      }

      for (int i = 0; i < initialBoard.length; i++) {
        int[] triplet = initialBoard[i];
        int[] pos = {triplet[0], triplet[1]};
        int val = triplet[2];
        // Since a blank space is 0 in storage (for our purposes)
        othello.setBoardValue(pos, val + 1);
        setTile(pos, val);
      }

      // Reset turn indicator
      turn.setIcon(indicatorIcons[0]);
    }
  }

  private static void setTile(int[] pos, int id) {
    int x = pos[0];
    int y = pos[1];
    JLabel tile = tiles[x][y];

    // ID of -1 represents empty space
    if (id == -1) {
      tile.setIcon(null);
      // Causes JLabel to be resized and repainted
      //tile.revalidate();
      return;
    }

    tile.setIcon(tileIcons[id]);
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
