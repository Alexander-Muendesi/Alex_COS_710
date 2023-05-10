package dataset_reading_classes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
public class DataReader {
    private String filename;
    private LinkedHashSet<Map<Integer, Double>> dataset;

    /**
     * @param filename The location of the .csv file being read. Use absolute path from the Project Root
     */
    public DataReader(String filename){
        this.filename = filename;
        dataset = new LinkedHashSet<Map<Integer, Double>>();
    }

    /**
     * This method reads the dataset into a LinkedHashSet
     */
    public void readData(){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            /*String[] keys = {
                "Duration","Distance","PLong","PLatd","DLong","DLatd","Haversine","Pmonth","PDay",
                "Phour","Pmin","PDWeek","Dmonth","Dday","Dhour","Dmin","DDweek","Temp",
                "Precip","Wind","Humid","Solar","Snow","GroundTemp","Dust"
            };*/
            dataset = new LinkedHashSet<Map<Integer, Double>>();
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
                dataset.add(row);
            }
            reader.close();//release the system resources
        }
        catch (Exception e) {
            System.out.print("Error Reading file in DataReader.java. \nError Message: ");
            System.out.println( e.getMessage());
            e.printStackTrace();

        }
    }

    public LinkedHashSet<Map<Integer, Double>> getDataset(){
        return this.dataset;
    }

    // /**
    //  * This method gets the average of the dataset as well as find the number of items in the dataset.
    //  * @return
    //  */
    // public double getdatasetAverage(){
    //     try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
    //         reader.readLine();//skip first line with headings

    //         String line = "";
    //         double result = 0;
    //         int counter = 0;
    //         while((line = reader.readLine()) != null){
    //             if(counter++ > trainingLimit){
    //                 //skip the first column which is just a counter
    //                 int startIndex = 0;
    //                 int endIndex = line.indexOf(",");
    //                 startIndex = endIndex + 1;
    //                 endIndex = line.indexOf(",", startIndex);
    
    //                 // System.out.println(line.substring(startIndex,endIndex));
    //                 result += Double.valueOf(line.substring(startIndex,endIndex));
    //                 numItems++;
    //             }
    //         }
    //         reader.close();
    //         return result / numItems;
            
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return -1;
    //     }
    // }

}
