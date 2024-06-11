package lib;

import java.util.Random;


public class GridWorldModel {
    // each different object is represented by having a single bit
    // set (a bit mask is used in the model), so any power of two
    // represents different objects. Other numbers represent combinations
    // of objects which are all located in the same cell of the grid.
    public static final int       CLEAN    = 0;
    public static final int       ZEBRA_CROSSING = 1;
    public static final int       CAR    = 2;
    public static final int       PEDESTRIAN    = 4;
    public static final int       BUILDING = 8;

    public static final int       STREET = 16;
    public static final int       STREET_UP = 32;
    public static final int       STREET_DOWN = 64;
    public static final int       STREET_RIGHT = 128;
    public static final int       STREET_LEFT = 256;

    protected int                 width, height;
    protected int[][]             data = null;
    protected static Location[]          agPos;
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

    @SuppressWarnings("unused")
    public boolean inBuilding(int x, int y) {
        System.out.println("inBuilding: " + ((data[x][y] & BUILDING) != 0) + " x: " + x + " y: " + y);
        return (((data[x][y] & BUILDING) != 0) && inGrid(x, y)) || ((data[x][y] & ZEBRA_CROSSING) != 0);
    }

    public boolean hasObject(int obj, Location l) {
        return inGrid(l.x, l.y) && (data[l.x][l.y] & obj) != 0;
    }
    public boolean hasObject(int obj, int x, int y) {
        return inGrid(x, y) && (data[x][y] & obj) != 0;
    }

    public void set(int value, int x, int y) {
        data[x][y] = value;
        if (view != null){
            view.update(x,y);
            System.out.println("View updated: (" + x + ", " + y + ")");
        }
        System.out.println("Cell added: (" + x + ", " + y + ")" + " with value: " + value);
    }

    public void add(int value, Location l) {
        add(value, l.x, l.y);
    }
    public void add(int value, int x, int y) {
        data[x][y] |= value;
        
        if (view != null){
            view.update(x,y);
            System.out.println("Cell updated: (" + x + ", " + y + ")" + " with value: " + value);
        }
    }
    // from x1,y1 to x2,y2 add all buildings
    public void addBuilding(int x1, int y1, int x2, int y2) {
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
        if (view != null) view.update(x,y);     // put here: agents disappear after moving
        // if (view != null) view.update(x,y, data[x][y]); 
        // TODO CHOOSE WHICH ONE TO USE: we could pass directly in the update the data[x][y] value so later we don't have to do model.data[x][y] & obj
    }

    public void setCarPos(int ag, int x, int y) {
        setCarPos(ag, new Location(x, y));
    }
    public void setCarPos(int ag, Location l) {
        Location oldLoc = getAgPos(ag);
        if (oldLoc != null) { //clear the previous position
            remove(CAR, oldLoc.x, oldLoc.y);
        };
        agPos[ag] = l;
        data[l.x][l.y] |= CAR;
        if (view != null) view.update(l.x, l.y, CAR);         
    }

    public void setPedestrianPos(int ag, int x, int y) {
        setPedestrianPos(ag, new Location(x, y));
    }
    public void setPedestrianPos(int ag, Location l) {
        Location oldLoc = getAgPos(ag);
        if (oldLoc != null) { //clear the previous position
            remove(PEDESTRIAN, oldLoc.x, oldLoc.y);
        };
        agPos[ag] = l;
        data[l.x][l.y] |= PEDESTRIAN;
        if (view != null) view.update(l.x, l.y, PEDESTRIAN); 
    }
    
    /** returns the agent at location l or -1 if there is not one there */
    public int getAgAtPos(Location l) {
        return getAgAtPos(l.x, l.y);
    }
    public static Location getAgPos(int ag) {
        try {
            if (agPos[ag].x == -1)
                return null;
            else
                return (Location)agPos[ag].clone();
        } catch (Exception e) {
            return null;
        }
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

    /**returns the types contained in a block at a specific position */
    public int getBlockTypeAtPos(Location l) {
        return getBlockTypeAtPos(l.x, l.y);
    }
    public int getBlockTypeAtPos(int x, int y) {
        return data[x][y];
    }

    /** returns true if the location l has no building neither agent */
    public boolean isFree(Location l) {
        return isFree(l.x, l.y);
    }
    /** returns true if the location x,y has neither building nor agent */
    public boolean isFree(int x, int y) {
        return inGrid(x, y) && (data[x][y] & BUILDING) == 0 && (data[x][y] & CAR) == 0;
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
