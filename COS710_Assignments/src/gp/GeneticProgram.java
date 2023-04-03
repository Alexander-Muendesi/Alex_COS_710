package gp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;//used to generate a unique ID for each node

import dataset_reading_classes.DataReader;
import performance_measures.MAE;
import performance_measures.MedianAbsoluteDev;
import performance_measures.RMSD;
import performance_measures.RSquared;

//NB: Assume root node is depth 0
public class GeneticProgram {

    private final int populationSize;
    private final int maxDepth;
    private final int numTerminals = 24;

    private final int numFunctions = 4;
    private final int tournamentSize;
    private final Random random;

    private Node[] population;
    private final int numGenerations;
    private final double mutationRate;

    private final double crossoverRate;
    private final int maxOffspringDepth;
    public MAE meanAbsoluteError;

    public MedianAbsoluteDev meanAbsolDev;
    public RMSD rmsd;
    public RSquared rSquared;

    private final int maxGlobalDepthIndex = 3;//this is depth where we start counting the similarity
    private final int maxGlobalGenerationsIndex = 5;//allow 10 generations to pass for the global similar nodes to settle down

    //this will be the node against which the similarity index is calculated. Its only changed if the global nodes similarity are different from current
    //for 5 consercutive generations
    private Node globalSimilarityRoot = null;
    private Node tempGlobalSimilaryRoot = null;

    private final int globalSimilarityThreshhold = 2;
    private final int localSimilarityThreshhold = 4;//might need to change this
    private Node prevGlobalSimilarityRoot = null;

    /**
     * Constructor which initializes various constants for the genetic progrma
     * @param populationSize
     * @param maxDepth
     * @param seed
     * @param tournamentSize
     * @param numGenerations
     */
    public GeneticProgram(int populationSize,int maxDepth, int seed,int tournamentSize, int numGenerations,double mutationRate,
            double crossoverRate, int maxOffspringDepth){

        this.populationSize = populationSize;
        this.maxDepth = maxDepth;
        random = new Random(seed);
        population = new Node[populationSize];
        this.tournamentSize = tournamentSize;
        this.numGenerations = numGenerations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.maxOffspringDepth = maxOffspringDepth;
    }

    public Node[] getPopulation(){
        return this.population;
    }

    public int getPopulationSize(){
        return populationSize;
    }

    public int getTournamentSize(){
        return this.tournamentSize;
    }

    /**
     * This method basically returns the tree representing the program we created with the grow method
     * @return The root node
     */
    public Node generateIndividual(){
        //saying 1 + because we don't want a single node tree which is just a terminal. Bad for diversity
        int depthLimit = 1 + random.nextInt(maxDepth);
        int depthCounter = 0;

        Node result = grow(depthLimit,null,depthCounter);

        while(result instanceof FunctionNode == false){//avoid generating a single terminal node in population
            result = grow(depthLimit,null,depthCounter);
        }
        return result;//passing null as parent of root node
    }

    /**
     * The if part randomly chooses when to create a terminal node. In this case if the sum of the number of functions and terminals
                used a upper bound to random is less than the number of terminals generate a terminal node
     * @param depthLimit How deep the tree is to grow
     * @param parent Parent Node of current node
     * @param depthCounter The current depth in the generation process
     * @return A node in the tree
     */
    public Node grow(int depthLimit, Node parent, int depthCounter){
        if(depthCounter == depthLimit || random.nextInt(numFunctions+numTerminals) < numFunctions){
            return new TerminalNode(random.nextInt(numTerminals),depthCounter,parent,UUID.randomUUID().toString());
        }
        else{
            FunctionNode function = new FunctionNode(random.nextInt(numFunctions),depthCounter,parent,UUID.randomUUID().toString());

            for(int i=0; i< function.getNumArguments();i++){
                function.setArgument(i, grow(depthLimit,function,depthCounter+1));
            }

            return function;
        } 
    }

    /**
     * This method creates a population of individuals/programs based on the populationSize parameter
     */
    public void generatePopulation(){
        for(int i=0; i< populationSize;i++)
            population[i] = generateIndividual();
    }

