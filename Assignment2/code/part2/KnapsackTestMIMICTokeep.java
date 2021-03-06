package opt.test;

/** only change the sample parameter of MIMIC algorithm;*/
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.*;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.UniformCrossOver;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * A test of the knap sack problem
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class KnapsackTestMIMICTokeep {
    /** Random number generator */
    private static final Random random = new Random();
    /** The number of items */
    private static final int NUM_ITEMS = 40;
    /** The number of copies each */
    private static final int COPIES_EACH = 4;
    /** The maximum weight for a single element */
    private static final double MAX_WEIGHT = 50;
    /** The maximum volume for a single element */
    private static final double MAX_VOLUME = 50;
    /** The volume of the knapsack */
    private static final double KNAPSACK_VOLUME = 
         MAX_VOLUME * NUM_ITEMS * COPIES_EACH * .4;
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) throws IOException {
        int[] copies = new int[NUM_ITEMS];
        Arrays.fill(copies, COPIES_EACH);
        double[] weights = new double[NUM_ITEMS];
        double[] volumes = new double[NUM_ITEMS];
        for (int i = 0; i < NUM_ITEMS; i++) {
            weights[i] = random.nextDouble() * MAX_WEIGHT;
            volumes[i] = random.nextDouble() * MAX_VOLUME;
        }
        int[] ranges = new int[NUM_ITEMS];
        Arrays.fill(ranges, COPIES_EACH + 1);
        EvaluationFunction ef = new KnapsackEvaluationFunction(weights, volumes, KNAPSACK_VOLUME, copies);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new UniformCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        // need to change
        
        int numIterations = 20;
        String outputDir = "/Users/craiglab/Documents/ml/Assignment2/";
        String winner = "";
        double mimicBest = Integer.MIN_VALUE;
        double GABest = Integer.MIN_VALUE;
        double SABest = Integer.MIN_VALUE;
        double RHCBest = Integer.MIN_VALUE;
        double mimicWinCounter = 0;
        double GAWinCounter = 0;
        double SAWinCounter = 0;
        double RHCWinCounter = 0;
        double[] mimicTrialBests = new double[numIterations];
        double[] GATrialBests = new double[numIterations];
        double[] SATrialBests = new double[numIterations];
        double[] RHCTrialBests = new double[numIterations];

        double[] mimicTrainingTimes = new double[numIterations];
        double[] GATrainingTimes = new double[numIterations];
        double[] SATrainingTimes = new double[numIterations];
        double[] RHCTrainingTimes = new double[numIterations];
        int[] tokeep = new int[numIterations];
        tokeep[0] = 10;
        for (int i = 1; i < numIterations; i ++){
        	tokeep[i] = tokeep[i-1] + 10;
        	
        }

        for (int i=0; i < numIterations; i++) {
        	int keep = tokeep[i] ; 
        	
        	

            System.out.println("~~~~~~~~~~~~~~~RUN " + i + "~~~~~~~~~~~~~~~");
            
            
            
            MIMIC mimic = new MIMIC(250, keep, pop);
            FixedIterationTrainer fit = new FixedIterationTrainer(mimic, 1000);
            double start = System.nanoTime(), end;
            fit.train();
            end = System.nanoTime();
            mimicTrainingTimes[i] = (end - start)/Math.pow(10,9);
            mimicTrialBests[i] = ef.value(mimic.getOptimal());
            if(ef.value(mimic.getOptimal()) > mimicBest){
                mimicBest = ef.value(mimic.getOptimal());
            }
            System.out.println("TOKEEP: " + tokeep[i]);
            System.out.println("MIMIC BEST: " + ef.value(mimic.getOptimal()));
            System.out.println("MIMIC TRAINING TIME: " + mimicTrainingTimes[i]);




        }
        
        System.out.println();
        double avgRHCScore = 0;
        double avgSAScore = 0;
        double avgGAScore = 0;
        double avgMimicScore = 0;

        for(int i = 0; i < numIterations; i++){
            //avgRHCScore += RHCTrialBests [i];
            //avgSAScore += SATrialBests[i];
            //avgGAScore += GATrialBests[i];
            avgMimicScore += mimicTrialBests[i];
        }
        //avgRHCScore = avgRHCScore / numIterations;
        //avgSAScore = avgSAScore / numIterations;
        //avgGAScore = avgGAScore / numIterations;
        avgMimicScore = avgMimicScore / numIterations;
        //System.out.println("RHC BEST RUN: " + RHCBest);
        //System.out.println("RHC AVG SCORE: " + avgRHCScore);
        //System.out.println();
        //System.out.println("SA BEST RUN: " + SABest);
        //System.out.println("SA AVG SCORE: " + avgSAScore);
        //System.out.println();
        //System.out.println("GA BEST RUN: " + GABest);
        //System.out.println("GA AVG SCORE: " + avgGAScore);
        System.out.println();
        System.out.println("MIMIC BEST RUN: " + mimicBest);
        System.out.println("MIMIC AVG SCORE: " + avgMimicScore);

        //double avgRHCTrainingTimes = 0;
        //double avgSATrainingTimes = 0;
        //double avgGATrainingTimes = 0;
        double avgMimicTrainingTimes = 0;

        for(int i = 0; i < numIterations; i++){
//            avgGATrainingTimes += GATrainingTimes[i];
            //avgRHCTrainingTimes += SATrainingTimes[i];
            //avgSATrainingTimes += RHCTrainingTimes[i];
            avgMimicTrainingTimes += mimicTrainingTimes[i];
        }
//        avgGATrainingTimes = avgGATrainingTimes / numIterations;
        //avgRHCTrainingTimes = avgRHCTrainingTimes / numIterations;
        //avgSATrainingTimes = avgSATrainingTimes / numIterations;
        avgMimicTrainingTimes = avgMimicTrainingTimes / numIterations;
        System.out.println();
        //System.out.println("RHC AVERAGE TRAINING TIME: " + avgRHCTrainingTimes );
        //System.out.println("SA AVERAGE TRAINING TIME: " + avgSATrainingTimes );
//        System.out.println("GA AVERAGE TRAINING TIME: " + avgGATrainingTimes );
        System.out.println("Mimic AVERAGE TRAINING TIME: " + avgMimicTrainingTimes );

        //output
       File file = new File(outputDir + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "_Results.csv");
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        PrintWriter pwtr = new PrintWriter(new BufferedWriter(writer));
        //pwtr.println("RHC SCORE, RHC TRAINING TIME, SA SCORE, SA TRAINING TIME, GA SCORE, GA TRAINING TIME, MIMIC SCORE, MIMIC TRAINING TIME");
        pwtr.println("TOKEEP, MIMIC SCORE, MIMIC TRAINING TIME");
        
        for(int i = 0; i < numIterations; i++) {
            //pwtr.println(RHCTrialBests[i] + "," + RHCTrainingTimes[i] + "," + SATrialBests[i] + "," + SATrainingTimes[i] + "," + GATrialBests[i] + "," + GATrainingTimes[i] + "," + mimicTrialBests[i] + "," + mimicTrainingTimes[i]);
        	pwtr.println(tokeep[i] + "," +  mimicTrialBests[i] + "," + mimicTrainingTimes[i]);
        	 
        }
        pwtr.println();
        //pwtr.println("FINAL RESULTS");
        //pwtr.println("RHC BEST SCORE, RHC AVG SCORE, RHC AVG TIME, RHC WIN PERCENTAGE, SA BEST SCORE, SA AVG SCORE, SA AVG TIME,  SA WIN PERCENTAGE, GA BEST SCORE, GA AVG SCORE, GA AVG TIME, GA WIN PERCENTAGE, MIMIC BEST SCORE, MIMIC AVG SCORE, MIMIC AVG TIME, MIMIC WIN PERCENTAGE");
        //pwtr.println(RHCBest + "," + avgRHCScore + "," + avgRHCTrainingTimes + "," + 100*(RHCWinCounter/numIterations) + "," + SABest + "," + avgSAScore + "," + avgSATrainingTimes + "," + 100*(SAWinCounter/numIterations) + "," + GABest + "," + avgGAScore + "," + avgGATrainingTimes + "," + 100*(GAWinCounter/numIterations) + "," + mimicBest + "," + avgMimicScore + "," + avgMimicTrainingTimes + "," + 100*(mimicWinCounter/numIterations));
        //pwtr.println("WINNER: " + "," + winner);
        pwtr.close();

    }
  }