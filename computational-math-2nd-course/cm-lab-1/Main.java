package vychmat;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("Please choose an option to input: ");
            System.out.println("1. From Keyboard");
            System.out.println("2. From file");
            System.out.println("3. Exit\n");
            System.out.println("If you choose an option 2, in your file you need to have in first line: ");
            System.out.println("Size_of_matrix accuracy");
            int option = 0;
            try {
                option = sc.nextInt();
            } catch (Exception e) {
                System.out.println("You entered not a number, please try again");
                sc.next();
                continue;
            }

            Matrix data = null;

            switch (option) {
                case 1: data = readFromKeyboard(sc); break;
                case 2: data = readFromFile(sc); break;
                case 3: System.exit(0);
                default: {
                    System.out.println("Invalid option. Please try again.");
                }
            }
            if (data == null) continue;

            MathSolver.solve(data);
        }
    }

    private static Matrix readFromKeyboard(Scanner scanner) {
        Matrix res = new Matrix();
        int n = readInt(scanner, "Enter the size of matrix (1-20): ", 1, 20);

        double epsilon = readDoubleRange(scanner, "Enter accuracy (0 < eps < 1): ", 1e-15, 0.999);
        res.setEpsilon(epsilon);

        double[][] matrix = new double[n][n];
        double[] b_vector = new double[n];

        System.out.println("Write the lines of the matrix (A1 A2 ... An B):");
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            while (true) {
                System.out.printf("Line %d (need to %d numbers): ", i + 1, n + 1);
                String line = scanner.nextLine().trim().replace(",", ".");
                String[] parts = line.split("\\s+");

                if (parts.length != n + 1) {
                    System.out.printf("You wrote %d numbers, but you need %d. Try again.%n", parts.length, n + 1);
                    continue;
                }

                try {
                    for (int j = 0; j < n; j++) matrix[i][j] = Double.parseDouble(parts[j]);
                    b_vector[i] = Double.parseDouble(parts[n]);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("In the line must be only numbers.");
                }
            }
        }

        if (!MathSolver.checkDiagonal(matrix, b_vector)) {
            System.out.println("Diagonal dominance is impossible. Try again");
            return null;
        }

        res.setMatrix(matrix);
        res.setB_vector(b_vector);
        return res;
    }

    private static Matrix readFromFile(Scanner scanner) {
        return new Matrix(); // TODO
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.next().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Enter a number (fractional numbers through a point).");
            }
        }
    }

    private static int readInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(sc.next());
                if (val >= min && val <= max) return val;
                System.out.printf("Number must be from %d to %d\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Enter integer.");
            }
        }
    }

    private static double readDoubleRange(Scanner sc, String prompt, double min, double max) {
        while (true) {
            double val = readDouble(sc, prompt);
            if (val >= min && val <= max) return val;
            System.out.printf("Number must be from %f to %f\n", min, max);
        }
    }
}
