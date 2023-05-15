package grammaticalEvolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chromosome {
    private Random random;
    private int numCodons;
    private int maxNumCodons;
    private List<Codon> chromosome;
    private final int CODON_LENGTH = 8;
    private int codonCounter = 0;
    private double rawFitness = Double.MAX_VALUE;

    public Chromosome(Random random, int numCodons, int maxNumCodons){
        this.random = random;
        this.numCodons = numCodons; 
        this.chromosome = new ArrayList<Codon>();
        this.maxNumCodons = maxNumCodons;

        generateCodons();
    }

    public Chromosome(Random random,List<Codon> chromosome, int numCodons, int maxNumCodons){
        this.random = random;
        this.chromosome = chromosome;
        this.numCodons = numCodons;
        this.maxNumCodons = maxNumCodons;
    }

    public double getRawFitness(){
        return this.rawFitness;
    }

    public void setRawFitness(double r){
        this.rawFitness = r;
    }

    private void generateCodons(){
        for(int i=0; i<numCodons;i++){
            StringBuilder builder = new StringBuilder();

            for(int j=0;j<CODON_LENGTH;j++)
                builder.append(random.nextInt(2));

            chromosome.add(new Codon(builder.toString().toCharArray()));
        }
    }

    public void mutate(){
        chromosome.get(random.nextInt(chromosome.size())).mutate(random.nextInt(CODON_LENGTH));
    }

    public void crossover(Chromosome secondParent, int crossoverIndex){
        List<Codon> currSublist = new ArrayList<>(this.chromosome.subList(0, crossoverIndex+1));
        List<Codon> secondTail = new ArrayList<>(secondParent.getChromosome().subList(crossoverIndex+1, secondParent.getChromosomeLength()));

        currSublist.addAll(secondTail);

        if(currSublist.size() > maxNumCodons)//trim the excess of if it exceeds the max codon length
            chromosome = new ArrayList<>(currSublist.subList(0, maxNumCodons+1));
        else
            chromosome = currSublist;
    }

    public int getChromosomeLength(){
        return this.chromosome.size();
    }

    public List<Codon> getChromosome(){
        return this.chromosome;
    }

    public Chromosome clone(){
        List<Codon> c = new ArrayList<Codon>();

        for(Codon cdn: chromosome)
            c.add(cdn.clone());

        return new Chromosome(random, c, numCodons, maxNumCodons);
    }

    // public Codon getCodon(){
    //     if(codonCounter < chromosome.size()){
    //         System.out.println("Codon counter: " + codonCounter + " size: " + chromosome.size());
    //         return chromosome.get(codonCounter++);
    //     }
    //     else{
    //         codonCounter=0;
    //         return chromosome.get(codonCounter);
    //     }
    // }

    public Codon getCodon(int index){
        return chromosome.get(index);
    }

    public void resetCodonCounter(){
        codonCounter = 0;
    }
}
