import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.sql.Timestamp;
import java.util.Date;
import java.nio.file.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.table.*;
import javax.swing.JTable;
import java.io.FileWriter;

/**
 * The WriteExcel class is meant to process the information from a Mouse object and put it into an Excel file format.
 * This class handles writing to XLS and XLSX filetypes.
 * 
 * @author Margo Morton
 * @version 3/31/2015
 */
public class WriteExcel {
    private String inputFile;
    private Mouse myMouse;
    private String imagePath;

    
    /**
     * Basic "setter" function for the Mouse object that will be written to a file
     * @param m The Mouse to save. Must have completed analysis.
     */
    public void setMouse(Mouse m){
        myMouse = m;
    }
    
    /**
     * Basic "setter" function for the filepath to write to.
     * @param inputFile The file that the Mouse data will be saved to. It may or may not refer to an already existing file.
     */
    public void setOutputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Basic "setter" function for the imagepath associated with the Mouse object.
     * @param path Image path of Mouse that will be stored in the Excel file
     */
        public void setImagePath(String path){
        imagePath = path;
    }
    
    /**
     * This function formats and writes the data from the Mouse object to the chosen filepath. This function will handle writing
     * to preexisting files as well as new files.
     */
    public void write() throws IOException {
        if(! (inputFile.endsWith(".xls") || inputFile.endsWith(".xlsx"))){
            inputFile = inputFile+".xls";
        }
        File file = new File(inputFile);
        java.util.Date date= new java.util.Date();
        String time = new Timestamp(date.getTime()).toString();

        Double percentage = Double.parseDouble(myMouse.calculateLogSpecies()) *100;
        if (percentage < 50){
            percentage = 100 - percentage;
        }
        
        try{
            if (! file.exists()) { // file is new, need to add headers
                String[] columnNames = {"Species",
                            "Accuracy",
                            "R",
                            "G",
                            "B",
                            "Tail:Body Ratio",
                            "Ear:Body Ratio",
                            "Tail Color Distance",
                            "Image",
                            "Timestamp"
                        };

                Object[][] data = {
                        {myMouse.getSpecies(),
                            percentage, 
                            myMouse.getRedInCoat(), 
                            myMouse.getGreenInCoat(), 
                            myMouse.getBlueInCoat(), 
                            myMouse.getTailBodyRatio(),
                            myMouse.getEarBodyRatio(),
                            myMouse.getTailColorDistance(),
                            imagePath,
                            time
                        },
                    };
                JTable table = new JTable(data, columnNames);
                TableModel model = table.getModel();
                FileWriter excel = new FileWriter(file, true); //appends to the file

                for(int i = 0; i < model.getColumnCount(); i++){
                    // custom formatting of cell widths
                    if(i == 0){
                        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        table.getColumnModel().getColumn(i).setPreferredWidth(100);
                    }
                    if (i == 9){
                        table.getColumnModel().getColumn(i).setMinWidth(400);
                        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                    }
                    excel.write(model.getColumnName(i) + "\t");
                }

                excel.write("\n");

                for(int i=0; i< model.getRowCount(); i++) {
                    for(int j=0; j < model.getColumnCount(); j++) {
                        // custom formatting of cell widths
                        if(j == 0){
                            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            table.getColumnModel().getColumn(0).setPreferredWidth(100);
                            table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                        }
                        if (j == 9){
                            table.getColumnModel().getColumn(i).setMinWidth(400);
                            table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                        }
                        excel.write(model.getValueAt(i,j).toString()+"\t");
                                                

                    }
                    excel.write("\n");
                }

                excel.close();

            }
            else{
                String[] columnNames = {"Species",
                            "Accuracy",
                            "R",
                            "G",
                            "B",
                            "Tail:Body Ratio",
                            "Ear:Body Ratio",
                            "Tail Color Distance",
                            "Image",
                            "Timestamp"
                        };

                Object[][] data = {
                        {myMouse.getSpecies(),
                            percentage, 
                            myMouse.getRedInCoat(), 
                            myMouse.getGreenInCoat(), 
                            myMouse.getBlueInCoat(), 
                            myMouse.getTailBodyRatio(),
                            myMouse.getEarBodyRatio(),
                            myMouse.getTailColorDistance(),
                            imagePath,
                            time
                        },
                    };
                JTable table = new JTable(data, columnNames);
                TableModel model = table.getModel();
                FileWriter excel = new FileWriter(file, true); //appends to the file

                for(int i=0; i< model.getRowCount(); i++) { // append data to the excel file, one cell at a time
                    for(int j=0; j < model.getColumnCount(); j++) {
                        excel.write(model.getValueAt(i,j).toString()+"\t");
                        
                        // custom formatting of cell widths
                        if(j == 0){
                            table.getColumnModel().getColumn(0).setPreferredWidth(100);
                        }
                    }
                    excel.write("\n");
                }

                excel.close();

            }

        }catch(IOException e){ 
            e.printStackTrace();
        }
    }
} 