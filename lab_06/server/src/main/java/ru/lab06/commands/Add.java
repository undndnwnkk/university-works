package ru.lab06.commands;

import ru.lab06.model.*;

import ru.lab06.core.Storage;
import java.util.Date;
import java.util.Scanner;

/**
 * The Add class handles adding new Flat objects to the storage.
 */
public class Add {
    private final Storage storage;

    /**
     * Constructs an Add command and reads user input to create a new Flat.
     * @param storage the storage to which the flat is added
     */
    public Add(Storage storage) {
        this.storage = storage;
        Flat newFlat = readFlatFromConsole();
        if (newFlat != null) {
            addFlat(newFlat);
        }
    }

    /**
     * Constructs an Add command with a predefined Flat object.
     * @param storage the storage to which the flat is added
     * @param flat the flat to be added
     */
    public Add(Storage storage, Flat flat) {
        this.storage = storage;
        addFlat(flat);
    }

    /**
     * Reads flat attributes from user input via the console.
     * @return a new Flat object with the provided attributes
     */
    private Flat readFlatFromConsole() {
        Scanner scanner = new Scanner(System.in);
        Flat flat = new Flat();

        System.out.print("Enter flat name (non-empty): ");
        flat.setName(scanner.nextLine().trim());

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
        flat.setCoordinates(coordinates);

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
        flat.setArea(area);

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
        flat.setNumberOfRooms(numberOfRooms);

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
        flat.setFurnish(furnish);

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
        flat.setView(view);

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
        flat.setTransport(transport);

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
            flat.setHouse(house);
        } else {
            flat.setHouse(null);
        }

        return flat;
    }

    /**
     * Adds a flat to the storage and assigns an ID.
     * @param flat the flat to be added
     */
    private void addFlat(Flat flat) {
        flat.setId(storage.getCurrentId());
        storage.setCurrentId(storage.getCurrentId() + 1);
        flat.setCreationDate(new Date());
        storage.getFlatStorage().add(flat);
        System.out.println("Flat added successfully!");
    }
}
