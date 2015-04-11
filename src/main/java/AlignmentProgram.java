import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by gourav on 2/14/15.
 */
public class AlignmentProgram {

  public static final char GAP = '_';

  Map<Character, Map<Character, Integer>> scores;
  int gap;

  public AlignmentProgram(String substitutionFile, int gap) throws IOException {
    scores = readSubstitutionMatrix(substitutionFile);
    this.gap = gap;
  }

  public AlignmentProgram(Map<Character, Map<Character, Integer>> scores, int gap) {
    this.scores = scores;
    this.gap = gap;
  }

  public static class TransformStep {
    char a;
    char b;
    int nextA;
    int nextB;

    public TransformStep(char a, char b, int nextA, int nextB) {
      this.a = a;
      this.b = b;
      this.nextA = nextA;
      this.nextB = nextB;
    }
  }

  int[][] align(char[] seq1, char[] seq2, Map<Integer, Map<Integer, Set<TransformStep>>> result) {
    int[][] memory = new int[seq1.length+1][seq2.length+1];

    for (int i=0; i<=seq1.length; i++) {
      result.put(i, new HashMap<Integer, Set<TransformStep>>());
      for (int j=0; j<=seq2.length; j++) {
        Set<TransformStep> transformSteps = new HashSet<TransformStep>();
        if (i==0 && j==0) {
          memory[i][j] = 0;
          transformSteps.add(new TransformStep(' ', ' ', 0, 0));
        } else if (i==0) {
          memory[i][j] = memory[i][j-1] + gap;
          transformSteps.add(new TransformStep(GAP, seq2[j-1] ,i, j-1));
        } else if (j==0) {
          memory[i][j] = memory[i-1][j] + gap;
          transformSteps.add(new TransformStep(seq1[i-1], GAP, i-1, j));
        } else {
          int substitution = memory[i-1][j-1] + scores.get(seq1[i-1]).get(seq2[j-1]);
          int addition = memory[i-1][j] + gap;
          int deletion = memory[i][j-1] + gap;

          if (substitution >= addition && substitution >= deletion ) {
            transformSteps.add(new TransformStep(seq1[i-1], seq2[j-1], i-1, j-1));
          }
          if (addition >= substitution && addition >= deletion ) {
            transformSteps.add(new TransformStep(seq1[i-1], GAP, i-1, j));
          }
          if (deletion >= substitution && deletion >= addition) {
            transformSteps.add(new TransformStep(GAP, seq2[j-1] ,i, j-1));
          }

          memory[i][j] = Math.max(substitution, Math.max(addition, deletion));
        }
        result.get(i).put(j, transformSteps);
      }

    }
    return memory;
  }

  public void printTable(int[][] matrix, char[] seq1, char[] seq2) {
    System.out.print("{ ");
    for (char base : seq1) {
      System.out.print("l | ");
    }
    System.out.println("l }");
    System.out.print(String.format("%3c", ' '));
    for (char base : seq1) {
      System.out.print(String.format(" &%3c", base));
    }
    System.out.println(" \\\\ \\hline");
    for (int j=1; j<matrix[0].length; j++) {
      System.out.print(String.format("%3c", seq2[j-1]));
      for (int i=1; i<matrix.length; i++) {
        System.out.print(String.format(" &%3d", matrix[i][j]));
      }
      System.out.println(" \\\\ \\hline");
    }
  }

  public void printMatrix(int[][] matrix, char[] seq1, char[] seq2) {
    System.out.print(String.format("%3c", ' '));
    for (char base : seq1) {
      System.out.print(String.format("%3c", base));
    }
    System.out.println();
    for (int j=1; j<matrix[0].length; j++) {
      System.out.print(String.format("%3c", seq2[j-1]));
      for (int i=1; i<matrix.length; i++) {
        System.out.print(String.format("%3d", matrix[i][j]));
      }
      System.out.println();
    }
  }

