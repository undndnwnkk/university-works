package ru.lab05.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The Writer class is responsible for writing objects to JSON files.
 */
public class JsonWriter {
    /**
     * Default constructor(nothing to do)
     */
    public JsonWriter() {}

    /**
     * Writes a given object as JSON to a file.
     * @param filename the name of the file to write to
     * @param data the object to serialize and write
     * @param <T> the type of object being written
     */
    public static <T> void writeJson(String filename, T data) {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            fileWriter.write(gson.toJson(data));
            System.out.println("Success!");
        } catch (IOException e) {
            System.out.println("Error! I can't write to file!");
        }
    }
}
