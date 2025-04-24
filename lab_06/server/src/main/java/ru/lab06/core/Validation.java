package ru.lab06.core;

import org.apache.logging.log4j.core.Logger;
import ru.lab06.model.Flat;
import ru.lab06.model.Furnish;
import ru.lab06.model.View;

import java.util.Vector;

/**
 * The {@code Validation} class is responsible for loading data from a JSON file
 * and validating it. Invalid objects are not added to the collection, and the
 * number of discarded elements is displayed in the console.
 */
public class Validation {
    private final Vector<Flat> validFlats;
    private final Logger logger;

    /**
     * Constructs a {@code Validation} instance that loads and validates data from a JSON file.
     *
     * @param filename the name of the JSON file to load data from.
     */
    public Validation(String filename, Logger logger) {
        Vector<Flat> loadedFlats = Reader.readJson(filename);
        this.logger = logger;
        this.validFlats = new Vector<>();
        int invalidCount = 0;

        if (loadedFlats != null) {
            for (Flat flat : loadedFlats) {
                if (isValid(flat)) {
                    validFlats.add(flat);
                } else {
                    invalidCount++;
                }
            }
        } else {
            logger.warn("Error loading file. No data has been loaded.");
        }

        logger.info("Validation completed: " + invalidCount + " invalid elements found.");
    }

    /**
     * Checks whether a {@code Flat} object meets the required conditions.
     *
     * @param flat the object to validate.
     * @return {@code true} if the object is valid, otherwise {@code false}.
     */
    private boolean isValid(Flat flat) {
        if(flat != null && flat.getName() != null
            && !flat.getName().isEmpty()
            && flat.getCoordinates() != null &&
            flat.getCoordinates().getY() > -46 &&
            flat.getArea() > 0 && flat.getNumberOfRooms() > 0 &&
            flat.getFurnish() != null && flat.getView() != null) {

            return (flat.getFurnish() == Furnish.BAD ||
                    flat.getFurnish() == Furnish.FINE ||
                    flat.getFurnish() == Furnish.LITTLE) &&
                    (flat.getView() == View.PARK ||
                            flat.getView() == View.YARD ||
                            flat.getView() == View.STREET ||
                            flat.getView() == View.TERRIBLE);
        }
        else {
            return false;
        }
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
