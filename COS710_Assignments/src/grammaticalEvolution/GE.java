package grammaticalEvolution;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;

import dataset_reading_classes.DataReader;
import performance_measures.MAE;
import performance_measures.MedianAbsoluteDev;
import performance_measures.RMSD;
import performance_measures.RSquared;

public class GE {
    private Random random;
    private int populationSize;
    private double mutationRate, crossoverRate;
    private int lowerLengthLimit, upperLengthLimit, tournamentSize, maxGenerations;
    private LinkedHashSet<Chromosome> population;
    private final String filename = "150kData.csv";
    private LinkedHashSet<Map<Integer, Double>> dataset;
    private final TournamentSelection tournament;
    private double datasetAverage = 0.0;
    private MAE mae;
    private MedianAbsoluteDev mDev;
    private RMSD rmsd;
    private RSquared rSquared;


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

    //true is for training data, false is for test data
    public double calcDatasetAverage(boolean type){
        int maxTrainingCounter = (int)(dataset.size() * 0.7);//only read 70% of the data for training
        int counter = type == true ? 0 : maxTrainingCounter+1;
        double average = 0.0;

        if(type){
            for(Map<Integer,Double> m: dataset){
                if(counter <= maxTrainingCounter){
                    average += m.get(-1);
                }
                else 
                    break;
                counter++;
            }
            average /= maxTrainingCounter;

        }
        else{
            for(Map<Integer,Double> m: dataset){
                if(counter >= maxTrainingCounter){
                    average += m.get(-1);
                }
                counter++;
            }
            average /= (dataset.size()-maxTrainingCounter);
        }
        return average;
    }

    public void generateInitialPopulation(){
        for(int i = 0;i<populationSize;i++){
            population.add(new Chromosome(random, random.nextInt(lowerLengthLimit, upperLengthLimit+1), upperLengthLimit));
        }
    }

