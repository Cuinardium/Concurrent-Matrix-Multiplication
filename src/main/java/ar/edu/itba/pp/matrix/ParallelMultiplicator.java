package ar.edu.itba.pp.matrix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelMultiplicator implements MatrixMultiplicator {

    private final ExecutorService pool;

    public ParallelMultiplicator(int threads) {
        this.pool = Executors.newFixedThreadPool(threads);
    }

    @Override
    public void multiply(double[][] a, double[][] b, double[][] c) {
        // Asumo matrices cuadradas de mismo tamano y que c esta inicializada en 0
        int n = a.length;

        for (int i = 0; i < n; i++) {
            final int finalI = i;
            pool.execute(() -> {
                for (int j = 0; j < n; j++) {
                    double sum = 0;
                    for (int k = 0; k < n; k++) {
                        sum += a[finalI][k] * b[k][j];
                    }
                    c[finalI][j] = sum;
                }
            });
        }

        // Shutdown bien hecho
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }
    }
}
