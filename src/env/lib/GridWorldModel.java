package lib;

import java.util.Random;
import jason.asSyntax.Atom;
import jason.asSyntax.Term;


public class GridWorldModel {
    // each different object is represented by having a single bit
    // set (a bit mask is used in the model), so any power of two
    // represents different objects. Other numbers represent combinations
    // of objects which are all located in the same cell of the grid.
    public static final int       CLEAN    = 0;
    public static final int       ZEBRA_CROSSING = 16384;
    public static final int       CAR    = 2;
    public static final int       PEDESTRIAN_CHILD    = 524288;
    public static final int       PEDESTRIAN_ADULT    = 1048576;

    public static final int       BUILDING = 8;
    public static final int       SUPERMARKET = 256;
    public static final int       PARK = 512;
    public static final int       OFFICE = 1024;
    public static final int       SCHOOL = 2048;

    public static final int       STREET_UP = 16;
    public static final int       STREET_DOWN = 32;
    public static final int       STREET_RIGHT = 64;
    public static final int       STREET_LEFT = 128;

    public static final int       PRECEDENCE_UP = 32768;
    public static final int       PRECEDENCE_DOWN = 65536;
    public static final int       PRECEDENCE_RIGHT = 131072;
    public static final int       PRECEDENCE_LEFT = 262144;

    public static final int       PARKING_HELICOPTER = 4096;
    public static final int       HELICOPTER = 8192;

    protected int                 width, height;
    protected int[][]             data = null;
    protected static Location[]   agPos;
    protected static int[]        agTypes;          //contains the types of each agent, ordered by id
    protected static int[]        agCars;           //contains the id of the cars
    protected static int[]        agChildPedestrians;    //contains the id of the child pedestrians
    protected static int[]        agAdultPedestrians;    //contains the id of the adult pedestrians
    protected static Term[]       agNames;          //contains the names of each agent, ordered by id
    protected GridWorldView       view;
    protected Random              random = new Random();
    protected Location            school;
    protected Location            park;
    protected Location            office;
    protected Location            supermarket;

