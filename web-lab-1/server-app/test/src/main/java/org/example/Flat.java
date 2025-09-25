package ru.lab06.util;

import ru.lab06.model.*;

import java.util.Scanner;

public class FlatBuilder {
    public static Object[] build() {
        Scanner scanner = new Scanner(System.in);
        Object[] result = new Object[8];

        System.out.print("Enter flat name (non-empty): ");
        result[0] = scanner.nextLine().trim();

        Coordinates coordinates = new Coordinates();
        float x;
        while (true) {
            System.out.print("Enter coordinate X (float): ");
            try {
                x = Float.parseFloat(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid value for X. Please try again.");
            }
        }

        float y;
        while (true) {
            System.out.print("Enter coordinate Y (must be greater than -46): ");
            try {
                y = Float.parseFloat(scanner.nextLine().trim());
                if (y > -46) break;
                else System.out.println("Y must be greater than -46.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid value for Y. Please try again.");
            }
        }
        coordinates.setX(x);
        coordinates.setY(y);
        result[1] = coordinates;

        int area;
        while (true) {
            System.out.print("Enter flat area (integer > 0): ");
            try {
                area = Integer.parseInt(scanner.nextLine().trim());
                if (area > 0) break;
                else System.out.println("Area must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
        result[2] = area;

        long numberOfRooms;
        while (true) {
            System.out.print("Enter number of rooms (integer > 0): ");
            try {
                numberOfRooms = Long.parseLong(scanner.nextLine().trim());
                if (numberOfRooms > 0) break;
                else System.out.println("Number of rooms must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
        result[3] = numberOfRooms;

        Furnish furnish = null;
        while (true) {
            System.out.println("Available options for furnish: FINE, BAD, LITTLE");
            System.out.print("Enter furnish type: ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                furnish = Furnish.valueOf(input);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid value. Please try again.");
            }
        }

        result[4] = furnish;

        View view = null;
        while (true) {
            System.out.println("Available options for view: STREET, YARD, PARK, TERRIBLE");
            System.out.print("Enter view type: ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                view = View.valueOf(input);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid value. Please try again.");
            }
        }
        result[5] = view;

        Transport transport = null;
        while (true) {
            System.out.println("Available options for transport: FEW, NONE, LITTLE, NORMAL, ENOUGH");
            System.out.print("Enter transport type (or leave empty for null): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty() || input.equalsIgnoreCase("null")) break;
            try {
                transport = Transport.valueOf(input);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid value. Please try again.");
            }
        }
        result[6] = transport;

        System.out.print("Do you want to enter house data? (yes/no/null):