import dataset_reading_classes.DataReader;
import gp.FunctionNode;
import gp.GeneticProgram;
import gp.Node;
import gp.TerminalNode;

public class App {
    public static void main(String[] args) throws Exception {
        String filename = "Alex_COS_710/COS710_Assignments/src/dataset_reading_classes/dataset.csv";
        double[] tempVals = {20.0,1.0,-2.0,1.22,2,3,44,5,6,7,5,44,3,2,45,6,6,73,2,6,3,7,8,55};

        GeneticProgram gp = new GeneticProgram(100, 10, 24, 4, 244);
        gp.generatePopulation();
        Node[] population = gp.getPopulation();

        gp.printIndividual(population[0]);
        System.out.println("ansL: "+ population[0].evaluate(tempVals));

        /*FunctionNode node = (FunctionNode)gp.generateIndividual();
        System.out.println("Individual: " + node.getValue() + " "+node.depth);
        System.out.println("Left child: " + node.getLeftChild().getValue());
        System.out.println("Right child: " + node.getRightChild().getValue());

        if(node.getRightChild() instanceof TerminalNode){
            TerminalNode t1 = (TerminalNode)node.getRightChild();
            System.out.println("Right Child1: " + t1.getValue() + " "+t1.depth);
        }
        else{
            FunctionNode t11 = (FunctionNode)node.getRightChild();
            System.out.println("Right Child11: " + t11.getValue() + " "+t11.depth);
        }

        if(node.getLeftChild() instanceof TerminalNode){
            TerminalNode t2 = (TerminalNode)node.getLeftChild();
            System.out.println("Left Child: " + t2.getValue() + " " + t2.depth);
        }*/
        DataReader reader = new DataReader(filename,gp);
        //reader.readData();
    }
}

//note: those performance metric functions can be used as your Fitness functions I believe. The main idea is the following:
// you want to predict trip duration so you use a genetic program to find a solution. The terminals of the genetic program
// will be all the independent variables in the dataset except the trip duration which will be the actual trip duration 
// time we will be trying to get our program as close as possible to. The program output is the predicted value.
//Note:  you need to split your dataset into a training set and a test set. A good ratio is 70(training) : 30(testing)
