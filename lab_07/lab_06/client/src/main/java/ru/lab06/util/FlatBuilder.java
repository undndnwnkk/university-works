package ru.lab06.util;

import ru.lab06.model.*;

import java.util.Date;
import java.util.Scanner;

public class FlatBuilder {

    private static final Scanner scanner = new Scanner(System.in);

    public static Flat build() {
        Flat flat = new Flat();

        System.out.print("Enter flat name (non-empty): ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("Name cannot be empty. Try again: ");
            name = scanner.nextLine().trim();
        }
        flat.setName(name);

        int x;
        float y;
        while (true) {
            try {
                System.out.print("Enter coordinate X (int): ");
                x = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter coordinate Y (float > -46): ");
                y = Float.parseFloat(scanner.nextLine());
                if (y <= -46) throw new IllegalArgumentException();
                break;
            } catch (Exception e) {
                System.out.println("Invalid coordinates. Try again.");
            }
        }
        flat.setCoordinates(new Coordinates(x, y));
        flat.setCreationDate(new Date());

        while (true) {
            try {
                System.out.print("Enter flat area (int > 0): ");
                int area = Integer.parseInt(scanner.nextLine());
                if (area <= 0) throw new IllegalArgumentException();
                flat.setArea(area);
                break;
            } catch (Exception e) {
                System.out.println("Invalid area. Try again.");
            }
        }

        while (true) {
            try {
                System.out.print("Enter number of rooms (long > 0): ");
                long rooms = Long.parseLong(scanner.nextLine());
                if (rooms <= 0) throw new IllegalArgumentException();
                flat.setNumberOfRooms(rooms);
                break;
            } catch (Exception e) {
                System.out.println("Invalid number. Try again.");
            }
        }

        flat.setFurnish(askEnum(Furnish.class, "furnish"));

        flat.setView(askEnum(View.class, "view"));

        flat.setTransport(askEnum(Transport.class, "transport"));

        System.out.print("Do you want to enter house info? (yes/no): ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (answer.equals("yes")) {
            House house = new House();

            System.out.print("Enter house name: ");
            house.setName(scanner.nextLine().trim());

            while (true) {
                try {
                    System.out.print("Enter house year (<= 822): ");
                    int year = Integer.parseInt(scanner.nextLine());
                    if (year <= 0 || year > 822) throw new IllegalArgumentException();
                    house.setYear(year);
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid year. Try again.");
                }
            }

            while (true) {
                try {
                    System.out.print("Enter flats on floor (long > 0): ");
                    long floors = Long.parseLong(scanner.nextLine());
                    if (floors <= 0) throw new IllegalArgumentException();
                    house.setNumberOfFloors(floors);
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid number. Try again.");
                }
            }

            flat.setHouse(house);
        } else if (answer.equals("no") || answer.isEmpty()) {
            flat.setHouse(null);
        } else {
            flat.setHouse(null);
        }

        return flat;
    }

    public static Flat updateBuild(int id) {
        Flat flat = build();
        flat.setId(id);
        return flat;
    }

    private static <T extends Enum<T>> T askEnum(Class<T> enumType, String label) {
        while (true) {
            try {
                System.out.printf("Enter %s (%s): ", label, String.join(", ",
                        EnumNames(enumType)));
                String input = scanner.nextLine().trim().toUpperCase();
                return Enum.valueOf(enumType, input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid value. Try again.");
            }
        }
    }

    private static <T extends Enum<T>> String[] EnumNames(Class<T> enumType) {
        T[] values = enumType.getEnumConstants();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }
}
