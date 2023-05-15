package grammaticalEvolution;

import java.util.Random;

public class TournamentSelection {
    private final int tournamentSize;
    private final Random random;
    private Chromosome[] participants;
    
    public TournamentSelection(int tournamentSize, Random random){
        this.tournamentSize = tournamentSize;
        this.random = random;
        this.participants = new Chromosome[tournamentSize];
    }

    public Chromosome selectIndividual(Chromosome[] population){
        //randomly select individuals for the tournament
        for(int i=0;i<tournamentSize;i++){
            int randomIndex = random.nextInt(population.length);
            participants[i] = population[randomIndex];
        }

        Chromosome best = participants[0];

        for(int i=0;i<participants.length;i++){
            if(participants[i].getRawFitness() <= best.getRawFitness())
                best = participants[i];
        }

        return best;
    }
}
