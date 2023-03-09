package performance_measures;

//mean absolute error
public class MAE {
    private int numItems;
    private double mae;

    //think if you can maybe pass the number of items as a parameter
    public MAE(){
        numItems = 0;
        mae = 0.0;
    }

    public void calcDiff(double actualVal, double predictedVal){
        mae += Math.abs(predictedVal - actualVal);
        numItems++;
    }

    public double getMae(){
        return mae / numItems;
    }
}
