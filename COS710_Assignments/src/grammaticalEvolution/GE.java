package grammaticalEvolution;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;

import dataset_reading_classes.DataReader;

public class GE {
    private Random random;
    private int populationSize;
    private double mutationRate, crossoverRate;
    private int lowerLengthLimit, upperLengthLimit, tournamentSize, maxGenerations;
    private LinkedHashSet<Chromosome> population;
    private final String filename = "150kData.csv";
    private LinkedHashSet<Map<Integer, Double>> dataset;
    private final TournamentSelection tournament;


    public GE(Random random, int populationSize, double mutationRate, double crossoverRate, int lowerLengthLimit, int upperLengthLimit, int tournamentSize,
                int maxGenerations){
        this.random = random;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.lowerLengthLimit = lowerLengthLimit;
        this.upperLengthLimit = upperLengthLimit;
        this.tournamentSize = tournamentSize;
        this.population = new LinkedHashSet<Chromosome>();
        this.maxGenerations = maxGenerations;
        DataReader reader = new DataReader(filename);
        reader.readData();

        tournament = new TournamentSelection(tournamentSize, random);
        dataset = reader.getDataset();
    }

    public void generateInitialPopulation(){
        for(int i = 0;i<populationSize;i++){
            population.add(new Chromosome(random, random.nextInt(lowerLengthLimit, upperLengthLimit+1), upperLengthLimit));
        }
    }

    public double expression(Chromosome c, Map<Integer, Double> data, int codonIndex) throws Exception{
        // for(Codon cc: c.getChromosome())
            // System.out.print(cc.getDenaryValue() + " ");

        codonIndex = (codonIndex < c.getChromosomeLength()) ? codonIndex : 0;

        Codon codon = c.getCodon(codonIndex);
        // System.out.println("codon value: " + codon.getDenaryValue() + " codon index: " + codonIndex);
        int productionRule = codon.getDenaryValue() % 2;

        // System.out.println("\n ProductionRule: " + productionRule);
        if(productionRule == 0){
            //call expression
            // System.out.println("In rule 0");
            double left = expression(c, data, codonIndex+1);
            //call operator
            char  op = operator(c.getCodon(codonIndex));
            //call expression again
            double right = expression(c, data, codonIndex+1);
            // System.out.println("after right");
            return applyOperator(left,right,op);
        }
        else{
            return terminal(c.getCodon(codonIndex), data);
        }
    }

    public double applyOperator(double left, double right,char op) throws Exception{
        switch (op) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '*':
                return left * right;
            case '/':
                if(right == 0)
                    return 1;
                else
                    return left / right;
            default:
                throw new Exception("Invalid operator");
        }

    }

    public char operator(Codon codon){
        int productionRule = codon.getDenaryValue() % 4;

        switch (productionRule) {
            case 0:
                return '+';
            case 1:
                return '-';
            case 2:
                return '*';
            case 3:
                return '/';
            default:
                return 'E';
        }
    }

    public double terminal(Codon codon, Map<Integer, Double> data){
        int productionRule = codon.getDenaryValue() % 24;//we have 24 input data elements to consider
        return data.get(productionRule);

    }

    //type represents a boolean value for whether trainig data or test data. true = training data, false = test data
    public double evaluateIndividual(Chromosome chromosome, boolean type){
        //note as you are running through the training/test data for the individual you want to reset the codonCounter
        //in chromosome to zero so that the same individual is always produced.

        int maxTrainingCounter = (int)(dataset.size() * 0.7);//only read 70% of the data for training
        int counter = type == true ? 0 : maxTrainingCounter+1;

        double rawFitness = 0.0;

        try {
            for(Map<Integer,Double> m: dataset){
                chromosome.resetCodonCounter();
                if(counter <= maxTrainingCounter){
                    rawFitness += Math.abs(m.get(-1) - expression(chromosome, m, 0));
                }
                else
                    break;
                counter++;
            }  
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //either return the raw fitness or an array containing all the performance metrics
        //alternatively you can just store those values in the chromosome
        chromosome.setRawFitness(rawFitness);
        return rawFitness;
    }

    /**
     * True is for training data, false is for test data
     * @param type True=training, False=test
     */
    private Chromosome evaluatePopulation(boolean type){
        Chromosome bestChromosome = null;
        int counter = 0;
        for(Chromosome chromosome: population){
            double fitness = evaluateIndividual(chromosome, type);
            // System.out.println("Just after evaluate individual function call: " + fitness);
            if(counter == 0){
                bestChromosome = chromosome;
                counter++;
            }
            else{
                if(fitness < bestChromosome.getRawFitness())//can change this to RSquared maybe later
                    bestChromosome = chromosome;
            }
        }

        return bestChromosome;
    }

    private void generateNewPopulation(){
        LinkedHashSet<Chromosome> newPopulation = new LinkedHashSet<Chromosome>();

        int crossoverEnd = (int)(crossoverRate * populationSize);

        //create new population with crossover
        Chromosome[] pop = population.toArray(new Chromosome[population.size()]);
        for(int i=0;i<crossoverEnd;i++){
            Chromosome one = tournament.selectIndividual(pop);
            Chromosome two = tournament.selectIndividual(pop);

            int shortestChromosomeLength = (one.getChromosomeLength() <= two.getChromosomeLength()) ? 
                                            (one.getChromosomeLength()) :(two.getChromosomeLength());

            int crossoverIndex = random.nextInt(shortestChromosomeLength-1);

            Chromosome offSpringOne = one.clone();
            offSpringOne.crossover(two.clone(), crossoverIndex);

            Chromosome offSpringTwo = two.clone();
            offSpringTwo.crossover(one.clone(), crossoverIndex);

            newPopulation.add(offSpringOne);
            newPopulation.add(offSpringTwo);

        }

        //create rest of population with mutation
        for(int i=crossoverEnd;i<populationSize;i++){
            Chromosome one = tournament.selectIndividual(pop);
            Chromosome offSpringOne = one.clone();
            offSpringOne.mutate();
            newPopulation.add(offSpringOne);
        }

        population = newPopulation;
        System.gc();//clear memory
    }

    public LinkedHashSet<Chromosome> getPopulation(){
        return this.population;
    }

    public void executeTraining(){
        int counter = 0;
        generateInitialPopulation();
        //evaluate fitness of the population
        Chromosome best = evaluatePopulation(true);
        while(best.getRawFitness() < 500000 || counter < maxGenerations){
            //select individuals for mating
            //recombine individuals
            //evaluate fitness of offspring
            //replace all individuals in the population with offspring
            generateNewPopulation();
            best = evaluatePopulation(true);
            System.out.println("Raw fitness: " + best.getRawFitness());
        }
    }
}
