import java.util.*;

/**
 * Generalization of Reversi/Othello games
 *
 * @author  Ryan Nguyen
 * @version 2018-11-29
 */
public class Othello {
  /**
   * The turn number
   */
  private int turn = 1;
  /**
   * The number of players
   */
  private int playerCount = 3;
  /**
   * The width of the board
   */
  private int width;
  /**
   * The height of the board
   */
  private int height;
  /**
   * A 2D array of all tiles on the board
   */
  private int[][] board;

  public String boardToString() {
    String string = "";

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        string += board[x][y];
      }

      string += '\n';
    }

    return string;
  }

  public String format(int[] pos) {
    return "(" + pos[0] + ", " + pos[1] + ")";
  }

  public void move(int x, int y) {
    State state = place(new int[] {x, y});
    System.out.println(state.toString());
    System.out.println(boardToString());
  }

  public static void main(String[] args) {
    Othello othello = new Othello(8, 8);
    System.out.println(othello.boardToString());

    while (true) {
      System.out.println("# Player " + othello.getTurn());
      int x = IBIO.inputInt("x: ");
      int y = IBIO.inputInt("y: ");
      othello.move(x, y);
    }
  }

  public Othello(int width, int height) {
    this.width = width;
    this.height = height;
    this.board = new int[width][height];
    // This is just something rough to try to initialize the board
    int cWidth = width / 2 - 1;
    int cHeight = height / 2 - 1;
    board[cWidth][cHeight] = board[cWidth + 1][cHeight + 1] = 1;
    board[cWidth + 1][cHeight] = board[cWidth][cHeight + 1] = 2;
  }

  public Othello(int width, int height, int playerCount) {
    this.playerCount = playerCount;
    this.width = width;
    this.height = height;
    this.board = new int[width][height];
    // This is just something rough to try to initialize the board
    int cWidth = width / 2 - 1;
    int cHeight = height / 2 - 1;
    board[cWidth][cHeight] = board[cWidth + 1][cHeight + 1] = 1;
    board[cWidth + 1][cHeight] = board[cWidth][cHeight + 1] = 2;
  }

  /**
   * Places a tile on the board with a check for legality
   *
   * @param pos the position where a tile is to be placed
   * @return all tiles that would be captured if this move were made
   */
  public State place(int[] pos) {
    State state = new State();

    if (!hasValidMoves()) {
      state.isSkipped = true;
      increaseTurn();
    // Basically, if you didn't capture anything, it won't place the tile
    // Users will have to check for this themselves with the return value
    // Also, you obviously need to place a tile in an empty space
    } else if (isValidMove(pos, turn)) {
      ArrayList<int[]> updates = getCaptures(pos, turn);
      updates.add(pos);

      for (int i = 0; i < updates.size(); i++) {
        int[] update = updates.get(i);
        setBoardValue(update, turn);
      }

      // If the move was valid, the next player is allowed to move
      increaseTurn();

      state.updates = new ArrayList<int[]>(updates);
      state.isValidMove = true;
    }

    // Returning the coordinates of the captured tiles makes
    // rendering a lot easier for the user since they know
    // exactly which tiles must be updated
    return state;
  }

  /**
   * Gets all tiles that would be captured
   *
   * @param start the position where a tile would be placed
   * @param attacker the attacker
   * @return all tiles that would be captured if this move were made
   */
  public ArrayList<int[]> getCaptures(int[] start, int attacker) {
    ArrayList<int[]> captures = new ArrayList<int[]>();

    // If the starting tile is not on the board,
    // the move was clearly invalid and no pieces would be captured
    if (!isInBoard(start)) {
      return captures;
    }

    // We'll use these to search for captured stones
    int[][] directions = {
      {-1, -1},
      {-1, 0},
      {-1, 1},
      {0, -1},
      {0, 1},
      {1, -1},
      {1, 0},
      {1, 1}
    };

    for (int i = 0; i < directions.length; i++) {
      ArrayList<int[]> temp = new ArrayList<int[]>();
      int[] dir = directions[i];
      int dx = dir[0];
      int dy = dir[1];
      // We clone so as not to mutate start for future iterations
      int[] pos = start.clone();
      // System.out.println("Trying " + format(dir));

      // I thought about this for a long time and couldn't
      // come up with a nice stopping position
      // How sad
      while (true) {
        // Walk in that direction
        pos[0] += dx;
        pos[1] += dy;

        // If it's off the board or empty, this attempt should be discarded
        if (!isInBoard(pos) || getBoardValue(pos) == 0) {
          temp.clear();
          // System.out.println("\tPsych!");
          break;
        // If we're back to an attacker tile, we might've found something
        // This is fine even if we just walked one tile over because
        } else if (getBoardValue(pos) == attacker) {
          // System.out.println("\tSuccess.");
          break;
        }

        // If we don't clone it, the position we mean to add will get mutated
        // in the next iteration of the while loop
        temp.add(pos.clone());
        // System.out.println("\tFound " + format(pos));
      }

      // Finally, add everything from the temp list to the captures list
      // regardless of how many items are in it
      // This is okay since, if the tile was off the board of empty,
      // it would have been emptied thereby discarding this attempt
      captures.addAll(temp);
    }

    return captures;
  }

  /**
   * Gets the turn number
   *
   * @return the turn
   */
  public int getTurn() {
    return turn;
  }

  /*
  private ArrayList<int[]> getAdjacents(int[] pos) {
    ArrayList<int[]> adjacents = new ArrayList<int[]>();
    int x = pos[0];
    int y = pos[1];
    int[][] guesses = {
      {x + 1, y},
      {x - 1, y},
      {x, y + 1},
      {x, y - 1}
    };

    for (int i = 0; i < guesses.length; i++) {
      int[] guess = guesses[i];

      if (isInBoard(guess)) {
        adjacents.add(guess);
      }
    }

    return adjacents;
  }
  */

  /**
   * Checks if a position is on the board
   *
   * @param pos a position on the board
   * @return a boolean of whether the position is in the board
   */
  private boolean isInBoard(int[] pos) {
    int x = pos[0];
    int y = pos[1];

    // This is really self-explanatory
    // Brackets are actually unnecessary, but it's easy to read now
    return (0 <= x && x < this.width) && (0 <= y && y < this.height);
  }

  /**
   * Gets the tile at a position on the board
   *
   * @param pos a position on the board
   * @return the value of the board at that position
   */
  public int getBoardValue(int[] pos) {
    int x = pos[0];
    int y = pos[1];

    return board[x][y];
  }

  /**
   * Sets the tile at a position on the board
   *
   * @param pos a position on the board
   * @param val the new value of the board at that position
   */
  private void setBoardValue(int[] pos, int val) {
    int x = pos[0];
    int y = pos[1];

    board[x][y] = val;
  }

  /**
   * Determines whether a move is valid
   *
   * @param pos a position on the board
   * @param attacker the attacker
   * @return whether or not a move is valid
   */
  private boolean isValidMove(int[] pos, int attacker) {
    // Technically, this may be optimized since getCaptures
    // doesn't stop as soon as it finds a valid move
    return getCaptures(pos, attacker).size() > 0;
  }

  /**
   * Determines whether the current player has a valid move
   *
   * @return whether or not there is valid move for the current player
   */
  private boolean hasValidMoves() {
    for (int x = 0; x < board.length; x++) {
      for (int y = 0; y < board[x].length; y++) {
        int[] pos = {x, y};

        if (isValidMove(pos, turn)) {
          return true;
        }
      }
    }

    return false;
  }

  private void increaseTurn() {
    turn = (turn + 1) % playerCount;

    if (turn == 0) {
      turn = playerCount;
    }
  }

  /**
   * A way to emit data after the user attempts to place a tile
   */
  public class State {
    /**
     * A list of coordinates of all changed tiles
     */
    public ArrayList<int[]> updates = null;
    /**
     * Whether or not the attempted move was valid
     */
    public boolean isValidMove = false;
    /**
     * Whether or not the player was skipped since they had not any valid moves
     */
    public boolean isSkipped = false;

    public State() {}

    // Just for debugging
    public String toString() {
      String string =
        "isValidMove: " + isValidMove + "\n"
        + "isSkipped: " + isSkipped;

      if (updates != null) {
        string += "\nupdates";

        for (int i = 0; i < updates.size(); i++) {
          int[] update = updates.get(i);
          string += "\n\t(" + update[0] + ", " + update[1] + ")";
        }
      }

      return string;
    }

  }
}
