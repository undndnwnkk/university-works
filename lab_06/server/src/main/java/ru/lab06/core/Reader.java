package ru.lab06.core;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import ru.lab06.model.Flat;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Vector;

/**
 * The Reader class is responsible for reading JSON files and parsing them into objects.
 */
public class Reader {
    /**
     * Default constructor(nothing to do)
     */
    public Reader(){}
    /**
     * Reads a JSON file and parses it into a collection of Flat objects.
     * @param filename the name of the JSON file
     * @return a Vector of Flat objects
     */
    public static Vector<Flat> readJson(String filename) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8")) {
            Gson gson = new Gson();
            Type type = new TypeToken<Vector<Flat>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (JsonSyntaxException e) {
            System.out.println("Invalid JSON: " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding: " + filename);
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        }
        return null;
    }
}
