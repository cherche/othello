import java.util.*;

/**
 * Generalization of Reversi/Othello games
 *
 * @author  Ryan Nguyen
 * @version 2019-01-14
 */
public class Othello {
  /**
   * The turn number
   */
  private int turn = 0;
  /**
   * The number of players
   */
  private int playerCount = 2;
  /**
   * The width of the board
   */
  private int width;
  /**
   * The height of the board
   */
  private int height;
  // It should be noted that although the turn counter
  // goes from 0 to playerCount, the values in the board go from
  // 0 to playerCount + 1, where 0 is an empty space and
  // a value of k represents that belonging to player k - 1
  // This is just because the board is, by default, filled with 0s,
  // so we have to make do
  /**
   * A 2D array of all spcaes on the board
   */
  private int[][] board;
  private ArrayList<Entry> log = new ArrayList<Entry>();

  public Othello(int width, int height) {
    this.width = width;
    this.height = height;
    this.board = new int[width][height];
  }

  public Othello(int width, int height, int playerCount) {
    this.width = width;
    this.height = height;
    this.playerCount = playerCount;
    this.board = new int[width][height];
  }

  /**
   * Reverts the instance to an earlier snapshot
   *
   * @param index the index of the entry in the log
   */
  public void revert(int index) {
    try {
      Entry entry = log.get(index);
      this.turn = entry.turn;
      this.board = entry.board;
      log = new ArrayList<Entry>(log.subList(0, index));
    } catch (Exception e) {

    }
  }

  /**
   * Reverts the instance to the previous snapshot
   */
  public void undo() {
    // Revert to last entry
    revert(log.size() - 1);
  }

  /**
   * Gets the size of the log
   *
   * @return the size of the log
   */
  public int getLogSize() {
    return log.size();
  }

  /**
   * Gets the turn
   *
   * @return the turn number
   */
  public int getTurn() {
    return turn;
  }

  /**
   * Gets the value of the board at a position
   *
   * @param pos the position
   * @return the value at the position
   */
  public int getBoardValue(int[] pos) {
    int x = pos[0];
    int y = pos[1];
    return board[x][y];
  }

  /**
   * Sets the value of the board at a position
   *
   * @param pos the position
   * @param value the value at the position
   */
  public void setBoardValue(int[] pos, int value) {
    int x = pos[0];
    int y = pos[1];
    board[x][y] = value;
  }

  /**
   * Places a tile on the board with a check for legality
   *
   * @param pos the position where a tile is to be placed
   * @return all tiles that would be captured if this move were made
   */
  public State makeMove(int[] pos) {
    // We assume that the move is valid
    log.add(new Entry(turn, getBoardClone()));
    State state = new State();
    int attacker = turn + 1;
    // Returning the coordinates of the captured tiles makes
    // rendering a lot easier for the user since they know
    // exactly which tiles must be updated
    ArrayList<int[]> updates = getCaptures(pos, attacker);
    updates.add(pos);

    // And now, apply the updates
    for (int i = 0; i < updates.size(); i++) {
      int[] update = updates.get(i);
      int x = update[0];
      int y = update[1];
      // Clearly, whenever I make a move, I'm only changing claiming territory
      // As a result, we don't actually need to check what the new board value is
      // It must always be attacker
      board[x][y] = attacker;
    }

    state.updates = updates;
    // The idea is that if we know the current turn and next turn,
    // we know if any turns were skipped, and whether the game is over
    state.currentTurn = turn;
    turn = getNextTurn();
    state.nextTurn = turn;
    // If we cycled through all turns and no one could go,
    // getNextTurn() should have returned -1
    state.isDone = turn == -1;
    return state;
  }

  /**
   * Determines the next turn depending on who has valid moves
   *
   * @return an integer representing the next turn
   */
  private int getNextTurn() {
    int nextTurn = turn;

    // Why do we iterate playerCount times?
    // This looks at all next players AND the current player a second time
    // This way, the game still progresses if everyone's turn
    // (except for the current player's) is skipped
    for (int i = 0; i <= playerCount; i++) {
      nextTurn = (nextTurn + 1) % playerCount;
      int attacker = nextTurn + 1;

      if (getValidMoves(attacker).size() > 0) {
        return nextTurn;
      }
    }

    // Basically, this means that we went through everyone and no one could go
    // Game over.
    return -1;
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
    // Think of them like vectors. {-1, -1} looks for
    // a line of stones that are to the top-left
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
      // come up with a nice stopping condition
      // How sad. At least for now, we just break.
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
   * Checks if a position is on the board
   *
   * @param pos a position on the board
   * @return a boolean of whether the position is in the board
   */
  public boolean isInBoard(int[] pos) {
    int x = pos[0];
    int y = pos[1];

    // This is really self-explanatory
    // Brackets are actually unnecessary, but it's easy to read now
    return (0 <= x && x < this.width) && (0 <= y && y < this.height);
  }

  /**
   * Gets all valid moves for an attacker
   *
   * @param attacker the attacker
   * @return a list of the valid moves
   */
  public ArrayList<int[]> getValidMoves(int attacker) {
    ArrayList<int[]> validMoves = new ArrayList<int[]>();

    for (int x = 0; x < board.length; x++) {
      for (int y = 0; y < board[x].length; y++) {
        int[] pos = {x, y};

        if (isValidMove(pos, attacker)) {
          validMoves.add(pos);
        }
      }
    }

    return validMoves;
  }

  /**
   * Determines whether a move is valid
   *
   * @param pos a position on the board
   * @param attacker the attacker
   * @return whether or not a move is valid
   */
  public boolean isValidMove(int[] pos, int attacker) {
    // Only slightly inefficient since getCaptures()
    // doesn't stop until it gets ALL the captures
    return (getBoardValue(pos) == 0) && (getCaptures(pos, attacker).size() > 0);
  }

  /*
  public static int getMoveIndex(ArrayList<int[]> list, int[] search) {
    for (int i = 0; i < list.size(); i++) {
      int[] pos = list.get(i);

      if (pos[0] == search[0] && pos[1] == search[1]) {
        return i;
      }
    }

    return -1;
  }
  */

  /**
   * Determines the tile counts for each player
   *
   * @return the tile counts for each player
   */
  public int[] getCounts() {
    int[] counts = new int[playerCount];

    // In this case, order of iteration is irrelevant
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        int tile = board[i][j];

        // Basically, count everything
        if (tile == 0) {
          continue;
        } else {
          int index = tile - 1;
          counts[index]++;
        }
      }
    }

    return counts;
  }

  /**
   * Creates a clone of the board so as not to mutate the original
   *
   * @return a clone of the board
   */
  private int[][] getBoardClone() {
    int[][] clone = new int[width][height];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        clone[x][y] = board[x][y];
      }
    }

    return clone;
  }
}
