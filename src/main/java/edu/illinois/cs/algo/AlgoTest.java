package edu.illinois.cs.algo;

import java.io.IOException;
import java.util.List;

/**
 * Created by gourav on 4/21/15.
 */
public class AlgoTest {

  public static void main(String[] args) {
    String dir = "/Users/gourav/code/motif_finder/benchmarks/default/dataset0/";
    try {
      List<char[]> sequences = FileUtil.readSequences(dir);
      printPart(sequences.get(0), 12, 8);
      printPart(sequences.get(1), 335, 8);
      printPart(sequences.get(2), 284, 8);
      printPart(sequences.get(3), 218, 8);
      printPart(sequences.get(4), 357, 8);
      printPart(sequences.get(5), 358, 8);
      printPart(sequences.get(6), 407, 8);
      printPart(sequences.get(7), 396, 8);
      printPart(sequences.get(8), 326, 8);
      printPart(sequences.get(9), 55, 8);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static void printPart(char[] arr, int begin, int len) {
    for (int i=0; i<len; i++) {
      System.out.print(arr[i+begin] + " ");
    }
    System.out.println();
  }
}
