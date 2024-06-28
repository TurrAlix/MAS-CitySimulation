package city;

import jason.asSyntax.Atom;
import jason.asSyntax.Term;
import lib.GridWorldModel;
import lib.Location;

import java.util.logging.Logger;
import java.util.Random;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.ObsProperty;

public class City extends Artifact {

    private static Logger logger = Logger.getLogger(City.class.getName());

    static WorldModel  model = null;
    static WorldView   view;

    static int     simId    = 6;    // different type of environment
    static int     sleep    = 200;
    static boolean hasGUI   = true;
    int     agId     = -1;

    public enum Move {
        UP, DOWN, RIGHT, LEFT
    };

    @OPERATION
    public void init(int scenario_id, int agId) {
        this.agId = agId;
        initWorld(scenario_id);
    }
    public int getSimId() {
        return simId;
    }
    public void setSleep(int s) {
        sleep = s;
    }

    @OPERATION void up() throws Exception {     move(Move.UP);    }
    @OPERATION void down() throws Exception {   move(Move.DOWN);  }
    @OPERATION void right() throws Exception {  move(Move.RIGHT); }
    @OPERATION void left() throws Exception {   move(Move.LEFT);  }

    
    void move(Move m) throws Exception {
        if (sleep > 0) {
            await_time(sleep);
        }
        boolean success=false;
        boolean pedestrian=false;
        if (WorldModel.getAgType(agId)==WorldModel.CAR){    
            if (WorldModel.getHelicopter()==-1){ //no helicopter instantiated
                boolean[] result = new boolean[2];
                result = model.move(m, agId);
                success = result[0];
                /*pedestrian=true if the movement failed because a pedestrian was standing
                in front of the car (so on a zebra-crossing)*/
                pedestrian = result[1]; 
            }
            else {
                ObsProperty st = getObsProperty("state");
                Random random = new Random();
                int randomNumber = random.nextInt(50);
                if (randomNumber == 0) { //1 chance out of 50 for the car to break down
                    st.updateValue(0, broken_down); //success stays false
                } else {
                    boolean[] result = new boolean[2];
                    result = model.move(m, agId);
                    success = result[0];
                    /*pedestrian=true if the movement failed because a pedestrian was standing
                    in front of the car (so on a zebra-crossing)*/
                    pedestrian = result[1]; 
                }
            }
        } else if ((WorldModel.getAgType(agId)==WorldModel.PEDESTRIAN_CHILD) || (WorldModel.getAgType(agId)==WorldModel.PEDESTRIAN_ADULT)){
            success = model.walk(m, agId);
        } else if (WorldModel.getAgType(agId)==WorldModel.HELICOPTER) {
            success = model.fly(m, agId);
        }
        
        updateAgPercept();
        ObsProperty s = getObsProperty("success");
        ObsProperty ls = getObsProperty("fail");

        if (success) {
            s.updateValue(0, m.toString().toLowerCase()); // Add percept for successful move
        } else {
            ls.updateValue(0, m.toString().toLowerCase()); // Add percept for failed move
            ls.updateValue(1, pedestrian); // Add percept for reason of failed move (goal: know if it is because of a pedestrian (true) or not)
        }
    }


    @OPERATION void change_state() {
        ObsProperty st = getObsProperty("state");
        st.updateValue(0, works); //success stays false       
        updateAgPercept();
    }


    @OPERATION void skip() {
        if (sleep > 0) await_time(sleep);
        updateAgPercept();
    }

