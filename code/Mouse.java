import java.util.Scanner;
import java.lang.Math;
import java.awt.Color;
import java.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.io.BufferedWriter;

/**
 * The mouse class is meant to classify species of mice found in images as P. Maniculatus or P. Leucopus.
 * Used in conjuction with MyGlassPanel and MouseImageProcessor for picture analysis.
 * 
 * @author Margo Morton
 * @version 3/31/2015
 */
public class Mouse{
    // the following are the average RGB value in fur coat
    private double redInCoat;
    private double greenInCoat;
    private double blueInCoat;

    private double tailLength; //length of tail in pixels
    private double bodyLength; //length of body in pixels
    private double earLength; //length of ear in pixels
    private Color[] tailColors = new Color[2]; // two points on the tail gradient
    private double tailColorDistance; //difference bewteen tail colors

    int maniculatusVotes = 0;
    int leucopusVotes = 0;
    
    private String species = "";

    /**
     * Constructor for objects of class Mouse
     */
    public Mouse(){
    }

    /**
     * Basic "setter" function for the RGB fur color. Sets the instance variables redInCoat, greenInCoat, and blueInCoat.
     * @param r,g,b Doubles representing the average coat color in red, green, and blue.
     */
    public void setCoatColor(double r, double g, double b){
        redInCoat = r;
        greenInCoat = g;
        blueInCoat = b;
    }

    /**
     * Basic "setter" function for tail length of the mouse.
     * @param length The length of the tail in pixels.
     */
    public void setTailLength(double length){
        tailLength = length;
    }

    /**
     * Basic "setter" function for body length of the mouse.
     * @param length The length of the body in pixels.
     */
    public void setBodyLength(double length){
        bodyLength = length;
    }
    
     /**
     * Basic "setter" function for ear length of the mouse.
     * @param length The length of the ear in pixels.
     */
    public void setEarLength(double length){
        earLength = length;
    }

    /**
     * Basic "setter" function for tail colors
     */
    public void setTailColors(Color pixel1, Color pixel2){
        tailColors[0] = pixel1;
        tailColors[1] = pixel2;
    }

    /**
     * Determines whether the fur color is closer to the average Maniculatus color or Leucopus color.
     * The decision is made on which distance in 3D Colorspace is shorter.
     */
    public void analyzeCoatColor(){
        //leucopus RGB is an average fur color for leucopus mice
        double leucopusRed = 128.1818181818;
        double leucopusGreen = 105.5545454545;
        double leucopusBlue = 74.3090909091;

        //get distance in 3D space between two colors
        double leucopusDistance = Math.sqrt(
                Math.pow((redInCoat-leucopusRed),2)
                +Math.pow((greenInCoat-leucopusGreen),2)
                +Math.pow((blueInCoat-leucopusBlue),2)
            );

        //maniculatus RGB is an average fur color for maniculatus mice
        double maniculatusRed = 121.0636363636;
        double maniculatusGreen = 109.6272727273;
        double maniculatusBlue = 97.7909090909;

        //get distance in 3D space between two colors
        double maniculatusDistance = Math.sqrt(
                Math.pow((redInCoat-maniculatusRed),2)
                +Math.pow((greenInCoat-maniculatusGreen),2)
                +Math.pow((blueInCoat-maniculatusBlue),2)
            );

        //System.out.println("L Distance: "+leucopusDistance);
        //System.out.println("M Distance: "+maniculatusDistance);

        //check if the fur color is closer in color-space distance to one species or the other
        if(leucopusDistance < maniculatusDistance){
            //System.out.println("Mouse is P. Leucopus");
            leucopusVotes++;
        }
        else if(maniculatusDistance < leucopusDistance){
            //System.out.println("Mouse is P. Maniculatus");
            maniculatusVotes++;
        }
        else{
            //System.out.println("Species not able to be determined");
        }
    }

    /**
     * Calculates the ratio of tail:body length. If the tail is longer than the body, this algorithm guesses that it is Maniculatus, otherwise, Leucopus.
     */
    public void analyzeTailBodyRatio(){
        double ratio = tailLength/bodyLength;

        //System.out.println("Ratio is: " + ratio);
        if(ratio<1){
            //System.out.println("Mouse is P. Leucopus");
            leucopusVotes++;
        }
        else if(ratio>=1){
            //System.out.println("Mouse is P. Maniculatus");
            maniculatusVotes++;
        }
        else{
            //System.out.println("Species not able to be determined");
        }
    }

