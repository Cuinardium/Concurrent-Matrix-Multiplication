package ar.edu.itba.pp;

import ar.edu.itba.pp.matrix.ForkJoinMultiplicator;
import ar.edu.itba.pp.matrix.MatrixGenerator;
import ar.edu.itba.pp.matrix.MatrixMultiplicator;
import ar.edu.itba.pp.matrix.ParallelMultiplicator;
import ar.edu.itba.pp.matrix.SequentialMultiplicator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class App {

    private static final int N = 1024;
    private static final int SEED = 6834723;
    private static final int ITERATIONS = 10;
    private static final String OUTPUT_DIR = "output/";

    public static void main(String[] args) {

        int maxThreads = Runtime.getRuntime().availableProcessors();

        MatrixGenerator generator = new MatrixGenerator(N);

        Map<Integer, Integer> bestTresholds = new HashMap<>();

        thresholdAnalysis(maxThreads, generator, bestTresholds);
        threadAnalysis(maxThreads, bestTresholds, generator);

        System.out.println("Done!");
    }

    private static long multiplyAndMeasure(
            MatrixMultiplicator multiplicator, double[][] a, double[][] b, double[][] c) {
        long start = System.currentTimeMillis();
        multiplicator.multiply(a, b, c);
        long end = System.currentTimeMillis();
        return end - start;
    }

    private static boolean equalResults(double[][] c1, double[][] c2, double[][] c3) {
        for (int i = 0; i < c1.length; i++) {
            for (int j = 0; j < c1.length; j++) {
                if (c1[i][j] != c2[i][j] || c1[i][j] != c3[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void writeResults(String results, String filename) {

        // Ensure output directory exists
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_DIR + filename))) {
            writer.write(results);
            System.out.println("Results written to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing results to file: " + e.getMessage());
        }
    }

    private static void thresholdAnalysis(
            int maxThreads,
            MatrixGenerator generator,
            Map<Integer, Integer> bestThresholds) {
        StringBuilder results = new StringBuilder();
        results.append("Threads,Threshold,Time\n");

        double[][] a = generator.generate();
        double[][] b = generator.generate();

        for (int threads = 1; threads <= maxThreads; threads++) {
            System.out.println("Analyzing performance for " + threads + " threads");

            int bestThreshold = 0;
            double bestTime = Double.MAX_VALUE;

            for (int threshold = 1; threshold <= N; threshold *= 2) {
                System.out.println(
                        "Testing threshold = " + threshold + " with " + threads + " threads");

                double totalTime = 0;

                for (int i = 0; i < ITERATIONS; i++) {
                    System.out.println("Iteration " + (i + 1));

                    double[][] c = generator.generateZero();
                    MatrixMultiplicator forkJoin = new ForkJoinMultiplicator(threads, threshold);

                    long forkJoinTime = multiplyAndMeasure(forkJoin, a, b, c);
                    totalTime += forkJoinTime;

                    results.append(String.format("%d,%d,%d\n", threads, threshold, forkJoinTime));
                }

                double averageTime = totalTime / ITERATIONS;
                if (averageTime < bestTime) {
                    bestTime = averageTime;
                    bestThreshold = threshold;
                }
            }

            bestThresholds.put(threads, bestThreshold);
            System.out.println("Best threshold for " + threads + " threads: " + bestThreshold);
        }

        // Escribo los resultados de la prueba de thresholds a un archivo CSV
        writeResults(results.toString(), "threshold_results.csv");
    }

    private static void threadAnalysis(
            int maxThreads,
            Map<Integer, Integer> bestTresholds,
            MatrixGenerator generator) {
        StringBuilder results = new StringBuilder();
        results.append("Threads,ForkJoin,Parallel,Sequential\n");

        double[][] a = generator.generate();
        double[][] b = generator.generate();

        for (int i = 0; i < ITERATIONS; i++) {

            double[][] cSequential = generator.generateZero();
            MatrixMultiplicator sequential = new SequentialMultiplicator();

            System.out.println("Iteration " + (i + 1));

            System.out.println("Running Sequential");
            long sequentialTime = multiplyAndMeasure(sequential, a, b, cSequential);
            results.append(String.format("1,,,%d\n", sequentialTime));

            // Itero por numero de threads
            for (int threads = 1; threads <= maxThreads; threads++) {
                double[][] cParallel = generator.generateZero();
                double[][] cForkJoin = generator.generateZero();

                MatrixMultiplicator parallel = new ParallelMultiplicator(threads);

                int threshold = bestTresholds.get(threads);
                MatrixMultiplicator forkJoin = new ForkJoinMultiplicator(threads, threshold);

                System.out.println("Running ForkJoin with " + threads + " threads");
                long forkJoinTime = multiplyAndMeasure(forkJoin, a, b, cForkJoin);
                System.out.println("Running Parallel with " + threads + " threads");
                long parallelTime = multiplyAndMeasure(parallel, a, b, cParallel);

                // Valido resultados
                if (!equalResults(cSequential, cParallel, cForkJoin)) {
                    System.err.println("Results are not equal!");
                    System.exit(1);
                }

                results.append(String.format("%d,%d,%d,\n", threads, forkJoinTime, parallelTime));
            }
        }

        writeResults(results.toString(), "thread_results.csv");
    }
}
