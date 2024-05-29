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
        case GridWorldModel.STREET.id:   drawStreetRight(g, x, y);  break;
        }
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        Color idColor = Color.black;
        super.drawAgent(g, x, y, Color.yellow, -1);
        g.setColor(idColor);
        drawString(g, x, y, defaultFont, String.valueOf(id+1));
    }


    public void drawStreetUp(Graphics g, int x, int y) {
        // Set the color of the street
        g.setColor(Color.yellow);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        
        // Set the text
        String text="^";

        // Set color for text
        g.setColor(Color.black);
        
        // Calculate position to center the text
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int centerX = x * cellSizeW + (cellSizeW - textWidth) / 2;
        int centerY = y * cellSizeH + (cellSizeH - textHeight) / 2 + fm.getAscent();
    
        // Draw the text
        g.drawString(text, centerX, centerY);
    }


    public void drawStreetDown(Graphics g, int x, int y) {
        // Set the color of the street
        g.setColor(Color.yellow);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        
        // Set the text
        String text="v";

        // Set color for text
        g.setColor(Color.black);
    
        // Calculate position to center the text
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int centerX = x * cellSizeW + (cellSizeW - textWidth) / 2;
        int centerY = y * cellSizeH + (cellSizeH - textHeight) / 2 + fm.getAscent();
    
        // Draw the text
        g.drawString(text, centerX, centerY);
    }


    public void drawStreetLeft(Graphics g, int x, int y) {
        // Set the color of the street
        g.setColor(Color.yellow);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        
        //Set the text
        String text="<";

        // Set color for text
        g.setColor(Color.black);
    
        // Calculate position to center the text
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int centerX = x * cellSizeW + (cellSizeW - textWidth) / 2;
        int centerY = y * cellSizeH + (cellSizeH - textHeight) / 2 + fm.getAscent();
    
        // Draw the text
        g.drawString("<", centerX, centerY);
    }
    

    public void drawStreetRight(Graphics g, int x, int y) {
        //Set the color of the street
        g.setColor(Color.yellow);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        
        //Set the text
        String text=">";

        // Set color for text
        g.setColor(Color.black);
    
        // Calculate position to center the text
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int centerX = x * cellSizeW + (cellSizeW - textWidth) / 2;
        int centerY = y * cellSizeH + (cellSizeH - textHeight) / 2 + fm.getAscent();
    
        // Draw the text
        g.drawString(">", centerX, centerY);
    }


}
