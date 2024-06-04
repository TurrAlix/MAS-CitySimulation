package city;

import jason.asSyntax.Atom;
import jason.asSyntax.Term;
import lib.Location;

import java.util.logging.Logger;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.ObsProperty;

public class City extends Artifact {

    private static Logger logger = Logger.getLogger(City.class.getName());

    static WorldModel  model = null;
    static WorldView   view;

    static int     simId    = 3;    // different type of environment
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
        if (sleep > 0) await_time(sleep);
        boolean success = model.move(m, agId);
        updateAgPercept();

        if (success) {
            addPercept(m.toString().toLowerCase() + "_successful"); // Add percept for successful move
        } else {
            addPercept(m.toString().toLowerCase() + "_failed"); // Add percept for failed move
        }      
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
                case 1: model = WorldModel.world1(); break;
                case 2: model = WorldModel.world2(); break;
                case 3: model = WorldModel.world3(); break;
                default:
                    logger.info("Invalid index for the enviroment!");
                    return;
                }
                if (hasGUI) {
                    view = new WorldView(model);
                    view.setEnv(this);
                }
            }
            // Observable properties of Cartago
            defineObsProperty("gsize", simId, model.getWidth(), model.getHeight());
            defineObsProperty("pos", -1, -1);

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
        Location l = model.getAgPos(agId);
        ObsProperty p = getObsProperty("pos");
        p.updateValue(0, l.x);
        p.updateValue(1, l.y);

        // what's around (the private function under)
        for (int i=-1;i<1;i++) { //should be i<2 but for now does not work
            for (int j=-1;j<1;j++) { //should be j<2 but for now does not work
                updateAgPercept(l.x + i, l.y + j);
            }
        }
    }

    //Term: Logical term, used to represent entities, Atom: indivisible entity in logic programming
    private static Term building = new Atom("building");
    private static Term street_up = new Atom("street_up");
    private static Term street_down = new Atom("street_down");
    private static Term street_right = new Atom("street_right");
    private static Term street_left = new Atom("street_left");
    private static Term agent = new Atom("agent");


    private void updateAgPercept(int x, int y) {
        if (model == null || !model.inGrid(x,y)) {
            System.out.println("x: " + x + ", y: " + y + " are out of the grid or model is null.");
            return;
        }
        try {
            removeObsPropertyByTemplate("cell", null, null, null); //remove the property that match these arguments
        } catch (IllegalArgumentException e) {}

        if (model.hasObject(WorldModel.BUILDING, x, y)) {
            defineObsProperty("cell", x, y, building);
        } 
        if (model.hasObject(WorldModel.STREET_UP, x, y)) {
            defineObsProperty("cell", x, y, street_up);
        }
        if (model.hasObject(WorldModel.STREET_DOWN, x, y)) {
            defineObsProperty("cell", x, y, street_down);
        }
        if (model.hasObject(WorldModel.STREET_RIGHT, x, y)) {
            defineObsProperty("cell", x, y, street_right);
        }
        if (model.hasObject(WorldModel.STREET_LEFT, x, y)) {
            defineObsProperty("cell", x, y, street_left);
        }
        if (model.hasObject(WorldModel.AGENT, x, y)) {
            defineObsProperty("cell", x, y, agent);
        }        
    }

    
    private void addPercept(String percept) {
        defineObsProperty(percept);
    }

}
