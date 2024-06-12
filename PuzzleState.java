import java.util.*;

class PuzzleState {
    int[][] tiles;
    int zeroX, zeroY;

    PuzzleState(int[][] tiles) {
        this.tiles = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.tiles[i][j] = tiles[i][j];
                if (tiles[i][j] == 0) {
                    zeroX = i;
                    zeroY = j;
                }
            }
        }
    }

    boolean isGoal() {
        int[][] goal = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        return Arrays.deepEquals(this.tiles, goal);
    }

    int h1() {
        int misplaced = 0;
        int[][] goal = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (this.tiles[i][j] != 0 && this.tiles[i][j] != goal[i][j]) {
                    misplaced++;
                }
            }
        }
        return misplaced;
    }

    int h2() {
        int distance = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tiles[i][j] != 0) {
                    int value = tiles[i][j];
                    int targetX = value / 3;
                    int targetY = value % 3;
                    distance += Math.abs(i - targetX) + Math.abs(j - targetY);
                }
            }
        }
        return distance;
    }

    //returns list of all possible legal moves
    List<PuzzleState> getSuccessors() {
        List<PuzzleState> successors = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] direction : directions) {
            int newX = zeroX + direction[0];
            int newY = zeroY + direction[1];
            if (newX >= 0 && newX < 3 && newY >= 0 && newY < 3) {
                int[][] newTiles = new int[3][3];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        newTiles[i][j] = tiles[i][j];
                    }
                }
                newTiles[zeroX][zeroY] = newTiles[newX][newY];
                newTiles[newX][newY] = 0;
                successors.add(new PuzzleState(newTiles));
            }
        }
        return successors;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PuzzleState that = (PuzzleState) obj;
        return Arrays.deepEquals(tiles, that.tiles);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(tiles);
    }
}