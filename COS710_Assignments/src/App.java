import dataset_reading_classes.DataReader;

public class App {
    public static void main(String[] args) throws Exception {
        String filename = "Alex_COS_710/COS710_Assignments/src/dataset_reading_classes/dataset.csv";
        DataReader reader = new DataReader(filename);
        reader.readData();
    }
}
