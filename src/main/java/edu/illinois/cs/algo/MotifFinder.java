package edu.illinois.cs.algo;

import edu.illinois.cs.benchmark.BenchmarkGenerator;
import edu.illinois.cs.eval.Evaluator;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gourav on 4/17/15.
 */
public class MotifFinder {

  public static final int A=0;
  public static final int C=1;
  public static final int G=2;
  public static final int T=3;

  int[][] runningProfileMatrix;
  double bestScore;

  String dir;

  public MotifFinder(String dir) {
    this.dir = dir;
  }

  void writeSites(List<Integer> positions) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + "/predictedsites.txt")));
    for (int pos : positions) {
      //System.out.println("writing " + sitePos);
      writer.write(pos + "");
      writer.newLine();
    }
    writer.flush();
    writer.close();
  }

  void writeMatrix(int[][] matrix) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + "/predictedmotif.txt")));
    writer.write(">PMOTIF\t" + matrix[0].length);
    writer.newLine();
    for (int j=0; j<matrix[0].length; j++) {
      for (int i=0; i<matrix.length; i++) {
        writer.write(matrix[i][j] + "\t");
      }
      writer.newLine();
    }
    writer.write("<");
    writer.flush();
    writer.close();
  }

  public static void printMatrix(int[][] matrix) {
    for (int j=0; j<matrix[0].length; j++) {
      for (int i=0; i<matrix.length; i++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.println();
    }
  }

  public static void printMatrix(double[][] matrix) {
    for (int j=0; j<matrix[0].length; j++) {
      for (int i=0; i<matrix.length; i++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.println();
    }
  }

  List<Integer> getAlignment(char[] s1, char[] s2, int l) {
    int pos1 = -1, pos2 = -1;
    double maxScore = Integer.MIN_VALUE;
    for (int i=0; i<s1.length -l + 1; i++) {
      for (int j=0; j<s2.length -l + 1; j++) {
        //System.out.println("Get Score : " + i +", " + j);
        double score = getScore(s1, s2, l, i, j);
        if (score > maxScore) {
          //System.out.println("Got score: " + score);
          maxScore = score;
          pos1 = i;
          pos2 = j;
        }
      }
    }
    List<Integer> positions = new ArrayList<Integer>();
    positions.add(pos1);
    positions.add(pos2);
    return positions;
  }

  private double getScore(char[] s1, char[] s2, int l, int i, int j) {
    List<char[]> sequences = new ArrayList<char[]>();
    sequences.add(s1);
    sequences.add(s2);
    List<Integer> statingPoints = new ArrayList<Integer>();
    statingPoints.add(i);
    statingPoints.add(j);
    return getScore(sequences, statingPoints, l);
  }

  private double getScore(List<char[]> sequences, List<Integer> startingPoints, int l) {
    //calculate profile matrix
    int[][] profileMatrix = getProfileMatrix(sequences, startingPoints, l);
    //double q = sequences.size() * 1.0 / 4.0;

    return getScore(profileMatrix, sequences.size());
  }

  private double getScore(int[][] profileMatrix, int sc) {
    //TODO: Wait, why q is the length of the motif / 4 - shouldn't it be SC /4 ?
    // looks like it really doesn't have much performance effect
    //double q = profileMatrix[0].length * 1.0 / 4.0;
    double q = sc * 1.0 / 4.0;
    //calculate info content
    double score = 0;
    for (int column=0; column<profileMatrix[0].length; column++) {
      for (int beta=0; beta<profileMatrix.length; beta++) {
        double W_beta = profileMatrix[beta][column] * 1.0;
        if (W_beta != 0) {
          score += W_beta * Math.log(W_beta / q);
        } else {
          //TODO: Is Psuedo counts ok here ?
          // Confirm - psuedo counts affect the numbers, which are better in absence of these.
          //score += Evaluator.PSUEDO_COUNT * Math.log(Evaluator.PSUEDO_COUNT / q);
        }
      }
    }
    //System.out.println(score);
    return score;
  }

  public static int[][] getProfileMatrix(List<char[]> sequences, List<Integer> startingPoints, int l) {
    //System.out.println("ProfileMatrix: starting points: " + startingPoints);
    int[][] profileMatrix = new int[4][l];
    //int columnSize = sequences.get(0).length;

    for (int column=0; column < l; column++) {
      profileMatrix[A][column] = 0;
      profileMatrix[C][column] = 0;
      profileMatrix[G][column] = 0;
      profileMatrix[T][column] = 0;

      for (int i=0; i<sequences.size(); i++) {
        int index = startingPoints.get(i) + column;
        //System.out.println("i, index:" + i + ", " + index);
        if (sequences.get(i)[index] == 'A') {
          profileMatrix[A][column]++;
        }
        if (sequences.get(i)[index] == 'C') {
          profileMatrix[C][column]++;
        }
        if (sequences.get(i)[index] == 'G') {
          profileMatrix[G][column]++;
        }
        if (sequences.get(i)[index] == 'T') {
          profileMatrix[T][column]++;
        }
      }

    }
    return profileMatrix;
  }

  int getBestLMer(char[] seq, int l, int sc) {
    double maxScore = Integer.MIN_VALUE;
    int bestStartPoint = -1;
    int[][] bestProfileMatrix = null;

    for (int startPoint=0; startPoint<seq.length - l + 1; startPoint++) {

      int[][] temp = copy(runningProfileMatrix);
      for (int column = 0; column < l; column++) {
        int index = column + startPoint;

        if (seq[index] == 'A') {
          temp[A][column]++;
        }
        if (seq[index] == 'C') {
          temp[C][column]++;
        }
        if (seq[index] == 'G') {
          temp[G][column]++;
        }
        if (seq[index] == 'T') {
          temp[T][column]++;
        }
      }

      double score = getScore(temp, sc);
      if (score > maxScore) {
        maxScore = score;
        bestStartPoint = startPoint;
        bestProfileMatrix = temp;
      }
    }

    runningProfileMatrix = bestProfileMatrix;
    bestScore = maxScore;
    return bestStartPoint;

  }

  public static int[][] copy(int[][] original) {
    int[][] duplicate = new int[original.length][original[0].length];
    int index = 0;
    for (int[] row : original) {
      duplicate[index++] = Arrays.copyOf(row, row.length);
    }
    return duplicate;
  }

  public MotifResult greedySearch(List<char[]> sequences, int l, int mode) {
    //1. pick the best l-mer for first two sequences
    List<Integer> positions = getAlignment(sequences.get(0), sequences.get(1), l);
    //System.out.println("Best positions: " + positions);

    List<char[]> seenSequences = new ArrayList<char[]>();
    seenSequences.add(sequences.get(0));
    seenSequences.add(sequences.get(1));

    //reset global variables - not required to set bestScore though
    runningProfileMatrix = getProfileMatrix(seenSequences, positions, l);
    bestScore = Integer.MIN_VALUE;


    // t-2 iterations: keep the runningProfileMatrix and positions
    int i=2;
    while (i<sequences.size()) {
      //System.out.println("Iteration " + i);
      positions.add(getBestLMer(sequences.get(i), l, i+1));
      i++;
    }

    // Either print to terminal or write to a file
    if (mode == Constants.PRINT_TO_CONSOLE) {
      System.out.println("Positions: " + positions);
      for (int[] arr : runningProfileMatrix) {
        System.out.println(Arrays.toString(arr));
      }
    } else if (mode == Constants.WRITE_TO_FILE) {
      try {
        writeSites(positions);
        writeMatrix(runningProfileMatrix);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    //compose and return result
    return new MotifResult(runningProfileMatrix, bestScore, positions);
  }

  public static long runAll (String parent) {
    long start = System.currentTimeMillis();
    MotifFinder finder = null;
    for (int dataset=0; dataset<10; dataset++) {
      String dir = parent + "/dataset" + dataset + "/";
      //String dir = "/Users/gourav/code/motif_finder/benchmarks/default/dataset0/";
      finder = new MotifFinder(dir);
      try {
        List<char[]> sequences = FileUtil.readSequences(dir);
        int l = FileUtil.readMotifLength(dir);
        finder.greedySearch(sequences, l, Constants.WRITE_TO_FILE);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return (System.currentTimeMillis() - start);
  }

  public void greedySearchWithPerms(List<char[]> sequences, int l, int mode) {
    double maxScore = Integer.MIN_VALUE;
    MotifResult bestMotifResult = null;
    int bestFirstSeq = -1;
    int bestSecondSeq = -1;

    int calleeMode = Constants.STEALTH_EXECUTION;

    for (int i=0; i<sequences.size(); i++) {
      for (int j=i+1; j<sequences.size(); j++) {
        // start with i and j
        List<char[]> shuffledSequences = new ArrayList<char[]>(sequences);
        char[] firstSeq = null;
        char[] secondSeq = null;
        if (i > j) {
          firstSeq = shuffledSequences.remove(i);
          secondSeq = shuffledSequences.remove(j);
        } else if (i < j) {
          secondSeq = shuffledSequences.remove(j);
          firstSeq = shuffledSequences.remove(i);
        } else {
          System.out.println("ERROR: How can " + j + " and " + i + " be equal.");
          System.exit(1);
        }

        shuffledSequences.add(0, secondSeq);
        shuffledSequences.add(0, firstSeq);

        // call greedy search
        if (calleeMode == Constants.PRINT_TO_CONSOLE) {
          System.out.println("\nPerm: " + i + ", " + j);
        }
        MotifResult result = greedySearch(shuffledSequences, l, calleeMode);

        //check if it's the best
        if (result.score > maxScore) {
          maxScore = bestScore;
          bestMotifResult = result;
          bestFirstSeq = i;
          bestSecondSeq = j;
        }
      }
    }

    if (calleeMode == Constants.PRINT_TO_CONSOLE) {
      System.out.println("\nResult: ");
    }

    // Either print to terminal or write to a file
    if (mode == Constants.PRINT_TO_CONSOLE) {
      System.out.println("First and Second Seq. index: " + bestFirstSeq + ", " + bestSecondSeq);
      System.out.println("Positions: " + bestMotifResult.positions);
      for (int[] arr : bestMotifResult.profileMatrix) {
        System.out.println(Arrays.toString(arr));
      }
    } else if (mode == Constants.WRITE_TO_FILE) {
      try {
        writeSites(bestMotifResult.positions);
        writeMatrix(bestMotifResult.profileMatrix);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static long runAllWithPerms (String parent) {
    long start = System.currentTimeMillis();
    MotifFinder finder = null;
    for (int dataset=0; dataset<10; dataset++) {
      String dir = parent + "/dataset" + dataset + "/";
      //String dir = "/Users/gourav/code/motif_finder/benchmarks/default/dataset0/";
      finder = new MotifFinder(dir);
      try {
        List<char[]> sequences = FileUtil.readSequences(dir);
        int l = FileUtil.readMotifLength(dir);
        finder.greedySearchWithPerms(sequences, l, Constants.WRITE_TO_FILE);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return (System.currentTimeMillis() - start);
  }


  public static void main(String[] args) {

    //String path = "/Users/gourav/code/motif_finder/benchmarks/";

    /*runAll(BenchmarkGenerator.path + "/default/");

    runAll(BenchmarkGenerator.path + "/ML1/");
    runAll(BenchmarkGenerator.path + "/ML2/");

    runAll(BenchmarkGenerator.path + "/NM1/");
    runAll(BenchmarkGenerator.path + "/NM2/");

    runAll(BenchmarkGenerator.path + "/SC1/");
    runAll(BenchmarkGenerator.path + "/SC2/");*/


    MotifFinder finder = new MotifFinder("/aba-daba-do");
    List<char[]> sequences = new ArrayList<char[]>();
    sequences.add(new char[]{'G', 'A', 'G', 'C'});
    sequences.add(new char[]{'G', 'G', 'C', 'G'});
    sequences.add(new char[]{'T', 'G', 'A', 'G'});
    sequences.add(new char[]{'A', 'G', 'C', 'G'});
    sequences.add(new char[]{'A', 'G', 'C', 'G'});
    sequences.add(new char[]{'A', 'G', 'C', 'G'});


    finder.greedySearchWithPerms(sequences, 3, Constants.PRINT_TO_CONSOLE);

    //System.out.print(finder.getAlignment(sequences.get(0), sequences.get(1), 3));

    /*sequences.add(new char[]{'A', 'T', 'T', 'G'});
    List<Integer> statingPoints = new ArrayList<Integer>();
    statingPoints.add(0);
    statingPoints.add(1);
    statingPoints.add(2);
    int[][] matrix = finder.getProfileMatrix(sequences, statingPoints, 2);
    for (int[] arr : matrix) {
      System.out.println(Arrays.toString(arr));
    }*/
  }
}
