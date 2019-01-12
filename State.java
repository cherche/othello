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

  // Just for debugging
  public String toString() {
    String string =
      "isDone: " + isDone + "\n"
      + "currentTurn: " + currentTurn
      + "nextTurn: " + nextTurn;

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
