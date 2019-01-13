import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.sampled.*;

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
  private static Font TITLE_FONT = new Font("Gill Sans", Font.PLAIN, 72);
  private static Font INFO_FONT = new Font("Open Sans", Font.PLAIN, 44);
  private static Font NOTIFICATION_FONT = new Font("Open Sans", Font.PLAIN, 24);
  private static Font HEADING_FONT = new Font("Gill Sans", Font.PLAIN, 44);;
  private static Font BODY_FONT = new Font("Open Sans", Font.PLAIN, 16);
  private static Color FORE = new Color(125, 130, 142);
  private static Color BACK = new Color(50, 54, 62);
  private static Color FORWARD_BACK = new Color(40, 44, 53);
  private static Color BOARD_BACK = new Color(0, 187, 84);
  private static Color BOARD_OUTLINE = BOARD_BACK.darker();
  private static int MAX_PLAYER_COUNT = 4;
  private static boolean isDone = false;
  private static JFrame settings;
  private static JFrame instructions;
  private static JFrame frame;
  private static JPanel main;
  private static JLabel indicator;
  private static CardLayout mainLayout;
  private static JTextField playerCountField;
  private static JButton play;
  private static ImageIcon[] playIcons = {
    createImageIcon("icons/menu/play.png"),
    createImageIcon("icons/menu/play-disabled.png")
  };
  private static JLabel[] countLabels;
  private static JPanel countsContainer;
  private static JLabel status;
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

      if (!othello.isValidMove(pos, othello.getTurn() + 1)) {
        playClip("audio/invalid-move.wav");
        status.setText("That is not a valid move.");
        return;
      }
      State state = othello.makeMove(pos);
      isDone = state.isDone;
      updateCountPanels();
      status.setText(" ");
      ArrayList<int[]> updates = state.updates;
      int TILE_PLACED_VARIANTS = 3;
      int clipName = ((int) (Math.random() * TILE_PLACED_VARIANTS));

      for (int i = 0; i < updates.size(); i++) {
        int[] update = updates.get(i);
        setTile(update, state.currentTurn);
      }

      if (isDone) {
        status.setText("The game is finished.");
        playClip("audio/game-over.wav");
        // We don't want to update the turn indicator if the game is over
        return;
      } else {
        playClip("audio/tile-placed/" + clipName + ".wav");
      }

      if (state.nextTurn != (state.currentTurn + 1) % playerCount) {
        status.setText("Turns were skipped.");
      }

      indicator.setIcon(indicatorIcons[state.nextTurn]);
    }

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}
  };

  public static void main(String[] args) {
    initIndicatorIcons();
    initTileIcons();
    JPanel content = new Game();
    content.setPreferredSize(new Dimension(1024, 768));
    frame = new JFrame("Othello");
    frame.setContentPane(content);
    // Setting the window size directly actually includes the title bar
    // in the height, which means that, for example, a window size
    //  of (600, 500) would leave maybe (600, 480) for our actual content
    frame.pack();
    //frame.setResizable(false);
    // Centres the window on the screen
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    initSettings();
    initInstructions();
  }

  private static void initSettings() {
    settings = new JFrame();
    settings.setSize(300, 300);
  }

  // Thank God we don't need to do crazy stuff like this in web development
  // Could you imagine making THIS many containers to display a webpage
  // as we know it? It would be so frustrating.
  private static void initInstructions () {
    JPanel everything = new JPanel(new BorderLayout());
    JLabel heading = new JLabel("Instructions");
    heading.setFont(HEADING_FONT);
    heading.setForeground(FORE);
    heading.setHorizontalAlignment(JLabel.CENTER);
    everything.add(heading, BorderLayout.NORTH);
    JPanel container = new JPanel();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.add(createTextArea("1. Capture tiles by surrounding them on both sides (horizontally, vertically, or diagonally) with your own."));
    container.add(createDiagram("images/1.png"));
    container.add(createTextArea("2. Your turn will be skipped if, and only if, you cannot capture any tiles."));
    container.add(createTextArea("3. Once all players skip in a row, the game is over. Although this will inevitably happen when the board is full, it could occur earlier."));
    container.add(createDiagram("images/3.png"));
    container.add(createTextArea("4. At the end of the game, the player with the most tiles wins."));
    container.add(createDiagram("images/4.png"));
    container.setOpaque(false);
    everything.add(container, BorderLayout.CENTER);
    everything.setBorder(new EmptyBorder(15, 15, 15, 15));
    everything.setOpaque(false);
    JScrollPane scrollPane = new JScrollPane(everything);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    // Just like the JTextField later, the difference between this
    // and setBorder(null) is that null is the OS default (from what I've read)
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    //scrollPane.setBackground(FORWARD_BACK);
    scrollPane.getViewport().setBackground(FORWARD_BACK);
    instructions = new JFrame();
    instructions.setContentPane(scrollPane);
    instructions.setSize(448, 576);
    // The difference between the instructions window
    // and the settings window is that the instructions
    // window should reappear in the same spot that
    // it last was when the user closed it
    instructions.setLocationRelativeTo(null);
    instructions.setResizable(false);
  }

  public static JPanel createDiagram(String path) {
    JPanel panel = new JPanel();
    JLabel label = new JLabel(createImageIcon(path));
    panel.add(label);
    panel.setOpaque(false);
    return panel;
  }

  public static JTextArea createTextArea(String text) {
    JTextArea textArea = new JTextArea(text);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setBorder(new EmptyBorder(12, 0, 4, 0));
    textArea.setForeground(FORE);
    textArea.setFont(BODY_FONT);
    textArea.setOpaque(false);
    return textArea;
  }

  private Game() {
    this.setLayout(new BorderLayout());
    JPanel sidebar = initSidebar();
    this.add(sidebar, BorderLayout.WEST);
    mainLayout = new CardLayout();
    main = new JPanel(mainLayout);
    main.setBackground(BACK);
    JPanel menu = initMenu();
    main.add(menu, "menu");
    // GridLayout() takes row count then column count
    // That results in this weird ordering of (height, width),
    // but it's totally right - just a bit weird
    JPanel boardContainer = initBoardScreen();
    main.add(boardContainer, "boardContainer");
    this.add(main, BorderLayout.CENTER);
  }

  private static JButton createIconButton(String iconURL, String actionCommand, ActionListener actionListener) {
    JButton button = new JButton();
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    button.setIcon(createImageIcon(iconURL));
    button.setActionCommand(actionCommand);
    button.addActionListener(actionListener);
    button.setOpaque(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    return button;
  }

  private static JButton createSidebarButton(String name, String actionCommand, ActionListener actionListener) {
    String iconURL = "icons/sidebar/" + name + ".png";
    JButton button = createIconButton(iconURL, actionCommand, actionListener);
    return button;
  }

  private static void setSidebarMode(boolean isVisible) {
    for (int i = 0; i < boardSidebarComponents.length; i++) {
      boardSidebarComponents[i].setVisible(isVisible);
    }
  }

  private JPanel initSidebar() {
    JPanel sidebar = new JPanel(new BorderLayout());
    sidebar.setBackground(FORWARD_BACK);
    sidebar.setPreferredSize(new Dimension(108, 768));
    JPanel top = new JPanel(new GridLayout(2, 0));
    JButton home = createSidebarButton("home", "home", this);
    top.add(home);
    JButton undo = createSidebarButton("undo", "undo", this);
    top.add(undo);
    top.setOpaque(false);
    top.setPreferredSize(new Dimension(108, 216));
    sidebar.add(top, BorderLayout.NORTH);
    JPanel center = new JPanel(new GridBagLayout());
    indicator = new JLabel();
    indicator.setHorizontalAlignment(JLabel.CENTER);
    indicator.setOpaque(false);
    center.add(indicator);
    center.setOpaque(false);
    sidebar.add(center, BorderLayout.CENTER);
    // Basically, these are the ones that should only be visible
    // when the board is visible
    boardSidebarComponents = new JComponent[] {
      home,
      undo,
      indicator
    };
    setSidebarMode(false);
    JPanel bottom = new JPanel(new GridLayout(2, 0));
    bottom.add(createSidebarButton("instructions", "instructions", this));
    bottom.add(createSidebarButton("settings", "settings", this));
    bottom.setOpaque(false);
    bottom.setPreferredSize(new Dimension(108, 216));
    sidebar.add(bottom, BorderLayout.SOUTH);
    return sidebar;
  }

  private JPanel initMenu() {
    // GridBagLayout centres its children by default
    JPanel menu = new JPanel(new GridBagLayout());
    JPanel container = new JPanel(new BorderLayout());
    JLabel title = new JLabel("Othello");
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    title.setFont(TITLE_FONT);
    title.setForeground(FORE);
    title.setBorder(new EmptyBorder(0, 0, 15, 0));
    container.add(title, BorderLayout.NORTH);
    JPanel config = new JPanel(new GridBagLayout());
    // I just moved this to a new method because it was
    // a huge pain to read right here. This is for readability.
    // Actually, all the init methods are for readability and maintainability.
    // They don't actually generalize anything, unlike the create methods.
    JPanel configField = initPlayerCountField();
    config.add(configField);
    config.setOpaque(false);
    container.add(config, BorderLayout.CENTER);
    play = createIconButton("icons/menu/play.png", "play", this);
    play.setBorder(new EmptyBorder(30, 0, 0, 0));
    play.setOpaque(false);
    play.setAlignmentX(Component.CENTER_ALIGNMENT);
    container.add(play, BorderLayout.SOUTH);
    container.setOpaque(false);
    menu.add(container);
    menu.setOpaque(false);
    return menu;
  }

  private JPanel initPlayerCountField() {
    JPanel pair = new JPanel();
    JLabel label = new JLabel(createImageIcon("icons/menu/player-count.png"));
    label.setBorder(new EmptyBorder(0, 15, 0, 5));
    pair.add(label);
    JTextField field = new JTextField("2", 2);
    field.setFont(INFO_FONT);
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
    // We want the tooltip to appear right away
    ToolTipManager.sharedInstance().setInitialDelay(0);
    label.setToolTipText("You may have 2 to 4 players.");
    return pair;
  }

  private static JPanel initBoardScreen() {
    // GridBagLayout vertically and horizontally centres its children by default
    JPanel boardScreen = new JPanel(new GridBagLayout());
    // That's why we need this additional container
    // We want the components in this panel to stick together,
    // but overall, everything hsould be centred
    JPanel container = new JPanel(new BorderLayout());
    JPanel board = createBoard();
    // Here, we might do some calculations to figure out
    // how big to make the board
    // This will do for now
    board.setPreferredSize(new Dimension(512, 512));
    board.setOpaque(false);
    container.add(board, BorderLayout.CENTER);
    countsContainer = new JPanel();
    countsContainer.setOpaque(false);
    container.add(countsContainer, BorderLayout.NORTH);
    // We want to have another panel so that hiding the status
    // will not "collapse" our GridBagLayout
    JPanel bottom = new JPanel();
    status = new JLabel(" ");
    status.setForeground(FORE);
    status.setFont(NOTIFICATION_FONT);
    bottom.add(status);
    bottom.setOpaque(false);
    container.add(bottom, BorderLayout.SOUTH);
    container.setBorder(new EmptyBorder(0, 15, 5, 15));
    container.setBackground(FORWARD_BACK);
    boardScreen.add(container);
    boardScreen.setOpaque(false);
    return boardScreen;
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
        tile.setHorizontalAlignment(JLabel.CENTER);
        board.add(tile);
        tiles[x][y] = tile;
      }
    }

    return board;
  }

  private static void updateCountsContainer() {
    countsContainer.removeAll();
    countsContainer.setLayout(new GridLayout(0, playerCount));
    countLabels = new JLabel[playerCount];

    for (int i = 0; i < playerCount; i++) {
      JPanel count = new JPanel();
      JLabel label = new JLabel();
      label.setIcon(tileIcons[i]);
      label.setForeground(FORE);
      label.setFont(INFO_FONT);
      count.add(label);
      count.setOpaque(false);
      countsContainer.add(count);
      countLabels[i] = label;
    }
  }

  private static void updateCountPanels() {
    int[] counts = othello.getCounts();

    for (int i = 0; i < playerCount; i++) {
      countLabels[i].setText(String.valueOf(counts[i]));
    }
  }

  private static void initIndicatorIcons() {
    indicatorIcons = new ImageIcon[MAX_PLAYER_COUNT];

    for (int i = 0; i < MAX_PLAYER_COUNT; i++) {
      indicatorIcons[i] = createImageIcon("icons/turns/indicators/" + i + ".png");
    }
  }

  private static void initTileIcons() {
    tileIcons = new ImageIcon[MAX_PLAYER_COUNT];

    for (int i = 0; i < MAX_PLAYER_COUNT; i++) {
      tileIcons[i] = createImageIcon("icons/turns/tiles/" + i + ".png");
    }
  }

  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();

    if ("instructions".equals(actionCommand)) {
      instructions.setVisible(true);
    } else if ("settings".equals(actionCommand)) {
      settings.setLocation(0, 0);
      settings.setVisible(true);
    } else if ("home".equals(actionCommand)) {
      /* Something like this to make sure they don't lose everything
      Object[] options = { "Continue", "Cancel" };
      JOptionPane.showOptionDialog(null, "Your progress will not be saved.", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
      */
      setSidebarMode(false);
      mainLayout.show(main, "menu");
    } else if ("undo".equals(actionCommand)) {
      // If there are no moves to undo ... don't do anything
      if (othello.getLogSize() == 0) {
        return;
      }

      // Technically, we don't need to reenable the game,
      // but I want to since it's nice (especially for debugging)
      isDone = false;
      othello.undo();
      updateCountPanels();
      indicator.setIcon(indicatorIcons[othello.getTurn()]);
      playClip("audio/undo.wav");
      status.setText("The last move was undone.");

      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          int[] pos = new int[] {x, y};
          int value = othello.getBoardValue(pos);
          setTile(pos, value - 1);
        }
      }
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
          {3, 4, 0},
          {4, 3, 0},
          {3, 3, 1},
          {4, 4, 1}
        };
      } else if (playerCount == 3) {
        initialBoard = new int[][] {
          {5, 1, 0}, {6, 2, 0}, {5, 2, 1}, {6, 1, 1},
          {1, 3, 1}, {2, 4, 1}, {2, 3, 2}, {1, 4, 2},
          {4, 5, 2}, {5, 6, 2}, {5, 5, 0}, {4, 6, 0}
        };
      } else {
        initialBoard = new int[][] {
          {2, 1, 0}, {3, 2, 0}, {3, 1, 1}, {2, 2, 1},
          {1, 4, 1}, {2, 5, 1}, {2, 4, 2}, {1, 5, 2},
          {4, 5, 2}, {5, 6, 2}, {5, 5, 3}, {4, 6, 3},
          {5, 2, 3}, {6, 3, 3}, {6, 2, 0}, {5, 3, 0}
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

      // Update counts (and perhaps reset)
      updateCountsContainer();
      updateCountPanels();
      // Reset turn indicator
      indicator.setIcon(indicatorIcons[0]);
      playClip("audio/play.wav");
      // Reset status text
      status.setText(" ");
    }
  }

  private static void setTile(int[] pos, int id) {
    int x = pos[0];
    int y = pos[1];
    JLabel tile = tiles[x][y];

    // ID of -1 represents empty space
    if (id == -1) {
      tile.setIcon(null);
      return;
    }

    tile.setIcon(tileIcons[id]);
  }

  private static int getPlayerCountFieldValue() {
    // Returns -1 if the playerCount would be unacceptable
    try {
      int value = Integer.parseInt(playerCountField.getText());

      if (2 <= value && value <= MAX_PLAYER_COUNT) {
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

  private static void playClip(String path) {

    // We need the try-catch since there is some chance that the path is wrong
    // or that the audio file isn't acceptable
    try {
      java.net.URL url = Game.class.getResource(path);
      AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
      Clip clip = AudioSystem.getClip();
      // Open and play (obviously)
      clip.open(audioIn);
      clip.start();
    } catch (Exception e) {
      System.out.println("Cannot open resource at \"" + path + "\".");
    }
  }
}
