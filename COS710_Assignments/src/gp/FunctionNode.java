package gp;

import java.util.Map;

public class FunctionNode extends Node {
    private final int index;//basically we will have functions in a string array. This index identifies that function
    private Node[] arguments;//this will be the arguments of the function
    private final int numArguments = 2;//for now we are dealing with the binary arithmetic operators
    private final int leftChildIndex = 0;
    private final int rightChildIndex = 1;
    public final int depth;//temporary maybe for printing purposes

    public FunctionNode(int index, int depth){
        this.index = index;
        this.depth = depth;
        arguments = new Node[numArguments];
    }

    /***
     * @brief This function basically associates an operator with its arguments.
     * @param index This will show which position in the arguments array the arg is
     * @param arg Mostly a double value that will be applied when using an operator
     */
    public void setArgument(int index, Node arg){
        arguments[index] = arg;
    }

    /**
     * @brief This function uses recursion to evaluate the operands of an operator and returns the result
     */
    @Override
    // public double evaluate(double[] input) throws Exception{//I dont think I need that input parameter for anything except for overriding issue
    public double evaluate(Map<Integer, Double> input) throws Exception{//I dont think I need that input parameter for anything except for overriding issue
        double[] argumentVals = new double[numArguments];

        for(int i=0;i<numArguments;i++){
            argumentVals[i] = arguments[i].evaluate(input);//this basically gets filled up with terminal values
        }

        return applyFunctionOperator(index,argumentVals);
    }

    public int getNumArguments(){
        return numArguments;
    }

    /**
     * @brief This function basically evaluates the operands of an operator and returns the result
     * @param index
     * @param args
     * @return
     * @throws Exception
     */
    public double applyFunctionOperator(int index, double[] args) throws Exception{
        switch (index) {
            case 0:
                return args[0] + args[1];
            case 1:
                return args[0] - args[1];
            case 2:
                return args[0] * args[1];
            case 3:
                if(args[1] == 0.0){
                    return 1;//for division by 0 return 1. Might change this who knows
                }
                else
                    return args[0] / args[1];
            default:
                throw new Exception("Invalid index provided: " + index);
        }
    }

    /**
     * 
     * @return The value of the operator as a string
     */
    public String getValue() throws Exception{
        switch (index) {
            case 0:
                return "+";
            case 1:
                return "-";
            case 2:
                return "*";
            case 3:
                return "/";
            default:
                throw new Exception("Invalid index of Terminal Node: " + index);
        }
    }

    public Node getLeftChild(){
        return arguments[leftChildIndex];
    }

    public Node getRightChild(){
        return arguments[rightChildIndex];
    }

}
