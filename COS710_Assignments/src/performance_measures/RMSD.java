package performance_measures;

//Formual: Square root of the (summation of (xi - xbar) squared divided by N)
// where N is the number of data points

public class RMSD {
    private double numerator;//stores the value of the numerator in the formula
    private double numItems;
    private final double exponent;

    public RMSD(){
        numerator = 0.0;
        numItems = 0;
        exponent = 2.0;
    }

    public void sumSqauredDifference(double acutalVal, double predictedVal){
        numerator = Math.pow((acutalVal-predictedVal),exponent);
        numItems = numItems + 1;
    }

    public double calcFinalResult(){
        double ans = numerator / numItems;
        return Math.sqrt(ans);
    }
}