    /**
     * This method prints the individual program in a breadth first manner. Mainly for debugging purposes
     * @param root This is the root of the tree
     * @throws Exception 
     */
    public void printIndividual(Node root) throws Exception{
        if(root == null)
            return;
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(root);
        int currentDepth = root.getDepth();

        while(queue.isEmpty() == false){
            Node node = queue.remove();

            if(node.getDepth() > currentDepth || node.getDepth() == root.getDepth()){
                System.out.println();
                currentDepth = node.getDepth();
            }
            System.out.print("D("+ node.getDepth() + "): " + node.getValue() + " ");

            if(node instanceof FunctionNode){
                FunctionNode functionNode = (FunctionNode) node;
                queue.add(functionNode.getLeftChild());
                queue.add(functionNode.getRightChild());
            }
        }
        System.out.println();
    }

    /**
     * This method performs subtree crossover. If an offspring breaks the depth limit it returns one of its parents
     * NB: You have to keep an eye out for object comparison in the replaceSubtree method. .equals was overriden
     * to allow comparison of Nodes but I am in doubt as to whether I implemented it right
     * @param parentOne
     * @param parentTwo
     * @return Either one or two offspring
     */
    public Node[] subtreeCrossover(Node parentOne, Node parentTwo){
        Node first = cloneTree(parentOne.getRoot(),null);
        Node[] one = first.getAllNodes(first.getRoot());

        Node second = cloneTree(parentTwo.getRoot(),null);
        Node[] two = second.getAllNodes(second.getRoot());

        Node firstt = cloneTree(parentOne.getRoot(),null);
        Node[] onee = firstt.getAllNodes(firstt.getRoot());

        Node secondd = cloneTree(parentTwo.getRoot(),null);
        Node[] twoo = secondd.getAllNodes(secondd.getRoot());

        Node[] r = {cloneTree(parentOne.getRoot(),null),cloneTree(parentTwo.getRoot(),null)};

        //select a random node from each parent
        int index1 = random.nextInt(one.length);
        int index2 = random.nextInt(two.length);

        Node node1 = one[index1];//crossover point root
        Node node2 = two[index2];//crossover point root

        Node node11 = onee[index1];
        Node node22 = twoo[index2];

        Node offSpringOne = null;
        Node offSpringTwo = null;

        
        //for offspring1
        if(index1 == 0){//replacing root
            //replace the root node1 with node2
            node2.setParent(null);
            offSpringOne = node2;
        }
        else{//replacing non root
            Node parent = node1.getParent();   

            if(parent.getLeftChild().equals(node1)){
                parent.setLeftChild(node2);
                node2.setParent(parent);
                node1.setParent(null);
                offSpringOne = parent.getRoot();
            }
            else if(parent.getRightChild().equals(node1)){
                parent.setRightChild(node2);
                node2.setParent(parent);
                node1.setParent(null);
                offSpringOne = parent.getRoot();
            }
            else{
                System.out.println("error 200");
            }
        }
        //for offspring 2
        if(index2 == 0){//replacing root
            node11.setParent(null);
            offSpringTwo = node11;
        }
        else{
            Node parent = node22.getParent();
            if(parent.getLeftChild().equals(node22)){
                parent.setLeftChild(node11);
                node11.setParent(parent);
                node22.setParent(null);
                offSpringTwo = parent.getRoot();
            }
            else if(parent.getRightChild().equals(node22)){
                parent.setRightChild(node11);
                node11.setParent(parent);
                node22.setParent(null);
                offSpringTwo = parent.getRoot();
            }
            else{
                System.out.println("error 223");
            }
        }
        //fix the depth values and see if maxDepth has been exceeded
        Boolean oneResult = fixDepth(offSpringOne, 0);
        Boolean twoResult = fixDepth(offSpringTwo, 0);

        if(oneResult && twoResult){//both offspring break max depth limit/ randomly return one of the parents

            Node[] temp = {r[random.nextInt(2)]}; 
            fixDepth(temp[0].getRoot(), 0);//just call it to make the clone have different ID's
            return temp;
        }
        else if(oneResult){//first offspring break depth limit. Return one of its parents and the second offspring
            Node[] temp = {offSpringTwo,r[random.nextInt(2)]};
            fixDepth(temp[1].getRoot(), 0);//just call it to make the clone have different ID's
            return temp;
        }
        else if(twoResult){//second offspring breaks depth limit. Return one of its parents and the first offspring
            Node[] temp = {offSpringOne,r[random.nextInt(2)]};
            fixDepth(temp[1].getRoot(), 0);//just call it to make the clone have different ID's
            return temp;
        }
        else{//none of the offspring break depth limit. Return both offspring
            Node[] temp = {offSpringOne,offSpringTwo};
            return temp;
        }
    }

