package edu.illinois.cs.algo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

  String dir;

  public MotifFinder(String dir) {
    this.dir = dir;
  }

  List<Integer> getAlignment(char[] s1, char[] s2, int l) {
    int pos1 = -1, pos2 = -1;
    double maxScore = Double.MIN_VALUE;
    for (int i=0; i<s1.length -l + 1; i++) {
      for (int j=0; j<s2.length -l + 1; j++) {
        //System.out.println("Get Score : " + i +", " + j);
        double score = getScore(s1, s2, l, i, j);
        if (score > maxScore) {
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

    return getScore(profileMatrix);
  }

  private double getScore(int[][] profileMatrix) {
    double q = profileMatrix[0].length * 1.0 / 4.0;
    //calculate info content
    double score = 0;
    for (int column=0; column<profileMatrix[0].length; column++) {
      for (int beta=0; beta<profileMatrix.length; beta++) {
        double W_beta = profileMatrix[beta][column] * 1.0;
        if (W_beta != 0) {
          score += W_beta * Math.log(W_beta / q);
        }
      }
    }
    return score;
  }

  private int[][] getProfileMatrix(List<char[]> sequences, List<Integer> startingPoints, int l) {
    int[][] profileMatrix = new int[4][l];
    //int columnSize = sequences.get(0).length;

    for (int column=0; column < l; column++) {
      profileMatrix[A][column] = 0;
      profileMatrix[C][column] = 0;
      profileMatrix[G][column] = 0;
      profileMatrix[T][column] = 0;

      for (int i=0; i<sequences.size(); i++) {
        int index = startingPoints.get(i) + column;

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

  int getBestLMer(char[] seq, int l) {
    double maxScore = Double.MIN_VALUE;
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

      double score = getScore(temp);
      if (score > maxScore) {
        maxScore = score;
        bestStartPoint = startPoint;
        bestProfileMatrix = temp;
      }
    }

    runningProfileMatrix = bestProfileMatrix;
    return bestStartPoint;

  }

  private static int[][] copy(int[][] original) {
    int[][] duplicate = new int[original.length][original[0].length];
    int index = 0;
    for (int[] row : original) {
      duplicate[index++] = Arrays.copyOf(row, row.length);
    }
    return duplicate;
  }

  public void greedySearch(List<char[]> sequences, int l) {
    //1. pick the best l-mer for first two sequences
    List<Integer> positions = getAlignment(sequences.get(0), sequences.get(1), l);

    List<char[]> seenSequences = new ArrayList<char[]>();
    seenSequences.add(sequences.get(0));
    seenSequences.add(sequences.get(1));
    runningProfileMatrix = getProfileMatrix(seenSequences, positions, l);


    // t-2 iterations: keep the runningProfileMatrix and positions
    int i=2;
    while (i<sequences.size()) {
      //System.out.println("Iteration " + i);
      positions.add(getBestLMer(sequences.get(i), l));
      i++;
    }

    //System.out.println("Positions: "  + positions);

    //for (int[] arr : runningProfileMatrix) {
     // System.out.println(Arrays.toString(arr));
    //}

    try {
      writeSites(positions);
      writeMatrix();
    } catch (IOException e) {
      e.printStackTrace();
    }


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

  void writeMatrix() throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + "/predictedmotif.txt")));
    writer.write(">PMOTIF\t" + runningProfileMatrix[0].length);
    writer.newLine();
    for (int j=0; j<runningProfileMatrix[0].length; j++) {
      for (int i=0; i<runningProfileMatrix.length; i++) {
        writer.write(runningProfileMatrix[i][j] + "\t");
      }
      writer.newLine();
    }
    writer.write("<");
    writer.flush();
    writer.close();
  }

  public static void main(String[] args) {
    MotifFinder finder = new MotifFinder("/Users/gourav/code/motif_finder/benchmarks/default/dataset0/");

    List<char[]> sequences = new ArrayList<char[]>();
    sequences.add(new char[]{'G', 'A', 'G', 'C'});
    sequences.add(new char[]{'G', 'G', 'C', 'G'});
    sequences.add(new char[]{'T', 'G', 'A', 'G'});
    sequences.add(new char[]{'A', 'G', 'C', 'G'});

    finder.greedySearch(sequences, 3);

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
