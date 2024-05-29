package lib;

import java.util.Random;


public class GridWorldModel {

    // each different object is represented by having a single bit
    // set (a bit mask is used in the model), so any power of two
    // represents different objects. Other numbers represent combinations
    // of objects which are all located in the same cell of the grid.
    public static final int       CLEAN    = 0;
    public static final int       AGENT    = 2;
    public static final int       BUILDING = 4;



    // Enumeration associating directions with numerical values
    public enum Direction {
    UP(0), DOWN(1), RIGHT(2), LEFT(3);
    
    private final int value;
    
    // Constructor to associate value with constant
    Direction(int value) {
        this.value = value;
    }
    
    // Method to get the associated value
    public int getValue() {
        return value;
    }
    
    // Method to get the Direction from an integer value
    public static Direction fromInt(int value) {
        for (Direction direction : Direction.values()) {
            if (direction.getValue() == value) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid direction value: " + value);
    }
}

public static class STREET {
    public static final int id = 6;
    public Direction d;

    // Constructor to associate a street with a direction
    public STREET(int dir) {
        this.d = Direction.fromInt(dir);
    }

     // Method to get the associated value
     public int getValue() {
        return id;
    }

     // Method to get the associated direction
     public Direction getDirection() {
        return d;
    }
}


    protected int                 width, height;
    protected int[][]             data = null;
    protected Location[]          agPos;
    protected GridWorldView       view;

    protected Random              random = new Random();


    protected GridWorldModel(int w, int h, int nbAgs) {
        width  = w;
        height = h;

        // int data
        data = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                data[i][j] = CLEAN;
            }
        }

        agPos = new Location[nbAgs];
        for (int i = 0; i < agPos.length; i++) {
            agPos[i] = new Location(-1, -1);
        }
    }

    public void setView(GridWorldView v) {
        view = v;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNbOfAgs() {
        return agPos.length;
    }

    public boolean inGrid(Location l) {
        return inGrid(l.x, l.y);
    }

    public boolean inGrid(int x, int y) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }

    public boolean hasObject(int obj, Location l) {
        return inGrid(l.x, l.y) && (data[l.x][l.y] & obj) != 0;
    }
    public boolean hasObject(int obj, int x, int y) {
        return inGrid(x, y) && (data[x][y] & obj) != 0;
    }

    // gets how many objects of some kind are in the grid
    public int countObjects(int obj) {
        int c = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (hasObject(obj,i,j)) {
                    c++;
                }
            }
        }
        return c;
    }

    public void set(int value, int x, int y) {
        data[x][y] = value;
        if (view != null) view.update(x,y);
    }

    public void add(int value, Location l) {
        add(value, l.x, l.y);
    }

    public void add(int value, int x, int y) {
        data[x][y] |= value;
        if (view != null) view.update(x,y);
    }

    //for streets
    public void add(STREET street, int x, int y) {
        data[x][y] |= street.getValue();
        if (view != null) view.update(x,y);
    }

    public void addWall(int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                add(BUILDING, x, y);
            }
        }
    }

    public void remove(int value, Location l) {
        remove(value, l.x, l.y);
    }

    public void remove(int value, int x, int y) {
        data[x][y] &= ~value;
        if (view != null) view.update(x,y);
    }

    public void setAgPos(int ag, Location l) {
        Location oldLoc = getAgPos(ag);
        if (oldLoc != null) {
            remove(AGENT, oldLoc.x, oldLoc.y);
        }
        agPos[ag] = l;
        add(AGENT, l.x, l.y);
    }

    public void setAgPos(int ag, int x, int y) {
        setAgPos(ag, new Location(x, y));
    }

    public Location getAgPos(int ag) {
        try {
            if (agPos[ag].x == -1)
                return null;
            else
                return (Location)agPos[ag].clone();
        } catch (Exception e) {
            return null;
        }
    }

    /** returns the agent at location l or -1 if there is not one there */
    public int getAgAtPos(Location l) {
        return getAgAtPos(l.x, l.y);
    }

    /** returns the agent at x,y or -1 if there is not one there */
    public int getAgAtPos(int x, int y) {
        for (int i=0; i<agPos.length; i++) {
            if (agPos[i].x == x && agPos[i].y == y) {
                return i;
            }
        }
        return -1;
    }

    /** returns true if the location l has no building neither agent */
    public boolean isFree(Location l) {
        return isFree(l.x, l.y);
    }

    /** returns true if the location x,y has neither building nor agent */
    public boolean isFree(int x, int y) {
        return inGrid(x, y) && (data[x][y] & BUILDING) == 0 && (data[x][y] & AGENT) == 0;
    }

    /** returns true if the location l has not the object obj */
    public boolean isFree(int obj, Location l) {
        return inGrid(l.x, l.y) && (data[l.x][l.y] & obj) == 0;
    }
    /** returns true if the location x,y has not the object obj */
    public boolean isFree(int obj, int x, int y) {
        return inGrid(x, y) && (data[x][y] & obj) == 0;
    }

    public boolean isFreeOfBuilding(Location l) {
        return isFree(BUILDING, l);
    }
    public boolean isFreeOfBuilding(int x, int y) {
        return isFree(BUILDING, x, y);
    }

    /** returns a random free location using isFree to test the availability of some possible location (it means free of agents and buildings) */
    protected Location getFreePos() {
        for (int i=0; i<(getWidth()*getHeight()*5); i++) {
            int x = random.nextInt(getWidth());
            int y = random.nextInt(getHeight());
            Location l = new Location(x,y);
            if (isFree(l)) {
                return l;
            }
        }
        return null; // not found
    }

    /** returns a random free location using isFree(object) to test the availability of some possible location */
    protected Location getFreePos(int obj) {
        for (int i=0; i<(getWidth()*getHeight()*5); i++) {
            int x = random.nextInt(getWidth());
            int y = random.nextInt(getHeight());
            Location l = new Location(x,y);
            if (isFree(obj,l)) {
                return l;
            }
        }
        return null; // not found
    }
}
