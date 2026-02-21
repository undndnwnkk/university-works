package vychmat;

import java.util.Arrays;

public class MathSolver {
    public static boolean checkDiagonal(double[][] matrix, double[] b_vector) {
        for (int i = 0; i < matrix.length; i++) {
            double rowSumOfAbs = Arrays.stream(matrix[i]).map(Math::abs).sum();

            if (Math.abs(matrix[i][i]) < rowSumOfAbs - Math.abs(matrix[i][i])) {
                return tryToFixDiagonal(matrix, b_vector);
            }
        }
        return true;
    }

    public static boolean tryToFixDiagonal(double[][] matrix, double[] b_vector) {
        int n = matrix.length;
        int[] order = new int[n];
        Arrays.fill(order, -1);

        for (int i = 0; i < n; i++) {
            double rowSumOfAbs = Arrays.stream(matrix[i]).map(Math::abs).sum();

            for (int j = 0; j < n; j++) {
                if (Math.abs(matrix[i][j]) >= rowSumOfAbs - Math.abs(matrix[i][j])) {
                    if (order[j] == -1) {
                        order[j] = i;
                        break;
                    }
                }
            }
        }

        for (int val : order) if (val == -1) return false;

        reorder(matrix, b_vector, order);
        return true;
    }

    private static void reorder(double[][] matrix, double[] b, int[] order) {
        double[][] tempM = new double[matrix.length][];
        double[] tempB = new double[b.length];

        for (int j = 0; j < order.length; j++) {
            tempM[j] = matrix[order[j]];
            tempB[j] = b[order[j]];
        }

        System.arraycopy(tempM, 0, matrix, 0, matrix.length);
        System.arraycopy(tempB, 0, b, 0, b.length);
    }
}