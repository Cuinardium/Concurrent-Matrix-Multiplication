package ar.edu.itba.pp;

import ar.edu.itba.pp.matrix.ForkJoinMultiplicator;
import ar.edu.itba.pp.matrix.MatrixGenerator;
import ar.edu.itba.pp.matrix.MatrixMultiplicator;
import ar.edu.itba.pp.matrix.ParallelMultiplicator;
import ar.edu.itba.pp.matrix.SequentialMultiplicator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class App {

    public static void main(String[] args) {

        int n = 1024;
        int threshold = 64;
        int seed = 6834723;
        int maxThreads = Runtime.getRuntime().availableProcessors();
        int iterations = 3;

        MatrixGenerator generator = new MatrixGenerator(n, seed);
        double[][] a = generator.generate();
        double[][] b = generator.generate();

        StringBuilder results = new StringBuilder();
        results.append("Threads,ForkJoin,Parallel,Sequential\n");

        for (int i = 0; i < iterations; i++) {

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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("results.csv"))) {
            writer.write(results.toString());
            System.out.println("Results written to results.csv");
        } catch (IOException e) {
            System.out.println("Error writing results to file: " + e.getMessage());
        }
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
}
