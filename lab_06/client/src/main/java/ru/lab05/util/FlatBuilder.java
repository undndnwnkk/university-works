package ru.lab05.util;

import ru.lab05.model.*;

import java.util.Scanner;

public class FlatBuilder {
    public static Flat create(Scanner scanner) {
        Flat flat = new Flat();

        System.out.print("Enter flat name (non-empty): ");
        flat.setName(scanner.nextLine().trim());

        Coordinates coordinates = new Coordinates();

        while (true) {
            System.out.print("Enter coordinate X (float): ");
            try {
                coordinates.setX(Float.parseFloat(scanner.nextLine().trim()));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid float. Try again.");
            }
        }

        while (true) {
            System.out.print("Enter coordinate Y (must be > -46): ");
            try {
                float y = Float.parseFloat(scanner.nextLine().trim());
                if (y > -46) {
                    coordinates.setY(y);
                    break;
                } else {
                    System.out.println("Y must be > -46.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid float. Try again.");
            }
        }

        flat.setCoordinates(coordinates);

        while (true) {
            System.out.print("Enter area (int > 0): ");
            try {
                int area = Integer.parseInt(scanner.nextLine().trim());
                if (area > 0) {
                    flat.setArea(area);
                    break;
                } else {
                    System.out.println("Area must be > 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid int. Try again.");
            }
        }

        while (true) {
            System.out.print("Enter number of rooms (long > 0): ");
            try {
                long rooms = Long.parseLong(scanner.nextLine().trim());
                if (rooms > 0) {
                    flat.setNumberOfRooms(rooms);
                    break;
                } else {
                    System.out.println("Must be > 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid long. Try again.");
            }
        }

        while (true) {
            System.out.println("Furnish options: FINE, BAD, LITTLE");
            System.out.print("Enter furnish: ");
            try {
                flat.setFurnish(Furnish.valueOf(scanner.nextLine().trim().toUpperCase()));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid furnish. Try again.");
            }
        }

        while (true) {
            System.out.println("View options: STREET, YARD, PARK, TERRIBLE");
            System.out.print("Enter view: ");
            try {
                flat.setView(View.valueOf(scanner.nextLine().trim().toUpperCase()));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid view. Try again.");
            }
        }

        while (true) {
            System.out.println("Transport options: FEW, NONE, LITTLE, NORMAL, ENOUGH");
            System.out.print("Enter transport (or leave empty): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty() || input.equals("NULL")) {
                flat.setTransport(null);
                break;
            }
            try {
                flat.setTransport(Transport.valueOf(input));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid transport. Try again.");
            }
        }

        System.out.print("Enter house data? (yes/no): ");
        String houseChoice = scanner.nextLine().trim().toLowerCase();

        if (houseChoice.equals("yes") || houseChoice.equals("y")) {
            House house = new House();

            System.out.print("Enter house name (or leave empty): ");
            String houseName = scanner.nextLine().trim();
            house.setName(houseName.isEmpty() ? null : houseName);

            while (true) {
                System.out.print("Enter house year (int > 0, <= 822): ");
                try {
                    int year = Integer.parseInt(scanner.nextLine().trim());
                    if (year > 0 && year <= 822) {
                        house.setYear(year);
                        break;
                    } else {
                        System.out.println("Year must be in range (0, 822].");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid int. Try again.");
                }
            }

            while (true) {
                System.out.print("Enter flats on floor (long > 0): ");
                try {
                    long flats = Long.parseLong(scanner.nextLine().trim());
                    if (flats > 0) {
                        house.setNumberOfFlatsOnFloor(flats);
                        break;
                    } else {
                        System.out.println("Must be > 0.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid long. Try again.");
                }
            }

            flat.setHouse(house);
        } else {
            flat.setHouse(null);
        }

        return flat;
    }
}
