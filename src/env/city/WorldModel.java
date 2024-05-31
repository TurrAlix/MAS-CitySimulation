package city;

import lib.GridWorldModel;
import lib.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import city.City.Move;

public class WorldModel extends GridWorldModel {
    
    public static final int     DEPOT = 32;
    Location                    depot;
    private String              id = "WorldModel";
    protected static WorldModel model = null;

    synchronized public static WorldModel create(int w, int h, int nbAgs) {
        if (model == null) {
            model = new WorldModel(w, h, nbAgs);
        }
        return model;
    }
    public static WorldModel get() {
        return model;
    }
    public static void destroy() {
        model = null;
    }
    private WorldModel(int w, int h, int nbsAgs) {
        super(w, h, nbsAgs);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String toString() {
        return id;
    }
    public Location getDepot() {
        return depot;
    }
    public void setDepot(int x, int y) {
        depot = new Location(x, y);
        data[x][y] = DEPOT;     
    }

    /** Actions **/
    boolean move(Move dir, int ag) throws Exception {
        Location l = getAgPos(ag);
        switch (dir) {
            case UP:
                if (isFree(l.x, l.y - 1)) {
                    setAgPos(ag, l.x, l.y - 1);
                }
                break;
            case DOWN:
                if (isFree(l.x, l.y + 1)) {
                    setAgPos(ag, l.x, l.y + 1);
                }
                break;
            case RIGHT:
                if (isFree(l.x + 1, l.y)) {
                    setAgPos(ag, l.x + 1, l.y);
                }
                break;
            case LEFT:
                if (isFree(l.x - 1, l.y)) {
                    setAgPos(ag, l.x - 1, l.y);
                }
                break;
        }
        return true;
    }

    
    static WorldModel world1() throws Exception {
        WorldModel model = WorldModel.create(11, 11, 1);
        model.setId("Scenario 1");

        // model.setDepot(10, 10);
        model.setAgPos(0, 0, 0);
        return model;
    }
    
    /** Map with just one street*/
    static WorldModel world2() throws Exception {
        int w = 12;
        int h = 12;
        WorldModel model = WorldModel.create(w, h, 2);
        model.setId("Scenario 2");
        // model.setDepot(10, 10);
        model.setAgPos(0, 0, 5);
        model.setAgPos(1, 11, 6);

        // buildings
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < 5; y++) {
                model.add(WorldModel.BUILDING, x, y);
            }
        }
        for (int x = 0; x < w; x++) {
            for (int y = 7; y < h; y++) {
                model.add(WorldModel.BUILDING, x, y);
            }
        }
        // streets in the middle
        for (int x = 0; x < w; x++) {
            model.add(WorldModel.STREET_RIGHT, x, 5);
        }
        for (int x = 0; x < w; x++) {
            model.add(WorldModel.STREET_LEFT, x, 6);
        }


        return model;
    }

}
