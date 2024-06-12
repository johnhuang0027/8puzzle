import java.io.*;
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

    static PuzzleState readPuzzleFromFile(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        int[][] tiles = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tiles[i][j] = scanner.nextInt();
            }
        }
        scanner.close();
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

    static class AStarResult {
        int pathCost;
        int searchCost;
        long runtime;
        List<PuzzleState> solutionSteps;
    }

    static AStarResult runAStar(PuzzleState initialState, boolean useH1) {
        long startTime = System.nanoTime();
        AStar solver = new AStar(initialState, useH1);
        long endTime = System.nanoTime();
        int pathCost = solver.getPathCost();
        int searchCost = solver.getSearchCost();
        long runtime = endTime - startTime;
        List<PuzzleState> solutionSteps = solver.getSolutionSteps();

        AStarResult result = new AStarResult();
        result.pathCost = pathCost;
        result.searchCost = searchCost;
        result.runtime = runtime;
        result.solutionSteps = solutionSteps;
        return result;
    }

    static void collectData(int numCases) {
        int[] solutionLengths = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30};
        int numLengths = solutionLengths.length;

        double[][] h1Results = new double[numLengths][3]; //stores result data for h1[pathCost, runtime, searchCost]
        double[][] h2Results = new double[numLengths][3]; //restuls h2
        int[] casesCount = new int[numLengths];

        int validCases = 0;

        while (validCases < numCases) {
            PuzzleState initialState;
            do {
                initialState = generateRandomPuzzle();
            } while (!isSolvable(initialState));

            AStarResult resultH1 = runAStar(initialState, true);
            AStarResult resultH2 = runAStar(initialState, false);

            int pathLength = resultH1.solutionSteps.size() - 1;
            int lengthIndex = pathLength / 2 - 1;
            if (lengthIndex >= 0 && lengthIndex < numLengths) {
                h1Results[lengthIndex][0] += resultH1.pathCost;
                h1Results[lengthIndex][1] += resultH1.runtime;
                h1Results[lengthIndex][2] += resultH1.searchCost;
                h2Results[lengthIndex][0] += resultH2.pathCost;
                h2Results[lengthIndex][1] += resultH2.runtime;
                h2Results[lengthIndex][2] += resultH2.searchCost;
                casesCount[lengthIndex]++;
                validCases++;
            }
        }

        displayResults(solutionLengths, h1Results, h2Results, casesCount);
    }

    static void displayResults(int[] solutionLengths, double[][] h1Results, double[][] h2Results, int[] casesCount) {
        System.out.printf("%-15s %-15s %-20s %-20s %-20s %-20s %-20s %-20s\n", 
                          "Length", "Cases", "h1 Path Cost", "h2 Path Cost", 
                          "h1 Avg Runtime (ms)", "h2 Avg Runtime (ms)", 
                          "h1 Avg Search Cost", "h2 Avg Search Cost");
        for (int i = 0; i < solutionLengths.length; i++) {
            if (casesCount[i] > 0) {
                double avgH1PathCost = h1Results[i][0] / casesCount[i];
                double avgH2PathCost = h2Results[i][0] / casesCount[i];
                double avgH1Runtime = h1Results[i][1] / casesCount[i] / 1_000_000; 
                double avgH2Runtime = h2Results[i][1] / casesCount[i] / 1_000_000; 
                double avgH1SearchCost = h1Results[i][2] / casesCount[i];
                double avgH2SearchCost = h2Results[i][2] / casesCount[i];
                System.out.printf("%-15d %-15d %-20.2f %-20.2f %-20.2f %-20.2f %-20.2f %-20.2f\n", 
                                  solutionLengths[i], casesCount[i], 
                                  avgH1PathCost, avgH2PathCost, 
                                  avgH1Runtime, avgH2Runtime, 
                                  avgH1SearchCost, avgH2SearchCost);
            }
        }
    }

    static void singleTest() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select Input Method:");
        System.out.println("[1] Random");
        System.out.println("[2] File");
        int inputMethod = scanner.nextInt();
        scanner.nextLine(); 

        PuzzleState initialState = null;
        if (inputMethod == 1) {
            //now obsolete since we do not check for depth anymore, just solve and categorize
            System.out.println("Enter Solution Depth (2-20):");
            int depth = scanner.nextInt();
            do {
                initialState = generateRandomPuzzle();
            } while (!isSolvable(initialState));
        } else if (inputMethod == 2) {
            System.out.println("Enter file path:");
            String filePath = scanner.nextLine();
            try {
                initialState = readPuzzleFromFile(filePath);
                if (!isSolvable(initialState)) {
                    System.out.println("Unsolvable puzzle configuration.");
                    return;
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found.");
                return;
            }
        }

        System.out.println("Puzzle:");
        printPuzzle(initialState);

        System.out.println("Select H Function:");
        System.out.println("[1] H1");
        System.out.println("[2] H2");
        int hFunction = scanner.nextInt();

        AStarResult result;
        if (hFunction == 1) {
            result = runAStar(initialState, true);
        } else {
            result = runAStar(initialState, false);
        }

        System.out.println("Solution Found");
        for (int i = 0; i < result.solutionSteps.size(); i++) {
            System.out.println("Step: " + (i + 1));
            printPuzzle(result.solutionSteps.get(i));
        }

        System.out.println("Path Cost: " + result.pathCost);
        System.out.println("Search Cost: " + result.searchCost);
        System.out.println("Time: " + result.runtime / 1_000_000.0 + " ms");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Select:");
            System.out.println("[1] Single Test Puzzle");
            System.out.println("[2] Multi-Test Puzzle");
            System.out.println("[3] Exit");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    singleTest();
                    break;
                case 2:
                    System.out.println("Enter number of test cases:");
                    int numCases = scanner.nextInt();
                    collectData(numCases);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void printPuzzle(PuzzleState state) {
        for (int[] row : state.tiles) {
            for (int tile : row) {
                System.out.print(tile + " ");
            }
            System.out.println();
        }
    }
}
