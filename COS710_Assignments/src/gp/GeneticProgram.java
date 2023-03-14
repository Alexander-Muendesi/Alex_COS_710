package gp;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;//used to generate a unique ID for each node

import dataset_reading_classes.DataReader;

//NB: Assume root node is depth 0
//Note. random.nextInt returns a number from 0 inclusive to upper bound exclusive
public class GeneticProgram {

    private final int populationSize;
    private final int maxDepth;
    private final int numTerminals = 24;
    private final int numFunctions = 4;
    private final int tournamentSize;
    private final Random random;
    private Node[] population;//could possibly change this to linkedhashset for faster performance maybe?
    private final int numGenerations;
    private final double mutationRate;
    private final double crossoverRate;
    private final int maxOffspringDepth;

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
        //Thought: this is the population that is getting evolved. With each generation read the next populationSize from training data
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
            parent = function;

            for(int i=0; i< function.getNumArguments();i++){
                function.setArgument(i, grow(depthLimit,parent,depthCounter+1));
            }

            return function;
        } 
    }

    /**
     * This method creates a population of individuals/programs based on the populationSize parameter
     */
    public void generatePopulation(){
        for(int i=0; i< populationSize;i++){
            population[i] = generateIndividual();
            // try {
            //     printIndividual(population[i]);
            // } catch (Exception e) {
            //     System.out.println(e.getMessage());
            // }
        }
    }

    /**
     * This method prints the individual program in a breadth first manner. Mainly for debugging purposes
     * @param root This is the root of the tree
     * @throws Exception
     * NB: Note that the depth is currently really messed up in the nodes. The maxdepth is basically the root(bloody useless).
     * So this ended up affecting the inequality signs making them less than instead of bigger than. 
     */
    public void printIndividual(Node root) throws Exception{
        if(root == null)
            return;
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(root);
        int currentDepth = root.getDepth();
        System.out.println("\n");

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
        Node[] one = cloneNodes(parentOne.getAllNodes(parentOne.getRoot()));//wonder if I am not already passing the root to this method??
        Node[] two = cloneNodes(parentTwo.getAllNodes(parentTwo.getRoot()));

        Node[] onee = cloneNodes(parentOne.getAllNodes(parentOne.getRoot()));//wonder if I am not already passing the root to this method??
        Node[] twoo = cloneNodes(parentTwo.getAllNodes(parentTwo.getRoot()));

        Node[] r = {parentOne,parentTwo};

        //select a random node from each parent
        int index1 = random.nextInt(one.length);
        int index2 = random.nextInt(two.length);

        Node node1 = one[index1];//crossover point root
        Node node2 = two[index2];//crossover point root

        Node node11 = onee[index1];
        Node node22 = twoo[index2];

        //call the replace subtree method
        Node offSpringOne = replaceSubtree(node1, node2);
        Node offSpringTwo = replaceSubtree(node22, node11);

        //fix the depth values and see if maxDepth has been exceeded
        Boolean oneResult = fixDepth(offSpringOne, 0);
        Boolean twoResult = fixDepth(offSpringTwo, 0);

        if(oneResult && twoResult){//both offspring break max depth limit/ randomly return one of the parents

            Node[] temp = {r[random.nextInt(2)]}; 
            return temp;
        }
        else if(oneResult){//first offspring break depth limit. Return one of its parents and the second offspring

            //update the id's of the new offspring to be different from parent
            Node[] allNodes = offSpringTwo.getAllNodes(offSpringTwo.getRoot());
            for(int i=0;i< allNodes.length;i++)
                allNodes[i].setID(UUID.randomUUID().toString());

            Node[] temp = {offSpringTwo,r[random.nextInt(2)]};
            return temp;
        }
        else if(twoResult){//second offspring breaks depth limit. Return one of its parents and the first offspring


            Node[] allNodes = offSpringOne.getAllNodes((offSpringOne.getRoot()));
            for(int i=0;i<allNodes.length;i++)
                allNodes[i].setID(UUID.randomUUID().toString());

            Node[] temp = {offSpringOne,r[random.nextInt(2)]};
            return temp;
        }
        else{//none of the offspring break depth limit. Return both offspring


            Node []aNodes = offSpringOne.getAllNodes(offSpringOne.getRoot());
            Node []bNodes = offSpringTwo.getAllNodes(offSpringTwo.getRoot());

            for(int i=0;i<aNodes.length;i++)
                aNodes[i].setID(UUID.randomUUID().toString());

            for(int i=0;i<bNodes.length;i++)
                bNodes[i].setID(UUID.randomUUID().toString());

            Node[] temp = {offSpringOne,offSpringTwo};
            return temp;
        }
    }


    /**
     * Given two crossover node points this method gets the parent of each node and swaps it with other crossver point. It
     * then returns the whole complete tree not just the subtree
     * @param oldNode crossover point to be replaced
     * @param newNode new crossover point to add
     * @return The root of the tree that was modified and contains the offspring
     */
    public Node replaceSubtree(Node oldNode, Node replacementNode){//thinking of sending root back 
        if(oldNode.getParent() == null){//replacing the root node
            oldNode = replacementNode;
            return replacementNode;
        }
        else{
            Node parent = oldNode.getParent();
            if(parent.getLeftChild().equals(oldNode)){
                if(replacementNode instanceof FunctionNode){
                    FunctionNode fNode = (FunctionNode)replacementNode;
                    parent.setLeftChild(fNode);
                    fNode.setParent(parent);
                    return parent.getRoot();
                }
                else{
                    TerminalNode tNode = (TerminalNode)replacementNode;
                    parent.setLeftChild(tNode);
                    tNode.setParent(parent);
                    return parent.getRoot();
                }
            }
            else if(parent.getRightChild().equals(oldNode)){
                if(replacementNode instanceof FunctionNode){
                    FunctionNode fNode = (FunctionNode)replacementNode;
                    parent.setRightChild(fNode);
                    fNode.setParent(parent);
                    return parent.getRoot();
                }
                else{
                    TerminalNode tNode = (TerminalNode)replacementNode;
                    parent.setRightChild(tNode);
                    tNode.setParent(parent);
                    return parent.getRoot();
                }
            }
            return null;
        }
    }

    /**
     * This method clones all the nodes returned from  getAllNodes method call.
     * @param input
     * @return
     */
    public Node[] cloneNodes(Node[] input){
        Node[] result = new Node[input.length];
        for(int i=0; i< result.length;i++)
            result[i] = input[i].clone();
            
        return result;
    }

    /**
     * After a subtree crossover operation this function goes through a tree and fixes depth values
     * @param root
     * @param depth
     * @return True means me have exceeded max depth. False means we have not exceeded max depth
     */
    public boolean fixDepth(Node root, int depth){
        if(root != null){
            root.setDepth(depth);
            fixDepth(root.getLeftChild(), depth+1);
            fixDepth(root.getRightChild(),depth+1);
    
            if(depth > maxDepth)
                return true;
    
            return false;
        }
        return false;
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
        Node[] nodes = parent.clone().getAllNodes(parent.getRoot());
        int index = random.nextInt(nodes.length);
        System.out.println("Index: " + index);

        Node mutationPoint = nodes[index];

        //Node replacementParent = new FunctionNode(random.nextInt(numFunctions),0,mutationPoint.getParent(),UUID.randomUUID().toString());
        Node newSubtree = grow(maxOffspringDepth,mutationPoint.getParent(),0);

        Node result = replaceSubtree(mutationPoint, newSubtree);
        
        Boolean oneResult = fixDepth(result, 0);
        return (oneResult) ? parent : result;
    }

    /**
     * This method is the executor for the genetic program. It will run a while loop until stopping condition is met
     * @param tournament
     */
    public void execute(TSelection tournament, DataReader reader){
        Node[] newPopulation = new Node[populationSize];//array to hold the new population size
        int generationCounter = 0;

        //while termination condition is not met
        while(generationCounter < numGenerations){//temporary condition. Replace later
            try {
                //select individuals
                Node parentOne = tournament.calcTSelection(population);
                //Node parentTwo = tournament.calcTSelection(population);

                printIndividual(parentOne);
                //printIndividual(parentTwo);
                System.out.println("\n-----------------------");
    
                //Node[] temp = subtreeCrossover(parentOne, parentTwo);
                //for(int i=0;i<temp.length;i++)
                //    printIndividual(temp[i]);
    
                generationCounter++;
                printIndividual(mutate(parentOne));
                
            } catch (Exception e) {
                System.out.println("Error in execute: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
