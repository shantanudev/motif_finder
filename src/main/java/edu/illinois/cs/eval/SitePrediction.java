package edu.illinois.cs.eval;

import edu.illinois.cs.algo.FileUtil;
import edu.illinois.cs.algo.MotifFinder;
import edu.illinois.cs.benchmark.BenchmarkGenerator;

import java.io.IOException;
import java.util.List;

/**
 * Created by gourav on 4/29/15.
 */
public class SitePrediction {

  public static void main(String[] args) {

    //String path = "/Users/gourav/code/motif_finder/benchmarks/";

    Data def = runAll(BenchmarkGenerator.path + "/default/");

    Data ml1 = runAll(BenchmarkGenerator.path + "/ML1/");
    Data ml2 = runAll(BenchmarkGenerator.path + "/ML2/");
    System.out.println("ML [6,7,8]: " + ml1 + ", " + ml2 + ", " + def);

    Data nm1 = runAll(BenchmarkGenerator.path + "/NM1/");
    Data nm2 = runAll(BenchmarkGenerator.path + "/NM2/");
    System.out.println("NM [0,1,2]: " + nm1 + ", " + def + ", " + nm2);

    Data sc1 = runAll(BenchmarkGenerator.path + "/SC1/");
    Data sc2 = runAll(BenchmarkGenerator.path + "/SC2/");
    System.out.println("SC [5,10,20]: " + sc1 + ", " + def + ", " + sc2);


  }

  public static Data runAll(String parent) {
    //String dir = "/Users/gourav/code/motif_finder/benchmarks/default/dataset0/";
    double[] data = new double[10];
    for (int dataset=0; dataset<10; dataset++) {
      String dir = parent + "/dataset" + dataset + "/";
      try {
        List<Integer> sites = FileUtil.readSites(dir + "/sites.txt");
        List<Integer> predictedSites = FileUtil.readSites(dir + "/predictedsites.txt");

        //System.out.println(getRelativeEntropy(probMatrix, predictedProbMatrix) + " - " + dir);
        double overlap = getOverlap(sites, predictedSites);
        data[dataset] = overlap;

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return new Data(data);
  }

  static double getOverlap(List<Integer> x, List<Integer> y) {
    double overlap = 0;
    for (int i=0; i<x.size(); i++) {
      if (x.get(i).equals(y.get(i))) {
        overlap++;
      } else {
        //System.out.println("No overlap: " + x.get(i) + ", " + y.get(i));
      }
    }
    overlap = overlap / x.size()*1.0;
    //System.out.println("Overlap: " + overlap);

    return overlap;
  }



}
