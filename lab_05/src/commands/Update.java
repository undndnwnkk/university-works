package commands;

import collections.Flat;
import collections.Coordinates;
import collections.House;
import collections.Furnish;
import collections.View;
import collections.Transport;
import storage.Storage;
import java.util.Scanner;

/**
 * The Update class updates the attributes of an existing flat in the storage.
 */
public class Update {
    /**
     * Storage contains our classes
     */
    public final Storage storage;

    /**
     * Constructs an {@code Update} object and starts the update process for the given flat ID.
     * @param storage The storage containing flats.
     * @param commandArguments The arguments provided with the command, where the first argument should be the flat ID.
     */
    public Update(Storage storage, String[] commandArguments) {
        this.storage = storage;

        if(commandArguments.length < 1) {
            System.out.println("You don't write an id");
        } else {
            int id = Integer.parseInt(commandArguments[0]);
            readFromConsole(id);
        }
    }

    /**
     * Reads new attributes from the console and updates the flat with the given ID.
     * @param id The ID of the flat to update.
     */
    private void readFromConsole(int id) {
        Flat updatingFlat = findFlatByid(id);
        Scanner scanner = new Scanner(System.in);

        if(updatingFlat != null) {
            // name response
            System.out.print("Set a new name: ");
            String currentInput = scanner.nextLine().trim();
            if(!currentInput.isEmpty()) {
                updatingFlat.setName(currentInput);
            }

            // coordinates response
            float x;
            float y;
            Coordinates newCoordinates = new Coordinates();
            System.out.print("Enter new X coordinate: ");
            currentInput = scanner.nextLine().trim();

            if(!currentInput.isEmpty()) {
                try {
                    x = Float.parseFloat(currentInput);
                    newCoordinates.setX(x);
                } catch (NumberFormatException e) {
                    System.out.println("You don't wrote a number!");
                }
            } else {
                newCoordinates.setX(updatingFlat.getCoordinates().getX());
            }

            System.out.print("Enter new Y coordinate: ");
            currentInput = scanner.nextLine().trim();
            if(!currentInput.isEmpty()) {
                try {
                    y = Float.parseFloat(currentInput);
                    newCoordinates.setY(y);
                } catch (NumberFormatException e) {
                    System.out.println("You don't wrote a number!");
                }
            } else {
                newCoordinates.setY(updatingFlat.getCoordinates().getY());
            }
            updatingFlat.setCoordinates(newCoordinates);

            // Area response
            System.out.print("Set a new area : ");
            while(true) {
                currentInput = scanner.nextLine().trim();

                if(!currentInput.isEmpty()) {
                    try {
                        if(Integer.parseInt(currentInput) > 0) {
                            updatingFlat.setArea(Integer.parseInt(currentInput));
                            break;
                        } else {
                            System.out.println("You wrote number, which less than 0. Try again!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("You don't wrote a number! Try again!");
                    }
                }
            }

            // Number of rooms response
            System.out.print("Set a new number of rooms: ");
            while(true) {
                currentInput = scanner.nextLine().trim();

                if(!currentInput.isEmpty()) {
                    try {
                        updatingFlat.setNumberOfRooms(Integer.parseInt(currentInput));
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Incorrect data! Try again!");
                    }
                }
            }

            // Furnish response
            System.out.print("Set type of furnish(You can choose: fine, bad, little): ");
            currentInput = scanner.nextLine().trim();

            if(!currentInput.isEmpty()) {
                currentInput = currentInput.toUpperCase();
                switch(currentInput) {
                    case "FINE" -> updatingFlat.setFurnish(Furnish.FINE);
                    case "BAD" -> updatingFlat.setFurnish(Furnish.BAD);
                    case "LITTLE" -> updatingFlat.setFurnish(Furnish.LITTLE);
                    default -> System.out.println("Sorry, we don't has this type.. :(");
                }
            }

            // View response
            System.out.print("Set a new view type (You can choose: street, yard, park, terrible): ");
            currentInput = scanner.nextLine().trim();

            if(!currentInput.isEmpty()) {
                currentInput = currentInput.toUpperCase();
                switch(currentInput) {
                    case "STREET" -> updatingFlat.setView(View.STREET);
                    case "YARD" -> updatingFlat.setView(View.YARD);
                    case "PARK" -> updatingFlat.setView(View.PARK);
                    case "TERRIBLE" -> updatingFlat.setView(View.TERRIBLE);
                    default -> System.out.println("Sorry, we don't have this view type.. :(");
                }
            }

            // Transport response
            System.out.print("Choose a new transport type (You can choose: few, none, little, normal, enough): ");
            currentInput = scanner.nextLine().trim();
            if(!currentInput.isEmpty()) {
                currentInput = currentInput.toUpperCase();
                switch(currentInput) {
                    case "FEW" -> updatingFlat.setTransport(Transport.FEW);
                    case "NONE" -> updatingFlat.setTransport(Transport.NONE);
                    case "LITTLE" -> updatingFlat.setTransport(Transport.LITTLE);
                    case "NORMAL" -> updatingFlat.setTransport(Transport.NORMAL);
                    case "ENOUGH" -> updatingFlat.setTransport(Transport.ENOUGH);
                    default -> System.out.println("Sorry, we don't have this transport type.. :(");
                }
            }

            // House response
            System.out.print("Do you want to enter house data? (yes/no): ");
            currentInput = scanner.nextLine().trim().toLowerCase();

            if(currentInput.equals("yes") || currentInput.equals( "y")) {
                House newHouse = new House();

                // House name response
                System.out.print("Write a new house name(or write 'null' to delete older name): ");
                currentInput = scanner.nextLine().trim();

                if(!currentInput.isEmpty()) {
                    if(!currentInput.equals("null")) {
                        newHouse.setName(currentInput);
                    } else {
                        newHouse.setName(null);
                    }
                } else {
                    newHouse.setName(updatingFlat.getHouse().getName());
                }

                // House year response
                System.out.print("Write a new house year(it must be greater than 0 but lower than 822): ");
                if(!currentInput.isEmpty()) {
                    while(true) {
                        currentInput = scanner.nextLine().trim();

                        if(Integer.parseInt(currentInput) > 0 && Integer.parseInt(currentInput) <= 822) {
                            newHouse.setYear(Integer.parseInt(currentInput));
                            break;
                        } else {
                            System.out.println("Incorrect number! Try again!");
                        }
                    }
                } else {
                    newHouse.setYear(updatingFlat.getHouse().getYear());
                }

                // House Number of flats on room response
                System.out.print("Write a number of flats on room: ");
                while(true) {
                    currentInput = scanner.nextLine().trim();

                    if(!currentInput.isEmpty()) {
                        try {
                            if(Integer.parseInt(currentInput) > 0) {
                                newHouse.setNumberOfFlatsOnFloor(Integer.parseInt(currentInput));
                                break;
                            } else {
                                System.out.println("You should write more rooms than 0...");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Incorrect data! Try again!");
                        }
                    }
                }

                // Adding newHouse to updating flat
                updatingFlat.setHouse(newHouse);
                System.out.println("Congratulations! Your new Flat info: \n" + updatingFlat.toString() );
            }

        } else {
            System.out.println("We don't have an element with this id :(");
        }


    }

    /**
     * Finds a flat by input id
     * @param id It's id of the flat
     * @return The flat with given id, or {@code null} if not found
     */
    Flat findFlatByid(int id) {
        for(Flat flat : storage.getFlatStorage()) {
            if(flat.getId() == id) {
                return flat;
            }
        }
        return null;
    }
}
