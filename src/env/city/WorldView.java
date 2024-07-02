package city;

import lib.GridWorldView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
public class WorldView extends GridWorldView {

    City env = null;
    JSlider   jSpeed;

    public WorldView(WorldModel model) {
        super(model, "City simulation", 600);
        setVisible(true);
        repaint();
    }
    public void setEnv(City env) {
        this.env = env;
    }

    @Override
    public void initComponents(int width) {
        super.initComponents(width);
        JPanel args = new JPanel();
        args.setLayout(new BoxLayout(args, BoxLayout.Y_AXIS));

        jSpeed = new JSlider();
        jSpeed.setMinimum(0);
        jSpeed.setMaximum(2000);
        jSpeed.setValue(1000);
        jSpeed.setPaintTicks(true);
        jSpeed.setPaintLabels(true);
        jSpeed.setMajorTickSpacing(500);
        jSpeed.setMinorTickSpacing(200);
        jSpeed.setInverted(true);
        Hashtable<Integer,Component> labelTable = new Hashtable<Integer,Component>();
        labelTable.put( 0, new JLabel("max") );
        labelTable.put( 1000, new JLabel("speed") );
        labelTable.put( 2000, new JLabel("min") );
        jSpeed.setLabelTable( labelTable );
        JPanel p = new JPanel(new FlowLayout());
        p.setBorder(BorderFactory.createEtchedBorder());
        p.add(jSpeed);
        args.add(p);

        // Events handling, 
        // --> This method adjusts the speed of the agents based on the slider’s value.
        jSpeed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (env != null) {
                    env.setSleep((int)jSpeed.getValue());
                }
            }
        });

        JPanel s = new JPanel(new BorderLayout());
        s.add(BorderLayout.WEST, args);
        getContentPane().add(BorderLayout.SOUTH, s);
    }
}