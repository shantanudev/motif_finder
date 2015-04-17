package edu.illinois.cs.benchmark;

import edu.illinois.cs.algo.MotifFinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by gourav on 4/11/15.
 */
public class DatasetGenerator {


  int ML;
  int NM;
  int SL;
  int SC;

  public DatasetGenerator( int ML, int NM, int SL, int SC) {

    this.ML = ML;
    this.NM = NM;
    this.SL = SL;
    this.SC = SC;
  }

  public void generate(String dir) {
    // create dataset. Write into subdirectory of dir
    File file = new File(dir);
    file.mkdirs();

    //1. create SC sequences
    List<char[]> sequences = new ArrayList<char[]>();
    for (int i=0; i<SC; i++) {
      sequences.add(RandomUtil.getRandomSequence(SL));
    }

    //2. Generate motif n random positions
    char[] motif = RandomUtil.getRandomSequence(ML);
    Set<Integer> randomPositions = RandomUtil.getRandomPositions(NM, ML);

    //3. Generate SC 'sites' (modified motifs) which may differ at the random positions
    List<char[]> sites = new ArrayList<char[]>();
    for (int i=0; i<SC; i++) {
      char[] site = Arrays.copyOf(motif, motif.length);
      for (int variableIndex : randomPositions) {
        site[variableIndex] = RandomUtil.getRandomNucleotide();
      }
      sites.add(site);
    }

    //4. Plant the sites in sequences at random positions
    List<Integer> sitePositions = new ArrayList<Integer>();
    for (int i=0; i<SC; i++) {
      int sitePosition = RandomUtil.getRandomPosition(SL - ML + 1);
      sitePositions.add(sitePosition);
      char[] seq = sequences.get(i);
      char[] site = sites.get(i);
      for (int j=0; j<ML; j++) {
        seq[sitePosition + j] = site[j];
      }
    }

    try {
      //5. write four files -
      // sequences.fa,
      writeSequences(dir, sequences);
      // sites.txt,
      writeSites(dir, sitePositions);
      //motif.txt,
      writeMotif(dir, motif, randomPositions);
      //motiflength.txt
      writeMotifLength(dir, motif.length);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return;
  }

  private void writeMotifLength(String dir, int length) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + "/motiflength.txt")));
      //System.out.println("writing " + length);
      writer.write(length + "");
      writer.newLine();
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeMotif(String dir, char[] motif, Set<Integer> randomPositions) {
    String motifString = "";
    for (int index : randomPositions) {
      motif[index] = '*';
    }
    for (char c : motif) {
      motifString += c;
    }
    String line = "MOTIF1\t" + motif.length + "\t" + motifString;
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + "/motif.txt")));
      writer.write(line);
      writer.newLine();
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeSites(String dir, List<Integer> sitePositions) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + "/sites.txt")));
      for (int sitePos : sitePositions) {
        //System.out.println("writing " + sitePos);
        writer.write(sitePos + "");
        writer.newLine();
      }
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void writeSequences(String dir, List<char[]> sequences) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir + "/sequences.fa")));
    int index = 0;
    for (char[] sequence : sequences) {
      writer.write(">s" + index + ":");
      writer.write(sequence);
      writer.newLine();
      index++;
    }
    writer.flush();
    writer.close();
  }

  public static void main(String[] args) {
    DatasetGenerator datasetGenerator = new DatasetGenerator(8, 1, 10, 500);
    datasetGenerator.generate("/Users/gourav/code/motif_finder/benchmarks/default/dataset0/");
  }


}
