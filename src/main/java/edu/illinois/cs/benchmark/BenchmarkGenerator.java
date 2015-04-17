package edu.illinois.cs.benchmark;

import java.io.File;

/**
 * Created by gourav on 4/11/15.
 */
public class BenchmarkGenerator {

  public static final long seed = 1;

  int[] valNM = new int[]{0,2};
  int[] valML = new int[]{6,7};
  int[] valSC = new int[]{5,20};

  int NM = 1;
  int ML = 8;
  int SC = 10;
  int SL = 500;

  String dir;

  public BenchmarkGenerator(String dir) {
    this.dir = dir;
    File file = new File(dir);
    file.mkdirs();
  }

  public static void main(String[] args) {
    BenchmarkGenerator benchmarkGenerator = new BenchmarkGenerator(args[0]);
    benchmarkGenerator.generate();
  }

  public void generate() {
    //Use DatasetGenerator to create 70 datasets as per the param values.
    //default

    generate(dir + "/default", ML, NM, SL, SC);

    //vary NMs
    int index = 1;
    for (int nm : valNM) {
      generate(dir + "/NM" + index++, ML, nm, SL, SC);
    }

    //vary ML
    index = 1;
    for (int ml : valML) {
      generate(dir + "/ML" + index++, ml, NM, SL, SC);
    }

    //vary SC
    index = 1;
    for (int sc : valSC) {
      generate(dir + "/SC" + index++, ML, NM, SL, sc);
    }

  }

  public void generate(String dir, int ML, int NM, int SL, int SC) {
    for (int datasetIndex=0; datasetIndex<10; datasetIndex++) {
      DatasetGenerator datasetGenerator = new DatasetGenerator(ML, NM, SL, SC);
      datasetGenerator.generate(dir + "/dataset" + datasetIndex);
    }
  }
}
