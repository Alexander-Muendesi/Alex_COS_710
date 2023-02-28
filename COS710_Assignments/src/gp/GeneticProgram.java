package gp;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

//NB: Assume root node is depth 1
public class GeneticProgram {

    private final int populationSize;
    private final int maxDepth;
    private final int numTerminals;
    private final int numFunctions;
    private final Random random;
    private Node[] population;//could possibly change this to linkedhashset for faster performance maybe?

    public GeneticProgram(int populationSize,int maxDepth, int numTerminals, int numFunctions,int seed){
        this.populationSize = populationSize;
        this.maxDepth = maxDepth;
        this.numTerminals = numTerminals;
        this.numFunctions = numFunctions;
        random = new Random(seed);
        population = new Node[populationSize];
    }

    public Node[] getPopulation(){
        return this.population;
        //Thought: this is the population that is getting evolved. With each generation read the next populationSize from training data
    }

    public int getPopulationSize(){
        return populationSize;
    }

    /**
     * @brief This method basically returns the tree representing the program we created with the grow method
     * @return The root node
     */
    public Node generateIndividual(){
        int depth = 1 + random.nextInt(maxDepth);//could potentially change the 1 to 2 to avoid a tree which has one node,increase diversity
        return grow(depth);
    }

    /**
     * @brief The if part randomly chooses when to create a terminal node. In this case if the sum of the number of functions and terminals
                used a upper bound to random is less than the number of terminals generate a terminal node
     * @param depth How deep the tree is to grow
     * @return A node in the tree
     */
    public Node grow(int depth){
        if(depth == 1 || random.nextInt(numFunctions+numTerminals) < numFunctions){
            return new TerminalNode(random.nextInt(numTerminals),depth);
        }
        else{
            FunctionNode function = new FunctionNode(random.nextInt(numFunctions),depth);

            for(int i=0; i< function.getNumArguments();i++){
                function.setArgument(i, grow(depth - 1));
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
}
