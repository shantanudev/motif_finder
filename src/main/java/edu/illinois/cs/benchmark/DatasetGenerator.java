package edu.illinois.cs.benchmark;

import java.util.*;

/**
 * Created by gourav on 4/11/15.
 */
public class DatasetGenerator {

  String name;
  int ML;
  int NM;
  int SL;
  int SC;

  public DatasetGenerator(String name, int ML, int NM, int SL, int SC) {
    this.name = name;
    this.ML = ML;
    this.NM = NM;
    this.SL = SL;
    this.SC = SC;
  }

  public void generate(String dir) {
    // create dataset. Write into subdirectory of dir

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

    //5. write four files -
    // sequences.fa,
    writeSequences(dir, sequences);
    // sites.txt,
    writeSites(dir, sitePositions);
    //motif.txt,
    writeMotif(dir, motif, randomPositions);
    //motiflength.txt
    writeMotifLength(dir, motif.length);

    return;
  }

  private void writeMotifLength(String dir, int length) {
    //TODO
  }

  private void writeMotif(String dir, char[] motif, Set<Integer> randomPositions) {
    //TODO
  }

  private void writeSites(String dir, List<Integer> sitePositions) {
    //TODO
  }

  //Write FASTA file of sequences, sequences.fa
  void writeSequences(String dir, List<char[]> sequences) {
    //TODO
  }



}
