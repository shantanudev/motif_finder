package edu.illinois.cs.eval;

import edu.illinois.cs.algo.FileUtil;
import edu.illinois.cs.algo.MotifFinder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by gourav on 4/21/15.
 */
public class Evaluator {

  public static final double INF = 1000;

  public static void main(String[] args) {
    runAll("/Users/gourav/code/motif_finder/benchmarks/default/");

    runAll("/Users/gourav/code/motif_finder/benchmarks/ML1/");
    runAll("/Users/gourav/code/motif_finder/benchmarks/ML2/");

    runAll("/Users/gourav/code/motif_finder/benchmarks/NM1/");
    runAll("/Users/gourav/code/motif_finder/benchmarks/NM2/");

    runAll("/Users/gourav/code/motif_finder/benchmarks/SC1/");
    runAll("/Users/gourav/code/motif_finder/benchmarks/SC2/");

  }

  static void runAll(String parent) {
    //String dir = "/Users/gourav/code/motif_finder/benchmarks/default/dataset0/";
    double sumEntropy = 0;
    for (int dataset=0; dataset<10; dataset++) {
      String dir = parent + "/dataset" + dataset + "/";
      try {
        int l = FileUtil.readMotifLength(dir);
        List<char[]> sequences = FileUtil.readSequences(dir);
        List<Integer> sites = FileUtil.readSites(dir + "/sites.txt");
        List<Integer> predictedSites = FileUtil.readSites(dir + "/predictedsites.txt");

        double[][] probMatrix = getProbMatrix(MotifFinder.getProfileMatrix(sequences, sites, l));
        //MotifFinder.printMatrix(probMatrix);
        //System.out.println();
        double[][] predictedProbMatrix = getProbMatrix(MotifFinder.getProfileMatrix(sequences, predictedSites, l));
        //MotifFinder.printMatrix(predictedProbMatrix);
        //System.out.println();

        //System.out.println(getRelativeEntropy(probMatrix, predictedProbMatrix) + " - " + dir);
        sumEntropy += getRelativeEntropy(probMatrix, predictedProbMatrix);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println(sumEntropy / 10 + " - " + parent);
  }
  // Gives D(p || q)
  static double getRelativeEntropy(double[][] p, double[][] q) {
    double entropy = 0;
    for (int i=0; i<p.length; i++) {
      for (int j=0; j<p[0].length; j++) {
        //TODO: What if q(x) is 0 - should it go to infinity as per priceton doc ?
        if (p[i][j]!=0 && q[i][j]!=0) {
          entropy += p[i][j] * Math.log(p[i][j] / q[i][j]);
        } else if (p[i][j] != 0 && q[i][j]==0) {
          //entropy += INF;
        }
      }
    }
    //TODO: Should we take absolute ?
    return Math.abs(entropy);
  }

  static double[][] getProbMatrix(int[][] profileMatrix) {
    double[][] probMatrix = new double[profileMatrix.length][profileMatrix[0].length];
    int sum=0;
    for (int j=0; j<profileMatrix[0].length; j++) {
      for (int i=0; i<profileMatrix.length; i++) {
        sum+=profileMatrix[i][j];
      }
    }

    for (int j=0; j<profileMatrix[0].length; j++) {
      for (int i=0; i<profileMatrix.length; i++) {
        probMatrix[i][j] = (1.0*profileMatrix[i][j]) / (1.0*sum);
      }
    }

    return probMatrix;
  }


  static char[] getMotif(char[] seq, int begin, int len) {
    char[] motif = new char[len];
    for (int i=0; i<len; i++) {
      motif[i] = seq[i+begin];
    }
    return motif;
  }
}
