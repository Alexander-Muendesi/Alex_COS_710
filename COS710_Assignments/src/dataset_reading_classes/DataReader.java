package dataset_reading_classes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import gp.GeneticProgram;

/**
    * DataReader is a class for processing the dataset in batches of 50 
 */
public class DataReader {
    private String filename;
    private LinkedHashSet<Map<String, Double>> dataSet;
    private final int batchSize = 100000;

    /**
     * @param filename The location of the .csv file being read. Use absolute path from the Project Root
     */
    public DataReader(String filename){
        this.filename = filename;
        dataSet = new LinkedHashSet<Map<String, Double>>();
    }

    /**
     * @brief This method reads the data from the file and sends it for processing in batches of 50.
     *          I am thinking it should take a parameter which is a class that does something with the data
     * You then want to generate an individual and store it in the population array. Then you want to apply the whole training dataset
     * to the program/tree and store the results somewhere. This might cause some memory issues but you'll have to see. Could potentially
     * write the results to a file if you cannot store everything in memory. Also to save memory you can lower population size
     */
    public void readData(GeneticProgram gp){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String[] keys = {
                "Duration","Distance","PLong","PLatd","DLong","DLatd","Haversine","Pmonth","PDay",
                "Phour","Pmin","PDWeek","Dmonth","Dday","Dhour","Dmin","DDweek","Temp",
                "Precip","Wind","Humid","Solar","Snow","GroundTemp","Dust"
            };
            String line= "";

            reader.readLine();//skip this line as it contains only column names

            while((line = reader.readLine()) != null){
                int startIndex = 0;
                int endIndex = line.indexOf(',');
                int keyCounter = 0;
                Boolean skipFirstColumn = true;
                Map<String, Double> row = new HashMap<>();

                while(endIndex >= 0){//this innner while loop is meant to replace the .split method which is inefficient
                    if(!skipFirstColumn){
                        String elem = line.substring(startIndex, endIndex);
                        row.put(keys[keyCounter++],Double.parseDouble(elem));
                        startIndex = endIndex+1;
                        endIndex = line.indexOf(",", startIndex);
                    }
                    else{
                        startIndex = endIndex+1;
                        endIndex = line.indexOf(",", startIndex);
                        skipFirstColumn = false;
                    }
                }
                String elem = line.substring(startIndex);
                row.put(keys[keyCounter],Double.parseDouble(elem));//process last element

                dataSet.add(row);

                if(dataSet.size() == batchSize){
                    //call a method of some class to do some work on the current batch of 10 before moving to another batch

                    dataSet.clear();//remove all references from the data structure
                    dataSet = null;
                    dataSet = new LinkedHashSet<Map<String, Double>>();
                    System.gc();//Force Garbage collector to run
                }
            }

            if(!dataSet.isEmpty()){//read whatever is left in the dataset 
                //call a method to perform operations on the dataset
                
                dataSet.clear();
                dataSet = null;
                System.gc();
            }

        }
        catch (Exception e) {
            System.out.print("Error Reading file in DataReader.java. \nError Message: ");
            System.out.println( e.getMessage());

        }
    }

}
