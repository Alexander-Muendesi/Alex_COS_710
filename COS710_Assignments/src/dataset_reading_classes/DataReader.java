package dataset_reading_classes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
    * DataReader is a class for processing the dataset in batches of 50 
 */
public class DataReader {
    private String filename;
    private LinkedHashSet<Map<String, Double>> dataSet;
    private final int batchSize = 50;

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
     */
    public void readData(){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line= "";
            // double count = 0;

            reader.readLine();//skip this line as it contains only column names

            while((line = reader.readLine()) != null){
                String[] vals = line.split(",");
                Map<String, Double> row = new HashMap<>();
                int counter = 1;//starting at index 1 because index 0 is just line number

                row.put("Duration", Double.parseDouble(vals[counter++]));
                row.put("Distance", Double.parseDouble(vals[counter++]));
                row.put("PLong", Double.parseDouble(vals[counter++]));
                row.put("PLatd", Double.parseDouble(vals[counter++]));
                row.put("DLong", Double.parseDouble(vals[counter++]));
                row.put("Haversine", Double.parseDouble(vals[counter++]));
                row.put("Pmonth", Double.parseDouble(vals[counter++]));
                row.put("Pday", Double.parseDouble(vals[counter++]));
                row.put("Phour", Double.parseDouble(vals[counter++]));
                row.put("Pmin", Double.parseDouble(vals[counter++]));
                row.put("PDWeek", Double.parseDouble(vals[counter++]));
                row.put("Dmonth", Double.parseDouble(vals[counter++]));
                row.put("Dday", Double.parseDouble(vals[counter++]));
                row.put("Dhour", Double.parseDouble(vals[counter++]));
                row.put("Dmin", Double.parseDouble(vals[counter++]));
                row.put("DDweek", Double.parseDouble(vals[counter++]));
                row.put("Temp", Double.parseDouble(vals[counter++]));
                row.put("Precip", Double.parseDouble(vals[counter++]));
                row.put("Wind", Double.parseDouble(vals[counter++]));
                row.put("Humid", Double.parseDouble(vals[counter++]));
                row.put("Solar", Double.parseDouble(vals[counter++]));
                row.put("Snow", Double.parseDouble(vals[counter++]));
                row.put("GroundTemp", Double.parseDouble(vals[counter++]));
                row.put("Dust", Double.parseDouble(vals[counter++]));

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
