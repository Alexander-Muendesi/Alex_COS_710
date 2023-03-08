package gp;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

//NB: Assume root node is depth 1
//Note. random.nextInt returns a number from 0 inclusive to upper bound exclusive
public class GeneticProgram {

    private final int populationSize;
    private final int maxDepth;
    private final int numTerminals;
    private final int numFunctions;
    private final int tournamentSize;
    private final Random random;
    private Node[] population;//could possibly change this to linkedhashset for faster performance maybe?

    /**
     * 
     * @param populationSize
     * @param maxDepth
     * @param numTerminals
     * @param numFunctions
     * @param seed
     * @param tournamentSize
     */
    public GeneticProgram(int populationSize,int maxDepth, int numTerminals, int numFunctions,int seed,int tournamentSize){
        this.populationSize = populationSize;
        this.maxDepth = maxDepth;
        this.numTerminals = numTerminals;
        this.numFunctions = numFunctions;
        random = new Random(seed);
        population = new Node[populationSize];
        this.tournamentSize = tournamentSize;
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
     * @brief This method basically returns the tree representing the program we created with the grow method
     * @return The root node
     */
    public Node generateIndividual(){
        //saying 2 + because we don't want a single node tree which is just a terminal. Bad for diversity
        int depth = 2 + random.nextInt(maxDepth);
        return grow(depth,null);//passing null as parent of root node
    }

    /**
     * @brief The if part randomly chooses when to create a terminal node. In this case if the sum of the number of functions and terminals
                used a upper bound to random is less than the number of terminals generate a terminal node
     * @param depth How deep the tree is to grow
     * @param parent Parent Node of current node
     * @return A node in the tree
     */
    public Node grow(int depth, Node parent){
        if(depth == 1 || random.nextInt(numFunctions+numTerminals) < numFunctions){
            return new TerminalNode(random.nextInt(numTerminals),depth,parent);
        }
        else{
            FunctionNode function = new FunctionNode(random.nextInt(numFunctions),depth,parent);
            parent = function;

            for(int i=0; i< function.getNumArguments();i++){
                function.setArgument(i, grow(depth - 1,parent));
            }

            return function;
        } 
    }

    /**
     * @brief This method creates a population of individuals/programs based on the populationSize parameter
     */
    public void generatePopulation(){
        for(int i=0; i< populationSize;i++){
            population[i] = generateIndividual();
        }
    }

    /**
     * @brief This method prints the individual program in a breadth first manner. Mainly for debugging purposes
     * @param root This is the root of the tree
     * @throws Exception
     * NB: Note that the depth is currently really messed up in the nodes. The maxdepth is basically the root(bloody useless).
     * So this ended up affecting the inequality signs making them less than instead of bigger than. 
     */
    public void printIndividual(Node root) throws Exception{
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(root);
        int currentDepth = 0;

        TerminalNode tnode = null;
        FunctionNode fnode = null;

        if(root instanceof TerminalNode){
            tnode = (TerminalNode)root;
            currentDepth = tnode.depth;
        }
        else{
            fnode = (FunctionNode)root;
            currentDepth = fnode.depth;
        }

        while(queue.isEmpty() == false){
            Node node = queue.remove();
            System.out.print(node.getValue() + " ");

            if(node instanceof FunctionNode){
                FunctionNode temp = (FunctionNode) node;
                // System.out.println("temp.depth: " + temp.depth);
                if(temp.depth < currentDepth || (fnode != null && temp.depth == fnode.depth)){
                    System.out.println();
                    currentDepth = temp.depth;
                }
            }
            else{
                TerminalNode temp2 = (TerminalNode) node;
                // System.out.println("temp2.depth: " + temp2.depth);
                if(temp2.depth < currentDepth || (tnode != null && temp2.depth == tnode.depth)){
                    System.out.println();
                    currentDepth = temp2.depth;
                }
            }

            if(node instanceof FunctionNode){
                FunctionNode functionNode = (FunctionNode) node;
                queue.add(functionNode.getLeftChild());
                queue.add(functionNode.getRightChild());
            }


        }

    }


    public void subtreeCrossover(Node parentOne, Node parentTwo){
        Node[] one = cloneNodes(parentOne.getAllNodes(parentOne.getRoot()));//wonder if I am not already passing the root to this method??
        Node[] two = cloneNodes(parentTwo.getAllNodes(parentTwo.getRoot()));

        //select a random node from each parent
        int index1 = random.nextInt(one.length);
        int index2 = random.nextInt(two.length);

        Node node1 = one[index1];//crossover point root
        Node node2 = two[index2];//crossover point root

        //call the replace subtree method
    }

    /**
     * @brief
     * @param oldNode crossover point to be replaced
     * @param newNode new crossover point to add
     */
    public void replaceSubtree(Node oldNode, Node replacementNode){
        if(oldNode.getParent() == null){//replacing the root node
            oldNode = replacementNode;
        }
        else{
            Node parent = oldNode.getParent();
            if(parent.getLeftChild() == oldNode){
                if(replacementNode instanceof FunctionNode){
                    FunctionNode fNode = (FunctionNode)replacementNode;
                    parent.setLeftChild(fNode);
                    fNode.setParent(parent);
                }
                else{
                    TerminalNode tNode = (TerminalNode)replacementNode;
                    parent.setLeftChild(tNode);
                    tNode.setParent(parent);
                }
            }
            else{
                if(replacementNode instanceof FunctionNode){
                    FunctionNode fNode = (FunctionNode)replacementNode;
                    parent.setRightChild(fNode);
                    fNode.setParent(parent);
                }
                else{
                    TerminalNode tNode = (TerminalNode)replacementNode;
                    parent.setRightChild(tNode);
                    tNode.setParent(parent);
                }
            }
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
}
