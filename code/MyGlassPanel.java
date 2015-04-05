import java.util.Scanner;
import java.io.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFileChooser;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.awt.geom.Point2D;
import java.util.Stack;
import java.util.Deque;
import java.util.ArrayDeque;

/**
 * MyGlassPanel intercepts all of the mouseclick events that occur in the internal frame of MouseImageProcessor,
 * and controls the flow of the program. It provides visual interaction with the user by drawing useful 
 * shapes and lines where they click- without writing over the mouse image. MyGlassPanel provides basic undo and redo
 * functionality.
 * 
 * @author Margo Morton
 * @version 3/31/2015
 */

class MyGlassPanel extends JPanel implements MouseListener {
    boolean readyToDisplay = false; //controls when content gets drawn on the frame
    Rectangle rect; // rectangle constructed from two points the user clicks (opposing corners)
    String switchString = "furColor"; // controls the switch statement program flow- color analysis is the first step of the program
    Path2D.Double tailPath = new Path2D.Double(); // line segment represening the tail
    Path2D.Double bodyPath = new Path2D.Double(); // line segment roughly representing the body length (spine)
    Path2D.Double earPath = new Path2D.Double(); // line segment roughly representing the ear length
    boolean gotInitialPointTail = false; // the tail line segment has been initialized (false initially)
    boolean gotInitialPointBody = false; // the body length segment has been initialized (false initially)
    boolean gotInitialPointEar = false; // the ear line segment has been initialized (false initially)
    double tailLengthDistance = 0; // tail length is added with each click, initialized to 0
    double bodyLengthDistance = 0; // body length is added with each click, initialized to 0
    double earLengthDistance = 0; // ear length is added with each click, initialized to 0
    Point2D tailColor1; // a point selected of the tail; represents one color
    Point2D tailColor2; // another tail point; represents another color
    int numberOfClicks; // counts the number of clicks user has made, controls program flow

    // stacks for undoing and redoing
    Stack <java.lang.Double>doubleStack = new Stack<java.lang.Double>();
    Stack <Path2D.Double>pathStack = new Stack<Path2D.Double>();
    Stack <Rectangle>rectStack = new Stack<Rectangle>();
    Stack <Point2D>pointStack = new Stack<Point2D>();
    
    private int undoableActions = 0; // allows for control of undo function
    
    /**
     * Constructor for objects of class MyGlassPanel
     */
    MyGlassPanel() {
        super();
        addMouseListener(this); // listens for mouseEvents on the glassPane
    }

    /**
     * Calls the graphic's paint method, if the graphic is non-null. Draws the graphic on the glass pane.
     * This method is called with repaint().
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gfx = (Graphics2D)g;
        //gfx.setColor(Color.black);

        if(readyToDisplay){
            switch(switchString){
                case "furColor":{
                    // draw a black rectangle with the last two clicked points
                    gfx.drawRect(rect.x, rect.y, rect.width, rect.height);

                    // highlight boundary points with red dots
                    gfx.setColor(Color.red);
                    gfx.fillOval(rect.x, rect.y, 3, 3);
                    gfx.fillOval(rect.x+rect.width, rect.y+rect.height, 3, 3);
                    break;
                }

                case "tailLength":{
                    // set the line color to black, use a thick line, and remove pixelation
                    gfx.setColor(Color.black);
                    gfx.setStroke(new BasicStroke(4));
                    gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // draw line segment for the tail from the selected points
                    if (gotInitialPointTail){
                        gfx.draw(tailPath);
                    }
                    break;
                }

                case "bodyLength":{
                    // set the line color to black, use a thick line, and remove pixelation
                    gfx.setColor(Color.black);
                    gfx.setStroke(new BasicStroke(4));
                    gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // draw line segment for the body from the selected points
                    if (gotInitialPointTail){
                        gfx.draw(bodyPath);
                    }
                    break;
                }

                case "tailGradient":{
                    if (tailColor1 != null && tailColor2 != null){
                        // draw red dots where the user clicked
                        gfx.setColor(Color.red);
                        gfx.fillOval((int)tailColor1.getX(), (int)tailColor1.getY(), 5, 5);
                        gfx.fillOval((int)tailColor2.getX(), (int)tailColor2.getY(), 5, 5);
                    }
                    break;
                }

                case "earLength":{
                    // set the line color to black, use a thick line, and remove pixelation
                    gfx.setColor(Color.black);
                    gfx.setStroke(new BasicStroke(4));
                    gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // draw line segment for the body from the selected points
                    gfx.draw(earPath);
                    break;
                }

                default:
                break;
            }
        }
    }

    /**
     * This function is called automatically on every mouse click. It fills in instance variables with information from the mouse
     * click events, and calls for graphics objects to be drawn on the glass pane.
     */
    public void mouseClicked(MouseEvent e) {
        numberOfClicks++; // increment click count (initialized to 0)
        Point containerPoint = e.getPoint(); // get point clicked from MouseEvent
        //System.out.println(containerPoint.x+","+ containerPoint.y);

        switch(switchString){
            case "furColor":{
                if(numberOfClicks == 1){
                    // create rectangle with one point on its boundary
                    Point myPoint = new Point(e.getX(), e.getY());
                    rect = new Rectangle(myPoint);
                }
                else if(numberOfClicks == 2){
                    // add second point to rectangle boundary
                    rect.add(e.getPoint());
                    readyToDisplay = true;
                    repaint();
                    numberOfClicks = 0;
                }
                else{
                    numberOfClicks = 0;
                    readyToDisplay = false;
                }
                break;
            }

            case "tailLength":{
                if(gotInitialPointTail == false){ // set the initial point for the vector
                    tailPath.moveTo(e.getX(), e.getY());
                    gotInitialPointTail = true;
                }
                Point2D currPoint = tailPath.getCurrentPoint();
                tailLengthDistance += Math.hypot((currPoint.getX()-e.getX()), (currPoint.getY()-e.getY()));
                tailPath.lineTo(e.getX(), e.getY());
                repaint();
                break;
            }

            case "bodyLength":{
                if(gotInitialPointBody == false){ // set the initial point for the vector
                    bodyPath.moveTo(e.getX(), e.getY());
                    gotInitialPointBody = true;
                }
                Point2D currPoint = bodyPath.getCurrentPoint();
                bodyLengthDistance += Math.hypot((currPoint.getX()-e.getX()), (currPoint.getY()-e.getY()));
                bodyPath.lineTo(e.getX(), e.getY());
                
                repaint();
                numberOfClicks = 0;
                break;
            }

            case "tailGradient":{
                // grab one point of each color
                //readyToDisplay=false;
                repaint();
                if(numberOfClicks == 1){
                    tailColor1 = containerPoint;
                    readyToDisplay = false;
                    repaint();
                }
                else if(numberOfClicks == 2){
                    tailColor2 = containerPoint;
                    readyToDisplay = true;
                    repaint();
                    numberOfClicks = 0;
                }
                else{
                    numberOfClicks = 0;
                    readyToDisplay = false;
                    repaint();
                }
                break;
            }

            case "earLength":{
                if(gotInitialPointEar == false){ // set the initial point for the vector
                    earPath.moveTo(e.getX(), e.getY());
                    gotInitialPointEar = true;
                }
                Point2D currPoint = earPath.getCurrentPoint();
                earLengthDistance += Math.hypot((currPoint.getX()-e.getX()), (currPoint.getY()-e.getY()));
                earPath.lineTo(e.getX(), e.getY());
                repaint();
                break;
            }

            default:{
                repaint();
                break;
            }
        }
    }

