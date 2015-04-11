package edu.illinois.cs.benchmark;

/**
 * Created by gourav on 4/11/15.
 */
public class BenchmarkGenerator {

  public static final long seed = 1;

  int[] valNM = new int[]{0,1,2};
  int[] valML = new int[]{6,7,8};
  int[] valSC = new int[]{5,10,20};

  int NM = 1;
  int ML = 8;
  int SC = 10;
  int SL = 500;

  String dir;

  public BenchmarkGenerator(String dir) {
    this.dir = dir;
  }

  public static void main(String[] args) {
    BenchmarkGenerator benchmarkGenerator = new BenchmarkGenerator(args[0]);
    benchmarkGenerator.generate();
  }

  public void generate() {
    //Use DatasetGenerator to create 70 datasets as per the param values.
    //default

    //vary NMs

    //vary ML

    //vary SC
  }
}
