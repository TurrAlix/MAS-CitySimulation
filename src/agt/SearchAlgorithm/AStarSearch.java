package SearchAlgorithm;

import java.util.*;
import city.WorldModel;
// import lib.GridWorldModel;

public class AStarSearch {

    private static WorldModel model = WorldModel.get();

    public AStarSearch(WorldModel worldModel) {
        AStarSearch.model = worldModel;
    }    

    static class Location {
        int x, y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int distance(Location other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
        }

        private int heuristic(Location to) {
            if (! model.isWalkable(this.x, this.y)) {
                return Integer.MAX_VALUE;
            }
            return Math.abs(this.x - to.x) + Math.abs(this.y - to.y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x && y == location.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    static class Node {
        Location state;
        Node parent;
        int g;
        int h;
        String action;

        public Node(Location state, Node parent, int g, int h, String action) {
            this.state = state;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.action = action;
        }

        public int getF() {
            return g + h;
        }

        public Location getLocation() {
            return state;
        }
        public int getX() {
            return state.x;
        }
        public int getY() {
            return state.y;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "state=" + state +
                    ", g=" + g +
                    ", h=" + h +
                    ", action='" + action + '\'' +
                    '}';
        }
    }

    static class GridState {
        Location pos;
        Location to;
        String op;

        public GridState(Location pos, Location to, String op) {
            this.pos = pos;
            this.to = to;
            this.op = op;
        }

        public List<GridState> getSuccessors() {
            List<GridState> successors = new ArrayList<>();
            addSuccessor(successors, new Location(pos.x - 1, pos.y), to, "left");
            addSuccessor(successors, new Location(pos.x + 1, pos.y), to, "right");
            addSuccessor(successors, new Location(pos.x, pos.y - 1), to, "up");
            addSuccessor(successors, new Location(pos.x, pos.y + 1), to, "down");
            return successors;
        }

        // just consider as successors the walkable cells
        private void addSuccessor(List<GridState> successors, Location loc, Location to, String op) {
            if (model.isWalkable(loc.x, loc.y)) {
                successors.add(new GridState(loc, to, op));
            }
        }

        public int h() {
            return pos.heuristic(to);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridState gridState = (GridState) o;
            return pos.equals(gridState.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos);
        }

        @Override
        public String toString() {
            return "(" + pos + "-" + op + ")";
        }
    }

    public static String getDirection(int iagx, int iagy, int itox, int itoy) {
        Location startLocation = new Location(iagx, iagy);
        Location goalLocation = new Location(itox, itoy);

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        Map<Location, Integer> closedList = new HashMap<>();

        Node startNode = new Node(startLocation, null, 0, startLocation.distance(goalLocation), null);
        openList.add(startNode);
            
        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            Location currentState = currentNode.state;

            if (currentState.equals(goalLocation)) {
                while (currentNode.parent != null && currentNode.parent.parent != null) {
                    currentNode = currentNode.parent;
                }
                return currentNode.action;
            }

            GridState gridState = new GridState(currentState, goalLocation, currentNode.action);
            for (GridState successor : gridState.getSuccessors()) {
                int tentativeG = currentNode.g + 1;

                if (closedList.containsKey(successor.pos) && tentativeG >= closedList.get(successor.pos)) {
                    continue;
                }
                Node successorNode = new Node(successor.pos, currentNode, tentativeG, successor.h(), successor.op);
                if (!openList.contains(successorNode) || tentativeG < successorNode.g) {
                    openList.add(successorNode);
                }
            }
        }
        return "skip"; 
    }
}
