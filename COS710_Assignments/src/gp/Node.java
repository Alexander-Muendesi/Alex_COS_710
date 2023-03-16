package gp;

import java.util.Map;

/**
 * @brief This class acts an abstract parent to cater for the FunctionNode and TerminalNode classes.
 */
public abstract class Node {
    public int depth = -1;//temporary for now, used for printing purposes
    /**
     * @brief The evaluate function has 2 meaning based on whether we are using a Terminal or Function
            node. With a terminal node it will just return the value of the terminal. With a Function node 
            it will evaluate its operands based on the operator of the function and return the resulting 
            value
      
     * @param inputData Will either be an array of one element or 2 elements depending on type of node
     * 
     * @return Return value will be either value of terminal node or result of evaluating an function
     * @throws Exception
     */
    // public abstract double evaluate(double[] inputData) throws Exception;
    public abstract double evaluate(Map<Integer, Double> inputData) throws Exception;
    public abstract String getValue() throws Exception;

    public abstract Node getParent();
    public abstract void setParent(Node parent);
    public abstract Node getRoot();

    public abstract int getIndex();
    public abstract Node getLeftChild();
    public abstract Node getRightChild();
    
    public abstract void setLeftChild(Node input);
    public abstract void setRightChild(Node input);
    public abstract int getDepth();

    public abstract void setDepth(int depth);
    /**
     * @brief Given the root node, return all the nodes in the structure in an array. Does this in a
     *          breadth first manner
     * @param root
     * @return Array of Nodes
     */
    public abstract Node[] getAllNodes(Node root);
    public abstract String getID();//used to identify a node so that .equals can compare corectly
    public abstract void setID(String id);
    public abstract double getRawFitness();
    public abstract void calcRawFitness(double actualVal,double predictedVal);

    public abstract Node[] getArguments();
}
