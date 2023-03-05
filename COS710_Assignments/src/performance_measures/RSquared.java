package performance_measures;

public class RSquared{
    private double rss;
    private double tss;
    private final double exponent;

    public RSquared(){
        rss=0.0;
        tss=0.0;
        exponent = 2.0;
    }

    public void calcRss(double actualVal, double predictedVal){
        rss += Math.pow((actualVal-predictedVal),exponent);
    }

    //NB: you have to calculate that mean value before hand. Thinking will most probably have to read
    //dataset again
    public void calcTss(double actualVal, double meanValue){
        tss += Math.pow((actualVal-meanValue),exponent);
    }

    public void calcMeanVal(){
        //this method must return the mean value somehow
    }

    public double calcRSquared(){
        return 1 - (rss / tss);
    }
}