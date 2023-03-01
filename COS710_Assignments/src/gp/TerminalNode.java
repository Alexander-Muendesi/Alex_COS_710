package gp;

import java.util.Map;

public class TerminalNode extends Node{

    private final int index;//this will be the column number of data in the dataset in a specific row.
    public final int depth;//temporary for printing purposes for now
    private Double terminalValue;

    public TerminalNode(int index, int depth){
        this.depth = depth;
        this.index = index;
    }

    // public double evaluate(double[] input){//might change this to a linkedhashset or something since data is in linkedHashSet.
    public double evaluate(Map<Integer, Double> input){//might change this to a linkedhashset or something since data is in linkedHashSet.
        // return input[index];
        terminalValue = input.get(index);
        return input.get(index);
    }

    public String getValue(){
        return Double.toString(terminalValue);
    }
    
}