    public synchronized void initWorld(int w) {
        simId = w;
        try {
            if (model == null) {
                switch (w) {
                case 2: model = WorldModel.world2(); break;
                case 3: model = WorldModel.world3(); break;
                case 4: model = WorldModel.world4(); break;
                case 5: model = WorldModel.world5(); break;
                case 6: model = WorldModel.world6(); break;
                default:
                    logger.info("Invalid index for the environment!");
                    return;
                }
                if (hasGUI) {
                    view = new WorldView(model);
                    view.setEnv(this);
                }
            }
            // Observable properties of Cartago
            defineObsProperty("gsize", simId, model.getWidth(), model.getHeight());
            defineObsProperty("school", model.getSchoolPos().x, model.getSchoolPos().y);
            defineObsProperty("park", model.getParkPos().x, model.getParkPos().y);
            defineObsProperty("office", model.getOfficePos().x, model.getOfficePos().y);
            defineObsProperty("supermarket", model.getSupermarketPos().x, model.getSupermarketPos().y);

            defineObsProperty("pos", -1, -1);
            defineObsProperty("state", works);
            defineObsProperty("helicopterParkingPos", model.getHelicopterParkingPos().x, model.getHelicopterParkingPos().y);

            defineObsProperty("cellL", -1, -1, -1); //what type of infrastructure in the left cell?
            defineObsProperty("cellR", -1, -1, -1);
            defineObsProperty("cellC", -1, -1, -1);
            defineObsProperty("cellU", -1, -1, -1);
            defineObsProperty("cellD", -1, -1, -1);

            defineObsProperty("whoL", -1, -1, -1, -1); //is there an agent on the left?
            defineObsProperty("whoR", -1, -1, -1, -1);
            defineObsProperty("whoC", -1, -1, -1, -1);
            defineObsProperty("whoU", -1, -1, -1, -1);
            defineObsProperty("whoD", -1, -1, -1, -1);

            defineObsProperty("success", ""); // success in <argument> direction
            defineObsProperty("fail", "", false); // fail in <argument> direction, true instead of false if it is because of a pedestrian


            updateAgPercept();

        } catch (Exception e) {
            logger.warning("Error creating world "+e);
            e.printStackTrace();
        }
    }

    public void endSimulation() {
        defineObsProperty("end_of_simulation", simId, 0);
        if (view != null) view.setVisible(false);
        WorldModel.destroy();
    }

    private void updateAgPercept() {

        ObsProperty school = getObsProperty("school");
        school.updateValue(0, model.getSchoolPos().x);
        school.updateValue(1, model.getSchoolPos().y);

        ObsProperty park = getObsProperty("park"); 
        park.updateValue(0, model.getParkPos().x);
        park.updateValue(1, model.getParkPos().y);

        ObsProperty office = getObsProperty("office");
        office.updateValue(0, model.getOfficePos().x);
        office.updateValue(1, model.getOfficePos().y);

        ObsProperty supermarket = getObsProperty("supermarket"); 
        supermarket.updateValue(0, model.getSupermarketPos().x);
        supermarket.updateValue(1, model.getSupermarketPos().y);
        
        Location l = GridWorldModel.getAgPos(agId);
        ObsProperty p = getObsProperty("pos");
        p.updateValue(0, l.x);
        p.updateValue(1, l.y);

       
        ObsProperty cl = getObsProperty("cellL");
        ObsProperty cr = getObsProperty("cellR");
        ObsProperty cc = getObsProperty("cellC");
        ObsProperty cu = getObsProperty("cellU");
        ObsProperty cd = getObsProperty("cellD");

        ObsProperty whol = getObsProperty("whoL");
        ObsProperty whor = getObsProperty("whoR");
        ObsProperty whoc = getObsProperty("whoC");
        ObsProperty whou = getObsProperty("whoU");
        ObsProperty whod = getObsProperty("whoD"); 
        
        // percepts of the surroundings
        updateAgPercept(l.x, l.y - 1, cu, whou);
        updateAgPercept(l.x, l.y + 1, cd, whod);
        updateAgPercept(l.x, l.y, cc, whoc);
        updateAgPercept(l.x-1, l.y, cl, whol);
        updateAgPercept(l.x+1, l.y, cr, whor);
    }
   
    

    //Term: Logical term, used to represent entities, Atom: indivisible entity in logic programming
    private static Term building = new Atom("building");

