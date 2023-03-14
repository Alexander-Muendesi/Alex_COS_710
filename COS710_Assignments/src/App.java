import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import dataset_reading_classes.DataReader;
import gp.GeneticProgram;
import gp.Node;
import gp.TSelection;

public class App {
    public static void main(String[] args) throws Exception {
        // String filename = "Alex_COS_710/COS710_Assignments/src/dataset_reading_classes/dataset.csv";
        String filename = "dataset.csv";

        //max default depth should be 6 from textbook
        //populattion size of 500 seems to be sweet spot from textbook. Rarely need more
        final int populationSize = 5;
        final int maxDepth = 5;
        final int numTerminals = 24;
        final int numFunctions = 4;
        final int seed = 808;
        final int tournamentSize = 4;

        GeneticProgram gp = new GeneticProgram(populationSize, maxDepth, numTerminals, numFunctions, seed,tournamentSize);
        gp.generatePopulation();
        
        DataReader reader = new DataReader(filename,gp);
        //reader.readData();

        TSelection tournament = new TSelection(tournamentSize, gp.getRandom());
        System.out.println("Calling gp execute");
        gp.execute(tournament, reader);
    }
}

//note: those performance metric functions can be used as your Fitness functions I believe. The main idea is the following:
// you want to predict trip duration so you use a genetic program to find a solution. The terminals of the genetic program
// will be all the independent variables in the dataset except the trip duration which will be the actual trip duration 
// time we will be trying to get our program as close as possible to. The program output is the predicted value.
//Note:  you need to split your dataset into a training set and a test set. A good ratio is 70(training) : 30(testing)
