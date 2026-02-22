package vychmat;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    public static void solve(Matrix matrix) {
        int n = matrix.getMatrix().length;
        double[][] matrixCopy = new double[n][n];
        double[] b_vectorCopy = new double[n];

        for (int i = 0; i < n; i++) {
            double forDivide = matrix.getMatrix()[i][i];
            b_vectorCopy[i] = matrix.getB_vector()[i] / forDivide;

            for (int j = 0; j < n; j++) {
                if (i == j) matrixCopy[i][j] = 0;
                else matrixCopy[i][j] = -matrix.getMatrix()[i][j] / forDivide;
            }
        }

        double norm = calculateNorm(matrixCopy);
        System.out.println("Norm: " + norm);
        if (norm > 1) System.out.println("Norm is more than 1, method can diverge");

        iterationMethod(matrixCopy, b_vectorCopy, norm, matrix.getEpsilon());
    }

    private static double calculateNorm(double[][] matrix) {
        double res = Double.MIN_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            double currentLineSum = Arrays.stream(matrix[i]).map(Math::abs).sum();
            if (res < currentLineSum) res = currentLineSum;
        }

        return res;
    }

    private static void iterationMethod(double[][] matrix, double[] vector, double norm, double epsilon) {
        int countOfIterations = 0;
        boolean isFound = false;
        double[] x = vector.clone();
        double[] x_new = new double[matrix.length];
        while (!isFound) {
            double maxDiff = 0;

            for (int i = 0; i < matrix.length; i++) {
                double lineSum = vector[i];
                for (int j = 0; j < matrix.length; j++) {
                    lineSum += matrix[i][j] * x[j];
                }
                x_new[i] = lineSum;

                double diff = Math.abs(x_new[i] - x[i]);
                if (diff > maxDiff) maxDiff = diff;
            }

            x = x_new.clone();

            if (maxDiff < epsilon) {
                isFound = true;
            }
            String xFormatted = Arrays.stream(x)
                    .mapToObj(val -> String.format("%.4f", val))
                    .collect(Collectors.joining(", "));

            String out = countOfIterations + " iterations | " + xFormatted + " | max difference: " + maxDiff;
            System.out.println(out);

            if (countOfIterations > 1000) {
                System.out.println("Count of iterations > 1000. Solution not found");
                break;
            }
            countOfIterations++;
        }
    }
}