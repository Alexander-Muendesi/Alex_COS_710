import java.util.Random;

import grammaticalEvolution.Chromosome;
import grammaticalEvolution.Codon;
import grammaticalEvolution.GE;

public class App {
    public static void main(String[] args) throws Exception {
        // String filename = "Alex_COS_710/COS710_Assignments/src/dataset_reading_classes/150kData.csv";
        // String filename = "COS710_Assignments/src/dataset_reading_classes/150kData.csv";
        String filename = "150kData.csv";
        int populationSize = 100;//was 100
        int seed = 47;
        int numGenerations = 50;
        double mutationRate = 0.1;
        double crossoverRate = 0.9;
        int tournamentSize = 4;
        int chromosomeLengthUpperLimit = 50;//there is 24 terminals overall
        int chromosomeLengthLowerLimit = 5;//was 10 before
        Random random = new Random(seed);

        GE ge = new GE(random, populationSize, mutationRate, crossoverRate, chromosomeLengthLowerLimit, chromosomeLengthUpperLimit, tournamentSize, numGenerations);
        ge.executeTraining();
        // ge.generateInitialPopulation();
        // for(Chromosome s: ge.getPopulation()){
        //     for(Codon c: s.getChromosome())
        //         System.out.print(c.getDenaryValue() + " ");
        //     System.out.println();
        // }
    }
}