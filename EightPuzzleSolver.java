import java.util.*;

public class EightPuzzleSolver {

    static boolean isSolvable(PuzzleState state) {
        int[] array = new int[9];
        int index = 0;
        for (int[] row : state.tiles) {
            for (int tile : row) {
                array[index++] = tile;
            }
        }

        int inversions = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = i + 1; j < 9; j++) {
                if (array[i] > array[j] && array[j] != 0) {
                    inversions++;
                }
            }
        }
        return inversions % 2 == 0;
    }

    static PuzzleState readPuzzle() {
        Scanner scanner = new Scanner(System.in);
        int[][] tiles = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tiles[i][j] = scanner.nextInt();
            }
        }
        return new PuzzleState(tiles);
    }

    static PuzzleState generateRandomPuzzle() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        int[][] tiles = new int[3][3];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tiles[i][j] = numbers.get(index++);
            }
        }
        return new PuzzleState(tiles);
    }

    public static void main(String[] args) {
        System.out.println("Enter puzzle configuration (3x3):");
        PuzzleState initialState = generateRandomPuzzle();

        if (isSolvable(initialState)) {
            System.out.println("Using heuristic h1 (number of misplaced tiles):");
            new AStar(initialState, true);

            System.out.println("Using heuristic h2 (sum of Manhattan distances):");
            new AStar(initialState, false);
        } else {
            System.out.println("Unsolvable puzzle configuration.");
        }
    }
}