    private static Term street_up = new Atom("street_up");
    private static Term street_down = new Atom("street_down");
    private static Term street_right = new Atom("street_right");
    private static Term street_left = new Atom("street_left");
    private static Term street_up_left = new Atom("street_up_left");
    private static Term street_down_left = new Atom("street_down_left");
    private static Term street_up_right = new Atom("street_up_right");
    private static Term street_down_right = new Atom("street_down_right");

    private static Term agCar = new Atom("agCar");
    private static Term childPedestrian = new Atom("childPedestrian");
    private static Term adultPedestrian = new Atom("adultPedestrian");
    private static Term nobody = new Atom("nobody");
   
    private static Term works = new Atom("works");
    private static Term broken_down = new Atom("broken_down");

    // obs1: infrastructure, obs2: agent
    private void updateAgPercept(int x, int y, ObsProperty obs1, ObsProperty obs2) {
        if (model == null || !model.inGrid(x,y)) {
            return;
        }
        try {
            removeObsPropertyByTemplate(obs1.toString(), null, null, null);
            removeObsPropertyByTemplate(obs2.toString(), null, null, null);
        } catch (IllegalArgumentException e) {}

        obs1.updateValue(0, x);
        obs1.updateValue(1, y);
        obs2.updateValue(0, x);
        obs2.updateValue(1, y);

        if (model.hasObject(WorldModel.BUILDING, x, y)) {
            obs1.updateValue(2, building);
        } 
        if (model.hasObject(WorldModel.STREET_UP, x, y)) {
            if (model.hasObject(WorldModel.STREET_RIGHT, x, y)) {
                obs1.updateValue(2, street_up_right);
            } else if (model.hasObject(WorldModel.STREET_LEFT, x, y)) {
                obs1.updateValue(2, street_up_left);
            } else {
                obs1.updateValue(2, street_up);
            }
        }
        if (model.hasObject(WorldModel.STREET_DOWN, x, y)) {
            if (model.hasObject(WorldModel.STREET_RIGHT, x, y)) {
                obs1.updateValue(2, street_down_right);
            } else if (model.hasObject(WorldModel.STREET_LEFT, x, y)) {
                obs1.updateValue(2, street_down_left);
            } else {
                obs1.updateValue(2, street_down);
            }
        }
        if (model.hasObject(WorldModel.STREET_RIGHT, x, y)) {
            if (model.hasObject(WorldModel.STREET_UP, x, y)) {
                obs1.updateValue(2, street_up_right);
            } else if (model.hasObject(WorldModel.STREET_DOWN, x, y)) {
                obs1.updateValue(2, street_down_right);
            } else {
                obs1.updateValue(2, street_right);
            }
        }
        if (model.hasObject(WorldModel.STREET_LEFT, x, y)) {
            if (model.hasObject(WorldModel.STREET_UP, x, y)) {
                obs1.updateValue(2, street_up_left);
            } else if (model.hasObject(WorldModel.STREET_DOWN, x, y)) {
                obs1.updateValue(2, street_down_left);
            } else {
                obs1.updateValue(2, street_left);
            }
        }
        if (model.hasObject(WorldModel.CAR, x, y)) {
            obs2.updateValue(2, agCar);
            obs2.updateValue(3, WorldModel.getAgNameAtPos(x, y));
        } 
        if (model.hasObject(WorldModel.PEDESTRIAN_CHILD, x, y)) {
            obs2.updateValue(2, childPedestrian);
            obs2.updateValue(3, WorldModel.getAgNameAtPos(x, y));
        }
        if (model.hasObject(WorldModel.PEDESTRIAN_ADULT, x, y)) {
            obs2.updateValue(2, adultPedestrian);
            obs2.updateValue(3, WorldModel.getAgNameAtPos(x, y));
        }
        if (!(model.hasObject(WorldModel.CAR, x, y) || model.hasObject(WorldModel.PEDESTRIAN_ADULT, x, y) || model.hasObject(WorldModel.PEDESTRIAN_ADULT, x, y))) {
            obs2.updateValue(2, nobody);
        } 
    }

}