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

    /* Sets the size of the frame and adds the main components */
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
    public void update() {
        repaint();
    }
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

    /* Given just a location it looks at what's there in the grid enrivoment and it draws it
     * It exploits the int used to represent the infrastructures and agents, each of them being a power of two */
    private int limit = 1000000; 
    private void draw(Graphics g, int x, int y) {
        if ((model.data[x][y] & GridWorldModel.BUILDING) != 0) {
            draw(g, x, y, GridWorldModel.BUILDING);
        }
        int vl = 2; //infrastructure (except building) int go from 16 to 2048
        while (vl < limit) {
            if ((model.data[x][y] & vl) != 0) {
                draw(g, x, y, vl);
            }
            vl *= 2;
        }
    }

    /* Main function that based on an object and its location it draws it 
     * Arguments: Graphics object, x coordinate, y coordinate, Object in terms of the constant associated
     * Return: nothing it directly draw on the canva
     * 
     * If it's a street it differentiate between the 4 directions and more specifically if it has the precendence, 
     * we decided to draw specific symbols in order to visually see the constraints that the agents have in the enviroment 
    */
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
            case GridWorldModel.STREET_UP :
                if ((model.data[x][y] & GridWorldModel.STREET_RIGHT) != 0) {
                    if (((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) == 0)) {
                        drawStreet(g, x, y, "^>");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) == 0)) {
                        drawStreet(g, x, y, "^->");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) != 0)) {
                        System.out.println("[Warning] Two contradictory precedences have been asserted in block in "+x+","+y+".");
                        drawStreet(g, x, y, "^>");
                    } else {
                        System.out.println("[Warning] No precedence has been asserted to bidirectional block in "+x+","+y+".");
                        drawStreet(g, x, y, "^>");
                    }
                }
                else if ((model.data[x][y] & GridWorldModel.STREET_LEFT) != 0) {
                    if (((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) == 0)) {
                        drawStreet(g, x, y, "^>");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) == 0)) {
                        drawStreet(g, x, y, "<-^");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) != 0)) {
                        System.out.println("[Warning] Two contradictory precedences have been asserted in block in "+x+","+y+".");
                        drawStreet(g, x, y, "<^");
                    } else {
                        System.out.println("[Warning] No precedence has been asserted to bidirectional block in "+x+","+y+".");
                        drawStreet(g, x, y, "<^");
                    }
                }
                else {
                    drawStreet(g, x, y, "^");
                }
                break;
            case GridWorldModel.STREET_DOWN:
                if ((model.data[x][y] & GridWorldModel.STREET_RIGHT) != 0) {
                    if (((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) == 0)) {
                        drawStreet(g, x, y, "V>");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) == 0)) {
                        drawStreet(g, x, y, "v->");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) != 0)) {
                        System.out.println("[Warning] Two contradictory precedences have been asserted in block in "+x+","+y+".");
                        drawStreet(g, x, y, "v>");
                    } else {
                        System.out.println("[Warning] No precedence has been asserted to bidirectional block in "+x+","+y+".");
                        drawStreet(g, x, y, "v>");
                    }
                } else if ((model.data[x][y] & GridWorldModel.STREET_LEFT) != 0) {
                    if (((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) == 0)) {
                        drawStreet(g, x, y, "<V");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) == 0)) {
                        drawStreet(g, x, y, "<-v");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) != 0)) {
                        System.out.println("[Warning] Two contradictory precedences have been asserted in block in "+x+","+y+".");
                        drawStreet(g, x, y, "<v");
                    } else {
                        System.out.println("[Warning] No precedence has been asserted to bidirectional block in "+x+","+y+".");
                        drawStreet(g, x, y, "<v");
                    }
                } else {
                    drawStreet(g, x, y, "v");
                }
                break;
            case GridWorldModel.STREET_RIGHT:
                if ((model.data[x][y] & GridWorldModel.STREET_UP) != 0) {
                    if (((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) == 0)) {
                        drawStreet(g, x, y, "^->");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) == 0)) {
                        drawStreet(g, x, y, "^>");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) != 0)) {
                        System.out.println("[Warning] Two contradictory precedences have been asserted in block in "+x+","+y+".");
                        drawStreet(g, x, y, "^>");
                    } else {
                        System.out.println("[Warning] No precedence has been asserted to bidirectional block in "+x+","+y+".");
                        drawStreet(g, x, y, "^>");
                    }
                } else if ((model.data[x][y] & GridWorldModel.STREET_DOWN) != 0) {
                    if (((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) == 0)) {
                        drawStreet(g, x, y, "v->");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) == 0)) {
                        drawStreet(g, x, y, "V>");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_RIGHT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) != 0)) {
                        System.out.println("[Warning] Two contradictory precedences have been asserted in block in "+x+","+y+".");
                        drawStreet(g, x, y, "v>");
                    } else {
                        System.out.println("[Warning] No precedence has been asserted to bidirectional block in "+x+","+y+".");
                        drawStreet(g, x, y, "v>");
                    }
                } else {
                    drawStreet(g, x, y, ">");
                }
                break;
            case GridWorldModel.STREET_LEFT:
                if ((model.data[x][y] & GridWorldModel.STREET_UP) != 0) {
                    if (((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) == 0)) {
                        drawStreet(g, x, y, "<-^");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) == 0)) {
                        drawStreet(g, x, y, "<^");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_UP) != 0)) {
                        System.out.println("[Warning] Two contradictory precedences have been asserted in block in "+x+","+y+".");
                        drawStreet(g, x, y, "<^");
                    } else {
                        System.out.println("[Warning] No precedence has been asserted to bidirectional block in "+x+","+y+".");
                        drawStreet(g, x, y, "<^");
                    }
                } else if ((model.data[x][y] & GridWorldModel.STREET_DOWN) != 0) {
                    if (((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) == 0)) {
                        drawStreet(g, x, y, "<-v");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) == 0)) {
                        drawStreet(g, x, y, "<V");
                    } else if (((model.data[x][y] & GridWorldModel.PRECEDENCE_LEFT) != 0) && ((model.data[x][y] & GridWorldModel.PRECEDENCE_DOWN) != 0)) {
                        System.out.println("[Warning] Two contradictory precedences have been asserted in block in "+x+","+y+".");
                        drawStreet(g, x, y, "<v");
                    } else {
                        System.out.println("[Warning] No precedence has been asserted to bidirectional block in "+x+","+y+".");
                        drawStreet(g, x, y, "<v");
                    }
                } else {
                    drawStreet(g, x, y, "<");
                }
                break;
            // All other object that are not streets
            case GridWorldModel.SCHOOL:             drawSpecialBuilding(g, x, y, "School");   break;
            case GridWorldModel.SUPERMARKET:        drawSpecialBuilding(g, x, y, "Market");   break;
            case GridWorldModel.OFFICE:             drawSpecialBuilding(g, x, y, "Office");   break;
            case GridWorldModel.PARK:               drawSpecialBuilding(g, x, y, "Park");     break;
            case GridWorldModel.BUILDING:           drawBuilding(g, x, y);                      break;
            case GridWorldModel.PARKING_HELICOPTER: drawParkingHelicopter(g, x, y);             break;
            case GridWorldModel.CAR:                drawCar(g, x, y, GridWorldModel.getAgAtPos(x,y, GridWorldModel.CAR));                                   break;
            case GridWorldModel.PEDESTRIAN_ADULT:   drawAdultPedestrian(g, x, y, GridWorldModel.getAgAtPos(x,y, GridWorldModel.PEDESTRIAN_ADULT));          break;
            case GridWorldModel.PEDESTRIAN_CHILD:   drawChildPedestrian(g, x, y, GridWorldModel.getAgAtPos(x,y, GridWorldModel.PEDESTRIAN_CHILD));          break;
            case GridWorldModel.HELICOPTER:         drawHelicopter(g, x, y, GridWorldModel.getAgAtPos(x,y, GridWorldModel.HELICOPTER));                     break;
        }
    }

    public void drawCar(Graphics g, int x, int y, int id) {
        // Background 
        g.setColor(Color.lightGray);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        // Background on zebra crossing 
        if((model.data[x][y] & GridWorldModel.ZEBRA_CROSSING)!=0){
            drawZebraCrossing(g, x, y);
        }
        // Draw the vehicle 
        g.setColor(Color.yellow);
        g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        if (id >= 0) { 
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, String.valueOf(id+1));
        }
    }

    public void drawAdultPedestrian(Graphics g, int x, int y, int id){
        // Building Background 
        g.setColor(Color.orange);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        // Background on zebra crossing 
        if((model.data[x][y] & GridWorldModel.ZEBRA_CROSSING)!=0){
            drawZebraCrossing(g, x, y);
        }
        // Draw the body (blue circle)
        g.setColor(Color.blue);
        g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.setColor(Color.magenta);
        int headSizeW = (cellSizeW - 4) / 2;
        int headSizeH = (cellSizeH - 4) / 2;
        int headX = x * cellSizeW + 2 + (cellSizeW - 4) / 4;
        int headY = y * cellSizeH + 2 + (cellSizeH - 4) / 4;
        g.fillOval(headX, headY, headSizeW, headSizeH);
        // text
        g.setColor(Color.black);
        String name = GridWorldModel.getNameFromId(id); 
        String nameNumber = name.charAt(name.length()-1) + "";
        String text = "A" + nameNumber;
        drawString(g, x, y, defaultFont, text);
    }

    public void drawChildPedestrian(Graphics g, int x, int y, int id){
        // Building Background 
        g.setColor(Color.orange);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        // Background on zebra crossing 
        if((model.data[x][y] & GridWorldModel.ZEBRA_CROSSING)!=0){
            drawZebraCrossing(g, x, y);
        }
        // Draw the body (blue circle)
        g.setColor(Color.blue);
        g.fillOval(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.setColor(Color.yellow);
        int headSizeW = (cellSizeW - 4) / 2;
        int headSizeH = (cellSizeH - 4) / 2;
        int headX = x * cellSizeW + 2 + (cellSizeW - 4) / 4;
        int headY = y * cellSizeH + 2 + (cellSizeH - 4) / 4;
        g.fillOval(headX, headY, headSizeW, headSizeH);
        // text
        g.setColor(Color.black);
        String name = GridWorldModel.getNameFromId(id); 
        String nameNumber = name.charAt(name.length()-1) + "";
        String text = "C" + nameNumber;
        drawString(g, x, y, defaultFont, text);
    }


    public void drawHelicopter(Graphics g, int x, int y, int id) {
        // Background
        if ((model.data[x][y] & GridWorldModel.BUILDING) != 0){
            g.setColor(Color.ORANGE);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        }
        if ((model.data[x][y] & GridWorldModel.ZEBRA_CROSSING) != 0){
            drawZebraCrossing(g, x, y);
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

    
    public void drawStreet(Graphics g, int x, int y, String direction) {
        g.setColor(Color.lightGray);
        g.drawRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH); //we draw the outline
        if (((model.data[x][y] & GridWorldModel.CAR) == 0) 
           && ((model.data[x][y] & GridWorldModel.PEDESTRIAN_CHILD) == 0) 
           && ((model.data[x][y] & GridWorldModel.PEDESTRIAN_ADULT) == 0)
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
            if ((model.data[x][y] & GridWorldModel.PEDESTRIAN_CHILD) != 0) { //pedestrians can be on streets too (zebra-crossings)
                drawChildPedestrian(g, x, y, GridWorldModel.getAgAtPos(x,y,GridWorldModel.PEDESTRIAN_CHILD));
            }
            if ((model.data[x][y] & GridWorldModel.PEDESTRIAN_ADULT) != 0) { //pedestrians can be on streets too (zebra-crossings)
                drawAdultPedestrian(g, x, y, GridWorldModel.getAgAtPos(x,y,GridWorldModel.PEDESTRIAN_ADULT));
            }
            if ((model.data[x][y] & GridWorldModel.CAR) != 0) {
                drawCar(g, x, y, GridWorldModel.getAgAtPos(x,y,GridWorldModel.CAR));
            }
            if ((model.data[x][y] & GridWorldModel.HELICOPTER) != 0) {
                g.setColor(Color.lightGray);
                g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
                drawHelicopter(g, x, y, GridWorldModel.getAgAtPos(x,y,GridWorldModel.HELICOPTER));
            }
        }
        if((model.data[x][y] & GridWorldModel.ZEBRA_CROSSING) != 0){
            drawZebraCrossing(g, x, y);  
        }
    }

    public void drawZebraCrossing(Graphics g, int x, int y){
        int stripeHeight = cellSizeH / 4; // Height of each stripe
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                g.setColor(Color.white);
            } else {
                g.setColor(Color.black);
            }
            g.fillRect(x * cellSizeW, y * cellSizeH + i * stripeHeight, cellSizeW, stripeHeight);
        }
    }
    
    // Possible Special Buildings: School, Office, Park, Supermarket
    public void drawSpecialBuilding(Graphics g, int x, int y, String t) {
        g.setColor(Color.black);
        g.drawRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH); 
        g.setColor(Color.red);   
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        // If there is no agent on the block
        if (((model.data[x][y] & GridWorldModel.PEDESTRIAN_CHILD) == 0) && ((model.data[x][y] & GridWorldModel.PEDESTRIAN_ADULT) == 0)){            
            String text=t;
            g.setColor(Color.white);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int centerX = x * cellSizeW + (cellSizeW - textWidth) / 2;
            int centerY = y * cellSizeH + (cellSizeH - textHeight) / 2 + fm.getAscent();
            g.drawString(text, centerX, centerY);
        } else { 
            //if there is an agent on the block, we must make sure that it is drawn on top of the block
            if ((model.data[x][y] & GridWorldModel.PEDESTRIAN_CHILD) != 0) { //pedestrians can be on streets too (zebra-crossings)
                drawChildPedestrian(g, x, y, GridWorldModel.getAgAtPos(x,y,GridWorldModel.PEDESTRIAN_CHILD));
            }
            if ((model.data[x][y] & GridWorldModel.PEDESTRIAN_ADULT) != 0) { //pedestrians can be on streets too (zebra-crossings)
                drawAdultPedestrian(g, x, y, GridWorldModel.getAgAtPos(x,y,GridWorldModel.PEDESTRIAN_ADULT));
            }
        }
    }
    
    // Draw normal building blocks
    public void drawBuilding(Graphics g, int x, int y) {
        if ((model.data[x][y] & GridWorldModel.PEDESTRIAN_CHILD) != 0){
            drawChildPedestrian(g, x, y, GridWorldModel.getAgAtPos(x,y,GridWorldModel.PEDESTRIAN_CHILD));   
        }
        if ((model.data[x][y] & GridWorldModel.PEDESTRIAN_ADULT) != 0){
            drawAdultPedestrian(g, x, y, GridWorldModel.getAgAtPos(x,y,GridWorldModel.PEDESTRIAN_ADULT));   
        }
        if (((model.data[x][y] & GridWorldModel.PEDESTRIAN_CHILD) == 0) && ((model.data[x][y] & GridWorldModel.PEDESTRIAN_ADULT) == 0)){
            g.setColor(Color.orange);
            g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        }
    }  
    
    public void drawParkingHelicopter(Graphics g, int x, int y) {
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
            drawHelicopter(g, x, y, GridWorldModel.getHelicopter());
        }
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