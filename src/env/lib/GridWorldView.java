package lib;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JFrame;

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
        draw(g, x, y);
    }
    public void update(int x, int y, int value) {
        Graphics g = drawArea.getGraphics();
        if (g == null) return;
        drawEmpty(g, x, y);
        draw(g, x, y, value);
    }

    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
            case GridWorldModel.STREET_UP :
                if ((model.data[x][y] & GridWorldModel.STREET_RIGHT) != 0) {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "^>");
                } else if ((model.data[x][y] & GridWorldModel.STREET_LEFT) != 0) {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "<^");
                } else {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "^");
                }
                break;
            case GridWorldModel.STREET_DOWN:
                if ((model.data[x][y] & GridWorldModel.STREET_RIGHT) != 0) {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "v>");
                } else if ((model.data[x][y] & GridWorldModel.STREET_LEFT) != 0) {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "<v");
                } else {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "v");
                }
                break;
            case GridWorldModel.STREET_RIGHT:
                if ((model.data[x][y] & GridWorldModel.STREET_UP) != 0) {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "^>");
                } else if ((model.data[x][y] & GridWorldModel.STREET_DOWN) != 0) {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "v>");
                } else {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), ">");
                }
                break;
            case GridWorldModel.STREET_LEFT:
                if ((model.data[x][y] & GridWorldModel.STREET_UP) != 0) {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "<^");
                } else if ((model.data[x][y] & GridWorldModel.STREET_DOWN) != 0) {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "<v");
                } else {
                    drawStreet(g, x, y, model.getAgAtPos(x,y), "<");
                }
                break;
            case GridWorldModel.SCHOOL:             drawSpecialBuilding(g, x, y, model.getAgAtPos(x,y), "School");   break;
            case GridWorldModel.SUPERMARKET:        drawSpecialBuilding(g, x, y, model.getAgAtPos(x,y), "Market");   break;
            case GridWorldModel.OFFICE:             drawSpecialBuilding(g, x, y, model.getAgAtPos(x,y), "Office");   break;
            case GridWorldModel.PARK:               drawSpecialBuilding(g, x, y, model.getAgAtPos(x,y), "Park");     break;
            case GridWorldModel.BUILDING:           drawBuilding(g, x, y, model.getAgAtPos(x,y));                      break;
            case GridWorldModel.CAR:                drawCar(g, x, y, model.getAgAtPos(x,y));                           break;
            case GridWorldModel.PEDESTRIAN:         drawPedestrian(g, x, y, model.getAgAtPos(x,y));                    break;
            case GridWorldModel.HELICOPTER:         drawHelicopter(g, x, y, model.getAgAtPos(x,y));                    break;
            case GridWorldModel.PARKING_HELICOPTER: drawParkingHelicopter(g, x, y, model.getAgAtPos(x,y));             break;
        }
    }

    /* because of the int used to represent the infrastructures and agents, each of them being the power of two of the previous one!*/
    private int limit = 10000; 
    private void draw(Graphics g, int x, int y) {
        if ((model.data[x][y] & GridWorldModel.BUILDING) != 0) {
            draw(g, x, y, GridWorldModel.BUILDING);
        }

        int vl = 16; //infrastructure (except building) int go from 16 to 2048
        while (vl < limit) {
            if ((model.data[x][y] & vl) != 0) {
                draw(g, x, y, vl);
            }
            vl *= 2;
        }
    }
    public void drawCar(Graphics g, int x, int y, int id) {

        // Background 
        g.setColor(Color.lightGray);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);

        // on zebra crossing 
        if((model.data[x][y] & GridWorldModel.ZEBRA_CROSSING)!=0){
            g.setColor(Color.white);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        }
        
        // Draw the vehicle 
        g.setColor(Color.yellow);
        g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        if (id >= 0) { 
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, String.valueOf(id+1));
        }
    }


    public void drawPedestrian(Graphics g, int x, int y, int id) {

        // Building Background 
        g.setColor(Color.orange);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);

        // on zebra crossing 
        if((model.data[x][y] & GridWorldModel.ZEBRA_CROSSING)!=0){
            // on zebra crossing 
            g.setColor(Color.white);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        }
        
        // Draw the body (blue circle)
        g.setColor(Color.blue);
        g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);

        // child (?)
        if(id==2){
        g.setColor(Color.yellow);
        int headSizeW = (cellSizeW - 4) / 2;
        int headSizeH = (cellSizeH - 4) / 2;
        int headX = x * cellSizeW + 2 + (cellSizeW - 4) / 4;
        int headY = y * cellSizeH + 2 + (cellSizeH - 4) / 4;
        g.fillOval(headX, headY, headSizeW, headSizeH);
        // text
        g.setColor(Color.black);
        drawString(g, x, y, defaultFont, "C");
        }
        // Adult
        if(id==3){
            g.setColor(Color.magenta);
            int headSizeW = (cellSizeW - 4) / 2;
            int headSizeH = (cellSizeH - 4) / 2;
            int headX = x * cellSizeW + 2 + (cellSizeW - 4) / 4;
            int headY = y * cellSizeH + 2 + (cellSizeH - 4) / 4;
            g.fillOval(headX, headY, headSizeW, headSizeH);
            // text
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, "A");
        }       

    }


    public void drawHelicopter(Graphics g, int x, int y, int id) {
        if ((model.data[x][y] & GridWorldModel.BUILDING) != 0){
            g.setColor(Color.ORANGE);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        }
        if ((model.data[x][y] & GridWorldModel.ZEBRA_CROSSING) != 0){
            g.setColor(Color.WHITE);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        }
        if (((model.data[x][y] & GridWorldModel.STREET_UP) != 0) | ((model.data[x][y] & GridWorldModel.STREET_DOWN) != 0) | ((model.data[x][y] & GridWorldModel.STREET_LEFT) != 0) | ((model.data[x][y] & GridWorldModel.STREET_RIGHT) != 0)){
            g.setColor(Color.lightGray);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        }
        if ((model.data[x][y] & GridWorldModel.PARKING_HELICOPTER) != 0){
            g.setColor(Color.darkGray);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        }
        
        // Define the size and position of the rectangle
        int rectWidth = cellSizeW - 8; 
        int rectHeight = cellSizeH - 8;
        int rectX = x * cellSizeW + (cellSizeW - rectWidth) / 2; 
        int rectY = y * cellSizeH + (cellSizeH - rectHeight) / 2; 
        // Draw the helicopter body
        g.setColor(Color.black);
        g.fillRect(rectX, rectY, rectWidth, rectHeight);
        
        g.setColor(Color.white);
        drawString(g, x, y, defaultFont, "SOS");
    }

    public void drawStreet(Graphics g, int x, int y, int id, String direction) {
        g.setColor(Color.lightGray);
        g.drawRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH); //we draw the outline
        if (((model.data[x][y] & GridWorldModel.CAR) == 0) && ((model.data[x][y] & GridWorldModel.PEDESTRIAN) == 0)
           && ((model.data[x][y] & GridWorldModel.HELICOPTER) == 0)) {
            //means no agent has been spotted on the street block (cf. getAgAtPos)
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
        } else { //if there is an agent on the block, we must make sure that it is drawn on top of the street
            g.setColor(Color.lightGray);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
            if ((model.data[x][y] & GridWorldModel.PEDESTRIAN) != 0) { //pedestrians can be on streets too (zebra-crossings)
                drawPedestrian(g, x, y, id);
            }
            if ((model.data[x][y] & GridWorldModel.CAR) != 0) {
                drawCar(g, x, y, id);
            }
            if ((model.data[x][y] & GridWorldModel.HELICOPTER) != 0) {
                g.setColor(Color.lightGray);
                g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
                drawHelicopter(g, x, y, id);
            }
        }
        // zebra crossing
        if ((model.data[x][y] & GridWorldModel.ZEBRA_CROSSING) != 0){
            g.setColor(Color.white);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
            if((model.data[x][y] & GridWorldModel.PEDESTRIAN) != 0){
                // TODO IT DOESN'T ENTER HERE
                g.setColor(Color.white);
                g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
                // draw Pedestrian
                g.setColor(Color.blue);
                g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
                int headSizeW = (cellSizeW - 4) / 2;
                int headSizeH = (cellSizeH - 4) / 2;
                int headX = x * cellSizeW + 2 + (cellSizeW - 4) / 4;
                int headY = y * cellSizeH + 2 + (cellSizeH - 4) / 4;
                g.setColor(Color.yellow);
                g.fillOval(headX, headY, headSizeW, headSizeH);
                if (id >= 0) { 
                    g.setColor(Color.black);
                    drawString(g, x, y, defaultFont, String.valueOf(id + 1));
                }
            }
            if((model.data[x][y] & GridWorldModel.HELICOPTER) != 0){
                /*TODO: how to do so that the helicopter appears on top of the car
                and that the id doesn't take the value of the one of the car?*/
                System.out.println("Helicopter on zebra crossing");
                g.setColor(Color.white);
                g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
                drawHelicopter(g, x, y, id);
            }
            if((model.data[x][y] & GridWorldModel.CAR) != 0){
                g.setColor(Color.white);
                g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
                // Draw the vehicle 
                g.setColor(Color.yellow);
                g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
                if (id >= 0) { 
                    g.setColor(Color.black);
                    drawString(g, x, y, defaultFont, String.valueOf(id+1));
                }
            }
        }        
    }


    public void drawSpecialBuilding(Graphics g, int x, int y, int id, String t) {
        g.setColor(Color.black);
        g.drawRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH); 
        g.setColor(Color.red);   
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);

        if ((model.data[x][y] & GridWorldModel.PEDESTRIAN) == 0){ // no agent on the block            
            String text=t;
            g.setColor(Color.white);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int centerX = x * cellSizeW + (cellSizeW - textWidth) / 2;
            int centerY = y * cellSizeH + (cellSizeH - textHeight) / 2 + fm.getAscent();
            g.drawString(text, centerX, centerY);
        }else{ //if there is an agent on the block, we must make sure that it is drawn on top of the block
            if ((model.data[x][y] & GridWorldModel.PEDESTRIAN) != 0) { //pedestrians can be on streets too (zebra-crossings)
                drawPedestrian(g, x, y, id);
            }
        }
    }
    
    public void drawBuilding(Graphics g, int x, int y, int id) {
        if ((model.data[x][y] & GridWorldModel.PEDESTRIAN) == 0){
            g.setColor(Color.orange);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        }else{
            /*g.setColor(Color.orange);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);*/
            drawPedestrian(g, x, y, id);
        }
    }  
    
    public void drawParkingHelicopter(Graphics g, int x, int y, int id) {
        if((model.data[x][y] & GridWorldModel.HELICOPTER) == 0){
            g.setColor(Color.darkGray);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
            String text="P";
            g.setColor(Color.white);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int centerX = x * cellSizeW + (cellSizeW - textWidth) / 2;
            int centerY = y * cellSizeH + (cellSizeH - textHeight) / 2 + fm.getAscent();
            g.drawString(text, centerX, centerY);
        }else{ //helicopter parked
            g.setColor(Color.darkGray);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
            drawHelicopter(g, x, y, id);
        }
    }


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