    public Node cloneTree(Node root, Node parent){
        if(root == null){
            return null;
        }
        else if(root instanceof FunctionNode){
            Node newNode = new FunctionNode(root.getIndex(), root.getDepth(), parent, root.getRawFitness(), 
                            root.getID());
            newNode.setLeftChild(cloneTree(root.getLeftChild(),newNode));
            newNode.setRightChild(cloneTree(root.getRightChild(),newNode));

            FunctionNode rtemp = (FunctionNode)root;
            FunctionNode ntemp = (FunctionNode)newNode;

            //make sure the arguments have references to the clone nodes not the orginal ones
            for(int i=0;i<rtemp.getNumArguments();i++)
            {
                if(i==0)
                    ntemp.setArgument(i,ntemp.getLeftChild());
                else
                    ntemp.setArgument(i,ntemp.getRightChild());
            }

            newNode = (Node)ntemp;

            return newNode;
        }
        else if(root instanceof TerminalNode){//assume instance of terminal Node
            Node newNode = new TerminalNode(root.getIndex(), root.getDepth(), parent, root.getID());
            newNode.setLeftChild(cloneTree(root.getLeftChild(), newNode));
            newNode.setRightChild(cloneTree(root.getRightChild(), newNode));

            return newNode;
        }
        else{
            System.out.println("Error 291");
            return null;
        }
    }

    /**
     * After a subtree crossover operation this function goes through a tree and fixes depth values
     * @param root
     * @param depth
     * @return True means me have exceeded max depth. False means we have not exceeded max depth
     */
    public boolean fixDepth(Node root, int depth){
        if(root == null)
            return false;
        else if(depth > maxDepth){
            root.setDepth(depth);
            root.setID(UUID.randomUUID().toString());
            fixDepth(root.getLeftChild(), depth+1);
            fixDepth(root.getRightChild(),depth+1);
            return true;
        }
            // return true;
        else{
            root.setDepth(depth);
            root.setID(UUID.randomUUID().toString());
            boolean left = fixDepth(root.getLeftChild(), depth+1);
            boolean right = fixDepth(root.getRightChild(),depth+1);
            // return left && right;
            if(left)
                return left;
            else if(right)
                return right;
            else
                return false;
        }
    }

    /**
     * Getter for the random number generator
     * @return Random number generator
     */
    public Random getRandom(){
        return this.random;
    }

    /**
     * This method performs the mutation operation on GP. It will randomly select a point
     * then call the grow method. Currently limiting the max depth for the grow method to
     * depth 2 with root being depth 0
     * @param parent
     * @return
     */
    public Node mutate(Node parent){
        Node p1 = cloneTree(parent.getRoot(),null);
        Node[] nodes = p1.getAllNodes(p1.getRoot());

        int index = random.nextInt(nodes.length);
        Node mutationPoint = nodes[index];

        Node newSubtree = grow(maxOffspringDepth,mutationPoint.getParent(),0);
        Node result = null;

        if(mutationPoint.getParent() == null){//mutating the root
            result = newSubtree;
        }
        else{//replacing node other than root
            Node parentt = mutationPoint.getParent();
            if(parentt.getLeftChild().equals(mutationPoint)){
                parentt.setLeftChild(newSubtree);
                newSubtree.setParent(parentt);
                mutationPoint.setParent(null);
                result = parentt.getRoot();
            }
            else if(parentt.getRightChild().equals(mutationPoint)){
                parentt.setRightChild(newSubtree);
                newSubtree.setParent(parentt);
                mutationPoint.setParent(null);
                result = parentt.getRoot();
            }
            else{
                System.out.println("Error 333");
            }
        }
        
        Boolean oneResult = fixDepth(result, 0);
        Node par = cloneTree(parent.getRoot(), null);
        fixDepth(par.getRoot(), 0);//make the id's of parent clone unique
        return (oneResult) ? (par) : result;
    }

