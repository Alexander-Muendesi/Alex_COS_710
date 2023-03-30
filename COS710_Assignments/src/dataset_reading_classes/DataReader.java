package dataset_reading_classes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import gp.GeneticProgram;
import gp.Node;
import performance_measures.MAE;
import performance_measures.MedianAbsoluteDev;
import performance_measures.RMSD;
import performance_measures.RSquared;

public class DataReader {
    private String filename;
    private LinkedHashSet<Map<Integer, Double>> dataSet;
    private final int batchSize = 1500000;
    private final GeneticProgram gp;
    // private final int trainingLimit = 700000;//first 6.7 million lines will be training data
    private final int trainingLimit = 105000;//first 6.7 million lines will be training data

    private int numItems = 0;//used to keep track of the number of items in the dataset

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
    public void trainData(){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            /*String[] keys = {
                "Duration","Distance","PLong","PLatd","DLong","DLatd","Haversine","Pmonth","PDay",
                "Phour","Pmin","PDWeek","Dmonth","Dday","Dhour","Dmin","DDweek","Temp",
                "Precip","Wind","Humid","Solar","Snow","GroundTemp","Dust"
            };*/
            dataSet = new LinkedHashSet<Map<Integer, Double>>();
            //-1 will be for duration then 0 for distance, 1 for PLong etc
            int[] keys = {-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
            String line= "";

            reader.readLine();//skip this line as it contains only column names

            double numLines = 0;
            //total number of lines = 9601139.0
            //total lines read in map = 9577234.0;
            while((line = reader.readLine()) != null){
                if(numLines == trainingLimit){
                    processBatch(dataSet);
                    dataSet.clear();//remove all references from the data structure
                    dataSet = null;
                    dataSet = new LinkedHashSet<Map<Integer, Double>>();
                    System.gc();//Force Garbage collector to run
                    break;
                }
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
                    // numLines += dataSet.size();

                    dataSet.clear();//remove all references from the data structure
                    dataSet = null;
                    dataSet = new LinkedHashSet<Map<Integer, Double>>();
                    System.gc();//Force Garbage collector to run
                }
                numLines++;
            }

            if(!dataSet.isEmpty()){//read whatever is left in the dataset 
                //call a method to perform operations on the dataset
                //Issue here is that the remaining data is not a multiple of 10. We have 77234 left. Find solution to this.
                
                //System.out.println(dataSet.size());
                numLines += dataSet.size();

                dataSet.clear();
                dataSet = null;
                System.gc();
            }

            reader.close();//release the system resources
        }
        catch (Exception e) {
            System.out.print("Error Reading file in DataReader.java. \nError Message: ");
            System.out.println( e.getMessage());
            e.printStackTrace();

        }
    }

    /**
     * This is a method that will be used to process a batch of size batchSize
     */
    public void processBatch(LinkedHashSet<Map<Integer, Double>> batch){
        try{
            Node[] population = gp.getPopulation();

            for(int i=0;i < population.length;i++){
                Iterator<Map<Integer, Double>> it = batch.iterator();
                Node temp = population[i];
                while(it.hasNext()){
                        Map<Integer, Double> tempBatch = it.next();
                        double predictedVal;
                        predictedVal = temp.evaluate(tempBatch);
                        temp.calcRawFitness(tempBatch.get(-1),predictedVal);//-1 is index for the bike duration trip
                }
            }
    
            // for(int i=0;i < population.length;i++){
            //     Iterator<Map<Integer, Double>> it = batch.iterator();
            //     Node temp = population[i];
            //     Map<Integer, Double> tempBatch = it.next();
            //     double predictedVal = temp.evaluate(tempBatch);
            //     temp.calcRawFitness(tempBatch.get(-1),predictedVal);//-1 is index for the bike duration trip
            // }
        }
        catch(Exception e){
            System.out.println("Exception thrown in processBatch: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method gets the average of the dataset as well as find the number of items in the dataset.
     * @return
     */
    public double getDatasetAverage(){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            reader.readLine();//skip first line with headings

            String line = "";
            double result = 0;
            int counter = 0;
            while((line = reader.readLine()) != null){
                if(counter++ > trainingLimit){
                    //skip the first column which is just a counter
                    int startIndex = 0;
                    int endIndex = line.indexOf(",");
                    startIndex = endIndex + 1;
                    endIndex = line.indexOf(",", startIndex);
    
                    // System.out.println(line.substring(startIndex,endIndex));
                    result += Double.valueOf(line.substring(startIndex,endIndex));
                    numItems++;
                }
            }
            reader.close();
            return result / numItems;
            
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void testData(Node best, MAE mae,MedianAbsoluteDev mad, RSquared rSquared, RMSD rmsd){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            System.gc();
            int numLines = 0;
            dataSet = new LinkedHashSet<Map<Integer, Double>>();

            //-1 will be for duration then 0 for distance, 1 for PLong etc
            int[] keys = {-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
            String line= "";

            reader.readLine();//skip the heading line

            while((line = reader.readLine()) != null){
                if(numLines >= trainingLimit){//skip the training data
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
                        processTestBatch(dataSet,best,mae,mad,rSquared,rmsd);
                        dataSet = new LinkedHashSet<Map<Integer, Double>>();
                        System.gc();
                    }
                }
                numLines++;
            }
            reader.close();
            //check if dataSet is empty. If not process whatever remains there
            if(dataSet.isEmpty() == false){
                processTestBatch(dataSet,best,mae,mad,rSquared,rmsd);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void processTestBatch(LinkedHashSet<Map<Integer, Double>> data, Node best, MAE mae,MedianAbsoluteDev mad, RSquared rSquared, RMSD rmsd){
        try {
            for(Map<Integer,Double> val :  data){
                double predictedVal = best.evaluate(val);

                mae.calcDiff(val.get(-1), predictedVal);
                mad.calcAbsValue(val.get(-1), predictedVal);
                rSquared.calcRss(val.get(-1), predictedVal);
                rSquared.calcTss(val.get(-1));
                rmsd.sumSqauredDifference(val.get(-1), predictedVal);

                best.calcRawFitness(val.get(-1), predictedVal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
