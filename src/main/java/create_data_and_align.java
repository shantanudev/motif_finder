import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by gourav on 2/16/15.
 */
public class create_data_and_align {

  int L;
  Random random;

  public create_data_and_align(int L) {
    this.L = L;
    random = new Random(System.currentTimeMillis());
  }

  char getRandomNucleotide() {
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

  char[] getRandomSequence() {
    char[] s1 = new char[L];
    for (int i=0; i<L; i++) {
      s1[i] = getRandomNucleotide();
    }
    return s1;
  }

  char getMutation(char original) {
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

  char[] createRandomMutations(char[] s1) {
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

  public void writeFASTA(String filename, char[] seq) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
    writer.write(">" + filename);
    writer.newLine();
    writer.write(seq);
    writer.close();
  }

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage: java create_data_and_align <L> <name>");
      return;
    }
    String name = args[1];
    create_data_and_align dataCreator = new create_data_and_align(Integer.parseInt(args[0]));
    char[] s1 = dataCreator.getRandomSequence();
    char[] s2 = dataCreator.createRandomMutations(s1);
    char[] s3 = dataCreator.createRandomMutations(s1);
    //System.out.println(Arrays.toString(s1));
    //System.out.println(Arrays.toString(s2));
    //System.out.println(Arrays.toString(s3));
    String file1 = "1-" + name + ".txt";
    String file2 = "2-" + name + ".txt";
    try {
      dataCreator.writeFASTA(file1, s2);
      dataCreator.writeFASTA(file2, s3);
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
