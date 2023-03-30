package performance_measures;

public class RSquared{
    private double rss;
    private double tss;
    private final double exponent;
    private final double average;

    public RSquared(double average){
        rss=0.0;
        tss=0.0;
        exponent = 2.0;
        this.average = average;
    }

    public void calcRss(double actualVal, double predictedVal){
        rss += Math.pow((actualVal-predictedVal),exponent);
    }

    //NB: you have to calculate that mean value before hand. Thinking will most probably have to read
    //dataset again
    public void calcTss(double actualVal){
        tss += Math.pow((actualVal-average),exponent);
    }

    public void calcMeanVal(){
        //this method must return the mean value somehow
    }

    public double calcRSquared(){
        return 1 - (rss / tss);
    }
}