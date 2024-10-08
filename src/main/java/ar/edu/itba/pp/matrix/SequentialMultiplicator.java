package ar.edu.itba.pp.matrix;

public class SequentialMultiplicator implements MatrixMultiplicator {

    @Override
    public void multiply(double[][] a, double[][] b, double[][] c) {
        // Asumo matrices cuadradas de mismo tamano y que c esta inicializada en 0
        int n = a.length;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += a[i][k] * b[k][j];
                }
                c[i][j] = sum;
            }
        }
    }
}
