package performance_measures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Median Absolute Deviation
public class MedianAbsoluteDev {
    List<Double> values;
    Double average;

    public MedianAbsoluteDev(Double average){
        values = new ArrayList<Double>();
        this.average = average;
    }

    public void calcAbsValue(double actualValue, double predictedValue){
        values.add(Math.abs(actualValue - predictedValue));
    }

    public double getMedianValue(){
        int size = values.size();

        Collections.sort(values);//might be computationally heavy this operation

        if(size % 2 == 0){
            int midIndex = size / 2;
            return (values.get(midIndex-1) + values.get(midIndex)) / 2;
        }
        else 
            return values.get(size / 2);
    }
}