    /**
     * Detects a tail gradient by analyzing the color distance of the dorsal and ventral tail pixels.
     */
    public void analyzeTailColor(){
        double colorDistance = Math.sqrt(
                Math.pow((tailColors[0].getRed()-tailColors[1].getRed()),2)
                +Math.pow((tailColors[0].getGreen()-tailColors[1].getGreen()),2)
                +Math.pow((tailColors[0].getBlue()-tailColors[1].getBlue()),2)
            );

        //System.out.println("ColorDistance:"+colorDistance);
        tailColorDistance = colorDistance;
        if(colorDistance<100){
            //System.out.println("Mouse is P. Leucopus");
            leucopusVotes++;
        }
        else if(colorDistance>=100){
            //System.out.println("Mouse is P. Maniculatus");
            maniculatusVotes++;
        }
        else{
            //System.out.println("Species not able to be determined");
        }
    }

    /**
     * Calculates the ratio of ear:body length. If the ears were large in proportion to the body, it is more likely Maniculatus.
     */
    public void analyzeEarRatio(){
        double ratio = earLength/bodyLength;

        //System.out.println("Ratio is: " + ratio);
        if(ratio<0.8){
            //System.out.println("Mouse is P. Leucopus");
            leucopusVotes++;
        }
        else if(ratio>=0.8){
            //System.out.println("Mouse is P. Maniculatus");
            maniculatusVotes++;
        }
        else{
            //System.out.println("Species not able to be determined");
        }
    }
    
    /**
     * Depreciated, use calculateLogSpecies(). This function uses "votes" for each step of the analysis to see whether the mouse has more features that are Leucopus or Maniculatus.
     */
    public void speciesGuess(){
        if(leucopusVotes>maniculatusVotes){
            //System.out.println("***  Best Guess: Mouse is P. Leucopus  ***");
        }
        else if(leucopusVotes<maniculatusVotes){
            //System.out.println("***  Best Guess: Mouse is P. Maniculatus  ***");
        }
        else{
            //System.out.println("It could be either P. Maniculatus or P. Leucopus.");
        }
    }
    
    /**
     * Logistic regression equation developed to distinguish Leucopus and Maniculatus from a set of morphological features.
     */
    public String calculateLogSpecies(){
        double y = (4.94 + (0.0398*redInCoat) + (0.028*greenInCoat) - (0.0994*blueInCoat) - (5.40*(tailLength/bodyLength)) - (0.0665*tailColorDistance) + (30.3*(earLength/bodyLength)));
        double percentLeucopus = Math.exp(y)/(1 + Math.exp(y));
        String result = Double.toString(percentLeucopus);
        
        if (percentLeucopus >= 0.5)
            species = "P. leucopus";
        else{
            species = "P. maniculatus";
        }
        
        return result;
    }
    
    public String getSpecies(){
        return species;
    }
    
    public double getRedInCoat(){
        return redInCoat;
    }
    
    public double getGreenInCoat(){
        return greenInCoat;
    }

    public double getBlueInCoat(){
        return blueInCoat;
    }
    
    public double getTailBodyRatio(){
        return tailLength/bodyLength;
    }
    
    public double getEarBodyRatio(){
        return earLength/bodyLength;
    }
    
    public double getTailColorDistance(){
        return tailColorDistance;
    }
    
    /**
     * Depreciated, use WriteExcel class. This function stores the results of the analysis in a plain text file with basic formatting.
     */
    public void writeToFile(String imageFileName){
        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                String fileName = fileToSave.getAbsolutePath();
                //System.out.println("Save as file: " + fileName);
                
                BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
                out.write("___________________________________________");
                out.newLine();
                out.write("Image: "+imageFileName);
                out.newLine();
                out.write("Mouse fur color RGB value: ("+redInCoat+","+greenInCoat+","+blueInCoat+")");
                out.newLine();
                out.write("Mouse tail length (pixels): "+tailLength);
                out.newLine();
                out.write("Mouse body length (pixels): "+bodyLength);
                out.newLine();
                out.write("Tail:Body ratio: "+tailLength/bodyLength);
                out.newLine();
                out.write("Mouse tail colors: ("+tailColors[0].getRed()+","+tailColors[0].getGreen()+","+tailColors[0].getBlue()+") , ("+tailColors[1].getRed()+","+tailColors[1].getGreen()+","+tailColors[1].getBlue()+")");
                out.newLine();
                out.newLine();
                
                if(leucopusVotes>maniculatusVotes){
                    out.write("Best Guess: Mouse is P. Leucopus");
                    out.newLine();
                }
                else if(leucopusVotes<maniculatusVotes){
                    out.write("Best Guess: Mouse is P. Maniculatus");
                    out.newLine();
                }
                else{
                    out.write("It could be either P. Maniculatus or P. Leucopus.");
                    out.newLine();
                }
                java.util.Date date= new java.util.Date();
                String time = new Timestamp(date.getTime()).toString();
                out.write(time);
                out.newLine();
        
                out.write("___________________________________________");
                out.newLine();
                out.close();
            } catch (IOException e) {
                //System.out.println("Error writing to file");
            }
        }
    }
}
