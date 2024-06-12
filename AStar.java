import java.util.*;

class AStar {
    static class Node {
        PuzzleState state;
        Node parent;
        int g, h;

        Node(PuzzleState state, Node parent, int g, int h) {
            this.state = state;
            this.parent = parent;
            this.g = g;
            this.h = h;
        }

        int f() {
            return g + h;
        }
    }

    PriorityQueue<Node> openSet;
    Set<PuzzleState> closedSet;
    int pathCost; // To store the final path cost
    int searchCost; // To store the total number of nodes generated
    List<PuzzleState> solutionSteps; // To store the solution steps

    AStar(PuzzleState startState, boolean useH1) {
        Comparator<Node> comparator = Comparator.comparingInt(Node::f);
        openSet = new PriorityQueue<>(comparator);
        closedSet = new HashSet<>();
        solutionSteps = new ArrayList<>();
        searchCost = 0;

        int h = useH1 ? startState.h1() : startState.h2();
        openSet.add(new Node(startState, null, 0, h));
        searchCost++;

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.state.isGoal()) {
                pathCost = current.g; // Store the final path cost
                reconstructPath(current);
                return;
            }

            closedSet.add(current.state);

            for (PuzzleState neighbor : current.state.getSuccessors()) {
                if (closedSet.contains(neighbor)) continue;

                int tentativeG = current.g + 1;
                int hValue = useH1 ? neighbor.h1() : neighbor.h2();
                Node neighborNode = new Node(neighbor, current, tentativeG, hValue);

                if (!openSet.contains(neighborNode)) {
                    openSet.add(neighborNode);
                    searchCost++;
                }
            }
        }
    }

    int getPathCost() {
        return pathCost;
    }

    int getSearchCost() {
        return searchCost;
    }

    List<PuzzleState> getSolutionSteps() {
        return solutionSteps;
    }

    private void reconstructPath(Node node) {
        List<PuzzleState> path = new ArrayList<>();
        while (node != null) {
            path.add(node.state);
            node = node.parent;
        }
        Collections.reverse(path);
        solutionSteps.addAll(path);
    }

    void printSolution() {
        for (int i = 0; i < solutionSteps.size(); i++) {
            System.out.println("Step: " + (i + 1));
            printPuzzle(solutionSteps.get(i));
            System.out.println();
        }
    }

    private void printPuzzle(PuzzleState state) {
        for (int[] row : state.tiles) {
            for (int tile : row) {
                System.out.print(tile + " ");
            }
            System.out.println();
        }
    }
}
