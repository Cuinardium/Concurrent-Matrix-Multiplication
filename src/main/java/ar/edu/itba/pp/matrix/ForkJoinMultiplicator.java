package ar.edu.itba.pp.matrix;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class ForkJoinMultiplicator implements MatrixMultiplicator {

    private final ForkJoinPool pool;
    private final int threshold;

    public ForkJoinMultiplicator(int threads, int threshold) {
        this.pool = new ForkJoinPool(threads);
        this.threshold = threshold;
    }

    @Override
    public void multiply(double[][] a, double[][] b, double[][] c) {
        // Asumo matrices cuadradas de mismo tamano y que c esta inicializada en 0
        int n = a.length;

        MultiplyTask task = new MultiplyTask(a, b, c, 0, n, n, threshold);
        pool.invoke(task);

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



    static class MultiplyTask extends RecursiveAction {
        private final double[][] a, b, c;
        private final int startRow, endRow, size, threshold;

        public MultiplyTask(double[][] a, double[][] b, double[][] c, int startRow, int endRow, int size, int threshold) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.startRow = startRow;
            this.endRow = endRow;
            this.size = size;
            this.threshold = threshold;
        }

        @Override
        protected void compute() {
            if (endRow - startRow <= threshold) {
                multiplyDirectly();
            } else {
                int midRow = (startRow + endRow) / 2;

                MultiplyTask upperTask = new MultiplyTask(a, b, c, startRow, midRow, size, threshold);
                MultiplyTask lowerTask = new MultiplyTask(a, b, c, midRow, endRow, size, threshold);

                upperTask.fork();
                lowerTask.compute();
                upperTask.join();
            }
        }

        private void multiplyDirectly() {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < size; j++) {
                    double sum = 0;
                    for (int k = 0; k < size; k++) {
                        sum += a[i][k] * b[k][j];
                    }
                    c[i][j] = sum;
                }
            }
        }
    }
}
