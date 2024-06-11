package city;

import lib.GridWorldModel;
import lib.Location;

import city.City.Move;

public class WorldModel extends GridWorldModel {
    
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

    /** Actions **/
    //movement of car agents
    boolean move(Move dir, int ag) throws Exception {
        Location l = getAgPos(ag);
        boolean moved=false;

        switch (dir) {
            case UP:
                if (isFree(l.x, l.y - 1)) {
                    setCarPos(ag, l.x, l.y - 1);
                    moved=true;
                }
                break;
            case DOWN:
                if (isFree(l.x, l.y + 1)) {
                    setCarPos(ag, l.x, l.y + 1);
                    moved=true;
                }
                break;
            case RIGHT:
                if (isFree(l.x + 1, l.y)) {
                    setCarPos(ag, l.x + 1, l.y);
                    moved=true;
                }
                break;
            case LEFT:
                if (isFree(l.x - 1, l.y)) {
                    setCarPos(ag, l.x - 1, l.y);
                    moved=true;
                }
                break;
        }
        return moved;
    }

    //movement of pedestrian agents
    boolean walk(Move dir, int ag) throws Exception {
        Location l = getAgPos(ag);
        boolean moved=false;

        switch (dir) {
            case UP:
                if (inGrid(l.x, l.y - 1)) {
                    setPedestrianPos(ag, l.x, l.y - 1);
                    moved=true;
                }
                break;
            case DOWN:
                if (inGrid(l.x, l.y + 1)) {
                    setPedestrianPos(ag, l.x, l.y + 1);
                    moved=true;
                }
                break;
            case RIGHT:
                if (inGrid(l.x + 1, l.y)) {
                    setPedestrianPos(ag, l.x + 1, l.y);
                    moved=true;
                }
                break;
            case LEFT:
                if (inGrid(l.x - 1, l.y)) {
                    setPedestrianPos(ag, l.x - 1, l.y);
                    moved=true;
                }
                break;
        }
        return moved;
    }
    
    /** Maps **/
    static WorldModel world1() throws Exception {
        WorldModel model = WorldModel.create(11, 11, 1);
        model.setId("Scenario 1");
        model.setCarPos(0, 0, 0);
        return model;
    }
    
    /** Map with just one street*/
    static WorldModel world2() throws Exception {
        int w = 12;
        int h = 12;
        WorldModel model = WorldModel.create(w, h, 2);
        model.setId("Scenario 2");

        // Agents
        model.setCarPos(0, 0, 5);
        model.setCarPos(1, 0, 6);

        // buildings
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                model.add(WorldModel.BUILDING, x, y);
            }
        }
        // streets in the middle
        for (int x = 0; x < w; x++) {
            model.add(WorldModel.STREET_RIGHT, x, 5);
            model.add(WorldModel.STREET_LEFT, x, 6);
        }
        return model;
    }

    // Map with 2 streets, and so a crossroad
    static WorldModel world3() throws Exception {
        int w = 12;
        int h = 12;
        WorldModel model = WorldModel.create(w, h, 3);
        model.setId("Scenario 3");

        // Cars
        model.setCarPos(0, 0, 5);
        model.setCarPos(1, 0, 6);

        // Pedestrians
        model.setPedestrianPos(2,0,0);

        // Buildings
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                model.add(WorldModel.BUILDING, x, y);
            }
        }
        // Streets in the middle
        for (int x = 0; x < w; x++) {
            model.remove(WorldModel.BUILDING, x, 5);
            model.add(WorldModel.STREET_RIGHT, x, 5);
            model.remove(WorldModel.BUILDING, x, 6);
            model.add(WorldModel.STREET_LEFT, x, 6);
        }
        model.remove(WorldModel.STREET_RIGHT, 5, 5);
        model.remove(WorldModel.STREET_LEFT, 5, 6);

        //zebra_crossing
        // If I remove the street underneat the zebra crossing the visualization is fine!!
        // model.remove(WorldModel.STREET_RIGHT, 3, 5);
        // model.remove(WorldModel.STREET_LEFT, 3, 6);
        model.add(WorldModel.ZEBRA_CROSSING, 3, 5);
        model.add(WorldModel.ZEBRA_CROSSING, 3, 6);

        for (int y = 0; y < h; y++) {
            model.remove(WorldModel.BUILDING, 5, y);
            model.add(WorldModel.STREET_DOWN, 5, y);
            model.remove(WorldModel.BUILDING, 6, y);
            model.add(WorldModel.STREET_UP,6, y);
        }
        model.remove(WorldModel.STREET_UP, 6, 5);
        model.remove(WorldModel.STREET_UP, 6, 6);
        return model;
    }

}