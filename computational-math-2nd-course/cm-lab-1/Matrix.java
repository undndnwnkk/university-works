package vychmat;

public class Matrix {
    private double epsilon;
    private double[][] matrix;
    private double[] b_vector;

    public Matrix() {
    }

    public Matrix(double epsilon, double[][] matrix, double[] b_vector) {
        this.epsilon = epsilon;
        this.matrix = matrix;
        this.b_vector = b_vector;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public double[] getB_vector() {
        return b_vector;
    }

    public void setB_vector(double[] b_vector) {
        this.b_vector = b_vector;
    }
}