    private static int codonCounter = 0;
    public double expression(Chromosome c, Map<Integer, Double> data, int codonIndex) throws Exception{
        codonCounter = (codonCounter < c.getChromosomeLength()) ? codonCounter : codonCounter % c.getChromosomeLength();

        Codon codon = c.getCodon(codonCounter++);
        int productionRule = codon.getDenaryValue() % 2;

        if(productionRule == 0){//<expr> op <function>
            //call expression
            double left = expression(c, data, codonIndex+1);

            //call operator
            codonCounter++;
            codonCounter = (codonCounter < c.getChromosomeLength()) ? codonCounter : codonCounter % c.getChromosomeLength();
            char  op = operator(c.getCodon(codonCounter++));
            //call function
            codonCounter = (codonCounter < c.getChromosomeLength()) ? codonCounter : codonCounter % c.getChromosomeLength();
            codon = c.getCodon(codonIndex++);
            codonCounter = (codonCounter < c.getChromosomeLength()) ? codonCounter : codonCounter % c.getChromosomeLength();
            double right = function(codon, c.getCodon(codonCounter), data);

            return applyOperator(left,right,op);
        }
        else{
            codonCounter = (codonCounter < c.getChromosomeLength()) ? codonCounter : codonCounter % c.getChromosomeLength();
            return terminal(c.getCodon(codonCounter++), data);
            // return terminal(c.getCodon(codonIndex), data);
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

    public double function(Codon currCodon,Codon nextCodon, Map<Integer, Double> data){
        int productionRule = currCodon.getDenaryValue() % 4;
        switch(productionRule){
            case 0:
                return Math.sqrt(Math.abs(terminal(nextCodon, data)));
            case 1:
                return Math.sin(terminal(nextCodon, data));
            case 2:
                return Math.cos(terminal(nextCodon, data));
            case 3:{
                double result = terminal(nextCodon, data);
                if(result == 0)return 1;
                return Math.log(Math.abs(result));
            }
            default:
                return 1;
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
    //best=true means we are evaluating the best individual otherwise not
    public double evaluateIndividual(Chromosome chromosome, boolean type, boolean best){
        //note as you are running through the training/test data for the individual you want to reset the codonCounter
        //in chromosome to zero so that the same individual is always produced.

        int maxTrainingCounter = (int)(dataset.size() * 0.7);//only read 70% of the data for training
        int counter = type == true ? 0 : maxTrainingCounter+1;

        double rawFitness = 0.0;

        if(type){
            try {
                for(Map<Integer,Double> m: dataset){
                    chromosome.resetCodonCounter();
                    if(counter <= maxTrainingCounter){
                        codonCounter = 0;
                        double predictedVal = expression(chromosome, m, 0);
                        rawFitness += Math.abs(m.get(-1) - predictedVal);
                        if(best){
                            mae.calcDiff(m.get(-1), predictedVal);
                            mDev.calcAbsValue(m.get(-1), predictedVal);
                            rmsd.sumSqauredDifference(m.get(-1), predictedVal);
                            rSquared.calcRss(m.get(-1), predictedVal);
                            rSquared.calcTss(m.get(-1));
                        }
                    }
                    else{
                        break;
                    }
                    counter++;
                }  
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                for(Map<Integer,Double> m: dataset){
                    chromosome.resetCodonCounter();
                    if(counter >= maxTrainingCounter){
                        double predictedVal = expression(chromosome, m, 0);
                        rawFitness += Math.abs(m.get(-1) - predictedVal);

                        if(best){
                            mae.calcDiff(m.get(-1), predictedVal);
                            mDev.calcAbsValue(m.get(-1), predictedVal);
                            rmsd.sumSqauredDifference(m.get(-1), predictedVal);
                            rSquared.calcRss(m.get(-1), predictedVal);
                            rSquared.calcTss(m.get(-1));
                        }
                    }
                    counter++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            double fitness = evaluateIndividual(chromosome, type,false);
            // System.out.println("Fitness: " + fitness);
            // System.out.println("Just after evaluate individual function call: " + fitness);
            if(counter == 0){
                bestChromosome = chromosome;
                counter++;
            }
            else{
                if(fitness < bestChromosome.getRawFitness())//can change this to RSquared maybe later
                    bestChromosome = chromosome;
            }
            // System.out.println(counter + ": CodonCounter: " + codonCounter);
            counter++;
        }

        evaluateIndividual(bestChromosome, type, true);

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
            int crossoverIndexTwo;

            while(true){
                crossoverIndexTwo = random.nextInt(shortestChromosomeLength-1);
                if(crossoverIndexTwo != crossoverIndex)
                    break;
            }

            int smallestIndex = (crossoverIndex < crossoverIndexTwo) ? crossoverIndex:crossoverIndexTwo;
            int largetIndex =(crossoverIndex > crossoverIndexTwo) ? crossoverIndex:crossoverIndexTwo;

            Chromosome offSpringOne = one.clone();

            // offSpringOne.crossover(two.clone(), crossoverIndex);
            offSpringOne.crossover(two.clone(), smallestIndex,largetIndex);

            crossoverIndex = random.nextInt(shortestChromosomeLength-1);
            while(true){
                crossoverIndexTwo = random.nextInt(shortestChromosomeLength-1);
                if(crossoverIndexTwo != crossoverIndex)
                    break;
            }

            smallestIndex = (crossoverIndex < crossoverIndexTwo) ? crossoverIndex:crossoverIndexTwo;
            largetIndex =(crossoverIndex > crossoverIndexTwo) ? crossoverIndex:crossoverIndexTwo;

            Chromosome offSpringTwo = two.clone();
            // offSpringTwo.crossover(one.clone(), crossoverIndex);
            offSpringTwo.crossover(one.clone(), smallestIndex,largetIndex);

            if(newPopulation.size() < crossoverEnd)
                newPopulation.add(offSpringOne);

            if(newPopulation.size() < crossoverEnd)
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
        datasetAverage = calcDatasetAverage(true);
        mae = new MAE();
        mDev = new MedianAbsoluteDev(datasetAverage);
        rmsd = new RMSD();
        rSquared = new RSquared(datasetAverage);

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
            System.out.println(counter + ": Raw fitness: " + best.getRawFitness());
            
            counter++;
        }

        System.out.println("\nTrain data");
        System.out.println("MAE: " + mae.getMae());
        System.out.println("Median Absolute Deviation: "+mDev.getMedianValue());
        
        System.out.println("RMSD: " +rmsd.calcFinalResult());
        System.out.println("RSquared: " + rSquared.calcRSquared());

        executeTest(best);
    }

    public void executeTest(Chromosome best){
        datasetAverage = calcDatasetAverage(false);
        mae = new MAE();
        mDev = new MedianAbsoluteDev(datasetAverage);
        rmsd = new RMSD();
        rSquared = new RSquared(datasetAverage);

        evaluateIndividual(best, false, true);

        System.out.println("\nTest data");
        System.out.println("MAE: " + mae.getMae());
        System.out.println("Median Absolute Deviation: "+mDev.getMedianValue());
        
        System.out.println("RMSD: " +rmsd.calcFinalResult());
        System.out.println("RSquared: " + rSquared.calcRSquared());
    }
}