    /**
     * This method is the executor for the genetic program. It will run a while loop until stopping condition is met
     * @param tournament
     */
    public void executeTraining(TSelection tournament, DataReader reader){
        double average = reader.getDatasetAverage();
        // System.out.println("average: " + average);

        int generationCounter = 0;
        int crossEnd = (int) (populationSize * crossoverRate);

        int mutationStart = crossEnd+1;
        int prevGlobalSimilarity = 0;

        boolean once = false;
        int gCounter = 1;//basically this will change the global similarity root if the counter gets a value of 2.

        //initialize the population
        generatePopulation();
        Node best = null;
        //while termination condition is not met
        while(generationCounter < numGenerations){//temporary condition. Replace later
            try {
                // System.out.println("Generation: " + generationCounter);

                //evaluate the population
                reader.trainData();
                // printIndividual(getBestIndividual());
                best = getBestIndividual();

                //after 10 iterations set the tree to represent the global similarity nodes
                // if(generationCounter == maxGlobalGenerationsIndex){
                if(generationCounter == 5){
                    globalSimilarityRoot = best.getRoot();
                }

                //check if the global nodes are changing
                // if(generationCounter > maxGlobalGenerationsIndex){
                if(generationCounter > 5){
                    System.out.println("Generation Counter: " + generationCounter);
                    int result = calculateGlobalSimilarityIndex(globalSimilarityRoot, best);
                    System.out.println("Result: " + result);
                    if(!once){
                        prevGlobalSimilarity = result;
                        once = true;
                    }
                    else{
                        if(Math.abs(result- prevGlobalSimilarity) > globalSimilarityThreshhold || gCounter > 2){
                            prevGlobalSimilarityRoot = globalSimilarityRoot;
                            globalSimilarityRoot = best.getRoot();
                            prevGlobalSimilarity = result;

                            gCounter = 1;
                        }
                        else{
                            prevGlobalSimilarity = result;
                            gCounter++;
                        }
                    }
                }

                // System.out.println("Num Nodes in Fittest Individual: " + best.getAllNodes(best.getRoot()).length);
                // System.out.println("Best Depth: " + best.getDepth());
                if(generationCounter == numGenerations-1)
                    printIndividual(best);

                //select parents for next generation and apply genetic operators
                List<Node> nodes = performCrossover(crossEnd, tournament);
                if(nodes.contains(null)){
                    System.out.println("null is from crossvoer");
                }
                population = performMutation(mutationStart, tournament, nodes);
                generationCounter++;

                nodes = null;
                // System.out.println("____________________________________________________________________________");
                // System.out.println();
                System.gc();//clear whatever memory was being used
                

            } catch (Exception e) {
                System.out.println("Error in execute: " + e.getMessage());
                e.printStackTrace();
            }
        }
        meanAbsoluteError = new MAE();
        meanAbsolDev = new MedianAbsoluteDev(average);
        rSquared = new RSquared(average);
        rmsd = new RMSD();
        executeTest(best,meanAbsoluteError,meanAbsolDev,rSquared,rmsd,reader);
    }

    public void executeTest(Node best, MAE mae,MedianAbsoluteDev mad, RSquared rSquared, RMSD rmsd, DataReader reader){
        reader.testData(best,mae,mad,rSquared,rmsd);

        System.out.println("\nPerformance metrics for test data set");
        System.out.println("Mean Absolute Error: " + mae.getMae());
        System.out.println("Mean Absolute Deviation: " + mad.getMedianValue());
        System.out.println("RSquared: " + rSquared.calcRSquared());
        System.out.println("RMDS: " + rmsd.calcFinalResult());
    }

