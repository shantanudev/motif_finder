package edu.illinois.cs.eval;

/**
 * Created by gourav on 4/29/15.
 */
public class Data {
  double value;
  double stdDev;

  public Data(double[] data) {
    this.value = getMean(data);
    this.stdDev = getStdDev(data);
  }

  public static double getStdDev(double[] data) {
    if (data.length == 0) {
      return 0;
    }
    double mean = getMean(data);
    double temp = 0;
    for(double a :data) {
      temp += (mean - a) * (mean - a);
    }
    return Math.sqrt(temp/data.length);
  }

  public static double getMean(double[] data) {
    double sum = 0.0;
    for(double a : data)
      sum += a;
    return sum/data.length;
  }

  @Override
  public String toString() {
    return value + " [" + stdDev + "]";
  }
}