  public List<char[]> getAlignment(Map<Integer, Map<Integer, Set<TransformStep>>> result, int indexA, int indexB) {
    List<char[]> alignment = new ArrayList<char[]>();
    while (indexA != 0 && indexB != 0) {
      Set<TransformStep> transformSteps = result.get(indexA).get(indexB);
      for (TransformStep step : transformSteps) {
        alignment.add(0, new char[]{step.a, step.b});
        indexA = step.nextA;
        indexB = step.nextB;
        break;
      }
    }
    return alignment;
  }

  public Set<List<char[]>> getAlignments(Map<Integer, Map<Integer, Set<TransformStep>>> result, int indexA, int indexB) {
    Set<List<char[]>> alignments = new HashSet<List<char[]>>();
    if (indexA == 0 && indexB == 0) {
      alignments.add(new ArrayList<char[]>());
      return alignments;
    }
    Set<TransformStep> transformSteps = result.get(indexA).get(indexB);
    for (TransformStep step : transformSteps) {
      Set<List<char[]>> moreAlignments = getAlignments(result, step.nextA, step.nextB);
      for (List<char[]> alignment : moreAlignments) {
        alignment.add(new char[]{step.a, step.b});
        alignments.add(alignment);
      }
    }
    return alignments;
  }

  public void printAlignment(List<char[]> alignment) {
    for (char[] pair : alignment) {
      System.out.print(pair[0] + " ");
    }
    System.out.println();
    for (char[] pair : alignment) {
      System.out.print(pair[1] + " ");
    }
    System.out.println();
  }

  public void printAlignments(Set<List<char[]>> alignments) {
    System.out.println("Printing " + alignments.size() + " alignments");
    for (List<char[]> alignment : alignments) {
      printAlignment(alignment);
    }
  }

  static public String readFASTA(String filename) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
    String sequence = "";
    String line = reader.readLine();
    if (line.charAt(0) != '>') {
      System.out.println("ERROR");
      return null;
    }
    while ((line=reader.readLine()) != null) {
      sequence += line;
    }
    return sequence;
  }

  public Map<Character, Map<Character, Integer>> readSubstitutionMatrix(String filename) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
    Map<Character, Map<Character, Integer>> substitutionMatrix = new HashMap<Character, Map<Character, Integer>>();

    String line = reader.readLine();
    String[] parts = line.split("\t");
    char[] bases = new char[parts.length];
    for (int i=1; i<parts.length; i++) {
      bases[i-1] = parts[i].charAt(0);
    }

    //System.out.println(Arrays.toString(bases));

    while ((line = reader.readLine())!= null) {
      parts = line.split("\t");
      char base = parts[0].charAt(0);
      substitutionMatrix.put(base, new HashMap<Character, Integer>());
      for (int i=1; i<parts.length; i++) {
        substitutionMatrix.get(base).put(bases[i - 1], Integer.parseInt(parts[i]));
      }
    }
    return substitutionMatrix;
  }

  public void printScores() {
    for (Map.Entry<Character, Map<Character, Integer>> entry : scores.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue());
    }
  }

  public static void main(String[] args) {
    try {
      //System.out.println(Arrays.toString(args));
      if (args.length != 4) {
        System.out.println("Usage: java AlignmentProgram <fasta-1> <fasta-2> <subs-matrix> <gap-score>");
        return;
      }

      char[] seq1 = AlignmentProgram.readFASTA(args[0]).toCharArray();
      char[] seq2 = AlignmentProgram.readFASTA(args[1]).toCharArray();

      AlignmentProgram alignmentProgram = new AlignmentProgram(args[2], Integer.parseInt(args[3]));
      //alignmentProgram.printScores();

      Map<Integer, Map<Integer, Set<TransformStep>>> result = new HashMap<Integer, Map<Integer, Set<TransformStep>>>();
      int[][] memory = alignmentProgram.align(seq1, seq2, result);
      //alignmentProgram.printMatrix(memory, seq1, seq2);
      System.out.println("The optimal alignment between given sequences has score " + memory[seq1.length][seq2.length]);
      List<char[]> alignment = alignmentProgram.getAlignment(result, seq1.length, seq2.length);
      alignmentProgram.printAlignment(alignment);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