    /**
     * This method calls subtree crossover to generate crossover rate * population size number of individuals
     * @param crossEnd number of offspring to create from crossover
     * @param tournament 
     * @return the new population populated with offspring created from subtree crossover
     */
    public List<Node> performCrossover(int crossEnd, TSelection tournament){
        List<Node> nodes = new ArrayList<Node>();
        int counter = 0;

        while(counter <= crossEnd){
            //select parents for crossover
            Node parentOne = tournament.calcTSelection(population);
            Node parentTwo = tournament.calcTSelection(population);

            Node[] offSpring = subtreeCrossover(parentOne, parentTwo);
            if(offSpring.length == 1){
                nodes.add(offSpring[0]);
                counter++;
            }
            else if(offSpring.length == 2){
                nodes.add(offSpring[0]);
                counter++;

                if(counter >crossEnd)
                    break;
                nodes.add(offSpring[1]);
                counter++;
            }
            System.gc();
        }
        return nodes;
    }

    /**
     * This method selects parents for mutation and does mutation. It adds onto the parents from crossover
     * @param mutationStart just an index for where to start counting the mutation offspring
     * @param tournament Used to carry out tournament selection
     * @param nodes the structure which already parents from crossover
     * @return An array of the complete new population
     */
    public Node[] performMutation(int mutationStart,TSelection tournament,List<Node> nodes){
        for(int i=mutationStart;i<populationSize;i++){
            Node parentOne = tournament.calcTSelection(population);
            nodes.add(mutate(parentOne));
        }
        if(nodes.contains(null))
            System.out.println("null if from mutation");
        return nodes.toArray(new Node[nodes.size()]);
    }

    /**
     * This method returns the fittest individual in the generation
     * @return Node representing fittest individual
     */
    public Node getBestIndividual(){
        double fitness = Double.MAX_VALUE;
        int index = 0;

        for(int i=0;i< populationSize;i++)
            if(population[i].getRawFitness() < fitness){
                fitness = population[i].getRawFitness();
                index = i;
            }

        // System.out.println("Fitness: "+population[index].getRawFitness());
        return population[index];
    }

    /**
     * 
     * @param gsr Root of the Global Similarity Tree
     * @param node Node which we want to compare to 
     * @param similarity Just a counter for the similarity =
     * @return The similarity of node to gsr
     */
    public int calculateLocalSimilarityIndex(Node gsr, Node node){
        if(gsr == null || node == null)
            return 0;
        
        int count = 0;
        if(node instanceof FunctionNode && gsr instanceof FunctionNode){
            try {
                if(gsr.getValue().equals(node.getValue()) && node.getDepth() > maxGlobalDepthIndex && gsr.getDepth() > maxGlobalDepthIndex && node.getDepth() == gsr.getDepth()){
                    count++;
                    count += calculateLocalSimilarityIndex(gsr.getLeftChild(),node.getLeftChild());
                    count += calculateLocalSimilarityIndex(gsr.getRightChild(),node.getRightChild());

                    return count;
                }
                else{
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
                return Integer.MIN_VALUE;
            }
        }
        else
            return 0;
    }

    public int calculateGlobalSimilarityIndex(Node gsr, Node node){
        if(gsr == null || node == null){
            return 0;
        }

        int count = 0;
        if(node instanceof FunctionNode && gsr instanceof FunctionNode){
            try {
                if(gsr.getValue().equals(node.getValue()) && node.getDepth() <= maxGlobalDepthIndex && gsr.getDepth() <= maxGlobalDepthIndex && node.getDepth() == gsr.getDepth()){
                    count++;
                    count += calculateGlobalSimilarityIndex(gsr.getLeftChild(),node.getLeftChild());
                    count += calculateGlobalSimilarityIndex(gsr.getRightChild(),node.getRightChild());
                    return count;
                }
                else{
                    return 0;//this might lead to a potential error
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
                return Integer.MIN_VALUE;
            }
        }
        else
            return 0;
    }
}
