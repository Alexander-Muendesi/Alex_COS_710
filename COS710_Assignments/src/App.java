import dataset_reading_classes.DataReader;
import gp.GeneticProgram;
import gp.TSelection;

public class App {
    public static void main(String[] args) throws Exception {
        // String filename = "Alex_COS_710/COS710_Assignments/src/dataset_reading_classes/150kData.csv";
        // String filename = "COS710_Assignments/src/dataset_reading_classes/150kData.csv";
        String filename = "150kData.csv";
        int populationSize = 100;
        // int maxDepth = 10;
        int maxDepth = 6;
        int seed = 0;
        int tournamentSize = 4;
        // int numGenerations = 50;
        int numGenerations = 150;
        // double mutationRate = 0.70;
        double mutationRate = 0.70;
        // double crossoverRate = 0.30;
        double crossoverRate = 0.30;
        // int maxOffspringDepth = 2;
        int maxOffspringDepth = 1;
        // int []seeds = {0,1,2,3,5,7,8,10,11,15};
        int []seeds = {4,6,9,12,13,14,16};

        
        
            System.out.println("\n************************************************************************************");
            System.out.println("Seed: "+ seed);
            System.out.println("Population Size: " + populationSize);
            System.out.println("Max Depth: "  + maxDepth);
            System.out.println("Tournament Size: " + tournamentSize);
            System.out.println("numGenerations: " + numGenerations);
            System.out.println("mutation rate: " + mutationRate);
            System.out.println("crossover rate: " + crossoverRate);
            System.out.println("max offspring depth: " + maxOffspringDepth);
            GeneticProgram gp = new GeneticProgram(populationSize, maxDepth,seed,tournamentSize, numGenerations,mutationRate,crossoverRate,
            maxOffspringDepth);

            DataReader reader = new DataReader(filename,gp);

            TSelection tournament = new TSelection(tournamentSize, gp.getRandom());
            gp.executeTraining(tournament, reader);


        
    }
}