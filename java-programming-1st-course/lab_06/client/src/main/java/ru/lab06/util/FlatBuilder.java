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

        System.out.print("Do you want to enter house data? (yes/no/null): ");
        String houseChoice = scanner.nextLine().trim().toLowerCase();
        if (houseChoice.equals("yes") || houseChoice.equals("y")) {
            House house = new House();
            System.out.print("Enter house name (or leave empty for null): ");
            String houseName = scanner.nextLine().trim();
            if (houseName.isEmpty() || houseName.equals("null")) {
                houseName = null;
            }
            house.setName(houseName);

            int year;
            while (true) {
                System.out.print("Enter house year (integer > 0 and <= 822): ");
                try {
                    year = Integer.parseInt(scanner.nextLine().trim());
                    if (year > 0 && year <= 822) {
                        break;
                    } else {
                        System.out.println("Year must be in the range (0, 822].");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please try again.");
                }
            }
            house.setYear(year);

            long flatsOnFloor;
            while (true) {
                System.out.print("Enter number of flats on floor (integer > 0): ");
                try {
                    flatsOnFloor = Long.parseLong(scanner.nextLine().trim());
                    if (flatsOnFloor > 0) {
                        break;
                    } else {
                        System.out.println("Number of flats must be greater than 0.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please try again.");
                }
            }
            house.setNumberOfFlatsOnFloor(flatsOnFloor);
            result[7] = house;
        } else {
            result[7] = null;
        }
        return result;
    }

    public static Object[] updateBuild(int id) {
        Scanner scanner = new Scanner(System.in);
        Object[] result = new Object[9];
        result[0] = id;

        System.out.print("Enter flat name (non-empty): ");
        result[1] = scanner.nextLine().trim();

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
        result[2] = coordinates;

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
        result[3] = area;

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
        result[4] = numberOfRooms;

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

        result[5] = furnish;

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
        result[6] = view;

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
        result[7] = transport;

        System.out.print("Do you want to enter house data? (yes/no/null): ");
        String houseChoice = scanner.nextLine().trim().toLowerCase();
        if (houseChoice.equals("yes") || houseChoice.equals("y")) {
            House house = new House();
            System.out.print("Enter house name (or leave empty for null): ");
            String houseName = scanner.nextLine().trim();
            if (houseName.isEmpty() || houseName.equals("null")) {
                houseName = null;
            }
            house.setName(houseName);

            int year;
            while (true) {
                System.out.print("Enter house year (integer > 0 and <= 822): ");
                try {
                    year = Integer.parseInt(scanner.nextLine().trim());
                    if (year > 0 && year <= 822) {
                        break;
                    } else {
                        System.out.println("Year must be in the range (0, 822].");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please try again.");
                }
            }
            house.setYear(year);

            long flatsOnFloor;
            while (true) {
                System.out.print("Enter number of flats on floor (integer > 0): ");
                try {
                    flatsOnFloor = Long.parseLong(scanner.nextLine().trim());
                    if (flatsOnFloor > 0) {
                        break;
                    } else {
                        System.out.println("Number of flats must be greater than 0.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please try again.");
                }
            }
            house.setNumberOfFlatsOnFloor(flatsOnFloor);
            result[8] = house;
        } else {
            result[8] = null;
        }
        return result;
    }
}
