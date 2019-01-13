import java.util.*;

/**
 * A way to emit data after the user attempts to place a tile
 */
public class State {
  public ArrayList<int[]> updates = null;
  public boolean isDone = false;
  public int currentTurn;
  public int nextTurn;

  public State() {}

  /**
   * Creates a String of all of the variables of the instance
   *
   * @return a String of all the variables of the instance
   */
  public String toString() {
    String string =
      "isDone: " + isDone
      + "\ncurrentTurn: " + currentTurn
      + "\nnextTurn: " + nextTurn;

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
