package edu.illinois.cs.algo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gourav on 4/27/15.
 */
public class MotifResult {

  int[][] profileMatrix;
  double score;
  List<Integer> positions;

  //List<char[]> sequences and int l were provided.


  public MotifResult(int[][] profileMatrix, double score, List<Integer> positions) {
    //Make copies to be safe from global variables.
    this.profileMatrix = MotifFinder.copy(profileMatrix);
    this.score = score;
    this.positions = new ArrayList<Integer>(positions);
  }
}
