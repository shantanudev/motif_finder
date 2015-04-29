package edu.illinois.cs;

import edu.illinois.cs.algo.MotifFinder;
import edu.illinois.cs.benchmark.BenchmarkGenerator;
import edu.illinois.cs.eval.Data;
import edu.illinois.cs.eval.Evaluator;

/**
 * Created by gourav on 4/24/15.
 */
public class Main {

  public static void main(String[] args) {

    //0. Set seed
    //BenchmarkGenerator.seed = System.currentTimeMillis();
    BenchmarkGenerator.seed = 1429914670752L;
    System.out.println("Seed: " + BenchmarkGenerator.seed + "\n");

    //1. Create benchmark
    BenchmarkGenerator benchmarkGenerator = new BenchmarkGenerator(BenchmarkGenerator.path);
    benchmarkGenerator.generate();


    //2. Run Motif Finder and log running times
    long defTime = MotifFinder.runAllWithPerms(BenchmarkGenerator.path + "/default/");
    //long defTime2 = MotifFinder.runAll(BenchmarkGenerator.path + "/default/");
    //System.out.println("Default Times [1,2]: " + defTime + ", " + defTime2);

    long ml1Time = MotifFinder.runAllWithPerms(BenchmarkGenerator.path + "/ML1/");
    long ml2Time = MotifFinder.runAllWithPerms(BenchmarkGenerator.path + "/ML2/");
    System.out.println("ML Time [6,7,8]: " + ml1Time + ", " + ml2Time + ", " + defTime);

    long nm1Time = MotifFinder.runAllWithPerms(BenchmarkGenerator.path + "/NM1/");
    long nm2Time = MotifFinder.runAllWithPerms(BenchmarkGenerator.path + "/NM2/");
    System.out.println("NM Time [0,1,2]: " + nm1Time + ", " + defTime + ", " + nm2Time);

    long sc1Time = MotifFinder.runAllWithPerms(BenchmarkGenerator.path + "/SC1/");
    long sc2Time = MotifFinder.runAllWithPerms(BenchmarkGenerator.path + "/SC2/");
    System.out.println("SC Time [5,10,20]: " + sc1Time + ", " + defTime + ", " + sc2Time);


    System.out.println();


    //3. Evaluate Results
    Data def = Evaluator.runAll(BenchmarkGenerator.path + "/default/");

    Data ml1 = Evaluator.runAll(BenchmarkGenerator.path + "/ML1/");
    Data ml2 = Evaluator.runAll(BenchmarkGenerator.path + "/ML2/");
    System.out.println("ML [6,7,8]: " + ml1 + ", " + ml2 + ", " + def);

    Data nm1 = Evaluator.runAll(BenchmarkGenerator.path + "/NM1/");
    Data nm2 = Evaluator.runAll(BenchmarkGenerator.path + "/NM2/");
    System.out.println("NM [0,1,2]: " + nm1 + ", " + def + ", " + nm2);

    Data sc1 = Evaluator.runAll(BenchmarkGenerator.path + "/SC1/");
    Data sc2 = Evaluator.runAll(BenchmarkGenerator.path + "/SC2/");
    System.out.println("SC [5,10,20]: " + sc1 + ", " + def + ", " + sc2);
  }


}
