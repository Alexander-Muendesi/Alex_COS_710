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

        dataset = reader.getDataset();
    }

    public void generateInitialPopulation(){
        for(int i = 0;i<populationSize;i++){
            population.add(new Chromosome(random, random.nextInt(lowerLengthLimit, upperLengthLimit+1), upperLengthLimit));
        }
    }

    public double expression(Chromosome c, Map<Integer, Double> data) throws Exception{
        Codon codon = c.getCodon();
        int productionRule = codon.getDenaryValue() % 2;

        if(productionRule == 0){
            //call expression
            double left = expression(c, data);
            //call operator
            char  op = operator(c.getCodon());
            //call expression again
            double right = expression(c, data);
            return applyOperator(left,right,op);
        }
        else{
            return terminal(c.getCodon(), data);
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
        int productionRule = codon.getDenaryValue() % 25;//we have 24 input data elements to consider
        return data.get(productionRule);

    }

    //type represents a boolean value for whether trainig data or test data. true = training data, false = test data
    public void evaluateIndividual(Chromosome chromosome, boolean type){
        //note as you are running through the training/test data for the individual you want to reset the codonCounter
        //in chromosome to zero so that the same individual is always produced.

        int maxTrainingCounter = (int)(dataset.size() * 0.7);//only read 70% of the data for training
        int counter = type == true ? 0 : maxTrainingCounter+1;

        double rawFitness = 0.0;

        try {
            for(Map<Integer,Double> m: dataset){
                chromosome.resetCodonCounter();
                if(counter <= maxTrainingCounter){
                    rawFitness += Math.abs(m.get(-1) - expression(chromosome, m));
                }
                else
                    break;
            }  
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //either return the raw fitness or an array containing all the performance metrics
    }

    

    public void execute(){
        int counter = 0;

        while(counter < maxGenerations){
            //select individuals for mating
            //recombine individuals
            //evaluate fitness of offspring
            //replace all individuals in the population with offspring
        }
    }
}
