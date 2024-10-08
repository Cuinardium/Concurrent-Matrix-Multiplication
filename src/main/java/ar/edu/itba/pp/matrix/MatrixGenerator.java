package ar.edu.itba.pp.matrix;

import java.util.Random;

public class MatrixGenerator {

    private final Random random;
    private final int size;

    public MatrixGenerator(int size, long seed) {
        this.size = size;
        this.random = new Random(seed);
    }


    public double[][] generate() {
        double[][] resp = new double[size][size];

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                resp[i][j] = random.nextDouble();
            }
        }

        return resp;
    }

    public double[][] generateZero() {
        double[][] resp = new double[size][size];

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                resp[i][j] = 0;
            }
        }

        return resp;
    }
}
