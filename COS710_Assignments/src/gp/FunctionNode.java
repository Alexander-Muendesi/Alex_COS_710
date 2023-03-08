package gp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionNode extends Node {
    private final int index;//basically we will have functions in a string array. This index identifies that function
    private Node[] arguments;//this will be the arguments of the function
    private final int numArguments = 2;//for now we are dealing with the binary arithmetic operators
    private final int leftChildIndex = 0;
    private final int rightChildIndex = 1;
    public int depth;//temporary maybe for printing purposes
    private Node parent;

    private double rawFitness;//since root node will be function, root node will have a fitness value

    public FunctionNode(int index, int depth, Node parent){
        this.index = index;
        this.depth = depth;
        arguments = new Node[numArguments];
        rawFitness = 0;
        this.parent = parent;
    }

    public FunctionNode(int index,Node[] arguments,int depth, Node parent, double rawFitness){
        this.index = index;
        this.depth = depth;
        this.parent = parent;
        this.arguments = new Node[numArguments];
        this.rawFitness = rawFitness;

        for(int i=0;i < numArguments;i++)
            this.arguments[i] = arguments[i].clone();
    }

    public Node clone(){
        return new FunctionNode(index,arguments,depth,parent,rawFitness);
    }

    public Node getParent(){
        return this.parent;
    }

    public void setParent(Node parent){
        this.parent = parent;
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

    public double getRawFitness(){
        return this.rawFitness;
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

    public void setLeftChild(Node input){
        arguments[leftChildIndex] = input;
    }

    public Node getRightChild(){
        return arguments[rightChildIndex];
    }

    public void setRightChild(Node input){
        arguments[rightChildIndex] = input;
    }

    /**
     * @brief This function calculates the absolute value of the difference between predicted and actual value and adds it to raw fitness
     * @param actualVal 
     * @param predictedVal
     */
    public void calcRawFitness(double actualVal,double predictedVal){
        rawFitness = rawFitness + Math.abs(predictedVal - actualVal);
    }

    /**
     * @brief Given any node return the root of its structure
     */
    public Node getRoot(){
        Node current = this;

        while(current.getParent() != null){
            current = current.getParent();
        }

        return current;
    }

    public Node[] getAllNodes(Node root){
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(root);

        for(int i=0;i< nodes.size();i++){
            Node curr = nodes.get(i);

            if(curr instanceof FunctionNode){
                FunctionNode fNode = (FunctionNode)curr;
                nodes.add(fNode.getLeftChild());
                nodes.add(fNode.getRightChild());//note if you decided to add other operators have to add arity stuff here
            }
            else if(i != 0 && curr instanceof TerminalNode){
                nodes.add(curr);//add a terminal node
            }
        }

        return nodes.toArray(new Node[nodes.size()]);
    }
}
