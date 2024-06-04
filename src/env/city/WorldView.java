package city;

import lib.GridWorldModel;
import lib.GridWorldView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;


@SuppressWarnings("serial")
public class WorldView extends GridWorldView {

    City env = null;

    public WorldView(WorldModel model) {
        super(model, "City simulation", 600);
        setVisible(true);
        repaint();
    }

    public void setEnv(City env) {
        this.env = env;
    }

    JSlider   jSpeed;

    @Override
    public void initComponents(int width) {
        super.initComponents(width);
        JPanel args = new JPanel();
        args.setLayout(new BoxLayout(args, BoxLayout.Y_AXIS));

        jSpeed = new JSlider();
        jSpeed.setMinimum(0);
        jSpeed.setMaximum(400);
        jSpeed.setValue(50);
        jSpeed.setPaintTicks(true);
        jSpeed.setPaintLabels(true);
        jSpeed.setMajorTickSpacing(100);
        jSpeed.setMinorTickSpacing(20);
        jSpeed.setInverted(true);

        Hashtable<Integer,Component> labelTable = new Hashtable<Integer,Component>();
        labelTable.put( 0, new JLabel("max") );
        labelTable.put( 200, new JLabel("speed") );
        labelTable.put( 400, new JLabel("min") );
        jSpeed.setLabelTable( labelTable );

        JPanel p = new JPanel(new FlowLayout());
        p.setBorder(BorderFactory.createEtchedBorder());
        p.add(jSpeed);

        args.add(p);

        JPanel s = new JPanel(new BorderLayout());
        s.add(BorderLayout.WEST, args);
        getContentPane().add(BorderLayout.SOUTH, s);

    }

    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
            case WorldModel.STREET_UP:      drawStreet(g, x, y, model.getAgAtPos(x,y), "^");         break;
            case WorldModel.STREET_DOWN:    drawStreet(g, x, y, model.getAgAtPos(x,y), "v");         break;
            case WorldModel.STREET_RIGHT:   drawStreet(g, x, y, model.getAgAtPos(x,y), ">");         break;
            case WorldModel.STREET_LEFT:    drawStreet(g, x, y, model.getAgAtPos(x,y), "<");         break;
            case WorldModel.BUILDING:       drawBuilding(g, x, y);                                             break;
            case WorldModel.AGENT:          drawAgent(g, x, y, model.getAgAtPos(x,y));                         break;
        }
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, int id) {
        g.setColor(Color.yellow);
        g.drawArc(x, y, id, x, y, id);
        g.fillArc(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4, 0, 360);
        g.setColor(Color.black);
        drawString(g, x, y, defaultFont, String.valueOf(id+1));
    }


    /*public void drawDepot(Graphics g, int x, int y) {
        g.setColor(Color.blue);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.pink);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.drawLine(x * cellSizeW + 2, y * cellSizeH + 2, (x + 1) * cellSizeW - 2, (y + 1) * cellSizeH - 2);
        g.drawLine(x * cellSizeW + 2, (y + 1) * cellSizeH - 2, (x + 1) * cellSizeW - 2, y * cellSizeH + 2);
    }*/

}
