package edu.illinois.cs.algo;

import edu.illinois.cs.benchmark.BenchmarkGenerator;
import edu.illinois.cs.eval.Evaluator;

/**
 * Created by gourav on 4/24/15.
 */
public class Main {

  public static void main(String[] args) {

    //0. Set seed
    BenchmarkGenerator.seed = System.currentTimeMillis();
    System.out.println("Seed: " + BenchmarkGenerator.seed + "\n");

    //1. Create benchmark
    BenchmarkGenerator benchmarkGenerator = new BenchmarkGenerator(BenchmarkGenerator.path);
    benchmarkGenerator.generate();




    //2. Run Motif Finder and log running times
    long defTime = MotifFinder.runAll(BenchmarkGenerator.path + "/default/");

    long ml1Time = MotifFinder.runAll(BenchmarkGenerator.path + "/ML1/");
    long ml2Time = MotifFinder.runAll(BenchmarkGenerator.path + "/ML2/");
    System.out.println("ML Time [6,7,8]: " + ml1Time + ", " + ml2Time + ", " + defTime);

    long nm1Time = MotifFinder.runAll(BenchmarkGenerator.path + "/NM1/");
    long nm2Time = MotifFinder.runAll(BenchmarkGenerator.path + "/NM2/");
    System.out.println("NM Time [0,1,2]: " + nm1Time + ", " + defTime + ", " + nm2Time);

    long sc1Time = MotifFinder.runAll(BenchmarkGenerator.path + "/SC1/");
    long sc2Time = MotifFinder.runAll(BenchmarkGenerator.path + "/SC2/");
    System.out.println("SC Time [5,10,20]: " + sc1Time + ", " + defTime + ", " + sc2Time);


    System.out.println();


    //3. Evaluate Results
    double def = Evaluator.runAll(BenchmarkGenerator.path + "/default/");

    double ml1 = Evaluator.runAll(BenchmarkGenerator.path + "/ML1/");
    double ml2 = Evaluator.runAll(BenchmarkGenerator.path + "/ML2/");
    System.out.println("ML [6,7,8]: " + ml1 + ", " + ml2 + ", " + def);

    double nm1 = Evaluator.runAll(BenchmarkGenerator.path + "/NM1/");
    double nm2 = Evaluator.runAll(BenchmarkGenerator.path + "/NM2/");
    System.out.println("NM [0,1,2]: " + nm1 + ", " + def + ", " + nm2);

    double sc1 = Evaluator.runAll(BenchmarkGenerator.path + "/SC1/");
    double sc2 = Evaluator.runAll(BenchmarkGenerator.path + "/SC2/");
    System.out.println("SC [5,10,20]: " + sc1 + ", " + def + ", " + sc2);
  }
}
