import java.util.*;

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
