package gp;
import java.util.Random;

//NB: Assume root node is depth 1
public class GeneticProgram {

    private final int populationSize;
    private final int maxDepth;
    private final int numTerminals;
    private final int numFunctions;
    private final Random random;
    private Node[] population;

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
        if(maxDepth == 1 || random.nextInt(numFunctions+numTerminals) < numFunctions){
            return new TerminalNode(random.nextInt(numTerminals));
        }
        else{
            FunctionNode function = new FunctionNode(random.nextInt(numFunctions));

            for(int i=0; i< function.getNumArguments();i++){
                function.setArgument(i, grow(maxDepth - 1));
            }

            return function;
        } 
    }
}
