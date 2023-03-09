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
        FunctionNode bestIndividual = null;

        //randomly select the individuals for the tournament 
        for(int i=0;i<tournamentSize;i++){
            int randomIndex = random.nextInt(population.length);
            participants[i] = population[randomIndex];

        }

        //get individual with best fitness
        for(int i=0;i<tournamentSize;i++){
            if(i == 0){//a potential error could be if the population has a terminalNode tree somewhere, think of that
                bestIndividual = (FunctionNode)participants[i];
            }
            else{
                FunctionNode temp = (FunctionNode)participants[i];
                if(temp.getRawFitness() <= bestIndividual.getRawFitness())//can add optimization to get smallest tree perhaps?
                    bestIndividual = temp;
            }
        }

        return (Node)bestIndividual;
    }
}
