package gp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Tournament selection class
public class TSelection {
    private final int tournamentSize;
    private final Random random;
    private final Node[] participants;
    private int index = 0;//used for accessing participants array when adding elements
    private List<Integer> nonFreeParents;

    /**
     * 
     * @param tournamentSize
     * @param seed
     */
    public TSelection(int tournamentSize, Random random){
        this.tournamentSize = tournamentSize;
        this.random = random;
        participants = new Node[tournamentSize];
        nonFreeParents = new ArrayList<Integer>();
    }

    /**
     * 
     * @param population
     */
    public Node calcTSelection(Node[] population){
        FunctionNode bestIndividual = null;

        //randomly select the individuals for the tournament 
        for(int i=0;i<tournamentSize;i++){
            int randomIndex = getRandomIndex(population.length);
            participants[i] = population[randomIndex];

        }

        //get individual with best fitness
        for(int i=0;i<tournamentSize;i++){
            if(i == 0){
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

    public int getRandomIndex(int populationSize){
        int randomIndex = random.nextInt(populationSize);

        if(nonFreeParents.isEmpty() == false && nonFreeParents.contains(randomIndex) == false){
            nonFreeParents.add(randomIndex);
            return randomIndex;
        }
        else{
            randomIndex = getRandomIndex(populationSize);
            nonFreeParents.add(randomIndex);

            return randomIndex;
        }
    }
}
