package dataset_reading_classes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import java.util.Iterator;

import gp.GeneticProgram;
import gp.Node;

/**
    * DataReader is a class for processing the dataset in batches of 50 
 */
public class DataReader {
    private String filename;
    private LinkedHashSet<Map<Integer, Double>> dataSet;
    private final int batchSize = 100000;
    private final GeneticProgram gp;

    /**
     * @param filename The location of the .csv file being read. Use absolute path from the Project Root
     */
    public DataReader(String filename, GeneticProgram gp){
        this.filename = filename;
        this.gp = gp;
        dataSet = new LinkedHashSet<Map<Integer, Double>>();
    }

    /**
     * @brief This method reads the data from the file and sends it for processing in batches of 50.
     *          I am thinking it should take a parameter which is a class that does something with the data
     * You then want to generate an individual and store it in the population array. Then you want to apply the whole training dataset
     * to the program/tree and store the results somewhere. This might cause some memory issues but you'll have to see. Could potentially
     * write the results to a file if you cannot store everything in memory. Also to save memory you can lower population size
     */
    public void readData(){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            /*String[] keys = {
                "Duration","Distance","PLong","PLatd","DLong","DLatd","Haversine","Pmonth","PDay",
                "Phour","Pmin","PDWeek","Dmonth","Dday","Dhour","Dmin","DDweek","Temp",
                "Precip","Wind","Humid","Solar","Snow","GroundTemp","Dust"
            };*/
            //-1 will be for duration then 0 for distance, 1 for PLong etc
            int[] keys = {-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
            String line= "";

            reader.readLine();//skip this line as it contains only column names

            while((line = reader.readLine()) != null){
                int startIndex = 0;
                int endIndex = line.indexOf(',');
                int keyCounter = 0;
                Boolean skipFirstColumn = true;
                Map<Integer, Double> row = new HashMap<>();

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
                    processBatch(dataSet);

                    dataSet.clear();//remove all references from the data structure
                    dataSet = null;
                    dataSet = new LinkedHashSet<Map<Integer, Double>>();
                    System.gc();//Force Garbage collector to run
                }
            }

            if(!dataSet.isEmpty()){//read whatever is left in the dataset 
                //call a method to perform operations on the dataset
                //Issue here is that the remaining data is not a multiple of 10. We have 77234 left. Find solution to this.
                
                //System.out.println(dataSet.size());
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

    /**
     * @brief this is a method that will be used to process a batch of size batchSize
     */
    public void processBatch(LinkedHashSet<Map<Integer, Double>> batch){
        try{
            Node[] population = gp.getPopulation();
    
            for(int i=0;i < population.length;i++){
                Iterator<Map<Integer, Double>> it = batch.iterator();
                //System.out.println("Predicted val: " + population[i].evaluate(it.next()));
            }
        }
        catch(Exception e){
            System.out.println("Exception thrown in processBatch: " + e.getMessage());
        }
    }

}
