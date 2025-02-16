package commands;

import collections.Flat;
import collections.Coordinates;
import collections.House;
import collections.Furnish;
import collections.View;
import collections.Transport;
import storage.Storage;
import java.util.Scanner;
import java.util.Vector;

public class Update {
    private final Storage storage;

    public Update(Storage storage, String[] arguments) {
        this.storage = storage;

        if (arguments.length < 1) {
            System.out.println("Error! Usage: update id");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(arguments[0]);
        } catch (NumberFormatException e) {
            System.out.println("Error! ID must be an integer.");
            return;
        }

        Flat updatedFlat = readFlatFromConsole();
        if(updatedFlat == null) {
            return;
        }
        updateFlat(id, updatedFlat);
    }

    public Update(Storage storage, int id, Flat updatedFlat) {
        this.storage = storage;
        updateFlat(id, updatedFlat);
    }

    private Flat readFlatFromConsole() {
        Scanner scanner = new Scanner(System.in);
        Flat flat = new Flat();

        String name;
        while(true) {
            System.out.print("Enter new flat name (non-empty): ");
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
            System.out.print("Enter new coordinate X (float): ");
            try {
                x = Float.parseFloat(scanner.nextLine().trim());
                break;
            } catch(NumberFormatException e) {
                System.out.println("Invalid value for X. Please try again.");
            }
        }
        float y;
        while(true) {
            System.out.print("Enter new coordinate Y (must be greater than -46): ");
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
            System.out.print("Enter new flat area (integer > 0): ");
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
            System.out.print("Enter new number of rooms (integer > 0): ");
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
            System.out.print("Enter new furnish type: ");
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
            System.out.print("Enter new view: ");
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
            System.out.print("Enter new transport (or leave empty for null): ");
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

    private void updateFlat(int id, Flat updatedFlat) {
        Vector<Flat> flats = storage.getFlatStorage();
        for (int i = 0; i < flats.size(); i++) {
            if (flats.get(i).getId() == id) {
                updatedFlat.setId(id);
                updatedFlat.setCreationDate(flats.get(i).getCreationDate());
                flats.set(i, updatedFlat);
                System.out.println("Flat with ID " + id + " updated successfully.");
                return;
            }
        }
        System.out.println("Error! No flat found with ID " + id);
    }
}
