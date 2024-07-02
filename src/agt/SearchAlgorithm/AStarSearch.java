package SearchAlgorithm;

import java.util.*;
import city.WorldModel;

 /**
 * This file implements the A* search algorithm, one of the famous pathfinding algorithm. 
 * The A* efficiently finds the shortest path from a starting location to a target location in a given world model.
 * It's one of the best algorithm because it combines features of uniform-cost search and pure heuristic
 * search to achieve optimal performance. 
 * Indeed its core idea is to evaluate nodes with the cost to reach that node (function g) 
 * and the estimated cost to reach the goal from that node (heuristic function h).
 *  
 *
 * Main methods:
 * - getDirection(int iagx, int iagy, int itox, int itoy): finds the direction to move 
 *   from the start location to the goal location. 
 *   The pedestrian agents in the asl code call this method at each step going toward their goal.
 *
 * Other Classes:
 * - Location: Represents a coordinate in the grid, it provides the calculation of 
 *      - distance: function g for the cost
 *      - the heuristic: function h, the "intelligent" part of the algorithm
 * - Node: Represents a node in the A* search tree, having the cost from start (g), 
 *   heuristic cost to goal (h), and the action leading to the current node.
 * - GridState: Represents the state in the search grid, including methods to generate 
 *   successor states and calculate heuristics.
 *
 * Usage:
 * AStarSearch searchAlgorithm = new AStarSearch(worldModel);
 * String direction = AStarSearch.getDirection(startX, startY, goalX, goalY);
 * It provides a step direction towards the goal or return "skip" if no path is found.
 */
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

        /* Essential part of the searching algorithm, it gives the boost to the algorithm 
        to redirect the agents following this strategy */
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
        
        /* The formula that is behind the A* is the sum of both the function g for the cost 
        and h for the heuristic, so considering both for each node it choose the shortest path */
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

        // It just considers as successors the walkable cells, to delete the "obstacle" along the path
        private void addSuccessor(List<GridState> successors, Location loc, Location to, String op) {
            if (model.isWalkable(loc.x, loc.y)) {
                successors.add(new GridState(loc, to, op));
            }
        }

        // It calls the heuristic function, the core of this algorithm
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


    /**
     * Finds the direction to move from the start location to the goal location using the A* algorithm.
     *
     * @param iagx The x-coordinate of the starting location.
     * @param iagy The y-coordinate of the starting location.
     * @param itox The x-coordinate of the goal location.
     * @param itoy The y-coordinate of the goal location.
     * @return The direction to move ("left", "right", "up", "down") or "skip" if no path is found.
     */
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
