import java.util.*;

/**
 * Generalization of Reversi/Othello games
 *
 * @author  Ryan Nguyen
 * @version 2018-11-29
 */
public class Othello {
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

  public static void main(String[] args) {
    Othello othello = new Othello(8, 8);
    System.out.println(othello.boardToString());
    othello.place(new int[] {4, 2}, 1);
    System.out.println(othello.boardToString());
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

  /**
   * Places a tile on the board without checks for legality
   *
   * @param pos the position where a tile is to be placed
   * @param attacker the id of the placed tile
   * @return all tiles that would be captured if this move were made
   */
  private ArrayList<int[]> place(int[] pos, int attacker) {
    ArrayList<int[]> captures = getCaptures(pos, attacker);

    if (captures.size() == 0) {
      System.out.println("I disapprove.");
    } else {
      setBoardValue(pos, attacker);

      for (int i = 0; i < captures.size(); i++) {
        int[] capture = captures.get(i);
        setBoardValue(capture, attacker);
      }
    }

    return captures;
  }

  /**
   * Gets all tiles that would be captured
   *
   * @param start the position where a tile would be placed
   * @param attacker the id of the placed tile
   * @return all tiles that would be captured if this move were made
   */
  private ArrayList<int[]> getCaptures(int[] start, int attacker) {
    ArrayList<int[]> captures = new ArrayList<int[]>();

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
      // Walk in that direction
      int dx = dir[0];
      int dy = dir[1];
      int[] pos = {start[0], start[1]};
      System.out.println("Trying " + format(dir));

      // I thought about this for a long time and couldn't
      // come up with a nice stopping position
      // How sad
      while (true) {
        // Try walking in that direction
        pos[0] += dx;
        pos[1] += dy;

        // If it's off the board or empty, this attempt should be discarded
        if (!isInBoard(pos) || getBoardValue(pos) == 0) {
          temp.clear();
          System.out.println("\tPsych!");
          break;
        // If we're back to an attacker tile, we might've found something
        // This is fine even if we just walked one tile over because
        } else if (getBoardValue(pos) == attacker) {
          System.out.println("\tSuccess.");
          break;
        }

        // If we don't clone it, the position we mean to add will get mutated
        // in the next iteration of the while loop
        temp.add(pos.clone());
        System.out.println("\tFound " + format(pos));
      }

      captures.addAll(temp);
    }

    return captures;
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

    return (0 <= x && x < this.width) && (0 <= y && y < this.height);
  }

  /**
   * Gets the tile at a position on the board
   *
   * @param pos a position on the board
   * @return the value of the board at that position
   */
  private int getBoardValue(int[] pos) {
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
}
