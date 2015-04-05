import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.BufferedImage;
import java.awt.Font;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.lang.Double;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.net.URL;

/**
 * The MouseImageProcessor class is the main class in this program. It conducts the flow of the program,
 * generates the user interface, and guides the use of the program through a tutorial window.
 *
 * Used in conjuction with MyGlassPanel and Mouse for picture analysis. WriteExcel is used for saving the
 * resulting analysis information.
 * 
 * @author Margo Morton
 * @version 3/31/2015
 */
public class MouseImageProcessor extends JFrame {
    JTextArea display;
    private static JDesktopPane desktop;
    private static JInternalFrame displayWindow;
    private static BufferedImage pickedImage;
    private static boolean imageOpened = false;
    private static JLabel imageLabel;
    private static Mouse myMouse = new Mouse();
    private static MyGlassPanel glassPane = new MyGlassPanel();
    private static String fileName = "";
    private static boolean readyToSave = false;
    private static boolean helpDialog = true;
    private static JMenuItem menuItemSave = new JMenuItem("Save");
    private static JButton button1;
    private static JPanel tutorialPanel = new JPanel();

    /**
     * Constructor for objects of class MouseImageProcessor. This sets up the content panes and toolbars used by the program.
     */
    public MouseImageProcessor(String title) {
        super(title);

        //Set up the GUI.
        desktop = new JDesktopPane();
        desktop.putClientProperty("JDesktopPane.dragMode", "outline");
        desktop.setPreferredSize(new Dimension(1000, 700));
        JPanel toolbar = new JPanel();
        button1 = new JButton("Select");
        button1.setEnabled(false);
        button1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent click) {
                    switch(glassPane.switchString){
                        case "furColor":{

                            //done picking rectangle
                            //calculate the fur color
                            BufferedImage subImg = pickedImage.getSubimage(glassPane.rect.x, glassPane.rect.y, glassPane.rect.width, glassPane.rect.height);
                            calculateAverage(subImg);
                            myMouse.analyzeCoatColor();
                            glassPane.switchString = "tailLength";
                            //displayHelpDialog();
                            displayTutorial(glassPane.switchString);
                            tutorialPanel.revalidate();
                            tutorialPanel.repaint();
                            glassPane.repaint();
                            break;
                        }

                        case "tailLength":{
                            myMouse.setTailLength(glassPane.tailLengthDistance);
                            //System.out.println("Tail Length: "+glassPane.tailLengthDistance);
                            glassPane.switchString = "bodyLength";
                            //displayHelpDialog();
                            displayTutorial(glassPane.switchString);
                            tutorialPanel.repaint();
                            tutorialPanel.revalidate();
                            glassPane.repaint();
                            break;
                        }

                        case "bodyLength":{
                            myMouse.setBodyLength(glassPane.bodyLengthDistance);
                            //System.out.println("Body Length: "+glassPane.bodyLengthDistance);
                            myMouse.analyzeTailBodyRatio();
                            glassPane.switchString = "tailGradient";
                            //displayHelpDialog();
                            displayTutorial(glassPane.switchString);
                            tutorialPanel.repaint();
                            tutorialPanel.revalidate();
                            glassPane.repaint();
                            break;
                        }

                        case "tailGradient":{
                            myMouse.setTailColors(getPixelColor(glassPane.tailColor1), getPixelColor(glassPane.tailColor2));
                            myMouse.analyzeTailColor();
                            glassPane.switchString = "earLength";
                            //displayHelpDialog();
                            displayTutorial(glassPane.switchString);
                            tutorialPanel.repaint();
                            tutorialPanel.revalidate();
                            glassPane.repaint();
                            break;
                        }

                        case "earLength":{
                            myMouse.setEarLength(glassPane.earLengthDistance);
                            myMouse.analyzeEarRatio();
                            myMouse.speciesGuess();
                            readyToSave=true;
                            setSaveState(true);
                            button1.setEnabled(false);
                            glassPane.readyToDisplay = false;
                            glassPane.switchString = "save";
                            //displayHelpDialog();
                            displayTutorial(glassPane.switchString);
                            tutorialPanel.repaint();
                            tutorialPanel.revalidate();
                            glassPane.repaint();
                            break;
                        }

                        default:{
                            glassPane.repaint();
                            break;
                        }
                    }
                }
            });
        toolbar.add(button1);
        displayTutorial("open");
        tutorialPanel.setPreferredSize(new Dimension(442,100));
        getContentPane().add(tutorialPanel, BorderLayout.EAST);
        getContentPane().add(desktop, BorderLayout.CENTER);
        getContentPane().add(toolbar, BorderLayout.NORTH);
    }

    //Create the window that displays event information.
    protected static void createDisplayWindow(String name) {
        JPanel contentPane = new JPanel();
        displayWindow = new JInternalFrame(name,
            false,  //resizable
            true, //closable
            false, //maximizable
            true); //iconifiable

        //imageLabel = new JLabel(new ImageIcon("C:/Users/Margo/Documents/Collegework/Honors Thesis/whitespace.png"));
        imageLabel = new JLabel(new ImageIcon(pickedImage));
        contentPane.add(imageLabel);

        glassPane = new MyGlassPanel();
        displayWindow.getRootPane().setGlassPane(glassPane);
        glassPane.setVisible(true);
        glassPane.setOpaque(false);
        glassPane.repaint();
        displayWindow.getRootPane().add(glassPane);

        displayWindow.setContentPane(contentPane);
        displayWindow.pack();
        displayWindow.setVisible(true);
    }
    
    public static void displayTutorial(String s){
        if(helpDialog){
            tutorialPanel.setVisible(true);
            JLabel icon;
            JTextArea text = new JTextArea("");
            tutorialPanel.removeAll(); 
            JLabel header = new JLabel("Program Tutorial");
            header.setFont(new Font("Serif", Font.BOLD, 26));
            tutorialPanel.add(header);
            switch(s){
                case "furColor":{
                    //tutorialPanel.removeAll(); 
                    URL url = MouseImageProcessor.class.getResource("/mouseFur.jpg");
                    icon = new JLabel(new ImageIcon(url));
                    //icon = new JLabel(new ImageIcon("mouseFur.jpg"));
                    text = new JTextArea("Click two points on the fur to take a rectangular \nselection of the fur color. Do not include the white \nunderbelly or any background elements. Then click SELECT. \nTIP: Click opposing corners. \nTIP: Larger selections result in greater accuracy. \n \nYou can disable the help dialogue in the options menu.");
                    tutorialPanel.add(icon);
                    tutorialPanel.add(text);
                    break;
                }
    
                case "tailLength":{
                    //tutorialPanel.removeAll();
                    URL url = MouseImageProcessor.class.getResource("/mouseTail.jpg");
                    icon = new JLabel(new ImageIcon(url));
                    //icon =  new JLabel(new ImageIcon("mouseTail.jpg"));
                    text = new JTextArea("Click along the tail to fit a line to it. \nThen click SELECT. \nTIP: For curved tails, clicking more points will result \nin higher accuracy.");
                    tutorialPanel.add(icon);
                    tutorialPanel.add(text);
                    break;
                }
    
                case "bodyLength":{
                    //tutorialPanel.removeAll();
                    URL url = MouseImageProcessor.class.getResource("/mouseBody.jpg");
                    icon = new JLabel(new ImageIcon(url));
                    //icon =  new JLabel(new ImageIcon("mouseBody.jpg"));
                    text = new JTextArea("Click along the entire body to fit a line to it. \nBe sure the line goes from base of the tail to the tip of \nthe nose. Then click SELECT. \nTIP: Imagine clicking along the spine. \nTIP: Do not simply click the outline of the mouse.");
                    tutorialPanel.add(icon);
                    tutorialPanel.add(text);
                    break;
                }
    
                case "tailGradient":{
                    //tutorialPanel.removeAll();
                    URL url = MouseImageProcessor.class.getResource("/mouseGradient.jpg");
                    icon = new JLabel(new ImageIcon(url));
                    //icon =  new JLabel(new ImageIcon("mouseGradient.jpg"));
                    text = new JTextArea("Click two parts of the tail, one on the top half \nand one on the bottom half. This measures the tail gradient. \nThen click SELECT. \nTIP: If you see a color difference, try to click one of each color. \nTIP: Do not click too close to the edges of the tail. \nTIP: You can zoom in to the tail to get greater accuracy.");
                    tutorialPanel.add(icon);
                    tutorialPanel.add(text);    
                    break;
                }
    
                case "earLength":{
                    //tutorialPanel.removeAll();
                    URL url = MouseImageProcessor.class.getResource("/mouseEar.jpg");
                    icon = new JLabel(new ImageIcon(url));
                    //icon =  new JLabel(new ImageIcon("mouseEar.jpg"));
                    text = new JTextArea("Click on the base of the ear and then on the tip\n to fit a line to it. Then click SELECT. \nTIP: Try to get the longest segment of the ear.");
                    tutorialPanel.add(icon);
                    tutorialPanel.add(text);
                    break;
                }
                            
                case "save":{
                    //icon =  new JLabel(new ImageIcon("mouseSave.jpg"));
                    URL url = MouseImageProcessor.class.getResource("/mouseSave.jpg");
                    icon = new JLabel(new ImageIcon(url));
                    text = new JTextArea("The analysis is complete. If you are satisfied with \n the results, you can save them by selecting an Excel\n file or typing a filename for a new file to be generated. \n Afterword, you can analyze another mouse by opening an image.");
                    tutorialPanel.add(icon);
                    tutorialPanel.add(text);
                    text.setEditable(false);
                    text.setFocusable(false);
                    tutorialPanel.revalidate();
                    Double percentage = Double.parseDouble(myMouse.calculateLogSpecies()) *100;
                    
                    if (percentage < 50){
                        percentage = 100 - percentage;
                    }
                    
                    int n = JOptionPane.showConfirmDialog(null,
                    "Results \n This mouse is "+percentage+"% likely to be "+myMouse.getSpecies()+". Save the results of this analysis?",
                    "Would you like to save?",
                    JOptionPane.YES_NO_OPTION);
                    if(n == JOptionPane.YES_OPTION){
                        save();
                    }
                    
                    break;
                }
                
                default:{
                    //tutorialPanel.removeAll();
                    URL url = MouseImageProcessor.class.getResource("mouseOpen.jpg");
                    icon = new JLabel(new ImageIcon(url));
                    //icon = new JLabel(new ImageIcon("mouseOpen.jpg"));
                    text = new JTextArea("Open a photograph of a Peromyscus mouse that:\n1. Shows the entire body and tail\n2. Has no elements overlapping the mouse\n3. Has good lighting \n \nTIP: Use the shortcut CTRL + O to open files quickly");
                    tutorialPanel.add(icon);
                    tutorialPanel.add(text);
                    break;
                }
    
            }
            

            text.setWrapStyleWord(true);
            text.setEditable(false);
            text.setFocusable(false);
            text.setBackground(tutorialPanel.getBackground());
            tutorialPanel.revalidate();
            glassPane.repaint();
        }
        else{
            tutorialPanel.setVisible(false);
            tutorialPanel.repaint();
        }
    }

    public static void getImage() throws Exception{
        Component parent = null;
        //display file-picker pop up
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            //get full filepath and save as an image
            String absoluteFilePath = chooser.getSelectedFile().getAbsolutePath();
            pickedImage = ImageIO.read(new File(absoluteFilePath));
    
            imageOpened = true;
            fileName = chooser.getSelectedFile().getName();
            createDisplayWindow(chooser.getSelectedFile().getName());
            desktop.add(displayWindow);
            Dimension displaySize = displayWindow.getSize();
            displayWindow.setSize(pickedImage.getWidth()+5, pickedImage.getHeight()+35);
            button1.setEnabled(true);
            displayTutorial(glassPane.switchString);
        }
    }

    private void setSaveState(boolean state){
        menuItemSave.setEnabled(state);
    }

    public void calculateAverage(BufferedImage subImg){
        double redTotal = 0;
        double greenTotal = 0;
        double blueTotal = 0;
        double pixelCount = 0;

        for (int y = 0; y < subImg.getHeight(); y++){
            for (int x = 0; x < subImg.getWidth(); x++){
                Color c = new Color(subImg.getRGB(x,y));
                pixelCount++;
                redTotal += c.getRed();
                greenTotal += c.getGreen();
                blueTotal += c.getBlue();
            }
        }
        // calculate average of bitmap r,g,b values
        double red = (redTotal/pixelCount);
        double green = (greenTotal/pixelCount);
        double blue = (blueTotal/pixelCount);

        //System.out.println("Avg Red: "+red+", Avg Green: "+green+", Avg Blue: "+blue);
        myMouse.setCoatColor(red,green,blue);
    }

    public Color getPixelColor(Point2D p){
        int x = (int)p.getX();
        int y = (int)p.getY();
        Color c = new Color(pickedImage.getRGB(x,y));
        return c;
    }

    public static void resizeImage(double scale){
        int width = (int)(scale*pickedImage.getWidth());
        int height = (int)(scale*pickedImage.getHeight());
        //System.out.println("width:"+width+" height:"+height);
        // create new blank image of scaled size in ARGB format
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Paint scaled version of image to new image

        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.drawImage(pickedImage, 0, 0, width, height, null);

        // clean up
        graphics2D.dispose();

        pickedImage = scaledImage;
        imageLabel = new JLabel(new ImageIcon(pickedImage));
        displayWindow.getContentPane().removeAll();
        displayWindow.getContentPane().add(imageLabel);
        displayWindow.setSize(pickedImage.getWidth()+5, pickedImage.getHeight()+35);
        ((JPanel)displayWindow.getContentPane()).revalidate();
        displayWindow.repaint();
    }

    public void writeToFile(){
        final JFileChooser chooser = new JFileChooser();
        if (chooser.getSelectedFile().exists())
        {
            //System.out.println("Do You Want to Overwrite File?");
            return;
        }
        else
            chooser.approveSelection();
        //System.out.println("Saving "+chooser.getSelectedFile().getAbsolutePath());
    }

    public static void save(){
        try{
            JFrame parentFrame = new JFrame();
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XLS document", "xls", "xlsx");
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogTitle("Specify a file to save");
            int userSelection = fileChooser.showSaveDialog(parentFrame);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filepath = fileToSave.getAbsolutePath();
                String imageFileName = fileName;
                WriteExcel excel = new WriteExcel();
                excel.setMouse(myMouse);
                excel.setOutputFile(filepath);
                excel.setImagePath(imageFileName);
                excel.write();
            }
        }
        catch(Exception error){
            //System.out.println("invalid filetype");
            //System.out.println(error.getMessage());
            error.printStackTrace();
        }
    }
    
    /**
     * Creates the rootPane and menuBar.
     * For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new MouseImageProcessor("Mouse Analysis 1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);
        JMenuItem menuItemOpen = new JMenuItem("Open");
        menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItemOpen.getAccessibleContext().setAccessibleDescription("Open an image to process");
        menuFile.add(menuItemOpen);

        menuItemOpen.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    try{
                        menuItemSave.setEnabled(false);
                        //button1.setEnabled(true);
                        getImage();
                        //displayHelpDialog();
                        //displayTutorial(glassPane.switchString);
                    }
                    catch(Exception error){
                        //System.out.println("invalid filetype");
                    }
                }
            });

        //JMenuItem menuItemSave = new JMenuItem("Save");
        menuItemSave.setEnabled(false);
        menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItemSave.getAccessibleContext().setAccessibleDescription("Save analysis results");
        menuFile.add(menuItemSave);

        menuItemSave.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //System.out.println("Ready to save? "+readyToSave);
                    if(readyToSave){
                        save();
                    }
                    else{
                        //System.out.println("Analysis in progress");
                    }
                }
            });

        JMenu menuEdit = new JMenu("Edit");
        menuBar.add(menuEdit);    

        JMenuItem menuItemUndo = new JMenuItem("Undo");
        menuItemUndo.getAccessibleContext().setAccessibleDescription("Undo last action");
        menuEdit.add(menuItemUndo);
        menuItemUndo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    try{
                        if (glassPane.getUndoableActions() != 1){
                            glassPane.undo();
                        }
                    }
                    catch(Exception error){
                        error.printStackTrace();
                        //System.out.println("Unable to undo");
                    }
                }
            });

        JMenuItem menuItemRedo = new JMenuItem("Redo");
        menuItemRedo.getAccessibleContext().setAccessibleDescription("Redo last action");
        menuEdit.add(menuItemRedo);
        menuItemRedo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    try{
                        if (glassPane.getUndoableActions() != 0){
                            glassPane.redo();
                        }
                    }
                    catch(Exception error){
                        error.printStackTrace();
                        //System.out.println("Unable to redo");
                    }
                }
            });

        JMenuItem menuItemZoomIn = new JMenuItem("Zoom +");
        menuItemZoomIn.getAccessibleContext().setAccessibleDescription("Increase the size of the image");
        menuEdit.add(menuItemZoomIn);
        menuItemZoomIn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    try{
                        resizeImage(1.3);
                    }
                    catch(Exception error){
                        //System.out.println("invalid command");
                    }
                }
            });

        JMenuItem menuItemZoomOut = new JMenuItem("Zoom -");
        menuItemZoomOut.getAccessibleContext().setAccessibleDescription("Decrease the size of the image");
        menuEdit.add(menuItemZoomOut);
        menuItemZoomOut.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    try{
                        resizeImage(0.8);
                    }
                    catch(Exception error){
                        //System.out.println("invalid command");
                    }
                }
            });

        JMenu menuOptions = new JMenu("Options");
        menuBar.add(menuOptions);    
        JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Display Tutorial");
        cbMenuItem.getAccessibleContext().setAccessibleDescription("Toggle Help Content");
        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        menuOptions.add(cbMenuItem);
        cbMenuItem.setState(true);
        cbMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    helpDialog = !(helpDialog);
                    displayTutorial(glassPane.switchString);
                    tutorialPanel.repaint();
                }
            });

        JMenu menuAbout = new JMenu("About");
        menuBar.add(menuAbout);
        JMenuItem menuAboutItem = new JMenuItem("About Program");
        menuAbout.add(menuAboutItem);
        menuAboutItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try{
                        URL url = MouseImageProcessor.class.getResource("/about.jpg");
                        //BufferedImage img = ImageIO.read(new File("about.jpg"));
                        //ImageIcon icon = new ImageIcon(img);
                        ImageIcon icon = new ImageIcon(url);
                        JLabel label = new JLabel(icon);
                        JOptionPane.showMessageDialog(null, label);
                    }
                    catch(Exception about){
                        //System.out.println("Could not display about screen");
                    }
                }
            });

        frame.setJMenuBar(menuBar);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
    }
}