    protected GridWorldModel(int w, int h, int nbAgs) {
        width  = w;
        height = h;
        data = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                data[i][j] = CLEAN;
            }
        }
        agPos = new Location[nbAgs];
        agTypes = new int[nbAgs];
        agCars = new int[nbAgs];
        agChildPedestrians = new int[nbAgs];
        agAdultPedestrians = new int[nbAgs];
        agNames = new Term[nbAgs];
        for (int i = 0; i < agPos.length; i++) {
            agPos[i] = new Location(-1, -1);
            agTypes[i] = -1;
            agCars[i] = -1;
            agChildPedestrians[i] = -1;
            agAdultPedestrians[i] = -1;
            agNames[i] = null;
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
    public static String getNameFromId(int id) {
        return agNames[id].toString();
    }


    public boolean inGrid(Location l) {
        return inGrid(l.x, l.y);
    }
    public boolean inGrid(int x, int y) {
        return y >= 0 && y < height && x >= 0 && x < width;
    }

    public boolean inBuilding(int x, int y) {
        return (inGrid(x, y) && ((data[x][y] & BUILDING) != 0));
    }

    //if the next block is a zebra-crossing (result =/= 0), says
    //to the pedestrian if it is occupied by a car (result = 2) or not (result = 1)
    public boolean busyZebraCrossing(int x, int y) {
        if (inGrid(x,y) && ((data[x][y] & ZEBRA_CROSSING) != 0) && ((data[x][y] & CAR) != 0)) {
            return true; //zebra-crossing occupied by a car at the moment
        }
        else { return false; }
    }



    public boolean hasObject(int obj, Location l) {
        return inGrid(l.x, l.y) && (data[l.x][l.y] & obj) != 0;
    }
    public boolean hasObject(int obj, int x, int y) {
        return inGrid(x, y) && ((data[x][y] & obj) != 0);
    }

    public void set(int value, int x, int y) {
        data[x][y] = value;
        if (view != null){
            view.update(x,y);
        }
    }

    public void add(int value, Location l) {
        add(value, l.x, l.y);
    }
    public void add(int value, int x, int y) {
        data[x][y] |= value;
        if (view != null){
            view.update(x,y);
        }
    }

    public void remove(int value, Location l) {
        remove(value, l.x, l.y);
    }
    public void remove(int value, int x, int y) {
        data[x][y] &= ~value;
        if (view != null) view.update(x,y);     // put here: agents disappear after moving
    }

    // ----------------------------------------------------- //

    public void setSchoolPos(int x, int y) {
        setSchoolPos(new Location(x, y));
    }
    public void setSchoolPos(Location l) {
        school = l;
    }
    public Location getSchoolPos() {
        return school;
    }

    public void setParkPos(int x, int y) {
        setParkPos(new Location(x, y));
    }
    public void setParkPos(Location l) {
        park = l;
    }
    public Location getParkPos() {
        return park;
    }

    public void setOfficePos(int x, int y) {
        setOfficePos(new Location(x, y));
    }
    public void setOfficePos(Location l) {
        office = l;
    }
    public Location getOfficePos() {
        return office;
    }

    public void setSupermarketPos(int x, int y) {
        setSupermarketPos(new Location(x, y));
    }
    public void setSupermarketPos(Location l) {
        supermarket = l;
    }
    public Location getSupermarketPos() {
        return supermarket;
    }

    // ----------------------------------------------------- //


    public void setCarPos(int ag, int x, int y) {
        setCarPos(ag, new Location(x, y));
    }
    public void setCarPos(int ag, Location l) {
        Location oldLoc = getAgPos(ag);
        if (oldLoc != null) { //clear the previous position
            remove(CAR, oldLoc.x, oldLoc.y);
        } else { //first time we instantiate the car since the previous position was null
            int i=0;
            while (agCars[i]!=-1){i++;}
            agCars[i]=ag;
            agNames[ag] = new Atom("car"+(i+1));
            agTypes[ag] = CAR;
        };
        agPos[ag] = l;
        data[l.x][l.y] |= CAR;
        if (view != null) view.update(l.x, l.y, CAR);         
    }

    public void setChildPedestrianPos(int ag, int x, int y) {
        setChildPedestrianPos(ag, new Location(x, y));
    }
    public void setChildPedestrianPos(int ag, Location l) {
        Location oldLoc = getAgPos(ag);
        if (oldLoc != null) { //clear the previous position
            remove(PEDESTRIAN_CHILD, oldLoc.x, oldLoc.y);
        } else { //first time we instantiate the pedestrian since the previous position was null
            int i=0;
            while (agChildPedestrians[i]!=-1){i++;}
            agChildPedestrians[i]=ag;
            agNames[ag] = new Atom("pedestrian_child"+(i+1));
            agTypes[ag] = PEDESTRIAN_CHILD;
        }
        agPos[ag] = l;
        data[l.x][l.y] |= PEDESTRIAN_CHILD;
        if (view != null) view.update(l.x, l.y, PEDESTRIAN_CHILD); 
    }

    public void setAdultPedestrianPos(int ag, int x, int y) {
        setAdultPedestrianPos(ag, new Location(x, y));
    }
    public void setAdultPedestrianPos(int ag, Location l) {
        Location oldLoc = getAgPos(ag);
        if (oldLoc != null) { //clear the previous position
            remove(PEDESTRIAN_ADULT, oldLoc.x, oldLoc.y);
        } else { //first time we instantiate the pedestrian since the previous position was null
            int i=0;
            while (agAdultPedestrians[i]!=-1){i++;}
            agAdultPedestrians[i]=ag;
            agNames[ag] = new Atom("pedestrian_adult"+(i+1));
            agTypes[ag] = PEDESTRIAN_ADULT;
        }
        agPos[ag] = l;
        data[l.x][l.y] |= PEDESTRIAN_ADULT;
        if (view != null) view.update(l.x, l.y, PEDESTRIAN_ADULT); 
    }

    public void setHelicopterPos(int ag, int x, int y) {
        setHelicopterPos(ag, new Location(x, y));
    }
    public void setHelicopterPos(int ag, Location l) {
        Location oldLoc = getAgPos(ag);
        if (oldLoc != null) { //clear the previous position
            remove(HELICOPTER, oldLoc.x, oldLoc.y);
        } else { //first time we instantiate the helicopter since the previous position was null
            agNames[ag] = new Atom("helicopter");
            agTypes[ag] = HELICOPTER;
        };
        agPos[ag] = l;
        data[l.x][l.y] |= HELICOPTER;
        if (view != null) view.update(l.x, l.y, HELICOPTER);         
    }

    // ----------------------------------------------------- //
    
    /** returns the agent at location l or -1 if there is not one there */
    public int getAgAtPos(Location l) {
        return getAgAtPos(l.x, l.y);
    }
    /** returns the agent at x,y or -1 if there is not one there */
    public static int getAgAtPos(int x, int y) {
        for (int i=0; i<agPos.length; i++) {
            if (agPos[i].x == x && agPos[i].y == y) {
                return i;
            }
        }
        return -1;
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

    /** returns the agent name at location l or -1 if there is not one there */
    public Term getAgNameAtPos(Location l) {
        return getAgNameAtPos(l.x, l.y);
    }
    /** returns the agent at x,y or null if there is not one there */
    public static Term getAgNameAtPos(int x, int y) {
        for (int i=0; i<agPos.length; i++) {
            if (agPos[i].x == x && agPos[i].y == y) {
                return agNames[i];
            }
        }
        return null;
    }


    public static int getAgType(int ag) {
        try {
            if (agTypes[ag] == -1)
                return -1;
            else
                return (int)agTypes[ag];
        } catch (Exception e) {
            return -1;
        }
    }

    public static int getHelicopter() {
        int i=0;
        // System.out.println(agTypes.length);
        while (agTypes[i]!=HELICOPTER && i<agTypes.length-1){
            i++;
            // System.out.println(i);
            // System.out.println(agTypes[i]);
        }
        if (i==agTypes.length-1){ 
            return -1; 
        } // no helicopter found
        else { return i; }
    }


    // ----------------------------------------------------- //


    /** returns the localisation of the parking spot */
    public Location getHelicopterParkingPos() {
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if ((data[x][y] & PARKING_HELICOPTER) != 0) {
                    return (new Location(x,y)); 
                }
            }
        }
        return (new Location(-1,-1));
    }


    /**returns the types contained in a block at a specific position */
    /*public int getBlockTypeAtPos(Location l) {
        return getBlockTypeAtPos(l.x, l.y);
    }
    public int getBlockTypeAtPos(int x, int y) {
        return data[x][y];
    }*/

    // ----------------------------------------------------- //

    /** returns true if the location l has no building neither agent */
    public boolean isFree(Location l) {
        return isFree(l.x, l.y);
    }
    /** returns true if the location x,y has neither building nor agent ; used by cars */
    public boolean isFree(int x, int y) {
        return inGrid(x, y) && (data[x][y] & BUILDING) == 0 && (data[x][y] & CAR) == 0 && (data[x][y] & PEDESTRIAN_CHILD) == 0 && (data[x][y] & PEDESTRIAN_ADULT) == 0 && (data[x][y] & PARKING_HELICOPTER) == 0;
    }
    /** returns true if the location l has not the object obj */
    public boolean isFree(int obj, Location l) {
        return inGrid(l.x, l.y) && (data[l.x][l.y] & obj) == 0;
    }
    /** returns true if the location x,y has not the object obj */
    public boolean isFree(int obj, int x, int y) {
        return inGrid(x, y) && (data[x][y] & obj) == 0;
    }

    /** returns true if the location l has no building neither agent */
    public boolean isWalkable(Location l) {
        return isWalkable(l.x, l.y);
    }

    //used in tha AStar algorithm to check which path for pedestrians are valid
    public boolean isWalkable(int x, int y) {
        return (inGrid(x,y) && (inBuilding(x,y) || ((data[x][y] & ZEBRA_CROSSING) != 0)));
    }

    // ----------------------------------------------------- //

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
