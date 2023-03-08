package gp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TerminalNode extends Node{

    private final int index;//this will be the column number of data in the dataset in a specific row.
    public final int depth;//temporary for printing purposes for now
    private double terminalValue;
    private Node parent;

    public TerminalNode(int index, int depth, Node parent){
        this.depth = depth;
        this.index = index;
        this.parent = parent;
    }

    public TerminalNode(int index, int depth, Node parent, double terminalValue){
        this.index = index;
        this.depth = depth;
        this.parent = parent;
        this.terminalValue = terminalValue;
    }

    public Node clone(){
        return new TerminalNode(index, depth, parent, terminalValue);
    }

    public Node getParent(){
        return this.parent;
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
