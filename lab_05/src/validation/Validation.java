package validation;

import collections.Flat;
import readWrite.Reader;

import java.util.Vector;

/**
 * The {@code Validation} class is responsible for loading data from a JSON file
 * and validating it. Invalid objects are not added to the collection, and the
 * number of discarded elements is displayed in the console.
 */
public class Validation {
    private Vector<Flat> validFlats;
    private int invalidCount;

    /**
     * Constructs a {@code Validation} instance that loads and validates data from a JSON file.
     *
     * @param filename the name of the JSON file to load data from.
     */
    public Validation(String filename) {
        Vector<Flat> loadedFlats = Reader.readJson(filename);
        this.validFlats = new Vector<>();
        this.invalidCount = 0;

        if (loadedFlats != null) {
            for (Flat flat : loadedFlats) {
                if (isValid(flat)) {
                    validFlats.add(flat);
                } else {
                    invalidCount++;
                }
            }
        } else {
            System.out.println("Error loading file. No data has been loaded.");
        }

        System.out.println("Validation completed: " + invalidCount + " invalid elements found.");
    }

    /**
     * Checks whether a {@code Flat} object meets the required conditions.
     *
     * @param flat the object to validate.
     * @return {@code true} if the object is valid, otherwise {@code false}.
     */
    private boolean isValid(Flat flat) {
        return flat != null &&
                flat.getName() != null && !flat.getName().isEmpty() &&
                flat.getCoordinates() != null &&
                flat.getCoordinates().getY() > -46 &&
                flat.getArea() > 0 &&
                flat.getNumberOfRooms() > 0 &&
                flat.getFurnish() != null &&
                flat.getView() != null;
    }

    /**
     * Returns a collection of valid {@code Flat} objects.
     *
     * @return a {@code Vector} containing valid {@code Flat} objects.
     */
    public Vector<Flat> getValidFlats() {
        return validFlats;
    }
}
