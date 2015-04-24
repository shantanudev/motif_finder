package edu.illinois.cs.eval;

import edu.illinois.cs.algo.FileUtil;
import edu.illinois.cs.algo.MotifFinder;
import edu.illinois.cs.benchmark.BenchmarkGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by gourav on 4/21/15.
 */
public class Evaluator {

  public static final double INF = 1000;
  public static final double PSUEDO_COUNT = 1;

  public static void main(String[] args) {

    //String path = "/Users/gourav/code/motif_finder/benchmarks/";

    double def = runAll(BenchmarkGenerator.path + "/default/");

    double ml1 = runAll(BenchmarkGenerator.path + "/ML1/");
    double ml2 = runAll(BenchmarkGenerator.path + "/ML2/");
    System.out.println("ML [6,7,8]: " + ml1 + ", " + ml2 + ", " + def);

    double nm1 = runAll(BenchmarkGenerator.path + "/NM1/");
    double nm2 = runAll(BenchmarkGenerator.path + "/NM2/");
    System.out.println("NM [0,1,2]: " + nm1 + ", " + def + ", " + nm2);

    double sc1 = runAll(BenchmarkGenerator.path + "/SC1/");
    double sc2 = runAll(BenchmarkGenerator.path + "/SC2/");
    System.out.println("SC [5,10,20]: " + sc1 + ", " + def + ", " + sc2);


  }

  static double runAll(String parent) {
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
        sumEntropy += getRelativeEntropyWithPsuedoCounts(probMatrix, predictedProbMatrix);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    sumEntropy = sumEntropy / 10;
    //System.out.println(sumEntropy + " - " + parent);
    return sumEntropy;
  }

  // Gives D(p || q) with psuedoCounts
  static double getRelativeEntropyWithPsuedoCounts(double[][] p, double[][] q) {
    double entropy = 0;

    for (int j=0; j<p[0].length; j++) {
      double[] pVector = new double[p.length];
      double[] qVector = new double[q.length];
      for (int i=0; i<p.length; i++) {
        pVector[i] = p[i][j];
        qVector[i] = q[i][j];
      }
      entropy += getRelativeEntropy(pVector, qVector);
    }

    //return Math.abs(entropy);
    return (entropy / p[0].length);
  }

  // Gives D(p || q)
  static double getRelativeEntropy(double[][] p, double[][] q) {
    double entropy = 0;
    for (int i=0; i<p.length; i++) {
      for (int j=0; j<p[0].length; j++) {
        //TODO: What if q(x) is 0 - should it go to infinity as per princeton doc ?
        if (p[i][j]!=0 && q[i][j]!=0) {
          entropy += p[i][j] * Math.log(p[i][j] / q[i][j]);
        } else if (p[i][j] != 0 && q[i][j]==0) {
          //entropy += INF;
        }
      }
    }
    //TODO: Should we take absolute ?
    //return Math.abs(entropy);
    return (entropy);
  }

  //Entropy with pseudo counts
  static double getRelativeEntropy(double[] p, double[] q) {
    double entropy = 0;
    assert (p.length == q.length);

    for (int i=0; i<p.length; i++) {
      //add psuedo counts
      double pPsuedo = p[i] + PSUEDO_COUNT;
      double qPsuedo = q[i] + PSUEDO_COUNT;

      entropy += pPsuedo * Math.log(pPsuedo / qPsuedo);
    }
    return entropy;
  }

  static double[][] getProbMatrix(int[][] profileMatrix) {
    double[][] probMatrix = new double[profileMatrix.length][profileMatrix[0].length];

    for (int j=0; j<profileMatrix[0].length; j++) {
      int sum=0;
      for (int i=0; i<profileMatrix.length; i++) {
        sum+=profileMatrix[i][j];
      }

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
