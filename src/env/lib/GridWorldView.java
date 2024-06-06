package lib;


import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JFrame;

import city.WorldView;
import jason.stdlib.map.value;


public class GridWorldView extends JFrame {

    private static final long serialVersionUID = 1L;

    protected int cellSizeW = 0;
    protected int cellSizeH = 0;

    protected GridCanvas     drawArea;
    protected GridWorldModel model;

    protected Font defaultFont = new Font("Arial", Font.BOLD, 10);

    public GridWorldView(GridWorldModel model, String title, int windowSize) {
        super(title);
        this.model = model;
        initComponents(windowSize);
        model.setView(this);
    }
    /** sets the size of the frame and adds the components */
    public void initComponents(int width) {
        setSize(width, width);
        getContentPane().setLayout(new BorderLayout());
        drawArea = new GridCanvas();
        getContentPane().add(BorderLayout.CENTER, drawArea);
    }
    @Override
    public void repaint() {
        cellSizeW = drawArea.getWidth() / model.getWidth();
        cellSizeH = drawArea.getHeight() / model.getHeight();
        super.repaint();
        drawArea.repaint();
    }
    /** updates all the frame */
    public void update() {
        repaint();
    }
    /** updates only one position of the grid */
    public void update(int x, int y) {
        Graphics g = drawArea.getGraphics();
        if (g == null) return;
        drawEmpty(g, x, y);
        System.out.println("drawned empty: (" + x + ", " + y + ")");
        draw(g, x, y);
        System.out.println("View updated, drawn something: (" + x + ", " + y + ")");
    }

    // draw the car at x,y (if there is one) 
    public void drawCar(Graphics g, int x, int y, int id) {
        g.setColor(Color.yellow);
        g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        
        System.out.println("Car at: (" + x + ", " + y + ") with id: " + id);
        if (id >= 0) { 
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, String.valueOf(id+1));
        }
    }

    // draw the pedestrian at x,y (if there is one)
    public void drawPedestrian(Graphics g, int x, int y, int id) {
        // Draw the body (blue circle)
        g.setColor(Color.blue);
        g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);

        // Draw the head (yellow circle)
        g.setColor(Color.yellow);
        int headSizeW = (cellSizeW - 4) / 2;
        int headSizeH = (cellSizeH - 4) / 2;
        int headX = x * cellSizeW + 2 + (cellSizeW - 4) / 4;
        int headY = y * cellSizeH + 2 + (cellSizeH - 4) / 4;
        g.fillOval(headX, headY, headSizeW, headSizeH);

        System.out.println("Pedestrian at: (" + x + ", " + y + ") with id: " + id);

        // Draw the ID if it is greater than or equal to 0
        if (id >= 0) { 
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, String.valueOf(id + 1));
        }
    }


    public void drawStreet(Graphics g, int x, int y, int id, String direction) {
        g.setColor(Color.lightGray);
        g.drawRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);

        if (id<0){
            g.setColor(Color.lightGray);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
            
            String text=direction;
            g.setColor(Color.black);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int centerX = x * cellSizeW + (cellSizeW - textWidth) / 2;
            int centerY = y * cellSizeH + (cellSizeH - textHeight) / 2 + fm.getAscent();
            g.drawString(text, centerX, centerY);
            System.out.println("Street at: (" + x + ", " + y + ")" + " with value: " + text);

        }else{
            g.setColor(Color.lightGray);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
            drawCar(g, x, y, id);
        }
    }

    public void drawBuilding(Graphics g, int x, int y) {
        g.setColor(Color.orange);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.lightGray);
        g.drawRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
    } /*TODO: adapt it to be sure than the pedestrians appear ON TOP OF the building blocks
      (cf. street-car way of doing right above)*/

    public void drawString(Graphics g, int x, int y, Font f, String s) {
        g.setFont(f);
        FontMetrics metrics = g.getFontMetrics();
        int width = metrics.stringWidth( s );
        int height = metrics.getHeight();
        g.drawString( s, x*cellSizeW+(cellSizeW/2-width/2), y*cellSizeH+(cellSizeH/2+height/2));
    }

    public void drawEmpty(Graphics g, int x, int y) {
        g.setColor(Color.white);
        g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-1, cellSizeH-1);
        g.setColor(Color.lightGray);
        g.drawRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
    }


    /** method to draw unknown object, probably overridden by the user viewer class */
    public void draw(Graphics g, int x, int y, int object) {
        //g.setColor(Color.black);
        //drawString(g,x,y,defaultFont,String.valueOf(object));
    }
    private int limit = 1000;
    private void draw(Graphics g, int x, int y) {
        if ((model.data[x][y] & GridWorldModel.BUILDING) != 0) {
            drawBuilding(g, x, y);
        }

        if ((model.data[x][y] & GridWorldModel.PEDESTRIAN) != 0) {
            drawPedestrian(g, x, y, model.getAgAtPos(x, y));
        }
        
        if ((model.data[x][y] & GridWorldModel.CAR) != 0) {
            drawCar(g, x, y, model.getAgAtPos(x, y));
        }

        int vl = GridWorldModel.STREET*2;
        while (vl < limit) {
            if ((model.data[x][y] & vl) != 0) {
                draw(g, x, y, vl);
            }
            vl *= 2;
        }

    }

    public Canvas getCanvas() {
        return drawArea;
    }

    public GridWorldModel getModel() {
        return model;
    }

    class GridCanvas extends Canvas {

        private static final long serialVersionUID = 1L;
        public void paint(Graphics g) {
            cellSizeW = drawArea.getWidth() / model.getWidth();
            cellSizeH = drawArea.getHeight() / model.getHeight();
            int mwidth = model.getWidth();
            int mheight = model.getHeight();

            g.setColor(Color.lightGray);
            for (int l = 1; l <= mheight; l++) {
                g.drawLine(0, l * cellSizeH, mwidth * cellSizeW, l * cellSizeH);
            }
            for (int c = 1; c <= mwidth; c++) {
                g.drawLine(c * cellSizeW, 0, c * cellSizeW, mheight * cellSizeH);
            }
            for (int x = 0; x < mwidth; x++) {
                for (int y = 0; y < mheight; y++) {
                    draw(g,x,y);
                }
            }
        }
    }
}