package city;

import lib.GridWorldModel;
import lib.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import city.City.Move;

public class WorldModel extends GridWorldModel {
    

    public static final int   DEPOT = 32;
    Location                  depot;
    int                       pedestrianSuccess   = 0;
    private String            id = "WorldModel";
    protected static WorldModel model = null;

    synchronized public static WorldModel create(int w, int h) {
        if (model == null) {
            model = new WorldModel(w, h);
        }
        return model;
    }
    public static WorldModel get() {
        return model;
    }
    public static void destroy() {
        model = null;
    }
    private WorldModel(int w, int h) {
        super(w, h, 1);
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
        WorldModel model = WorldModel.create(21, 21);
        model.setId("Scenario 1");
        model.setDepot(0, 0);
        // for (int i = 0; i < nAgt; i++) {
        //     model.setAgPos(i, 1, 0);
        // }
        model.setAgPos(0, 1, 0);
        return model;
    }
    static WorldModel world2() throws Exception {
        return null;
    }

}
