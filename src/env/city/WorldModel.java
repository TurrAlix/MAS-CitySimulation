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

    // -------------------------------------------------------------------- //

    /** Actions **/
    boolean[] move(Move dir, int ag) throws Exception {
        Location l = getAgPos(ag);
        boolean[] result = new boolean[2];
        boolean moved=false;
        boolean pedestrian=false;
        switch (dir) {
            case UP:
                if (isFree(l.x, l.y - 1)) {
                    setCarPos(ag, l.x, l.y - 1);
                    moved=true;
                } else if (inGrid(l.x, l.y-1) && (((data[l.x][l.y-1] & PEDESTRIAN_ADULT) != 0) || ((data[l.x][l.y-1] & PEDESTRIAN_CHILD) != 0))){
                    pedestrian=true;
                }
                break;
            case DOWN:
                if (isFree(l.x, l.y + 1)) {
                    setCarPos(ag, l.x, l.y + 1);
                    moved=true;
                } else if (inGrid(l.x, l.y+1) && (((data[l.x][l.y-1] & PEDESTRIAN_ADULT) != 0) || ((data[l.x][l.y-1] & PEDESTRIAN_CHILD) != 0))){
                    pedestrian=true;
                }
                break;
            case RIGHT:
                if (isFree(l.x + 1, l.y)) {
                    setCarPos(ag, l.x + 1, l.y);
                    moved=true;
                } else if (inGrid(l.x+1, l.y) && (((data[l.x][l.y-1] & PEDESTRIAN_ADULT) != 0) || ((data[l.x][l.y-1] & PEDESTRIAN_CHILD) != 0))){
                    pedestrian=true;
                }
                break;
            case LEFT:
                if (isFree(l.x - 1, l.y)) {
                    setCarPos(ag, l.x - 1, l.y);
                    moved=true;
                } else if (inGrid(l.x-1, l.y) && (((data[l.x][l.y-1] & PEDESTRIAN_ADULT) != 0) || ((data[l.x][l.y-1] & PEDESTRIAN_CHILD) != 0))){
                    pedestrian=true;
                }
                break;
        }
        result[0]=moved;
        result[1]=pedestrian;
        return result;
    }

    //movement of pedestrian agents
    boolean walk(Move dir, int ag) throws Exception {
        Location l = getAgPos(ag);
        boolean moved=false;
        switch (dir) {
            //check if in Grid, if in Building and if in zebra_crossing
            case UP:
                if (isWalkable(l.x, l.y - 1)) { 
                    if (busyZebraCrossing(l.x, l.y - 1)){
                        Thread.sleep(500);
                        moved=walk(dir, ag);
                    }
                    else {
                        System.out.println("SWITCH IN WALK: "+GridWorldModel.getAgType(ag));
                        switch (GridWorldModel.getAgType(ag)) {
                            case PEDESTRIAN_CHILD:
                                setChildPedestrianPos(ag, l.x, l.y - 1);
                                moved=true;
                            break;
                            case PEDESTRIAN_ADULT:
                            setAdultPedestrianPos(ag, l.x, l.y - 1);
                            moved=true;
                            break;
                        }
                    }
                }
                break;
            case DOWN:
                if (isWalkable(l.x, l.y + 1)) {
                    if (busyZebraCrossing(l.x, l.y + 1)){
                        Thread.sleep(500);
                        moved=walk(dir, ag);
                    }
                    else {
                        switch (GridWorldModel.getAgType(ag)) {
                            case PEDESTRIAN_CHILD:
                                setChildPedestrianPos(ag, l.x, l.y + 1);
                                moved=true;
                            break;
                            case PEDESTRIAN_ADULT:
                            setAdultPedestrianPos(ag, l.x, l.y + 1);
                            moved=true;
                            break;
                        }
                    }
                }
                break;
            case RIGHT:
                if (isWalkable(l.x + 1, l.y)) {
                    if (busyZebraCrossing(l.x + 1, l.y)){
                        Thread.sleep(500);
                        moved=walk(dir, ag);
                    }
                    else {
                        switch (GridWorldModel.getAgType(ag)) {
                            case PEDESTRIAN_CHILD:
                                setChildPedestrianPos(ag, l.x + 1, l.y);
                                moved=true;
                            break;
                            case PEDESTRIAN_ADULT:
                            setAdultPedestrianPos(ag, l.x + 1, l.y);
                            moved=true;
                            break;
                        }
                    }
                }
                break;
            case LEFT:
                if (isWalkable(l.x - 1, l.y)) {
                    if (busyZebraCrossing(l.x - 1, l.y)){
                        Thread.sleep(500);
                        moved=walk(dir, ag);
                    }
                    else {
                        switch (GridWorldModel.getAgType(ag)) {
                            case PEDESTRIAN_CHILD:
                                setChildPedestrianPos(ag, l.x - 1, l.y);
                                moved=true;
                            break;
                            case PEDESTRIAN_ADULT:
                            setAdultPedestrianPos(ag, l.x - 1, l.y);
                            moved=true;
                            break;
                        }
                    }
                }
                break;
        }
        return moved;
    }
    
    //movement of the helicopter
    boolean fly(Move dir, int ag) throws Exception {
        Location l = getAgPos(ag);
        boolean moved=false;
        switch (dir) {
            //check if in Grid
            case UP:
                if (inGrid(l.x, l.y - 1)) { 
                    setHelicopterPos(ag, l.x, l.y - 1);
                    moved=true;
                }
                break;
            case DOWN:
                if (inGrid(l.x, l.y + 1)) {
                    setHelicopterPos(ag, l.x, l.y + 1);
                    moved=true;
                }
                break;
            case RIGHT:
                if (inGrid(l.x + 1, l.y)) {
                    setHelicopterPos(ag, l.x + 1, l.y);
                    moved=true;
                }
                break;
            case LEFT:
                if (inGrid(l.x - 1, l.y)) {
                    setHelicopterPos(ag, l.x - 1, l.y);
                    moved=true;
                }
                break;
        }
        return moved;
    }


    // -------------------------------------------------------------------- //


    /** Maps **/
   
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
        WorldModel model = WorldModel.create(w, h, 4);
        model.setId("Scenario 3");
        // Cars
        model.setCarPos(0, 1, 5);
        model.setCarPos(1, 0, 6);

        // Pedestrians
        model.setAdultPedestrianPos(2,0,0);
        model.setAdultPedestrianPos(3,11,11);
        // Buildings
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                model.add(WorldModel.BUILDING, x, y);
            }
        }
        // Streets in the middle
        for (int x = 0; x < w; x++) {
            model.remove(WorldModel.BUILDING, x, 5);
            model.add(WorldModel.STREET_LEFT, x, 5);
            model.remove(WorldModel.BUILDING, x, 6);
            model.add(WorldModel.STREET_RIGHT, x, 6);    
        }
        model.remove(WorldModel.STREET_LEFT, 5, 5);
        model.remove(WorldModel.STREET_RIGHT, 5, 6);

        //zebra_crossing
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

    // Map with different buildings
    static WorldModel world4() throws Exception {
        int w = 12;
        int h = 12;
        WorldModel model = WorldModel.create(w, h, 5);
        model.setId("Scenario 4");
        // Cars
        model.setCarPos(0, 11, 5);
        model.setCarPos(1, 0, 6);
        // Pedestrians:
        // Child
        model.setChildPedestrianPos(2,3,0);
        // Adults
        model.setAdultPedestrianPos(3,11,11);
        //Helicopter
        model.setHelicopterPos(4, 7, 11);
        // Buildings
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                model.add(WorldModel.BUILDING, x, y);
            }
        }
        // Parking helicopter
        model.remove(WorldModel.BUILDING, 7, 11);
        model.add(WorldModel.PARKING_HELICOPTER, 7, 11);
        // supermarket, a school, a park, and an office
        model.add(WorldModel.OFFICE, 0, 0);
        model.setOfficePos(0, 0);
        model.add(WorldModel.SCHOOL, 0, 11);
        model.setSchoolPos(0, 11);
        model.add(WorldModel.PARK, 11, 0);
        model.setParkPos(11, 0);
        model.add(WorldModel.SUPERMARKET, 11, 11);
        model.setSupermarketPos(11, 11);

        // Streets in the middle
        for (int x = 0; x < w; x++) {
            model.remove(WorldModel.BUILDING, x, 5);
            model.add(WorldModel.STREET_LEFT, x, 5);
            model.remove(WorldModel.BUILDING, x, 6);
            model.add(WorldModel.STREET_RIGHT, x, 6);    
        }
        for (int y = 0; y < h; y++) {
            model.remove(WorldModel.BUILDING, 5, y);
            model.add(WorldModel.STREET_DOWN, 5, y);
            model.remove(WorldModel.BUILDING, 6, y);
            model.add(WorldModel.STREET_UP,6, y);
        }
        //zebra_crossing
        model.add(WorldModel.ZEBRA_CROSSING, 3, 5);
        model.add(WorldModel.ZEBRA_CROSSING, 3, 6);
        model.add(WorldModel.ZEBRA_CROSSING, 8, 5);
        model.add(WorldModel.ZEBRA_CROSSING, 8, 6);
        model.add(WorldModel.ZEBRA_CROSSING, 5, 10);
        model.add(WorldModel.ZEBRA_CROSSING, 6, 10);
        
        return model;
    }

    // Map to check the behaviour of cars at zebra-crossings
    static WorldModel world5() throws Exception {
        int w = 4;
        int h = 4;
        WorldModel model = WorldModel.create(w, h, 4);
        model.setId("Scenario 5");
        // Cars
        model.setCarPos(0, 0, 1);
        model.setCarPos(1, 3, 2);
        // Pedestrians
        model.setAdultPedestrianPos(2,1,0);
        //Helicopter
        model.setHelicopterPos(3, 0, 3);

        // Buildings
        model.add(WorldModel.BUILDING, 1, 0);
        model.add(WorldModel.BUILDING, 2, 0);
        model.add(WorldModel.BUILDING, 2, 3);
        model.add(WorldModel.BUILDING, 3, 3);
        

        // Parking helicopter
        model.add(WorldModel.PARKING_HELICOPTER, 0, 3);

        // Streets in the middle
        for (int x = 0; x < w; x++) {
            model.add(WorldModel.STREET_RIGHT, x, 1);
            model.add(WorldModel.STREET_LEFT, x, 2);
        }

        //zebra_crossing
        model.add(WorldModel.ZEBRA_CROSSING, 2, 1);
        model.add(WorldModel.ZEBRA_CROSSING, 2, 2);

        return model;
    }

    static WorldModel world6() throws Exception {
        int w = 12;
        int h = 12;
        WorldModel model = WorldModel.create(w, h, 7);
        model.setId("Scenario 6");
        // Cars
        model.setCarPos(0, 11, 11);
        model.setCarPos(1, 0, 0);
        model.setCarPos(5, 11, 0);
        model.setCarPos(6, 0, 11);
        
        // Pedestrians
        model.setChildPedestrianPos(2,9,7);
        model.setAdultPedestrianPos(3,4 ,8);
        //Helicopter
        model.setHelicopterPos(4, 9, 9);

        // Buildings:
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                model.add(WorldModel.BUILDING, x, y);
            }
        }
        // Parking helicopter
        model.remove(WorldModel.BUILDING, 9, 9);
        model.add(WorldModel.PARKING_HELICOPTER, 9, 9);

        model.add(WorldModel.SCHOOL, 2, 2);
        model.setSchoolPos(2, 2);
        model.add(WorldModel.OFFICE, 4, 9);
        model.setOfficePos(4, 9);
        model.add(WorldModel.PARK, 7, 9);
        model.setParkPos(7, 9);
        model.add(WorldModel.SUPERMARKET, 9, 2);
        model.setSupermarketPos(9, 2);

        // Streets:
        for (int x = 0; x < w; x++) {
            model.remove(WorldModel.BUILDING, x, 0);
            model.remove(WorldModel.BUILDING, x, 1);
            model.remove(WorldModel.BUILDING, x, 6);
            model.remove(WorldModel.BUILDING, x, 10);
            model.remove(WorldModel.BUILDING, x, 11);

            model.add(WorldModel.STREET_LEFT, x, 0);
            model.add(WorldModel.STREET_RIGHT, x, 1);
            model.add(WorldModel.STREET_RIGHT, x, 6);    
            model.add(WorldModel.STREET_LEFT, x, 10);
            model.add(WorldModel.STREET_RIGHT, x, 11); 
        }
        for (int y = 0; y < h; y++) {
            model.remove(WorldModel.BUILDING, 0, y);
            model.remove(WorldModel.BUILDING, 1, y);
            model.remove(WorldModel.BUILDING, 5, y);
            model.remove(WorldModel.BUILDING, 6, y);
            model.remove(WorldModel.BUILDING, 10, y);
            model.remove(WorldModel.BUILDING, 11, y);

            model.add(WorldModel.STREET_DOWN, 0, y);    
            model.add(WorldModel.STREET_UP, 1, y);
            model.add(WorldModel.STREET_DOWN, 5, y);
            model.add(WorldModel.STREET_UP, 6, y);
            model.add(WorldModel.STREET_DOWN, 10, y);
            model.add(WorldModel.STREET_UP, 11, y);
        }

        //Precedence
        model.add(WorldModel.PRECEDENCE_UP, 11, 10);
        model.add(WorldModel.PRECEDENCE_UP, 1, 6);
        model.add(WorldModel.PRECEDENCE_UP, 1, 10);
        model.add(WorldModel.PRECEDENCE_UP, 6, 6);
        model.add(WorldModel.PRECEDENCE_DOWN, 0, 1);
        model.add(WorldModel.PRECEDENCE_DOWN, 0, 6);
        model.add(WorldModel.PRECEDENCE_DOWN, 5, 6);
        model.add(WorldModel.PRECEDENCE_DOWN, 10, 6);
        model.add(WorldModel.PRECEDENCE_DOWN, 10, 1);
        model.add(WorldModel.PRECEDENCE_RIGHT, 5, 1);
        model.add(WorldModel.PRECEDENCE_RIGHT, 6, 1);
        model.add(WorldModel.PRECEDENCE_RIGHT, 1, 1);
        model.add(WorldModel.PRECEDENCE_RIGHT, 6, 11);
        model.add(WorldModel.PRECEDENCE_LEFT, 5, 0);
        model.add(WorldModel.PRECEDENCE_LEFT, 5, 10);
        model.add(WorldModel.PRECEDENCE_LEFT, 6, 10);
        model.add(WorldModel.PRECEDENCE_LEFT, 10, 10);


        //zebra_crossing
        model.add(WorldModel.ZEBRA_CROSSING, 3, 6);
        model.add(WorldModel.ZEBRA_CROSSING, 8, 6);
        model.add(WorldModel.ZEBRA_CROSSING, 5, 4);
        model.add(WorldModel.ZEBRA_CROSSING, 6, 4);

        //refinements
        model.remove(WorldModel.STREET_UP, 11, 0);
        model.remove(WorldModel.STREET_RIGHT, 11, 1);
        model.remove(WorldModel.STREET_RIGHT, 11, 11);
        model.remove(WorldModel.STREET_RIGHT, 11, 6);
        model.remove(WorldModel.STREET_DOWN, 10, 11);
        model.remove(WorldModel.STREET_DOWN, 5, 11);
        model.remove(WorldModel.STREET_LEFT, 0, 10);
        model.remove(WorldModel.STREET_LEFT, 0, 11);
        model.remove(WorldModel.STREET_DOWN,0,11);
        model.remove(WorldModel.STREET_LEFT, 0, 0);
        model.remove(WorldModel.STREET_UP, 1, 0);
        model.remove(WorldModel.STREET_UP, 6, 0);
        
        return model;
    }
    
}