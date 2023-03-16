package gp;

import java.util.Random;

//Tournament selection class
public class TSelection {
    private final int tournamentSize;
    private final Random random;
    private final Node[] participants;

    /**
     * Constructor which initializes the tournament size and the random number generator
     * @param tournamentSize
     * @param seed
     */
    public TSelection(int tournamentSize, Random random){
        this.tournamentSize = tournamentSize;
        this.random = random;
        participants = new Node[tournamentSize];
    }

    /**
     * Method which performs the tournament selection
     * @param population The population from which individuals are selected for tournament selection
     * @return Node which wins the tournament
     */
    public Node calcTSelection(Node[] population){
        //randomly select the individuals for the tournament 
        for(int i=0;i<tournamentSize;i++){
            int randomIndex = random.nextInt(population.length);
            participants[i] = population[randomIndex];

        }

        Node bestIndividual = participants[0];//initialize the best individual to be the first participant
        //get individual with best fitness
        for(int i=0;i<tournamentSize;i++){
                if(participants[i].getRawFitness() <= bestIndividual.getRawFitness())//can add optimization to get smallest tree perhaps?
                    bestIndividual = participants[i];
        }
        return bestIndividual;
    }
}
