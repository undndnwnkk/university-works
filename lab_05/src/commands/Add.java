package commands;

import collections.Flat;
import collections.Coordinates;
import collections.House;
import collections.Furnish;
import collections.View;
import collections.Transport;
import storage.Storage;
import java.util.Date;
import java.util.Scanner;

public class Add {
    private final Storage storage;

    public Add(Storage storage, String[] arguments) {
        this.storage = storage;
        Flat newFlat = readFlatFromConsole();
        if(newFlat != null) {
            processFlat(newFlat);
        }
    }

    public Add(Storage storage, Flat flat) {
        this.storage = storage;
        this.storage.setCurrentId(storage.getCurrentId() + 1);
        processFlat(flat);
    }

    private Flat readFlatFromConsole() {
        Scanner scanner = new Scanner(System.in);
        Flat flat = new Flat();

        String name;
        while(true) {
            System.out.print("Enter flat name (non-empty): ");
            name = scanner.nextLine().trim();
            if(!name.isEmpty()) {
                break;
            }
            System.out.println("Name cannot be empty.");
        }
        flat.setName(name);

        Coordinates coordinates = new Coordinates();
        float x;
        while(true) {
            System.out.print("Enter coordinate X (float): ");
            try {
                x = Float.parseFloat(scanner.nextLine().trim());
                break;
            } catch(NumberFormatException e) {
                System.out.println("Invalid value for X. Please try again.");
            }
        }
        float y;
        while(true) {
            System.out.print("Enter coordinate Y (must be greater than -46): ");
            try {
                y = Float.parseFloat(scanner.nextLine().trim());
                if(y > -46) {
                    break;
                } else {
                    System.out.println("Y must be greater than -46.");
                }
            } catch(NumberFormatException e) {
                System.out.println("Invalid value for Y. Please try again.");
            }
        }
        coordinates.setX(x);
        coordinates.setY(y);
        flat.setCoordinates(coordinates);

        int area;
        while(true) {
            System.out.print("Enter flat area (integer > 0): ");
            try {
                area = Integer.parseInt(scanner.nextLine().trim());
                if(area > 0) {
                    break;
                } else {
                    System.out.println("Area must be greater than 0.");
                }
            } catch(NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
        flat.setArea(area);

        long numberOfRooms;
        while(true) {
            System.out.print("Enter number of rooms (integer > 0): ");
            try {
                numberOfRooms = Long.parseLong(scanner.nextLine().trim());
                if(numberOfRooms > 0) {
                    break;
                } else {
                    System.out.println("Number of rooms must be greater than 0.");
                }
            } catch(NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
        flat.setNumberOfRooms(numberOfRooms);

        Furnish furnish = null;
        while(true) {
            System.out.println("Available options for furnish: FINE, BAD, LITTLE");
            System.out.print("Enter furnish type: ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                furnish = Furnish.valueOf(input);
                break;
            } catch(IllegalArgumentException e) {
                System.out.println("Invalid value. Please try again.");
            }
        }
        flat.setFurnish(furnish);

        View view = null;
        while(true) {
            System.out.println("Available options for view: STREET, YARD, PARK, TERRIBLE");
            System.out.print("Enter view: ");
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                view = View.valueOf(input);
                break;
            } catch(IllegalArgumentException e) {
                System.out.println("Invalid value. Please try again.");
            }
        }
        flat.setView(view);

        Transport transport = null;
        while(true) {
            System.out.println("Available options for transport: FEW, NONE, LITTLE, NORMAL, ENOUGH");
            System.out.print("Enter transport (or leave empty for null): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if(input.isEmpty()) {
                break;
            }
            try {
                transport = Transport.valueOf(input);
                break;
            } catch(IllegalArgumentException e) {
                System.out.println("Invalid value. Please try again.");
            }
        }
        flat.setTransport(transport);

        System.out.print("Do you want to enter house data? (yes/no): ");
        String houseChoice = scanner.nextLine().trim().toLowerCase();
        if(houseChoice.equals("yes") || houseChoice.equals("y")) {
            House house = new House();
            System.out.print("Enter house name (or leave empty for null): ");
            String houseName = scanner.nextLine().trim();
            if(houseName.isEmpty()) {
                houseName = null;
            }
            house.setName(houseName);

            int year;
            while(true) {
                System.out.print("Enter house year (integer > 0 and <= 822): ");
                try {
                    year = Integer.parseInt(scanner.nextLine().trim());
                    if(year > 0 && year <= 822) {
                        break;
                    } else {
                        System.out.println("Year must be in the range (0, 822].");
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Invalid number. Please try again.");
                }
            }
            house.setYear(year);

            long flatsOnFloor;
            while(true) {
                System.out.print("Enter number of flats on floor (integer > 0): ");
                try {
                    flatsOnFloor = Long.parseLong(scanner.nextLine().trim());
                    if(flatsOnFloor > 0) {
                        break;
                    } else {
                        System.out.println("Number of flats must be greater than 0.");
                    }
                } catch(NumberFormatException e) {
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

    private void processFlat(Flat flat) {
        if (flat == null || flat.getName() == null || flat.getName().isEmpty() ||
                flat.getCoordinates() == null || flat.getArea() == null || flat.getArea() <= 0 ||
                flat.getNumberOfRooms() <= 0 || flat.getFurnish() == null || flat.getView() == null) {
            System.out.println("Error! Invalid flat data.");
            return;
        }

        flat.setId(storage.getCurrentId());
        storage.setCurrentId(storage.getCurrentId() + 1);

        flat.setCreationDate(new Date());
        storage.getFlatStorage().add(flat);
        System.out.println("Flat added successfully!");
    }

}
