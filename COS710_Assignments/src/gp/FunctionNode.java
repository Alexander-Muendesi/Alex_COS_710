package gp;

public class FunctionNode extends Node {
    private final int index;//basically we will functions in a string array. This index identifies that function
    private Node[] arguments;//this will be the arguments of the function
    private final int numArguments = 2;//for now we are dealing with the binary arithmetic operators

    public FunctionNode(int index){
        this.index = index;
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
    public double evaluate(double[] input) throws Exception{//I dont think I need that input parameter for anything except for overriding issue
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
                return args[0] - args[0];
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

}
