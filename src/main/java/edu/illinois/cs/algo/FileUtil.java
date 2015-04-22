package edu.illinois.cs.algo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gourav on 4/21/15.
 */
public class FileUtil {

  public static List<char[]> readSequences(String dir) throws IOException {
    List<char[]> sequences = new ArrayList<char[]>();
    BufferedReader reader = new BufferedReader(new FileReader(new File(dir + "/sequences.fa")));
    String line = null;
    while ((line = reader.readLine())!=null) {
      line = line.substring(4);
      //System.out.println("Seq length: " + line.length());
      sequences.add(line.toCharArray());
    }
    reader.close();
    return sequences;
  }

  public static List<char[]> readProfileMatrix(String dir) throws IOException {
    return  null;
  }

  public static List<Integer> readSites(String file) throws IOException {
    List<Integer> positions = new ArrayList<Integer>();
    BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
    String line = null;
    while ((line = reader.readLine())!=null) {
      positions.add(Integer.parseInt(line));
      //System.out.println("Seq length: " + line.length());

    }
    reader.close();
    return positions;
  }


  public static int readMotifLength(String dir) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File(dir + "/motiflength.txt")));
    int l = Integer.parseInt(reader.readLine());
    //System.out.println("motif length: " + l);
    reader.close();
    return l;
  }
}