    public int getUndoableActions(){
        return undoableActions;
    }
    
    
    /**
     * This function handles very basic undoing capabilities with stacks. It is restricted to only undoing the last UndoableAction.
     */
    public void undo(){
        undoableActions ++;
        switch(switchString){
            case "furColor":{
                //intStack.push(rect.x); //push rect x point to int stack
                //intStack.push(rect.y); //push rect y point to int stack
                rectStack.push(rect);
                rect = new Rectangle(); //clear rect
                repaint();
                break;
            }

            case "tailLength":{
                pathStack.push(tailPath); //push tail to path stack
                //System.out.println(tailPath);
                doubleStack.push(tailLengthDistance); //push tail distance to double stack
                
                gotInitialPointTail = false; // the tail line segment has been initialized (false initially)
                tailLengthDistance = 0; // tail length is added with each click, initialized to 0
                tailPath = new Path2D.Double(); //clear path
                repaint();
                break;
            }

            case "bodyLength":{
                pathStack.push(bodyPath); //push tail to path stack
                doubleStack.push(bodyLengthDistance); //push tail distance to double stack
                
                gotInitialPointBody = false; // the body length segment has been initialized (false initially)
                bodyLengthDistance = 0; // body length is added with each click, initialized to 0
                bodyPath = new Path2D.Double(); //clear path
                
                repaint();
                break;
            }

            case "tailGradient":{
                pointStack.push(tailColor1);
                pointStack.push(tailColor2);
                
                Point2D tailColor1 = new Point(0,0);
                Point2D tailColor2 = new Point(0,0);
                repaint();
                break;
            }

            case "earLength":{
                pathStack.push(earPath); //push tail to path stack
                doubleStack.push(earLengthDistance); //push tail distance to double stack
                
                gotInitialPointEar = false; // the ear line segment has been initialized (false initially)
                earLengthDistance = 0; // ear length is added with each click, initialized to 0
                earPath = new Path2D.Double();
                repaint();
                break;
            }

            default:{
                repaint();
                break;
            }
        }
    }

    /**
     * This function handles very basic redoing capabilities with stacks.
     */
    public void redo(){
        undoableActions --;
        switch(switchString){
            case "furColor":{
                //rect.y = intStack.pop();
                //rect.x = intStack.pop();
                rect = rectStack.pop();
                repaint();
                break;
            }

            case "tailLength":{
                gotInitialPointTail = true; // the tail line segment has been initialized (false initially)
                tailLengthDistance = doubleStack.pop(); // tail length is added with each click, initialized to 0
                tailPath = pathStack.pop();
                //System.out.println(tailPath);
                repaint();
                break;
            }

            case "bodyLength":{
                gotInitialPointBody = true; // the body length segment has been initialized (false initially)
                bodyLengthDistance = doubleStack.pop(); // body length is added with each click, initialized to 0
                bodyPath = pathStack.pop();
                repaint();
                break;
            }

            case "tailGradient":{
                tailColor1 = pointStack.pop();
                tailColor2 = pointStack.pop();
                repaint();
                break;
            }

            case "earLength":{
                gotInitialPointEar = true; // the ear line segment has been initialized (false initially)
                earLengthDistance = doubleStack.pop(); // ear length is added with each click, initialized to 0
                earPath = pathStack.pop();
                repaint();
                break;
            }

            default:{
                repaint();
                break;
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {             
    }

    public void mousePressed(MouseEvent e) { 
    }

    public void mouseReleased(MouseEvent e) { 
    }
}