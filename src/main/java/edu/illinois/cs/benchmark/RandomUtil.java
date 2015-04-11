package edu.illinois.cs.benchmark;

import java.io.*;
import java.util.*;

/**
 * Created by gourav on 2/16/15.
 */
public class RandomUtil {

  static Random random = new Random(BenchmarkGenerator.seed);

  // get random nt with uniform prob.
  public static char getRandomNucleotide() {
    int val = random.nextInt(4);
    switch (val) {
      case 0: return 'A';
      case 1: return 'C';
      case 2: return 'G';
      case 3: return 'T';
      default: System.out.println("ERROR");
    }
    return 'A';
  }


  // get random sequence of length l
  public static char[] getRandomSequence(int l) {
    char[] s1 = new char[l];
    for (int i=0; i<l; i++) {
      s1[i] = getRandomNucleotide();
    }
    return s1;
  }

  // get a specific mutation for original nt.
  public static char getMutation(char original) {
    if (original == 'A') {
      return 'C';
    } else if (original == 'T') {
      return 'G';
    } else if (original == 'C') {
      return 'A';
    } else if (original == 'G') {
      return 'T';
    } else {
      System.out.println("ERROR");
    }
    return 'A';
  }

  // create mutated sequence
  public static char[] createRandomMutations(char[] s1) {
    ArrayList<Character> s2 = new ArrayList<Character>();
    for (int i=0; i<s1.length; i++) {
      s2.add(s1[i]);
    }
    for (int i=0; i<s1.length/10; i++) {
      int pos = random.nextInt(s2.size());
      char c = s2.remove(i);
      if (random.nextBoolean()) {
        s2.add(pos, getMutation(c));
      }
    }
    char[] ans = new char[s2.size()];
    for (int i=0; i<s2.size(); i++) {
      ans[i] = s2.get(i);
    }
    return ans;
  }

  // write a seq into FASTA file
  public static void writeFASTA(String filename, char[] seq) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
    writer.write(">" + filename);
    writer.newLine();
    writer.write(seq);
    writer.close();
  }

  //returns a sorted list of n random positions between 0 and l-1
  public static Set<Integer> getRandomPositions(int n, int l) {
    List<Integer> availablePositions = new ArrayList<Integer>();
    for (int i=0; i<l; i++) {
      availablePositions.add(i);
    }
    Set<Integer> randomPositions = new HashSet<Integer>();
    for (int i=0; i<n; i++) {
      int index = random.nextInt(availablePositions.size());
      randomPositions.add(availablePositions.remove(index));
    }
    return randomPositions;
  }

  // return a random number between 0 and i-1
  public static int getRandomPosition(int i) {
    return random.nextInt(i);
  }

  // Alignment program main
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage: java create_data_and_align <L> <name>");
      return;
    }
    String name = args[1];
    char[] s1 = getRandomSequence(Integer.parseInt(args[0]));
    char[] s2 = createRandomMutations(s1);
    char[] s3 = createRandomMutations(s1);
    //System.out.println(Arrays.toString(s1));
    //System.out.println(Arrays.toString(s2));
    //System.out.println(Arrays.toString(s3));
    String file1 = "1-" + name + ".txt";
    String file2 = "2-" + name + ".txt";
    try {
      writeFASTA(file1, s2);
      writeFASTA(file2, s3);
      Process process = Runtime.getRuntime().exec("java -cp target/classes/ AlignmentProgram " + file1 + " " + file2 + " /Users/gourav/Downloads/assn2-alnproblem/subs.txt -500");
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedWriter writer = new BufferedWriter(new FileWriter(new File(name + ".txt")));
      String line;
      while ((line = br.readLine()) != null) {
        writer.write(line);
        writer.newLine();
        //System.out.println(line);
      }
      process.waitFor();
      //System.out.println ("exit: " + process.exitValue());
      process.destroy();
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
