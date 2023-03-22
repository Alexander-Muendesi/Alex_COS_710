import java.util.Random;

import dataset_reading_classes.DataReader;
import gp.GeneticProgram;
import gp.Node;
import gp.TSelection;

public class App {
    public static void main(String[] args) throws Exception {
        // String filename = "Alex_COS_710/COS710_Assignments/src/dataset_reading_classes/dataset.csv";
        // String filename = "COS710_Assignments/src/dataset_reading_classes/data.csv";
        String filename = "data.csv";


        //max default depth should be 6 from textbook
        //populattion size of 500 seems to be sweet spot from textbook. Rarely need more
        final int populationSize = 100;
        final int maxDepth = 10;
        // final int seed = 808;//seed causing errors
        // final int seed = 11;
        int seed = 1800;
        final int tournamentSize = 4;
        final int numGenerations = 50;//was 50
        final double mutationRate = 0.50;
        final double crossoverRate = 0.50;
        final int maxOffspringDepth = 2;


        

        for(seed=0; seed<= 50000;seed++){
            System.out.println("Seed: "+ seed);
            GeneticProgram gp = new GeneticProgram(populationSize, maxDepth,seed,tournamentSize, numGenerations,mutationRate,crossoverRate,
                                    maxOffspringDepth);
            //gp.generatePopulation();
            
            DataReader reader = new DataReader(filename,gp);
            //reader.readData();
    
            TSelection tournament = new TSelection(tournamentSize, gp.getRandom());
            // System.out.println("Calling gp execute");
            gp.executeTraining(tournament, reader);
            System.out.println("******************************************************");
        }

    }
}

//note: those performance metric functions can be used as your Fitness functions I believe. The main idea is the following:
// you want to predict trip duration so you use a genetic program to find a solution. The terminals of the genetic program
// will be all the independent variables in the dataset except the trip duration which will be the actual trip duration 
// time we will be trying to get our program as close as possible to. The program output is the predicted value.
//Note:  you need to split your dataset into a training set and a test set. A good ratio is 70(training) : 30(testing)
