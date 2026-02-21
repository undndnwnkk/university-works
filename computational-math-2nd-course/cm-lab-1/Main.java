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

            switch (option) {
                case 1: readFromKeyboard(sc); break;
                case 2: readFromFile(sc); break;
                case 3: System.exit(0);
                default: {
                    System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }

    private static Object[] readFromKeyboard(Scanner scanner) {
        Object[] res =  new Object[3];

        int n = readInt(scanner, "Введите размерность матрицы (1-20): ", 1, 20);

        double epsilon = readDouble(scanner, "Введите точность (например, 0.001): ");

//        res["epsilon"] = epsilon; TODO
        double[][] matrix = new double[n][n];
        double[] b_vector = new double[n];

        System.out.println("Введите строки матрицы (коэффициенты A и затем B):");
        for (int i = 0; i < n; i++) {
            System.out.printf("Строка %d: ", i + 1);
            for (int j = 0; j < n; j++) {
                matrix[i][j] = readDouble(scanner, "");
            }
            b_vector[i] = readDouble(scanner, "");
        }

        System.out.println("Данные успешно считаны!");

        if (!MathSolver.checkDiagonal(matrix, b_vector)) {
            System.out.println("Диагональное преобладание невозможно. Начинаем все сначала");
        }
        return null; // TODO
    }

    private static void readFromFile(Scanner scanner) {
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.next().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введите число (дробные через точку).");
            }
        }
    }

    private static int readInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(sc.next());
                if (val >= min && val <= max) return val;
                System.out.printf("Число должно быть в диапазоне от %d до %d\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка! Введите целое число.");
            }
        }
    }
}
