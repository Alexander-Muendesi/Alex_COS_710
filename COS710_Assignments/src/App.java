import dataset_reading_classes.DataReader;
import gp.GeneticProgram;

public class App {
    public static void main(String[] args) throws Exception {
        String filename = "Alex_COS_710/COS710_Assignments/src/dataset_reading_classes/dataset.csv";

        GeneticProgram gp = new GeneticProgram(100, 10, 24, 4, 244);
        DataReader reader = new DataReader(filename);
        reader.readData(gp);
    }
}

//note: those performance metric functions can be used as your Fitness functions I believe. The main idea is the following:
// you want to predict trip duration so you use a genetic program to find a solution. The terminals of the genetic program
// will be all the independent variables in the dataset except the trip duration which will be the actual trip duration 
// time we will be trying to get our program as close as possible to. The program output is the predicted value.
//Note:  you need to split your dataset into a training set and a test set. A good ratio is 70(training) : 30(testing)
