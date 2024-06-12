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

    AStar(PuzzleState startState, boolean useH1) {
        Comparator<Node> comparator = Comparator.comparingInt(Node::f);
        openSet = new PriorityQueue<>(comparator);
        closedSet = new HashSet<>();

        int h = useH1 ? startState.h1() : startState.h2();
        openSet.add(new Node(startState, null, 0, h));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.state.isGoal()) {
                printSolution(current);
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
                }
            }
        }
    }

    void printSolution(Node node) {
        List<PuzzleState> path = new ArrayList<>();
        while (node != null) {
            path.add(node.state);
            node = node.parent;
        }
        Collections.reverse(path);
        for (PuzzleState state : path) {
            printPuzzle(state);
            System.out.println();
        }
    }

    void printPuzzle(PuzzleState state) {
        for (int[] row : state.tiles) {
            for (int tile : row) {
                System.out.print(tile + " ");
            }
            System.out.println();
        }
    }
}