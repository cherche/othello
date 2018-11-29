import java.util.*;

public class Othello {
  private int width;
  private int height;
  private int[][] board;

  public static void main(String[] args) {
    Othello othello = new Othello(7, 7);
    // System.out.println(othello.isInBoard(new int[] {8, 0}));
  }

  public Othello(int width, int height) {
    this.width = width;
    this.height = height;
    this.board = new int[width][height];
  }

  private ArrayList<int[]> getCaptured(int attacker, int[] start) {
    ArrayList<int[]> captured = new ArrayList<int[]>();

    return captured;
  }

  private ArrayList<int[]> getAdjacents(int[] pos) {
    ArrayList<int[]> adjacents = new ArrayList<int[]>();
    int x = pos[0];
    int y = pos[1];
    int[][] diffs = {
      {1, 0},
      {-1 ,0},
      {0, 1},
      {0, -1}
    };

    for (int i = 0; i < diffs.length; i++) {
      int[] diff = diffs[i];
      int dx = diff[0];
      int dy = diff[1];
      int[] pos2 = {x + dx, y + dy};

      if (isInBoard(pos2)) {
        adjacents.add(pos2);
      }
    }

    return adjacents;
  }

  private boolean isInBoard(int[] pos) {
    int x = pos[0];
    int y = pos[1];

    return 0 <= x && x < this.width && 0 <= y && y < this.height;
  }
}
